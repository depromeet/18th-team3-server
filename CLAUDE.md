# 프로젝트 컨벤션

## Null 처리 원칙

**`== null` / `!= null` 분기를 제거한다.** 모든 nullable 처리는 Elvis(`?:`) + early return / throw / default 로 표현한다.

### 규칙
- **`== null` / `!= null` 사용 금지** — Elvis(`?:`)로 대체한다.
- **`requireNotNull` / `checkNotNull` 은 허용한다.** "non-null이어야 한다"는 의도가 시그니처에 명확히 드러나는 Kotlin 표준 idiom이며, 금지 대상은 어디까지나 `== null` / `!= null` 분기 패턴 한정이다.
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
