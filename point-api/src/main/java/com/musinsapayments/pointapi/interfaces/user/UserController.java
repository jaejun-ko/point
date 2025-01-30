package com.musinsapayments.pointapi.interfaces.user;

import com.musinsapayments.pointapi.application.user.UserFacade;
import com.musinsapayments.pointapi.interfaces.user.dto.UserDto;
import com.musinsapayments.pointcore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping
    public ApiResponse registerUser(@Valid @RequestBody UserDto.RegisterRequest request) {

        var command = request.toCommand();
        var user = userFacade.registerUser(command);
        var response = UserDto.Main.of(user);

        return ApiResponse.success(response);
    }

    @PutMapping("/change-max-points")
    public ApiResponse changeMaxPoints(@Valid @RequestBody UserDto.ChangeMaxPointsRequest request) {

        var command = request.toCommand();
        var user = userFacade.changeMaxPoints(command);
        var response = UserDto.Main.of(user);

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse getUser(@PathVariable Long id) {

        var user = userFacade.getUser(id);
        var response = UserDto.Main.of(user);

        return ApiResponse.success(response);
    }
}
