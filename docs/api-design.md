# 온라인 서점 API 명세서

## 개요
- **Base URL**: `{BaseUrl}/api`
- **인증 방식**: JWT (Access Token / Refresh Token)
- **공통 응답 형식**: `{ isSuccess, message, payload }`

---

## 1. 인증 (Auth)

### 1.1 회원가입
- **POST** `/api/users`
- **Request Body**:
```json
{
  "email": "example@email.com",
  "password": "password123",
  "name": "유저1",
  "birthDate": "2002-03-18",
  "gender": "MALE",
  "address": "전라남도 영광군",
  "phoneNumber": "010-1234-5678"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "유저를 생성했습니다",
  "payload": {
    "userId": 1
  }
}
```

### 1.2 로그인
- **POST** `/api/auth/login`
- **Request Body**:
```json
{
  "email": "example@email.com",
  "password": "password123"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "로그인 성공",
  "payload": {
    "accessToken": "...",
    "refreshToken": "...",
    "user": {
      "id": 1,
      "role": "ROLE_USER",
      "token_type": "user_token"
    }
  }
}
```

### 1.3 로그아웃
- **POST** `/api/auth/logout`
- **Request**: 없음 (헤더에 토큰 필요)
- **Response**: 없음

---

## 2. 사용자 (Users)

### 2.1 내 정보 조회
- **GET** `/api/users/me`
- **Request**: 없음
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "userId": 1,
    "name": "유저1",
    "email": "example@email.com",
    "birthDate": "2002-03-18",
    "gender": "MALE",
    "address": "전라남도 영광군",
    "phoneNumber": "010-1234-5678"
  }
}
```

### 2.2 회원 정보 수정
- **PATCH** `/api/users/me`
- **Request Body**:
```json
{
  "email": "example@email.com",
  "password": "password123"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "수정 완료",
  "payload": {
    "userId": 2,
    "updatedAt": "2025-09-15T22:06:37.773191"
  }
}
```

---

## 3. 도서 (Books)

### 3.1 도서 생성 (관리자)
- **POST** `/api/admin/books`
- **Request Body**:
```json
{
  "title": "스프링 부트 완벽 가이드",
  "author": "홍길동",
  "publisher": "한빛미디어",
  "summary": "스프링 부트의 기본부터 심화...",
  "isbn": "9781234567890",
  "price": 25000,
  "publicationDate": "2003-04-23"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "도서가 생성되었습니다",
  "payload": {
    "bookId": 1,
    "createdAt": "2025-09-15T22:06:37.773191"
  }
}
```

### 3.2 도서 단건 조회 (공개)
- **GET** `/api/public/books/{bookId}`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "bookId": 1,
    "title": "스프링 부트 완벽 가이드",
    "author": "홍길동",
    "publisher": "한빛미디어",
    "summary": "스프링 부트의 기본부터 심화 개념까지...",
    "isbn": "9781234567890",
    "price": 25000,
    "publicationDate": "2003-04-23"
  }
}
```

### 3.3 도서 목록 조회 (관리자)
- **GET** `/api/admin/books`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": [
    {
      "bookId": 1,
      "title": "스프링부트 완벽 가이드",
      "author": "홍길동",
      "publisher": "한빛미디어"
    }
  ]
}
```

### 3.4 도서 수정 (관리자)
- **PUT** `/api/admin/books/{bookId}`
- **Request Body**:
```json
{
  "title": "스프링 부트 완벽 가이드",
  "author": "홍길동",
  "publisher": "한빛미디어",
  "summary": "스프링 부트의 기본부터 심화 개념까지...",
  "isbn": "9781234567890",
  "price": 25000,
  "publicationDate": "2003-04-23"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "수정 완료",
  "payload": {
    "bookId": 2,
    "updatedAt": "2025-09-15T22:06:37.773191"
  }
}
```

### 3.5 도서 삭제 (관리자)
- **DELETE** `/api/admin/books/{bookId}`
- **Response**: 없음

### 3.6 도서별 평점 조회
- **GET** `/api/books/{bookId}/rating`
- **Response**:
```json
{
  "bookId": 1,
  "averageRating": 4.3,
  "reviewCount": 57
}
```

---

## 4. 장바구니 (Cart)

### 4.1 장바구니 항목 추가
- **POST** `/api/carts/items`
- **Request Body**:
```json
{
  "bookId": 2,
  "quantity": 1
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "장바구니에 추가되었습니다",
  "payload": {
    "cartId": 1
  }
}
```

### 4.2 장바구니 수량 수정
- **PUT** `/api/carts/items`
- **Request Body**:
```json
{
  "bookId": 2,
  "quantity": 4
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "수량이 수정되었습니다",
  "payload": {
    "cartId": 1
  }
}
```

### 4.3 장바구니 조회
- **GET** `/api/carts/items`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": [
    {
      "cartItemId": 1,
      "bookId": 3,
      "quantity": 4
    }
  ]
}
```

