# 프로젝트 컨벤션

## Null 처리 원칙

**코드에서 `null`이라는 단어 자체를 제거한다.**

### 규칙
- **`== null` / `!= null` 사용 금지** — 모든 null 분기는 Elvis(`?:`) + early return / throw / default 로 표현한다.
- **Elvis + early return 패턴을 기본으로 한다.**
  ```kotlin
  // ❌ 금지
  if (value == null) return Default
  val x = if (value == null) throw E() else value

  // ✅ 권장
  value ?: return Default
  val x = value ?: throw E()
  ```
- 복합 조건이 필요해 보이면 함수를 분해해 **guard clause 여러 줄**로 푼다.
  ```kotlin
  fun toField(value: T?, box: Box?): Field<T> {
      value ?: return Field.NotFound
      box ?: return Field.Inferred(value)
      return Field.Extracted(value, box.toBoundingBox())
  }
  ```
- `sealed class` / `sealed interface` 분기는 `when` + `is` 를 사용한다. (null 체크와는 무관)

### 예외
- 외부 라이브러리 시그니처가 강제하는 경우 (예: `Optional.isPresent()` 같은 Java interop)
- 이 경우에도 **주석으로 이유를 명시**한다.
