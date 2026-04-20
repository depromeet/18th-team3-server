# Dockerfile 구현 플랜

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Spring Boot 4.0.5 앱을 Layered JAR + 멀티 스테이지 빌드로 Docker 이미지화하고, `.env-local`로 로컬 실행 가능하게 한다.

**Architecture:** 3단계 멀티 스테이지 빌드 — builder(Gradle 빌드) → extractor(레이어 분리) → runtime(최소 실행 이미지). Spring Boot 2.4+에서 layered JAR은 기본 활성화되어 있으므로 `build.gradle.kts` 수정 불필요.

**Tech Stack:** eclipse-temurin:25-jdk, Spring Boot 4.0.5 layertools, Docker

---

## 파일 목록

| 파일 | 작업 |
|------|------|
| `Dockerfile` | 신규 생성 — 3단계 멀티 스테이지 빌드 |
| `.dockerignore` | 신규 생성 — 빌드 컨텍스트 최소화 |
| `.env-local.example` | 신규 생성 — 필요한 환경변수 목록 |
| `.gitignore` | 수정 — `.env-local` 추가 |

---

### Task 1: `.dockerignore` 생성

**Files:**
- Create: `.dockerignore`

- [ ] **Step 1: `.dockerignore` 파일 생성**

```
.git
.gradle
build
.idea
out
.kotlin
terraform
docs
*.md
.env*
.env-local
```

- [ ] **Step 2: 커밋**

```bash
git add .dockerignore
git commit -m "chore: .dockerignore 추가"
```

---

### Task 2: `.env-local` 관련 파일 설정

**Files:**
- Create: `.env-local.example`
- Modify: `.gitignore`

- [ ] **Step 1: `.env-local.example` 생성**

```
# 로컬 개발용 환경변수 — 이 파일을 복사해 .env-local 을 만들고 값을 채워주세요
# cp .env-local.example .env-local

GEMINI_API_KEY=your-gemini-api-key-here
```

- [ ] **Step 2: `.gitignore`에 `.env-local` 추가**

기존 `# Environment` 섹션 아래에 추가:

```
# Environment
.env
.env.test
.env-local
```

- [ ] **Step 3: 커밋**

```bash
git add .env-local.example .gitignore
git commit -m "chore: .env-local 환경변수 템플릿 및 gitignore 추가"
```

---

### Task 3: `Dockerfile` 작성

**Files:**
- Create: `Dockerfile`

- [ ] **Step 1: `Dockerfile` 생성**

```dockerfile
# ────────────────────────────────────────────
# Stage 1: builder — Gradle 로 JAR 빌드
# ────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# 의존성 레이어만 먼저 복사해서 캐시 활용
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# ────────────────────────────────────────────
# Stage 2: extractor — Layered JAR 분리
# ────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS extractor
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ────────────────────────────────────────────
# Stage 3: runtime — 최소 실행 이미지
# ────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS runtime
WORKDIR /app

# non-root 유저 생성
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# 레이어 순서대로 복사 (변경 빈도 낮은 것 → 높은 것)
COPY --from=extractor /workspace/dependencies/ ./
COPY --from=extractor /workspace/spring-boot-loader/ ./
COPY --from=extractor /workspace/snapshot-dependencies/ ./
COPY --from=extractor /workspace/application/ ./

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

- [ ] **Step 2: 커밋**

```bash
git add Dockerfile
git commit -m "chore: Dockerfile 추가 (Layered JAR + 멀티 스테이지 빌드)"
```

---

### Task 4: 빌드 & 실행 검증

**Files:** 없음 (검증만)

- [ ] **Step 1: 이미지 빌드**

```bash
docker build -t team3-server .
```

예상 출력: 각 스테이지 진행 후 `Successfully tagged team3-server:latest`

- [ ] **Step 2: `.env-local` 파일 준비 (없으면)**

```bash
cp .env-local.example .env-local
# .env-local 파일을 열어 GEMINI_API_KEY 값 채우기
```

- [ ] **Step 3: 컨테이너 실행**

```bash
docker run --env-file .env-local -p 8080:8080 team3-server
```

예상 출력: Spring Boot 시작 로그와 함께 `Started Team3Application`

- [ ] **Step 4: 헬스 체크**

```bash
curl http://localhost:8080/actuator/health
# 또는 브라우저에서 http://localhost:8080 접속
```

> actuator 미설정 시 404 응답도 정상 (서버가 뜨는지 확인이 목적)

- [ ] **Step 5: 레이어 캐시 확인 — 코드 수정 후 재빌드**

`src/` 아래 파일 하나 수정 후:

```bash
docker build -t team3-server .
```

`dependencies` 레이어는 `CACHED`로 표시되고 `application` 레이어만 재빌드되는지 확인.
