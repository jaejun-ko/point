package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.domain.user.UserReader;
import com.musinsapayments.pointcore.exception.point.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.ALREADY_USED_POINT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_AMOUNT_PER_ADD;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_CANCELLABLE_USE_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.NOT_USED_POINT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.POINT_NOT_ENOUGH;

@RequiredArgsConstructor
@Service
public class PointServiceImpl implements PointService {

    private final UserReader userReader;

    private final PointStore pointStore;
    private final PointReader pointReader;

    @Transactional
    @Override
    public void addPoint(PointCommand.AddPoint command) {

        var pointConfigure = pointReader.getActivePointConfigure();
        if (command.getAmount() > pointConfigure.getMaxAmountPerAdd()) {
            throw new PointException(EXCEED_MAX_AMOUNT_PER_ADD,
                    "1회 최대 적립 가능한 포인트를 초과하였습니다. (최대 적립 가능 금액: " + pointConfigure.getMaxAmountPerAdd() + ")");
        }

        var user = userReader.getUser(command.getUserId());
        var totalRemainingPoints = pointReader.getTotalRemainingPoints(user.getId());
        if (totalRemainingPoints + command.getAmount() > user.getMaxPoints()) {
            throw new PointException(EXCEED_MAX_AMOUNT,
                    "사용자의 최대 보유 가능 포인트를 초과하였습니다. (최대 보유 가능 포인트: " + user.getMaxPoints() + ")");
        }

        var point = command.toEntity(user);

        pointStore.store(point);
    }

    @Transactional
    @Override
    public void addCancelPoint(PointCommand.AddCancelPoint command) {

        var point = pointReader.getPoint(command.getPointId());
        if (point.isAlreadyUsed()) {
            throw new PointException(ALREADY_USED_POINT, "이미 사용된 포인트는 취소할 수 없습니다.");
        }

        point.cancel();

        var addCancelPoint = Point.builder()
                .user(point.getUser())
                .amount(-point.getAmount())
                .remainingAmount(-point.getRemainingAmount())
                .transactionType(PointTransactionType.ADD_CANCEL)
                .relatedPoint(point)
                .build();
        pointStore.store(addCancelPoint);
    }

    @Transactional
    @Override
    public void usePoint(PointCommand.UsePoint command) {

        var user = userReader.getUser(command.getUserId());
        var usePoint = command.toEntity(user);
        var storedUsePoint = pointStore.store(usePoint);

        var remainingPoints = pointReader.getRemainingPoints(user.getId());

        int requestAmount = command.getAmount();
        for (Point point : remainingPoints) {
            if (requestAmount <= 0) break;

            int deduction = Math.min(requestAmount, point.getRemainingAmount());
            point.use(deduction);

            var pointUsage = new PointUsage(storedUsePoint, point, -deduction);
            pointStore.store(pointUsage);

            requestAmount -= deduction;
        }

        if (requestAmount > 0) throw new PointException(POINT_NOT_ENOUGH, "포인트가 부족합니다.");
    }

    @Transactional
    @Override
    public void useCancelPoint(PointCommand.UseCancelPoint command) {

        var point = pointReader.getPoint(command.getPointId());
        if (! point.getTransactionType().equals(PointTransactionType.USE))
            throw new PointException(NOT_USED_POINT, "사용된 포인트만 취소할 수 있습니다.");
        if (point.getRemainingAmount() + command.getAmount() > 0)
            throw new PointException(EXCEED_MAX_CANCELLABLE_USE_AMOUNT, "취소할 포인트가 사용한 포인트를 초과했습니다.");
        point.use(-command.getAmount());

        var pointUsages = pointReader.getCancellablePointUsages(point.getId());
        if (CollectionUtils.isEmpty(pointUsages))
            throw new PointException(NOT_USED_POINT, "사용된 포인트가 없습니다.");

        var useCancelPoint = Point.builder()
                .user(point.getUser())
                .amount(command.getAmount())
                .remainingAmount(command.getAmount())
                .transactionType(PointTransactionType.USE_CANCEL)
                .relatedPoint(point)
                .build();
        pointStore.store(useCancelPoint);

        int remainingAmount = command.getAmount();

        for (PointUsage pointUsage : pointUsages) {
            if (remainingAmount <= 0) break;

            var relatedPoint = pointUsage.getRelatedPoint();
            int deduction = Math.min(remainingAmount, -pointUsage.getAmount());
            var cancelPointUsage = new PointUsage(point, relatedPoint, deduction);
            pointStore.store(cancelPointUsage);

            if (relatedPoint.isExpiredByDate()) {
                var newAddPoint = Point.builder()
                        .user(point.getUser())
                        .amount(deduction)
                        .remainingAmount(deduction)
                        .transactionType(PointTransactionType.ADD)
                        .expireAt(LocalDate.now().plusDays(365))
                        .relatedPoint(relatedPoint)
                        .build();
                pointStore.store(newAddPoint);
            }

            relatedPoint.use(-deduction);

            remainingAmount -= deduction;
        }

        if (remainingAmount < 0) {
            throw new PointException(EXCEED_MAX_CANCELLABLE_USE_AMOUNT, "취소할 포인트가 사용한 포인트를 초과했습니다.");
        }
    }

    @Transactional
    @Override
    public void expirePoint(PointCommand.ExpirePoint command) {

        var point = pointReader.getPoint(command.getPointId());
        if (point.getTransactionType() != PointTransactionType.ADD) return; // 적립 포인트만 만료 가능
        if (point.isExpired()) return;

        if (command.isForce()) {
            point.expireForce();
        }

        var expirePoint = Point.builder()
                .user(point.getUser())
                .amount(-point.getRemainingAmount())
                .remainingAmount(-point.getRemainingAmount())
                .transactionType(PointTransactionType.EXPIRE)
                .relatedPoint(point)
                .build();
        pointStore.store(expirePoint);

        point.use(point.getRemainingAmount());
    }

    @Transactional(readOnly = true)
    @Override
    public PointInfo.PointMain getPointInfo(Long userId) {

        var user = userReader.getUser(userId);
        var totalRemainingPoints = pointReader.getTotalRemainingPoints(userId);

        return new PointInfo.PointMain(user.getId(), totalRemainingPoints);
    }
}
