PR 올리기 전에 변경된 비즈니스 로직 / API에 대한 테스트 코드를 자동 작성·실행하고, 결과를 포함하여 /pr을 호출합니다.

## 절차

### 1단계: 빌드 확인

`./gradlew build`를 실행한다.

- 실패 시 → 컴파일 오류를 출력하고 **즉시 중단**. PR 생성하지 않는다.
- 성공 시 → 다음 단계로 진행.

### 2단계: 변경 파일 분석

아래 명령으로 변경 내역을 파악한다:

```
git diff main...HEAD --name-only
git diff main...HEAD
```

**테스트 작성 대상 파일 판단 기준:**

| 대상 O | 대상 X |
|--------|--------|
| `**/controller/**/*.kt` | `**/repository/**/*.kt` (interface만, 메서드 바디 없음) |
| `**/controller/dto/**/*.kt` (validation 있는 경우) | `**/service/dto/**/*.kt` |
| `**/service/**/*.kt` | `**/config/**/*.kt` |
| `**/domain/**/*.kt` (entity, VO) | `**/*Application.kt` |
| `**/repository/**/*.kt` (구현체, 메서드 바디 있음) | `**/test/**/*.kt` (테스트 파일 자체) |
| | `.github/**`, `*.md`, `*.yml`, `build.gradle.kts` |

파일명뿐 아니라 diff 내용을 확인한다. repository 파일은 실제 구현 로직(메서드 바디)이 있을 때만 대상에 포함한다.

테스트 대상 파일이 없으면 → 바로 **5단계**로 넘어간다.

### 3단계: 테스트 코드 작성

각 대상 파일에 대해 테스트를 작성한다.

- **기존 테스트 파일이 있으면** 보완한다
- **없으면** 신규 생성한다 (`src/test/kotlin/...` 경로에 동일한 패키지 구조로)
- **테스트 스타일**: JUnit5 + kotlin-test (`@Test`, `assertEquals`, `assertFailsWith` 등)
- **Spring 어노테이션**: 레이어에 맞게 사용 (`@WebMvcTest`, `@ExtendWith(MockitoExtension::class)` 등)
- 도메인 이름은 diff에서 동적으로 추출한다 (하드코딩 없음)

### 4단계: 테스트 실행

`./gradlew test`를 실행한다.

- **실패 시** → 실패한 테스트 목록과 오류 내역을 출력하고 **즉시 중단**. PR 생성하지 않는다.
  - 사용자가 수정 후 다시 `/pre-pr`을 실행하도록 안내한다.
- **성공 시** → 아래 형식으로 결과를 대화 컨텍스트에 출력한다:

```
## ✅ 테스트 결과

| 항목 | 결과 |
|------|------|
| 전체 | N passed |
| 신규 작성 | XxxTest, YyyTest, ... |
| 실행 시간 | N.Ns |

<details>
<summary>테스트 목록 보기</summary>

- ✅ XxxTest > 테스트 이름
- ✅ YyyTest > 테스트 이름
...
</details>
```

테스트 대상이 없어서 스킵된 경우:

```
## ⏭️ 테스트 스킵

비즈니스 로직 / API 변경이 감지되지 않아 테스트 작성을 건너뜁니다.
```

### 5단계: 테스트 파일 커밋

테스트 코드를 작성한 경우, `/commit` 커맨드를 호출하여 테스트 파일을 커밋한다.
- 커밋 타입은 `test:`를 사용한다.
- 테스트 파일만 커밋 대상에 포함한다.

### 6단계: PR 생성

`/pr` 커맨드를 호출한다.

`/pr`은 대화 컨텍스트를 기반으로 PR 본문을 작성하므로, 위에서 출력한 테스트 결과가 자연스럽게 PR 본문에 포함된다.

$ARGUMENTS
