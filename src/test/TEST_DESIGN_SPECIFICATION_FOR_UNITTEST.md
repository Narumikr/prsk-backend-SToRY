# テスト設計仕様書

## 概要

このドキュメントは、プロジェクトの単体テスト（Unit Test）を作成する際の設計方針、テストケースの粒度、命名規則、実装パターンを定義したものです。
新しいリソースのテストを実装する際は、この仕様書に従って同じ品質・粒度でテストを作成してください。

## 目次

1. [テスト方針](#テスト方針)
2. [テスト構成](#テスト構成)
3. [Controller層のテスト](#controller層のテスト)
4. [Service層のテスト](#service層のテスト)
5. [テストケース命名規則](#テストケース命名規則)
6. [テストデータの作成方針](#テストデータの作成方針)
7. [カバレッジ目標](#カバレッジ目標)
8. [参考実装例](#参考実装例)

---

## テスト方針

### 基本方針

- **カバレッジ100%を目指す**: 全てのメソッド、分岐、例外処理をカバーする
- **正常系と異常系を網羅**: 成功パス、エラーパス、エッジケースを含める
- **独立性の確保**: 各テストは他のテストに依存せず、独立して実行可能
- **可読性重視**: テストコードは仕様書の役割も果たすため、明確で読みやすく記述する
- **モックの活用**: 外部依存（Repository、Service等）はモック化する

### テスト対象

- **Controller層**: HTTPリクエスト/レスポンスの検証、バリデーション、ステータスコード
- **Service層**: ビジネスロジック、データ操作、例外処理

---

## テスト構成

### ディレクトリ構造

```
src/test/java/com/example/untitled/
├── {resource}/
│   ├── {Resource}ControllerTest.java
│   └── {Resource}ServiceTest.java
└── TEST_DESIGN_SPECIFICATION.md (このファイル)
```

### 使用フレームワーク・ライブラリ

- **JUnit 5**: テスティングフレームワーク
- **Mockito**: モッキングライブラリ
- **Spring MockMvc**: Controller層のテスト
- **AssertJ/Hamcrest**: アサーション

---

## Controller層のテスト

### テスト対象

Controller層では以下を検証します：

1. **HTTPステータスコード**: 正しいステータスコードが返却されるか
2. **レスポンスボディ**: JSONレスポンスの構造と値
3. **バリデーション**: リクエストパラメータ/ボディのバリデーションエラー
4. **例外ハンドリング**: Service層からの例外が適切にHTTPレスポンスに変換されるか
5. **Serviceメソッドの呼び出し**: 正しいパラメータでServiceメソッドが呼ばれているか

### アノテーション

```java
@WebMvcTest({Resource}Controller.class)
public class {Resource}ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private {Resource}Service {resource}Service;
}
```

### テストケースの粒度

#### 1. POST（作成系）エンドポイント

| #   | テストケース                           | 期待する結果                                |
| --- | -------------------------------------- | ------------------------------------------- |
| 1   | 正常系: 有効なデータでリソース作成     | 201 CREATED、レスポンスボディにリソース情報 |
| 2   | 異常系: 必須フィールドがnull           | 400 Bad Request、エラー詳細                 |
| 3   | 異常系: 文字数制限超過（全フィールド） | 400 Bad Request、該当フィールドのエラー     |
| 4   | 異常系: 重複データ（ユニーク制約違反） | 409 Conflict、重複フィールドのエラー        |

#### 2. GET（一覧取得）エンドポイント

| #   | テストケース                                 | 期待する結果                         |
| --- | -------------------------------------------- | ------------------------------------ |
| 1   | 正常系: デフォルトパラメータで一覧取得       | 200 OK、items配列とmeta情報          |
| 2   | 正常系: ページネーションパラメータ指定       | 200 OK、指定ページのデータとmeta情報 |
| 3   | 異常系: pageが0以下                          | 400 Bad Request                      |
| 4   | 異常系: limitが範囲外（0以下または上限超過） | 400 Bad Request                      |

#### 3. GET（単体取得）エンドポイント ※該当する場合

| #   | テストケース                   | 期待する結果         |
| --- | ------------------------------ | -------------------- |
| 1   | 正常系: 有効なIDでリソース取得 | 200 OK、リソース情報 |
| 2   | 異常系: 存在しないID           | 404 Not Found        |
| 3   | 異常系: 不正なIDフォーマット   | 400 Bad Request      |

#### 4. PUT（更新系）エンドポイント

| #   | テストケース                           | 期待する結果                            |
| --- | -------------------------------------- | --------------------------------------- |
| 1   | 正常系: 全フィールド更新               | 200 OK、更新後のリソース情報            |
| 2   | 正常系: 一部フィールドのみ更新         | 200 OK、更新後のリソース情報            |
| 3   | 異常系: 存在しないID                   | 404 Not Found                           |
| 4   | 異常系: 文字数制限超過                 | 400 Bad Request、該当フィールドのエラー |
| 5   | 異常系: 重複データ（ユニーク制約違反） | 409 Conflict、重複フィールドのエラー    |
| 6   | 異常系: 不正なIDパラメータ（0以下）    | 400 Bad Request                         |

#### 5. DELETE（削除系）エンドポイント

| #   | テストケース                   | 期待する結果   |
| --- | ------------------------------ | -------------- |
| 1   | 正常系: 有効なIDでリソース削除 | 204 No Content |
| 2   | 異常系: 存在しないID           | 404 Not Found  |

### 実装パターン

```java
/**
 * POST /{resources} : Response success
 * リソース作成の正常系
 */
@Test
public void create{Resource}Success() throws Exception {
    // Arrange: モックの設定
    {Resource} mock{Resource} = new {Resource}();
    mock{Resource}.setId(1L);
    mock{Resource}.setName("Test Name");

    when({resource}Service.create{Resource}(any())).thenReturn(mock{Resource});

    String reqBody = """
            {
                "name": "Test Name"
            }
            """;

    // Act & Assert: リクエスト実行と検証
    mockMvc.perform(post("/{resources}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(reqBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Name"));

    // Verify: Serviceメソッド呼び出しの検証
    verify({resource}Service, times(1)).create{Resource}(any());
}

/**
 * POST /{resources} : BadRequest
 * 必須フィールドのnullチェック
 */
@Test
public void create{Resource}Error_withBadRequest_RequiredFieldNull() throws Exception {
    String reqBody = """
            {
                "name": null
            }
            """;

    mockMvc.perform(post("/{resources}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(reqBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details").exists())
            .andExpect(jsonPath("$.details[0].field").value("name"));
}
```

---

## Service層のテスト

### テスト対象

Service層では以下を検証します：

1. **ビジネスロジック**: 正しい処理フローで実行されるか
2. **データ操作**: Repositoryメソッドが正しく呼ばれているか
3. **例外スロー**: 適切な条件で適切な例外がスローされるか
4. **データ変換**: DTO ⇔ Entity の変換が正しいか
5. **条件分岐**: 全ての分岐パターンをカバー

### アノテーション

```java
@ExtendWith(MockitoExtension.class)
public class {Resource}ServiceTest {

    @Mock
    private {Resource}Repository {resource}Repository;

    @InjectMocks
    private {Resource}Service {resource}Service;
}
```

### テストケースの粒度

#### 1. 作成系メソッド

| #   | テストケース                 | 期待する結果                                |
| --- | ---------------------------- | ------------------------------------------- |
| 1   | 正常系: 新規リソース作成成功 | リソースが保存され、正しい値が返却される    |
| 2   | 異常系: ユニーク制約違反     | DuplicationResourceException がスローされる |

#### 2. 一覧取得系メソッド

| #   | テストケース                 | 期待する結果                                 |
| --- | ---------------------------- | -------------------------------------------- |
| 1   | 正常系: ASCソートで一覧取得  | ソート済みのPageオブジェクトが返却される     |
| 2   | 正常系: DESCソートで一覧取得 | 降順ソート済みのPageオブジェクトが返却される |
| 3   | 正常系: 空リストの場合       | 空のPageオブジェクトが返却される             |

#### 3. 単体取得系メソッド ※該当する場合

| #   | テストケース                   | 期待する結果                           |
| --- | ------------------------------ | -------------------------------------- |
| 1   | 正常系: 有効なIDでリソース取得 | リソースが返却される                   |
| 2   | 異常系: 存在しないID           | EntityNotFoundException がスローされる |

#### 4. 更新系メソッド

| #   | テストケース                               | 期待する結果                                |
| --- | ------------------------------------------ | ------------------------------------------- |
| 1   | 正常系: 全フィールド更新                   | 全フィールドが更新され保存される            |
| 2   | 正常系: 一部フィールド更新                 | 指定フィールドのみ更新される                |
| 3   | 正常系: 同じ値で更新（ユニークフィールド） | 重複チェックがスキップされる                |
| 4   | 正常系: 全フィールドnull（Optional）       | 元の値が維持される                          |
| 5   | 異常系: 存在しないID                       | EntityNotFoundException がスローされる      |
| 6   | 異常系: ユニーク制約違反                   | DuplicationResourceException がスローされる |

#### 5. 削除系メソッド

| #   | テストケース         | 期待する結果                           |
| --- | -------------------- | -------------------------------------- |
| 1   | 正常系: 論理削除成功 | isDeletedフラグがtrueに設定される      |
| 2   | 異常系: 存在しないID | EntityNotFoundException がスローされる |

### 実装パターン

```java
/**
 * create{Resource} : 正常系 - リソースが正常に作成される
 */
@Test
public void create{Resource}Success() {
    // Arrange: テストデータ準備
    {Resource}Request request = new {Resource}Request();
    request.setName("Test Name");

    {Resource} created{Resource} = new {Resource}();
    created{Resource}.setId(1L);
    created{Resource}.setName("Test Name");

    // モックの設定
    when({resource}Repository.findByNameAndIsDeleted("Test Name", false))
            .thenReturn(Optional.empty());
    when({resource}Repository.save(any({Resource}.class)))
            .thenReturn(created{Resource});

    // Act: メソッド実行
    {Resource} result = {resource}Service.create{Resource}(request);

    // Assert: 結果検証
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Test Name", result.getName());

    // Verify: メソッド呼び出し検証
    verify({resource}Repository, times(1))
            .findByNameAndIsDeleted("Test Name", false);
    verify({resource}Repository, times(1))
            .save(any({Resource}.class));
}

/**
 * create{Resource} : 異常系 - 名前が重複しており、例外をスローする
 */
@Test
public void create{Resource}Error_withDuplication() {
    // Arrange
    {Resource}Request request = new {Resource}Request();
    request.setName("Duplicate Name");

    {Resource} existing{Resource} = new {Resource}();
    existing{Resource}.setId(1L);
    existing{Resource}.setName("Duplicate Name");

    when({resource}Repository.findByNameAndIsDeleted("Duplicate Name", false))
            .thenReturn(Optional.of(existing{Resource}));

    // Act & Assert: 例外検証
    DuplicationResourceException exception = assertThrows(
            DuplicationResourceException.class,
            () -> {resource}Service.create{Resource}(request)
    );

    assertNotNull(exception.getDetails());
    assertEquals("name", exception.getDetails().get(0).getField());

    // Verify: saveが呼ばれていないことを確認
    verify({resource}Repository, never()).save(any({Resource}.class));
}

/**
 * update{Resource} : 正常系 - 全フィールド更新
 */
@Test
public void update{Resource}Success_AllFields() {
    // Arrange
    {Resource} existing{Resource} = new {Resource}();
    existing{Resource}.setId(1L);
    existing{Resource}.setName("Original Name");

    Optional{Resource}Request request = new Optional{Resource}Request();
    request.setName("Updated Name");

    when({resource}Repository.findByIdAndIsDeleted(1L, false))
            .thenReturn(Optional.of(existing{Resource}));
    when({resource}Repository.findByNameAndIsDeleted("Updated Name", false))
            .thenReturn(Optional.empty());
    when({resource}Repository.save(any({Resource}.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    {Resource} result = {resource}Service.update{Resource}(1L, request);

    // Assert
    assertNotNull(result);
    assertEquals("Updated Name", result.getName());

    verify({resource}Repository, times(1)).findByIdAndIsDeleted(1L, false);
    verify({resource}Repository, times(1)).save(any({Resource}.class));
}
```

---

## テストケース命名規則

### 基本フォーマット

```
{メソッド名}{正常系/異常系}[_with{条件}][_{詳細}]
```

### 例

- **正常系**: `create{Resource}Success`
- **異常系（条件付き）**: `create{Resource}Error_withBadRequest_RequiredFieldNull`
- **正常系（条件付き）**: `get{Resource}ListSuccess_WithPaginationParams`

### 命名パターン一覧

| パターン                                 | 説明               | 例                                            |
| ---------------------------------------- | ------------------ | --------------------------------------------- |
| `{method}Success`                        | 正常系の基本ケース | `createArtistSuccess`                         |
| `{method}Success_{detail}`               | 正常系の特定条件   | `updateArtistSuccess_PartialUpdate`           |
| `{method}Error_with{ErrorType}`          | 異常系の基本ケース | `createArtistError_withConflict`              |
| `{method}Error_with{ErrorType}_{detail}` | 異常系の詳細ケース | `createArtistError_withBadRequest_LengthOver` |

### JavaDocコメント

各テストメソッドには以下のフォーマットでコメントを付与：

```java
/**
 * {HTTPメソッド} /{endpoint} : {レスポンスタイプ/シナリオ}
 * {テストの説明（日本語）}
 */
@Test
public void testMethodName() throws Exception {
    // テスト実装
}
```

---

## テストデータの作成方針

### 1. 有効なテストデータ

```java
// 正常系で使用する標準的なデータ
Artist artist = new Artist();
artist.setId(1L);
artist.setArtistName("Test Artist");
artist.setUnitName("Test Unit");
artist.setContent("Test Content");
```

### 2. バリデーションエラー用データ

```java
// 文字数超過
String tooLongName = generateRandomString(51); // 50文字制限の場合

// null値
String nullValue = null;

// 空文字
String emptyValue = "";
```

### 3. ページネーション用データ

```java
// 1ページ目（10件中の最初の5件）
Page<Artist> page1 = new PageImpl<>(
    List.of(artist1, artist2, artist3, artist4, artist5),
    PageRequest.of(0, 5),
    10  // 総件数
);

// 2ページ目（10件中の残り5件）
Page<Artist> page2 = new PageImpl<>(
    List.of(artist6, artist7, artist8, artist9, artist10),
    PageRequest.of(1, 5),
    10  // 総件数
);
```

### 4. エンティティ作成のヘルパーメソッド（推奨）

```java
// テストクラス内にヘルパーメソッドを作成
private {Resource} create{Resource}(Long id, String name) {
    {Resource} {resource} = new {Resource}();
    {resource}.setId(id);
    {resource}.setName(name);
    return {resource};
}
```

---

## カバレッジ目標

### 目標値

- **ライン カバレッジ**: 100%
- **ブランチ カバレッジ**: 100%
- **メソッド カバレッジ**: 100%

### 必須カバー項目

1. ✅ 全てのpublicメソッド
2. ✅ 全ての条件分岐（if/else, switch/case）
3. ✅ 全ての例外処理（try/catch, throw）
4. ✅ 全てのバリデーションルール
5. ✅ ページネーションの境界値

### カバレッジレポートの確認

プロジェクトでJaCoCoを設定している場合：

```bash
./gradlew test jacocoTestReport
```

レポートは `build/reports/jacoco/test/html/index.html` で確認可能。

---

## 参考実装例

### 完全な実装例

[ArtistControllerTest.java](./java/com/example/untitled/artist/ArtistControllerTest.java)
[ArtistServiceTest.java](./java/com/example/untitled/artist/ArtistServiceTest.java)

### 実装統計（Artistの例）

- **ArtistControllerTest**: 16テストケース
  - POST: 4ケース
  - GET: 4ケース
  - PUT: 6ケース
  - DELETE: 2ケース

- **ArtistServiceTest**: 13テストケース
  - createArtist: 2ケース
  - getAllArtists: 3ケース
  - updateArtist: 6ケース
  - deleteArtist: 2ケース

- **合計**: 29テストケース、全て成功

---

## チェックリスト

新しいリソースのテストを実装する際は、以下のチェックリストを使用してください：

### Controller層

- [ ] POST: 正常系（作成成功）
- [ ] POST: 必須フィールドnull
- [ ] POST: 文字数制限超過
- [ ] POST: ユニーク制約違反
- [ ] GET（一覧）: デフォルトパラメータ
- [ ] GET（一覧）: ページネーション指定
- [ ] GET（一覧）: 不正なpage値
- [ ] GET（一覧）: 不正なlimit値
- [ ] GET（単体）: 正常系 ※該当する場合
- [ ] GET（単体）: 存在しないID ※該当する場合
- [ ] PUT: 全フィールド更新
- [ ] PUT: 一部フィールド更新
- [ ] PUT: 存在しないID
- [ ] PUT: 文字数制限超過
- [ ] PUT: ユニーク制約違反
- [ ] PUT: 不正なIDパラメータ
- [ ] DELETE: 正常系
- [ ] DELETE: 存在しないID

### Service層

- [ ] create: 正常系
- [ ] create: 重複エラー
- [ ] getAll: ASCソート
- [ ] getAll: DESCソート
- [ ] getAll: 空リスト
- [ ] getById: 正常系 ※該当する場合
- [ ] getById: 存在しないID ※該当する場合
- [ ] update: 全フィールド更新
- [ ] update: 一部フィールド更新
- [ ] update: 同じ値（ユニークフィールド）
- [ ] update: 全フィールドnull
- [ ] update: 存在しないID
- [ ] update: 重複エラー
- [ ] delete: 正常系
- [ ] delete: 存在しないID

---

## 補足事項

### テスト実行コマンド

```bash
# 全テスト実行
./gradlew test

# 特定のテストクラスのみ実行
./gradlew test --tests "com.example.untitled.{resource}.{Resource}ControllerTest"
./gradlew test --tests "com.example.untitled.{resource}.{Resource}ServiceTest"

# 特定のテストメソッドのみ実行
./gradlew test --tests "com.example.untitled.{resource}.{Resource}ServiceTest.create{Resource}Success"
```

### トラブルシューティング

#### モックが機能しない

- `@MockitoBean`（Controller）または`@Mock`（Service）アノテーションを確認
- `when().thenReturn()`の設定が正しいか確認

#### JSONPathが見つからない

- レスポンス構造を確認（`meta`オブジェクトなどのネスト構造）
- `$.meta.pageIndex`のようにドット記法でアクセス

#### ページネーションのテストが失敗

- `PageImpl`の第3引数（総件数）が正しいか確認
- ページインデックスは0-basedであることに注意

---

## 更新履歴

| 日付       | バージョン | 変更内容                                   | 作成者 |
| ---------- | ---------- | ------------------------------------------ | ------ |
| 2025-12-19 | 1.0.0      | 初版作成（Artistリソースの実装を基に作成） | -      |

---

このドキュメントに従うことで、プロジェクト全体で統一された品質のテストコードを維持できます。
