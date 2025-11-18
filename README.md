# アプリケーション概要

> **不動産賃貸管理システム** — Web UI と簡易認証を備えた、物件・入居者・賃貸契約を管理するコンパクトな Spring Boot アプリケーション。

---

## 目的 / 価値提案

本プロジェクトは、物件オーナーや管理者向けの **最小限かつ実用的な賃貸管理** Web アプリケーションを提供します。物件の登録・一覧表示、入居者管理、賃貸契約の作成・管理（`ACTIVE`、`NOTICE`、`ENDED` などのステータスを含む）、および認証／登録のための簡易なユーザー機能といった、賃貸物件運用に必要な主要な業務フローに焦点を当てています。プロジェクトは意図的に小規模に設計されており、PoC、教育用サンプル、またはより高度なシステムの出発点として利用しやすくなっています。

---

## 想定ユーザー / ユースケース

* **物件管理者 / オーナー** — 物件カタログの管理、賃貸契約の作成、入居中または解約予告中の物件確認。
* **事務スタッフ / 管理担当者** — 入居者の登録、連絡先情報の更新、契約ライフサイクルの管理。
* **開発者 / 研修生** — Spring Boot + Thymeleaf による Web MVC、Spring Security によるセキュリティ、MyBatis による永続化、賃貸管理に関するドメインモデリングの学習。（サンプルのシードデータと DB スキーマが含まれており、迅速なテストが可能です。）

---

## 提供機能

* サーバーサイドバリデーション付きの物件 CRUD（作成 / 参照 / 更新 / 削除）。
* 入居者 CRUD（メール形式チェックなどのバリデーションあり）。
* 賃貸契約管理：賃貸契約の作成 / 更新 / 削除、ステータス `ACTIVE | NOTICE | ENDED`、敷金 / 礼金 / 家賃、開始日 / 終了日。業務ルールとして、同一物件に対するアクティブ契約の重複を防止します。
* 一連の操作に対するサーバーサイド描画の Web UI（Thymeleaf テンプレート）：一覧、フォーム、詳細ページ。コントローラーはテンプレートレンダリングのためにビュー名を返します。
* Spring Security による認証・登録、BCrypt によるパスワードハッシュ化、登録後の自動ログイン機能。
* MyBatis ベースの永続化層と SQL マッパー XML（賃貸契約の照会は物件・入居者と JOIN して詳細を返します）。
* 起動とデモ用に `schema.sql`／`data.sql` を用意した H2 インメモリ DB。

---

## 主要エンドポイント（Web UI）

本アプリはサーバーサイドレンダリングの Web コントローラー（Thymeleaf）として実装されています。主な URL パターンは以下のとおりです。

* **物件（Properties）**

  * `GET /properties` — 物件一覧（コントローラー：`PropertyController`）。
  * `GET /properties/{id}` — 物件詳細（当該物件の賃貸契約一覧も表示）。
  * `GET /properties/new`, `POST /properties` — 新規物件フォームと保存。
  * `POST /properties/{id}/delete` — 物件削除（有効な契約が存在する場合は削除不可）。

* **入居者（Tenants）**

  * `GET /tenants` — 入居者一覧。
  * `GET /tenants/new`, `POST /tenants` — 新規入居者フォーム / 保存。
  * `GET /tenants/{id}/edit`, `POST /tenants/{id}/delete` — 編集および削除（契約が存在する場合は削除不可）。

* **賃貸契約（Leases）**

  * `GET /leases/new` — 賃貸契約作成フォーム（`propertyId` による事前設定が可能）。
  * `POST /leases` — 賃貸契約の作成 / 更新。サービスは同一物件のアクティブ契約の重複を許容しません。
  * `POST /leases/{id}/delete` — 賃貸契約の削除。

* **認証（Authentication）**

  * `GET /login` — ログインフォーム。`POST /login` — ログイン処理（Spring Security）。`GET /register` と `POST /register` — ユーザー登録（登録後、自動ログイン）。

---

## ドメインモデル

主なドメインオブジェクトと主要フィールド／バリデーション：

* **Property（物件）**

  * `id`, `name`（必須）, `address`（必須）, `area`（必須）, `rooms`, `createdAt`, `updatedAt`。`@NotBlank` によるバリデーション。

