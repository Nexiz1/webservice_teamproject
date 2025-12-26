# 온라인 서점 API (Bookstore API)

온라인 서점 백엔드 REST API 서버입니다.

## 프로젝트 개요

### 주요 기능
- **회원 관리**: 회원가입, 로그인, 회원 정보 수정
- **도서 관리**: 도서 등록/수정/삭제, 도서 검색, 평점 조회
- **장바구니**: 장바구니 추가/수정/삭제/조회
- **주문 관리**: 주문 생성, 주문 조회, 주문 상태 변경
- **리뷰 관리**: 리뷰 작성/수정/삭제, 리뷰 조회
- **찜 관리**: 찜 등록/삭제/조회

### 기술 스택
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Authentication**: JWT (JSON Web Token)
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle 8.5

---

## 배포 정보

| 항목 | URL                                          |
|------|----------------------------------------------|
| **Base URL** | `http://113.198.66.75:10168/api`            |
| **Swagger UI** | `http://113.198.66.75:10168/swagger-ui.html` |
| **Health Check** | `http://113.198.66.75:10168/health`       |

---

## 실행 방법

### 환경 변수 설정

`.env.example`을 참고하여 `.env` 파일을 생성합니다.
```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true
DB_USERNAME=your_username
DB_PASSWORD=your_password
DB_DRIVER=com.mysql.cj.jdbc.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_must_be_at_least_256_bits_long_for_security

# Server Configuration
SERVER_PORT=8080
```

### 로컬 실행
```bash
# 프로젝트 클론
git clone https://github.com/{username}/bookstore.git
cd bookstore

# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행 (방법 1)
./gradlew bootRun

# 또는 JAR 파일 실행 (방법 2)
java -jar build/libs/bookstore-0.0.1-SNAPSHOT.jar
```

### 테스트 실행
```bash
./gradlew test
```

### JCloud 배포
```bash
# 1. 프로젝트 빌드
./gradlew clean build -x test

# 2. JAR 파일을 서버로 전송
scp -i your-key.pem build/libs/bookstore-0.0.1-SNAPSHOT.jar ubuntu@{서버IP}:~/

# 3. 서버에서 실행 (백그라운드)
nohup java -jar bookstore-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# 또는 systemd 서비스로 등록 (권장)
sudo systemctl start bookstore
```

---

## 테스트 계정

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin@example.com | admin123 |
| 일반 사용자 | user@example.com | password123 |

---

## DB 연결 정보 (테스트용)

| 항목 | 값 |
|------|-----|
| Host | `113.198.66.75` |
| Port | `3306` |
| Database | `bookstore` |
| Username | `.env` 파일 참조 |
| Password | `.env` 파일 참조 |
```bash
# MySQL 접속 명령어
mysql -h 113.198.66.75 -P 3306 -u {username} -p bookstore
```

---

## 인증 방식

### JWT 기반 인증
1. `/api/auth/login`으로 로그인하여 Access Token과 Refresh Token을 발급받습니다.
2. API 요청 시 헤더에 `Authorization: Bearer {access_token}`을 포함합니다.
3. Access Token 만료 시 `/api/auth/refresh`로 새 토큰을 발급받습니다.

### 토큰 유효시간
- Access Token: 1시간
- Refresh Token: 7일

---

## 역할 및 권한

| 역할 | 설명 |
|------|------|
| `ROLE_USER` | 일반 사용자 - 도서 조회, 장바구니, 주문, 리뷰, 찜 기능 |
| `ROLE_ADMIN` | 관리자 - 도서 CRUD, 사용자 관리, 주문 관리 등 모든 기능 |

### 권한별 접근 가능 API

| 구분 | 엔드포인트 |
|------|-----------|
| **Public** | `/api/users` (회원가입), `/api/auth/**`, `/api/public/**`, `/api/books/*/rating`, `/api/reviews/*`, `/api/books/*/reviews`, `/health` |
| **User** | `/api/users/me`, `/api/carts/**`, `/api/orders/**`, `/api/reviews/**` (POST/PATCH/DELETE), `/api/favorites/**` |
| **Admin** | `/api/admin/**` |

---

## API 엔드포인트 요약 (37개)

### 인증 (Auth) - 4개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | /api/users | 회원가입 | Public |
| POST | /api/auth/login | 로그인 | Public |
| POST | /api/auth/refresh | 토큰 재발급 | Public |
| POST | /api/auth/logout | 로그아웃 | User |

### 사용자 (User) - 6개
| Method | Endpoint                       | 설명 | 권한 |
|--------|--------------------------------|------|------|
| GET | /api/users/me                  | 내 정보 조회 | User |
| PATCH | /api/users/me                  | 회원 정보 수정 | User |
| GET | /api/admin/users               | 전체 사용자 목록 (검색: name) | Admin |
| GET | /api/admin/users/{userId}      | 사용자 상세 조회 | Admin |
| PATCH | /api/admin/users/{userId}/role | 사용자 권한 변경 | Admin |
| DELETE | /api/admin/users/{userId}      | 사용자 삭제 | Admin |