### 4.4 장바구니 항목 삭제 (소프트 삭제)
- **DELETE** `/api/carts/items/{cartItemId}`
- **Response**: 없음

---

## 5. 주문 (Orders)

### 5.1 주문 생성
- **POST** `/api/orders`
- **Request Body**:
```json
{
  "items": [
    {
      "bookId": 2,
      "quantity": 1
    }
  ]
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "주문이 정상적으로 생성되었습니다",
  "payload": {
    "orderId": 2,
    "createdAt": "2025-09-15T22:06:37.773191"
  }
}
```

### 5.2 주문 조회
- **GET** `/api/orders/{orderId}`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "orderId": 1,
    "bookId": "2025-09-15T22:06:37.773191",
    "quantity": 1,
    "totalAmount": 25000,
    "status": "CREATED"
  }
}
```

### 5.3 주문 상태 업데이트
- **PATCH** `/api/orders/{orderId}/status`
- **Request Body**:
```json
{
  "status": "SHIPPED"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "상태가 변경되었습니다",
  "payload": {
    "orderId": 2,
    "status": "SHIPPED",
    "updatedAt": "2025-09-15T22:06:37.773191"
  }
}
```

---

## 6. 리뷰 (Reviews)

### 6.1 리뷰 작성
- **POST** `/api/books/{bookId}/reviews`
- **Request Body**:
```json
{
  "rating": 4,
  "comment": "재밌었음"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "리뷰가 등록되었습니다",
  "payload": {
    "reviewId": 2,
    "createdAt": "2025-09-15T22:06:37.773191"
  }
}
```

### 6.2 내 리뷰 전체 조회
- **GET** `/api/reviews/me`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "userId": 2,
    "reviewCount": 4,
    "reviews": [
      {
        "reviewId": 1,
        "bookId": 2,
        "rating": 4,
        "comment": "재밌었음",
        "createdAt": "2025-09-15T22:08:47.620373"
      }
    ]
  }
}
```

### 6.3 리뷰 단건 조회
- **GET** `/api/reviews/{reviewId}`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "userId": 2,
    "bookId": 3,
    "comment": "재밌었음",
    "rating": 3,
    "createdAt": "2025-09-15T22:08:47.620373"
  }
}
```

### 6.4 리뷰 수정
- **PATCH** `/api/reviews/{reviewId}`
- **Request Body**:
```json
{
  "rating": 4,
  "comment": "재밌음"
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "리뷰가 수정되었습니다",
  "payload": {
    "reviewId": 2,
    "updatedAt": "2025-09-15T22:08:47.620373"
  }
}
```

### 6.5 리뷰 삭제
- **DELETE** `/api/reviews/{reviewId}`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "리뷰가 삭제되었습니다"
}
```

---

## 7. 찜 (Favorites)

### 7.1 찜 등록
- **POST** `/api/favorites`
- **Request Body**:
```json
{
  "bookId": 1
}
```
- **Response**:
```json
{
  "isSuccess": true,
  "message": "찜 목록에 추가되었습니다",
  "payload": {
    "favoriteId": 1
  }
}
```