* **Tenant（入居者）**

  * `id`, `fullName`（必須）, `phone`, `email`（`@Email`）, タイムスタンプ。

* **Lease（賃貸契約）**

  * `id`, `propertyId`, `tenantId`, `rent`, `startDate`, `endDate`, `status`（`ACTIVE|NOTICE|ENDED`）, `deposit`, `keymoney`, タイムスタンプ、およびナビゲーションプロパティ `property` と `tenant`。`@NotNull` / `@Positive` 等のバリデーションを含みます。マッパーは物件・入居者の詳細を結合した賃貸契約行を返します。

* **User（ユーザー）**

  * `id`, `username`（必須）, `password`（ハッシュ化して保存）, `email`。ログイン／登録に使用。パスワードは BCrypt でハッシュ化されます。

`src/main/resources/schema.sql` と `data.sql` にスキーマとサンプル行が提供されており、モデル（`users`、`properties`、`tenants`、`leases` テーブル）に対応します。

---

## アーキテクチャ / 技術スタック

* **言語 / プラットフォーム**：Java 21、Spring Boot（starter parent）。
* **ビュー層**：Thymeleaf と `thymeleaf-layout-dialect` — サーバーサイドレンダリングの UI テンプレート。
* **セキュリティ**：Spring Security（フォームログイン、CSRF 保護、BCrypt）および `UserDetailsService`（`UserMapper` による実装）。設定は `SecurityConfig` に実装。
* **永続化**：MyBatis（マッパーインターフェース + `mappers/*.xml`）およびデモ用の H2 インメモリ DB。MyBatis の設定は `com.example.app.model` を参照します。
* **ビルド / ツール**：Maven（pom に spring-boot-starter-web、validation、security、mybatis スターター、h2、lombok を含む）。テスト依存には `spring-boot-starter-test` を含みます。

---

## 主な業務ワークフロー

1. **物件閲覧・管理**

   * ユーザーが `/properties` にアクセス → `PropertyController` が `PropertyService` 経由でデータを取得（`PropertyMapper` を使用）。サービスが返すデータを Thymeleaf テンプレートが一覧・フォームとして描画します。物件削除は `leaseService.hasActiveLeases` によって有効な契約がある場合は制限されます。

2. **賃貸契約のライフサイクル**

   * `/leases/new` や編集フォームから賃貸契約を作成／更新。`LeaseServiceImpl` は同一物件に対してアクティブな契約が複数存在しないことを検証（`validateLease` が `findActiveLeasesByPropertyId` をチェック）。作成・更新処理はトランザクション内で実行されます。

3. **入居者管理**

   * `/tenants` から入居者情報を追加・編集。メール形式や必須項目のバリデーションを行います。関連する契約がある場合は入居者の削除を防止します。

4. **認証・登録**

   * `/register` による登録はユーザーを永続化（パスワードは BCrypt でハッシュ化）し、自動的に認証してセッションに `SecurityContext` を保存します。ログイン／ログアウトは Spring Security のフォームログインで処理されます。

---

## 品質保証

* **テスト依存関係**：`pom.xml` はユニットおよび Web セキュリティテストをサポートするために `spring-boot-starter-test` と `spring-security-test` を含みます。

* **推奨テスト**：

  * **ユニットテスト**：`LeaseServiceImpl`（同一物件のアクティブ契約重複を防ぐ検証）、`PropertyServiceImpl`、`TenantServiceImpl`。トランザクションの挙動や例外パス（`IllegalStateException`、`IllegalArgumentException`）を検証します。
  * **コントローラ（Web）テスト**：`@WebMvcTest` を用いてフォームバインディング、リダイレクト、セキュリティ制約（ログイン必須、CSRF トークン）を検証します。
  * **統合テスト**：組み込みの H2 データベース上で `schema.sql` / `data.sql` を実行して、実際の DB インタラクションおよびマッパークエリを検証します。シードデータは迅速な統合テストに利用できます。

* **手動 QA**：ローカル（H2 プロファイル）でアプリを起動し、Web UI にてユーザー登録後に物件／入居者／賃貸契約の各フローを操作します。業務ルール（例：別のアクティブ契約がある場合の `ACTIVE` 契約作成のブロック）が想定どおりに無効な状態を防げることを確認してください。