### 도서 (Book) - 7개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | /api/public/books/{bookId} | 도서 단건 조회 | Public |
| GET | /api/public/books | 도서 목록 조회 (검색/정렬/페이징) | Public |
| GET | /api/books/{bookId}/rating | 도서 평점 조회 | Public |
| POST | /api/admin/books | 도서 생성 | Admin |
| GET | /api/admin/books | 도서 목록 조회 | Admin |
| PUT | /api/admin/books/{bookId} | 도서 수정 | Admin |
| DELETE | /api/admin/books/{bookId} | 도서 삭제 | Admin |

### 장바구니 (Cart) - 4개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | /api/carts/items | 장바구니 추가 | User |
| PUT | /api/carts/items | 장바구니 수량 수정 | User |
| GET | /api/carts/items | 장바구니 조회 | User |
| DELETE | /api/carts/items/{cartItemId} | 장바구니 항목 삭제 | User |

### 주문 (Order) - 6개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | /api/orders | 주문 생성 | User |
| GET | /api/orders/{orderId} | 주문 조회 | User |
| GET | /api/orders | 내 주문 목록 조회 | User |
| PATCH | /api/orders/{orderId}/status | 주문 상태 변경 | User |
| GET | /api/admin/orders | 전체 주문 목록 | Admin |
| GET | /api/admin/orders/status/{status} | 상태별 주문 조회 | Admin |

### 리뷰 (Review) - 6개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | /api/books/{bookId}/reviews | 리뷰 작성 | User |
| GET | /api/reviews/me | 내 리뷰 조회 | User |
| GET | /api/reviews/{reviewId} | 리뷰 단건 조회 | Public |
| GET | /api/books/{bookId}/reviews | 도서별 리뷰 조회 | Public |
| PATCH | /api/reviews/{reviewId} | 리뷰 수정 | User |
| DELETE | /api/reviews/{reviewId} | 리뷰 삭제 | User |

### 찜 (Favorite) - 3개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | /api/favorites | 찜 등록 | User |
| GET | /api/favorites | 찜 목록 조회 | User |
| DELETE | /api/favorites/{favoriteId} | 찜 삭제 | User |

### 헬스체크 - 1개
| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | /health | 서버 상태 확인 | Public |

---

## 검색/정렬/페이지네이션

### 도서 검색 예시
```
GET /api/public/books?keyword=스프링&author=홍길동&page=0&size=20&sort=createdAt,DESC
```

### 쿼리 파라미터
| 파라미터 | 설명 | 기본값 | 최대값 |
|----------|------|--------|--------|
| keyword | 제목 검색어 | - | - |
| author | 저자 검색 | - | - |
| publisher | 출판사 검색 | - | - |
| page | 페이지 번호 (0부터) | 0 | - |
| size | 페이지 크기 | 20 | 100 |
| sort | 정렬 (field,ASC/DESC) | createdAt,DESC | - |

