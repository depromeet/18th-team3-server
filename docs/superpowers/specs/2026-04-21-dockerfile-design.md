# Dockerfile 설계 스펙

## 목표

Spring Boot 4.0.5 + Kotlin + JDK 25 앱을 Docker 이미지로 빌드하고 로컬에서 실행할 수 있는 환경을 구성한다.

## 접근 방식

**Layered JAR + 멀티 스테이지 빌드**

- Docker 안에서 Gradle 빌드 → JAR 레이어 분리 → 최소 런타임 이미지 생성
- 로컬에 Java/Gradle 설치 없이 `docker build` 하나로 빌드 가능
- 코드만 바뀌면 `application` 레이어만 재빌드되어 CI 캐시 효율 극대화

## Dockerfile 구조

**Stage 1: builder**
- 베이스 이미지: `eclipse-temurin:25-jdk`
- Gradle wrapper 및 `build.gradle.kts` 먼저 복사 → 의존성 다운로드 (캐시 레이어)
- 전체 소스 복사 후 `./gradlew bootJar` 실행

**Stage 2: extractor**
- `builder`에서 생성된 JAR에 `layertools`로 레이어 추출
- 추출 레이어 순서 (캐시 효율 순):
  1. `dependencies`
  2. `spring-boot-loader`
  3. `snapshot-dependencies`
  4. `application`

**Stage 3: runtime**
- 베이스 이미지: `eclipse-temurin:25-jdk`
- non-root 유저(`appuser`) 생성 후 레이어 복사
- `ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]`

## 환경변수 관리

- 민감한 값(API 키 등)은 이미지에 포함하지 않고 런타임에 주입
- 로컬 실행 시 `--env-file .env-local` 옵션으로 주입
- `.env-local`은 `.gitignore`에 추가
- `.env-local.example`을 커밋해서 필요한 변수 목록 공유

## 사용법

```bash
# 이미지 빌드
docker build -t team3-server .

# 로컬 실행
docker run --env-file .env-local -p 8080:8080 team3-server
```

## 변경 파일 목록

- `build.gradle.kts` — layered JAR 활성화 (`bootJar { layered { enabled = true } }`)
- `Dockerfile` — 3단계 멀티 스테이지 빌드
- `.env-local.example` — 필요한 환경변수 목록
- `.gitignore` — `.env-local` 추가
- `.dockerignore` — 불필요한 파일 빌드 컨텍스트 제외
