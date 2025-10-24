# SpringBootのプロジェクトディレクトリ構成の調査

### 技術スタック

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff)](#)

## ディレクトリ構成のベストプラクティスは

```text
my-spring-boot-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── demo/
│   │   │               ├── DemoApplication.java (メインクラス)
│   │   │               ├── user/
│   │   │               │   ├── User.java 
│   │   │               │   ├── UserDto.java
│   │   │               │   ├── UserController.java
│   │   │               │   ├── UserService.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── song/
│   │   │               │   ├── Song.java
│   │   │               │   ├── SongDto.java
│   │   │               │   ├── SongController.java
│   │   │               │   ├── SongService.java
│   │   │               │   └── SongRepository.java
│   │   │               ├── artist/
│   │   │               │   ├── Artist.java
│   │   │               │   ├── ArtistDto.java
│   │   │               │   ├── ArtistController.java
│   │   │               │   ├── ArtistService.java
│   │   │               │   └── ArtistRepository.java
│   │   │               └── config/           <-- 共通設定
│   │   │                   └── OpenApiConfig.java
│   │   └── resources/
│   │       ├── application.properties (または application.yml)
│   │       ├── static/ 
│   │       └── templates/ 
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── demo/
│                       └── DemoApplicationTests.java
└── build.gradle (または pom.xml)
```