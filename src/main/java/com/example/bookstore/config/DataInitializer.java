package com.example.bookstore.config;

import com.example.bookstore.entity.*;
import com.example.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initialization.");
            return;
        }

        log.info("Initializing database with seed data...");

        // Create users
        List<User> users = createUsers();
        log.info("Created {} users", users.size());

        // Create books
        List<Book> books = createBooks();
        log.info("Created {} books", books.size());

        // Create reviews
        createReviews(users, books);
        log.info("Created reviews");

        // Create orders
        createOrders(users, books);
        log.info("Created orders");

        // Create favorites
        createFavorites(users, books);
        log.info("Created favorites");

        log.info("Database initialization completed!");
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // Admin user
        User admin = User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .name("관리자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(User.Gender.MALE)
                .address("서울특별시 강남구")
                .phoneNumber("010-1234-5678")
                .role(User.Role.ROLE_ADMIN)
                .build();
        users.add(userRepository.save(admin));

        // Regular user
        User user = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("유저1")
                .birthDate(LocalDate.of(2002, 3, 18))
                .gender(User.Gender.MALE)
                .address("전라남도 영광군")
                .phoneNumber("010-1111-2222")
                .role(User.Role.ROLE_USER)
                .build();
        users.add(userRepository.save(user));

        // Additional users
        String[] names = {"김철수", "이영희", "박민수", "최지영", "정현우", "강수진", "윤태현", "한소희", "오준서", "신미래"};
        String[] addresses = {"서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도"};

        for (int i = 0; i < names.length; i++) {
            User u = User.builder()
                    .email("user" + (i + 2) + "@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .name(names[i])
                    .birthDate(LocalDate.of(1990 + random.nextInt(20), random.nextInt(12) + 1, random.nextInt(28) + 1))
                    .gender(i % 2 == 0 ? User.Gender.MALE : User.Gender.FEMALE)
                    .address(addresses[i])
                    .phoneNumber(String.format("010-%04d-%04d", random.nextInt(10000), random.nextInt(10000)))
                    .role(User.Role.ROLE_USER)
                    .build();
            users.add(userRepository.save(u));
        }

        return users;
    }

    private List<Book> createBooks() {
        List<Book> books = new ArrayList<>();

        String[][] bookData = {
                {"스프링 부트 완벽 가이드", "홍길동", "한빛미디어", "스프링 부트의 기본부터 심화 개념까지 다루는 완벽 가이드입니다.", "9781234567890", "35000"},
                {"자바 프로그래밍", "김자바", "이지퍼블", "자바 입문자를 위한 친절한 프로그래밍 교재입니다.", "9781234567891", "28000"},
                {"데이터베이스 설계", "박데이터", "디비북스", "효율적인 데이터베이스 설계 방법론을 소개합니다.", "9781234567892", "32000"},
                {"클린 코드", "로버트 마틴", "인사이트", "좋은 코드를 작성하기 위한 원칙과 패턴을 설명합니다.", "9781234567893", "33000"},
                {"리팩토링", "마틴 파울러", "한빛미디어", "코드 품질을 개선하는 리팩토링 기법을 다룹니다.", "9781234567894", "36000"},
                {"알고리즘 문제 해결 전략", "구종만", "인사이트", "프로그래밍 대회에서 배우는 알고리즘 문제 해결 전략", "9781234567895", "42000"},
                {"이펙티브 자바", "조슈아 블로크", "인사이트", "자바 프로그래밍의 베스트 프랙티스를 소개합니다.", "9781234567896", "38000"},
                {"모던 자바스크립트", "니콜라스 자카스", "한빛미디어", "ES6+ 기준의 자바스크립트 완벽 가이드", "9781234567897", "34000"},
                {"리액트를 다루는 기술", "김민준", "길벗", "실무에서 알아야 할 기술은 물론 최신 문법까지!", "9781234567898", "36000"},
                {"Node.js 교과서", "조현영", "길벗", "Node.js의 기본부터 실전까지", "9781234567899", "32000"},
                {"파이썬 코딩 도장", "남재윤", "길벗", "프로그래밍 기초부터 활용까지", "9781234567800", "25000"},
                {"코틀린 인 액션", "드미트리 제메로프", "에이콘", "실전에서 바로 쓰는 코틀린 프로그래밍", "9781234567801", "40000"},
                {"도커/쿠버네티스", "용찬호", "위키북스", "컨테이너 인프라 환경 구축을 위한 실습서", "9781234567802", "38000"},
                {"마이크로서비스 아키텍처", "샘 뉴먼", "한빛미디어", "대용량 서비스를 위한 마이크로서비스 설계 가이드", "9781234567803", "35000"},
                {"HTTP 완벽 가이드", "데이빗 고울리", "인사이트", "웹의 동작 원리를 이해하기 위한 필독서", "9781234567804", "44000"},
                {"운영체제", "Abraham Silberschatz", "홍릉과학출판사", "운영체제의 기본 개념과 원리", "9781234567805", "35000"},
                {"컴퓨터 네트워킹", "James Kurose", "퍼스트북", "컴퓨터 네트워크의 하향식 접근", "9781234567806", "40000"},
                {"기계학습", "오일석", "한빛아카데미", "패턴인식 관점에서 바라본 기계학습", "9781234567807", "32000"},
                {"딥러닝", "이안 굿펠로우", "제이펍", "심층학습의 이론과 실제", "9781234567808", "48000"},
                {"인공지능", "스튜어트 러셀", "에이콘", "현대적 접근방식으로 배우는 AI", "9781234567809", "55000"}
        };

        for (int i = 0; i < bookData.length; i++) {
            Book book = Book.builder()
                    .title(bookData[i][0])
                    .author(bookData[i][1])
                    .publisher(bookData[i][2])
                    .summary(bookData[i][3])
                    .isbn(bookData[i][4])
                    .price(Integer.parseInt(bookData[i][5]))
                    .publicationDate(LocalDate.of(2020 + random.nextInt(5), random.nextInt(12) + 1, random.nextInt(28) + 1))
                    .build();
            books.add(bookRepository.save(book));
        }

        // Additional books to reach 50+
        String[] titles = {"프로그래밍 입문", "웹 개발 실전", "데이터 분석", "보안 기초", "클라우드 컴퓨팅"};
        String[] authors = {"김작가", "이저자", "박필자", "최글쓴이", "정저술"};
        String[] publishers = {"테크북스", "개발출판", "IT미디어", "코딩하우스", "프로그램북"};

        for (int i = 0; i < 30; i++) {
            Book book = Book.builder()
                    .title(titles[i % 5] + " " + (i + 1))
                    .author(authors[i % 5])
                    .publisher(publishers[i % 5])
                    .summary("IT 분야의 핵심 기술을 다루는 도서입니다. 시리즈 " + (i + 1) + "편")
                    .isbn(String.format("978123456%04d", 7810 + i))
                    .price(25000 + (i * 1000))
                    .publicationDate(LocalDate.of(2021 + random.nextInt(4), random.nextInt(12) + 1, random.nextInt(28) + 1))
                    .build();
            books.add(bookRepository.save(book));
        }

        return books;
    }

    private void createReviews(List<User> users, List<Book> books) {
        String[] comments = {
                "정말 유익한 책입니다!",
                "초보자에게 강력 추천합니다.",
                "설명이 친절해서 좋았습니다.",
                "실무에서 바로 적용할 수 있어요.",
                "조금 어렵지만 배울 게 많습니다.",
                "가격 대비 내용이 알찹니다.",
                "기대 이상으로 좋았습니다.",
                "두고두고 참고하기 좋은 책입니다.",
                "전문적인 내용이라 좋습니다.",
                "다른 책들과 비교해도 최고입니다."
        };

        // Create reviews (approximately 100-150)
        for (int i = 0; i < 120; i++) {
            User user = users.get(random.nextInt(users.size()));
            Book book = books.get(random.nextInt(books.size()));

            // Check if review already exists
            if (!reviewRepository.existsByUserAndBookAndDeletedFalse(user, book)) {
                Review review = Review.builder()
                        .user(user)
                        .book(book)
                        .rating(random.nextInt(3) + 3) // 3-5 rating
                        .comment(comments[random.nextInt(comments.length)])
                        .build();
                reviewRepository.save(review);
            }
        }
    }

    private void createOrders(List<User> users, List<Book> books) {
        Order.OrderStatus[] statuses = Order.OrderStatus.values();

        // Create 50+ orders
        for (int i = 0; i < 60; i++) {
            User user = users.get(random.nextInt(users.size()));

            List<OrderItem> items = new ArrayList<>();
            int totalAmount = 0;
            int itemCount = random.nextInt(3) + 1; // 1-3 items per order

            Order order = Order.builder()
                    .user(user)
                    .status(statuses[random.nextInt(statuses.length)])
                    .totalAmount(0)
                    .build();

            for (int j = 0; j < itemCount; j++) {
                Book book = books.get(random.nextInt(books.size()));
                int quantity = random.nextInt(3) + 1;
                int price = book.getPrice() * quantity;
                totalAmount += price;

                OrderItem item = OrderItem.builder()
                        .order(order)
                        .book(book)
                        .quantity(quantity)
                        .price(price)
                        .build();
                items.add(item);
            }

            order.setTotalAmount(totalAmount);
            order.setItems(items);
            orderRepository.save(order);
        }
    }

    private void createFavorites(List<User> users, List<Book> books) {
        // Create 50+ favorites
        for (int i = 0; i < 70; i++) {
            User user = users.get(random.nextInt(users.size()));
            Book book = books.get(random.nextInt(books.size()));

            if (!favoriteRepository.existsByUserAndBookAndDeletedFalse(user, book)) {
                Favorite favorite = Favorite.builder()
                        .user(user)
                        .book(book)
                        .build();
                favoriteRepository.save(favorite);
            }
        }
    }
}
