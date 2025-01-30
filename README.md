# [무신사 페이먼츠] - Backend Engineer - 과제

<br>
무료 포인트 시스템(API)을 제공하는 서비스를 구현합니다.
<br>

## 기술 스택

* Java 21
* Spring Boot 3.4.2
* H2

## 요구 사항
1. 적립
   1. 1회 적립가능 포인트는 1포인트 이상, 10만포인트 이하로 가능하며 1회 최대 적립가능 포인트는 하드코딩이 아닌 방법으로 제어할수 있어야 한다.
   2. 개인별로 보유 가능한 무료포인트의 최대금액 제한이 존재하며, 하드코딩이 아닌 별도의 방법으로 변경할 수 있어야 한다.
   3. 특정 시점에 적립된 포인트는 1원단위까지 어떤 주문에서 사용되었는지 추적할수 있어야 한다.
   4. 포인트 적립은 관리자가 수기로 지급할 수 있으며, 수기지급한 포인트는 다른 적립과 구분되어 식별할 수 있어야 한다.
   5. 모든 포인트는 만료일이 존재하며, 최소 1일이상 최대 5년 미만의 만료일을 부여할 수 있다. (기본 365일)
2. 적립 취소
   1. 특정 적립행위에서 적립한 금액만큼 취소 가능하며, 적립한 금액중 일부가 사용된 경우라면 적립 취소 될 수 없다.
3. 사용
   1. 주문시에만 포인트를 사용할 수 있다고 가정한다.
   2. 포인트 사용시에는 주문번호를 함께 기록하여 어떤 주문에서 얼마의 포인트를 사용했는지 식별할 수 있어야 한다.
   3. 포인트 사용시에는 관리자가 수기 지급한 포인트가 우선 사용되어야 하며, 만료일이 짧게 남은 순서로 사용해야 한다.
4. 사용 취소
   1. 사용한 금액중 전제 또는 일부를 사용취소 할수 있다.
   2. 사용취소 시점에 이미 만료된 포인를 사용취소 해야 한다면 그 금액만큼 신규적립 처리 한다.

## DB 구조
* 아래 파일 참고
   * resources/scripts/schema.sql
   * resources/erd.png

## 프로젝트 및 패키지 구조
``` text
point
├──point-api
│   ├── application
│   │   ├── point
│   │   └── user 
│   └── interfaces
│       ├── point
│       └── user 
│ 
├──point-batch
│   ├── job
│   └── scheduler
│ 
└──point-core
    ├── common
    ├── configuration
    ├── domain
    │   ├── point
    │   └── user 
    ├── exception
    │   ├── point
    │   └── user 
    └── infrastructure
        ├── point
        └── user 
```

## 실행 방법
```shell
  docker-compose up -d # H2, Redis 실행
```
* point-api 실행
  * 포인트 설정 등록
    * http-test/point-configure-api.http 참고
  * 사용자 생성
    * http-test/user-api.http 참고
* point-batch 실행

## 구현 내용
* 성능을 고려해 Redis 캐싱을 적용하였습니다.
* 포인트 적립, 사용, 취소, 만료 등의 이력을 추적할 수 있도록 설계하였습니다.
* 포인트 사용시 "수동 등록, 만료일이 짧게 남은 순서"로 사용하도록 설계하였습니다.

## 제약 사항
* 사용자 정보와 같은 곳에는 LocalCache 를 도입할 수 있습니다.
* 가용성을 고려해 Message Queue 에 이벤트를 발행하고, Worker 가 처리하는 방식으로 변경할 수 있습니다.
   * resources/aws-architecture.png 참고

## API 명세
* 각 API 명세는 http-test/ 하위에 샘플을 작성하였습니다.

**1. 공통 정보**
* Response

| **필드** | **타입** | **설명** |
|------------|------------|--------------|
| result | string | 결과 (SUCCESS, FAIL) |
| data | object | 데이터 |
| message | string | 메시지 |
| errorCode | string | 에러코드 |

**2. 포인트 설정**

2.1 포인트 설정 등록

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/configures |

- Request

`curl -X POST http://localhost:8080/api/v1/points/configures`

``` json
{  
    "maxAmountPerAdd": 100000
}
```
- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```

2.2 포인트 설정(active) 조회

| **METHOD** | **URL**                                         |
|------------|-------------------------------------------------|
| GET | http://localhost:8080/api/v1/points/configures  |

- Request

`curl -X GET http://localhost:8080/api/v1/points/configures`

- Response

``` json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "maxAmountPerAdd": 100000
  },
  "message": null,
  "errorCode": null
}
```

**3. 사용자**

3.1 사용자 등록

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/users |

- Request

`curl -X POST http://localhost:8080/api/v1/users`

``` json
{
    "name": "신규 사용자",
    "maxPoints": 10000
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "name": "신규 사용자",
    "maxPoints": 10000
  },
  "message": null,
  "errorCode": null
}
```

3.2 보유 가능한 최대 포인트 수정

| **METHOD** | **URL**                                              |
|------------|------------------------------------------------------|
| PUT | http://localhost:8080/api/v1/users/change-max-points |

- Request

`curl -X PUT http://localhost:8080/api/v1/users/change-max-points`

``` json
{
    "userId": 1,
    "maxPoints": 10000000
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "name": "신규 사용자",
    "maxPoints": 10000000
  },
  "message": null,
  "errorCode": null
}
```

3.3 사용자 조회

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| GET | http://localhost:8080/api/v1/users/{userId} |

- Request

`curl -X GET http://localhost:8080/api/v1/users/1`

- Response

``` json
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "name": "신규 사용자",
    "maxPoints": 10000000
  },
  "message": null,
  "errorCode": null
}
```

**4. 포인트**

4.1 포인트 적립

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/add |

- Request

`curl -X POST http://localhost:8080/api/v1/points/add`

``` json
{
    "userId": 1,
    "amount": 1000,
    "manual": false,
    "expireAfterDays": 30,
    "orderId": 0
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```

4.2 포인트 적립 취소

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/add/cancel |

- Request

`curl -X POST http://localhost:8080/api/v1/points/add/cancel`

``` json
{
    "pointId": 1
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```

4.3 포인트 조회

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| GET | http://localhost:8080/api/v1/points/{pointId} |

- Request

`curl -X GET http://localhost:8080/api/v1/points/1`

- Response

``` json
{
  "result": "SUCCESS",
  "data": {
    "userId": 1,
    "totalRemainingPoints": 0
  },
  "message": null,
  "errorCode": null
}
```

4.4 포인트 사용

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/use |

- Request

`curl -X POST http://localhost:8080/api/v1/points/use`

``` json
{
  "userId": 1,
  "amount": 500,
  "orderId": 1
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```

4.5 포인트 강제 만료

| **METHOD** | **URL**                                          |
|------------|--------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/expire-force |

- Request

`curl -X POST http://localhost:8080/api/v1/points/expire-force`

``` json
{
  "pointId": 3
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```

4.6 포인트 사용 취소

| **METHOD** | **URL**                                        |
|------------|------------------------------------------------|
| POST | http://localhost:8080/api/v1/points/use/cancel |

- Request

`curl -X POST http://localhost:8080/api/v1/points/use/cancel`

``` json
{
  "userId": 1,
  "pointId": 4,
  "amount": 500
}
```

- Response

``` json
{
  "result": "SUCCESS",
  "data": null,
  "message": null,
  "errorCode": null
}
```