### 7.2 찜 목록 조회
- **GET** `/api/favorites`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": [
    {
      "favoriteId": 1,
      "userId": 3,
      "bookId": 2
    }
  ]
}
```

### 7.3 찜 삭제 (소프트 삭제)
- **DELETE** `/api/favorites/{favoriteId}`
- **Response**:
```json
{
  "isSuccess": true,
  "message": "찜이 삭제되었습니다"
}
```

---

## 엔티티 요약

### User
| 필드 | 타입 | 설명 |
|------|------|------|
| userId | Long | PK |
| email | String | 이메일 |
| password | String | 비밀번호 |
| name | String | 이름 |
| birthDate | LocalDate | 생년월일 |
| gender | String (Enum) | MALE/FEMALE |
| address | String | 주소 |
| phoneNumber | String | 전화번호 |
| role | String (Enum) | ROLE_USER/ROLE_ADMIN |

### Book
| 필드 | 타입 | 설명 |
|------|------|------|
| bookId | Long | PK |
| title | String | 제목 |
| author | String | 저자 |
| publisher | String | 출판사 |
| summary | String | 요약 |
| isbn | String | ISBN |
| price | Integer | 가격 |
| publicationDate | LocalDate | 출판일 |

### Cart / CartItem
| 필드 | 타입 | 설명 |
|------|------|------|
| cartItemId | Long | PK |
| userId | Long | FK -> User |
| bookId | Long | FK -> Book |
| quantity | Integer | 수량 |

### Order / OrderItem
| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | PK |
| userId | Long | FK -> User |
| totalAmount | Integer | 총액 |
| status | String (Enum) | CREATED/SHIPPED/DELIVERED/CANCELLED |
| createdAt | LocalDateTime | 생성일시 |

### Review
| 필드 | 타입 | 설명 |
|------|------|------|
| reviewId | Long | PK |
| userId | Long | FK -> User |
| bookId | Long | FK -> Book |
| rating | Integer | 별점 (1-5) |
| comment | String | 리뷰 내용 |
| createdAt | LocalDateTime | 생성일시 |

### Favorite
| 필드 | 타입 | 설명 |
|------|------|------|
| favoriteId | Long | PK |
| userId | Long | FK -> User |
| bookId | Long | FK -> Book |

---

## API 엔드포인트 요약 (37개)

### 인증 (Auth) - 4개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 1 | POST | /api/users | 회원가입 | Public |
| 2 | POST | /api/auth/login | 로그인 | Public |
| 3 | POST | /api/auth/refresh | 토큰 재발급 | Public |
| 4 | POST | /api/auth/logout | 로그아웃 | User |

### 사용자 (User) - 6개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 5 | GET | /api/users/me | 내 정보 조회 | User |
| 6 | PATCH | /api/users/me | 회원 정보 수정 | User |
| 7 | GET | /api/admin/users | 전체 사용자 목록 조회 | Admin |
| 8 | GET | /api/admin/users/{userId} | 사용자 상세 조회 | Admin |
| 9 | PATCH | /api/admin/users/{userId}/role | 사용자 권한 변경 | Admin |
| 10 | DELETE | /api/admin/users/{userId} | 사용자 삭제 | Admin |

### 도서 (Book) - 7개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 11 | GET | /api/public/books/{bookId} | 도서 단건 조회 | Public |
| 12 | GET | /api/public/books | 도서 목록 조회 (검색/정렬/페이징) | Public |
| 13 | GET | /api/books/{bookId}/rating | 도서 평점 조회 | Public |
| 14 | POST | /api/admin/books | 도서 생성 | Admin |
| 15 | GET | /api/admin/books | 도서 목록 조회 (관리자) | Admin |
| 16 | PUT | /api/admin/books/{bookId} | 도서 수정 | Admin |
| 17 | DELETE | /api/admin/books/{bookId} | 도서 삭제 | Admin |

### 장바구니 (Cart) - 4개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 18 | POST | /api/carts/items | 장바구니 추가 | User |
| 19 | PUT | /api/carts/items | 장바구니 수량 수정 | User |
| 20 | GET | /api/carts/items | 장바구니 조회 | User |
| 21 | DELETE | /api/carts/items/{cartItemId} | 장바구니 항목 삭제 | User |

### 주문 (Order) - 6개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 22 | POST | /api/orders | 주문 생성 | User |
| 23 | GET | /api/orders/{orderId} | 주문 조회 | User |
| 24 | GET | /api/orders | 내 주문 목록 조회 | User |
| 25 | PATCH | /api/orders/{orderId}/status | 주문 상태 변경 | User |
| 26 | GET | /api/admin/orders | 전체 주문 목록 조회 | Admin |
| 27 | GET | /api/admin/orders/status/{status} | 상태별 주문 조회 | Admin |

### 리뷰 (Review) - 6개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 28 | POST | /api/books/{bookId}/reviews | 리뷰 작성 | User |
| 29 | GET | /api/reviews/me | 내 리뷰 조회 | User |
| 30 | GET | /api/reviews/{reviewId} | 리뷰 단건 조회 | Public |
| 31 | GET | /api/books/{bookId}/reviews | 도서별 리뷰 조회 | Public |
| 32 | PATCH | /api/reviews/{reviewId} | 리뷰 수정 | User |
| 33 | DELETE | /api/reviews/{reviewId} | 리뷰 삭제 | User |

### 찜 (Favorite) - 3개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 34 | POST | /api/favorites | 찜 등록 | User |
| 35 | GET | /api/favorites | 찜 목록 조회 | User |
| 36 | DELETE | /api/favorites/{favoriteId} | 찜 삭제 | User |

### 헬스체크 - 1개
| # | Method | Endpoint | 설명 | 권한 |
|---|--------|----------|------|------|
| 37 | GET | /health | 서버 상태 확인 | Public |