### 페이지네이션 응답 형식
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "sort": "createdAt: DESC"
  }
}
```

---

## 에러 코드

### 에러 응답 형식
```json
{
  "timestamp": "2025-01-15T12:00:00",
  "path": "/api/books/999",
  "status": 404,
  "code": "BOOK_NOT_FOUND",
  "message": "도서를 찾을 수 없습니다",
  "details": null
}
```

### 에러 코드 목록 (27개)

| HTTP | 코드 | 설명 |
|------|------|------|
| 400 | BAD_REQUEST | 잘못된 요청 |
| 400 | VALIDATION_FAILED | 입력값 검증 실패 |
| 400 | INVALID_QUERY_PARAM | 쿼리 파라미터 오류 |
| 401 | UNAUTHORIZED | 인증 필요 |
| 401 | TOKEN_EXPIRED | 토큰 만료 |
| 401 | INVALID_TOKEN | 유효하지 않은 토큰 |
| 401 | INVALID_CREDENTIALS | 인증 실패 |
| 403 | FORBIDDEN | 접근 권한 없음 |
| 404 | RESOURCE_NOT_FOUND | 리소스 없음 |
| 404 | USER_NOT_FOUND | 사용자 없음 |
| 404 | BOOK_NOT_FOUND | 도서 없음 |
| 404 | CART_NOT_FOUND | 장바구니 없음 |
| 404 | CART_ITEM_NOT_FOUND | 장바구니 항목 없음 |
| 404 | ORDER_NOT_FOUND | 주문 없음 |
| 404 | REVIEW_NOT_FOUND | 리뷰 없음 |
| 404 | FAVORITE_NOT_FOUND | 찜 목록 없음 |
| 409 | DUPLICATE_RESOURCE | 리소스 중복 |
| 409 | DUPLICATE_EMAIL | 이메일 중복 |
| 409 | DUPLICATE_ISBN | ISBN 중복 |
| 409 | DUPLICATE_REVIEW | 리뷰 중복 |
| 409 | DUPLICATE_FAVORITE | 찜 중복 |
| 409 | STATE_CONFLICT | 리소스 상태 충돌 |
| 422 | UNPROCESSABLE_ENTITY | 처리 불가 |
| 429 | TOO_MANY_REQUESTS | 요청 한도 초과 |
| 500 | INTERNAL_SERVER_ERROR | 서버 오류 |
| 500 | DATABASE_ERROR | DB 오류 |
| 500 | UNKNOWN_ERROR | 알 수 없는 오류 |

---

## 보안 및 성능 고려사항

### 보안
- ✅ 비밀번호는 BCrypt로 해싱하여 저장
- ✅ JWT 토큰 기반 인증 (Access Token + Refresh Token)
- ✅ 환경변수를 통한 민감정보 관리 (.env)
- ✅ CORS 설정
- ✅ 역할 기반 접근 제어 (RBAC)
- ✅ Rate Limiting (IP당 분당 100회)

### 성능
- ✅ 페이지네이션 적용
- ✅ 데이터베이스 인덱스 설정

### Rate Limiting
- 제한: IP당 분당 100회 요청
- 초과 시: 429 Too Many Requests 응답
- 헤더: `X-RateLimit-Limit`, `X-RateLimit-Remaining`

---

## 프로젝트 구조
```
bookstore/
├── src/
│   ├── main/
│   │   ├── java/com/example/bookstore/
│   │   │   ├── config/          # 설정 (Security, Swagger, RateLimit)
│   │   │   ├── controller/      # REST 컨트롤러
│   │   │   ├── dto/             # 요청/응답 DTO
│   │   │   ├── entity/          # JPA 엔티티
│   │   │   ├── exception/       # 예외 처리
│   │   │   ├── repository/      # JPA 리포지토리
│   │   │   ├── security/        # JWT 관련
│   │   │   └── service/         # 비즈니스 로직
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/bookstore/
│           └── controller/      # 통합 테스트 (32개)
├── postman/
│   └── bookstore.postman_collection.json
├── ERD.sql                      # 데이터베이스 스키마
├── api-spec.md                  # API 명세서
├── .env.example                 # 환경변수 템플릿
├── build.gradle                 # Gradle 빌드 설정
└── README.md
```

---

## 시드 데이터

애플리케이션 시작 시 자동으로 생성되는 테스트 데이터:

| 테이블 | 건수 |
|--------|------|
| 사용자 (User) | 12명 |
| 도서 (Book) | 50권 |
| 리뷰 (Review) | ~120건 |
| 주문 (Order) | 60건 |
| 찜 (Favorite) | ~70건 |
| **총합** | **~300건 이상** |

---

## 한계 및 개선 계획

### 현재 한계
- 파일 업로드 기능 미구현
- 실시간 알림 기능 없음
- Redis 캐싱 미적용

### 개선 계획
- [ ] Redis 캐싱 적용 (인기 도서, 카테고리)
- [ ] 이미지 업로드 (S3 연동)
- [ ] 결제 시스템 연동
- [ ] 실시간 알림 (WebSocket)
- [ ] Docker 컨테이너화
- [ ] CI/CD 파이프라인 구축

---

## 최근 주요 업데이트 (2025-12-26)

### 1. 인증 시스템 고도화 및 UX 개선
- **Firebase & OAuth2**: Google 및 GitHub 소셜 로그인 연동을 완료하였습니다.
- **예외 처리**: OAuth2 로그인 시 비밀번호(password)가 null일 경우에 대한 로직을 보완했습니다.
- **리다이렉션**: 인증되지 않은 사용자가 접근할 경우, 에러 페이지가 아닌 로그인 페이지로 자동 리다이렉트되도록 수정하였습니다.

### 2. 배포 및 인프라 자동화
- **Dockerizing**: `docker-compose.yml`을 통해 MySQL, Redis, App을 원클릭으로 실행할 수 있습니다.
- **GHCR 배포**: GitHub Actions를 통해 `release` 브랜치 푸시 시 도커 이미지가 GHCR에 자동으로 빌드 및 업로드됩니다.
- **CI 파이프라인**: 모든 PR 및 푸시 시 Gradle 테스트가 자동으로 수행되어 코드 안정성을 보장합니다.

- Redis Integration: 세션 관리 및 성능 최적화를 위한 Redis 도입.
- 

## 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
