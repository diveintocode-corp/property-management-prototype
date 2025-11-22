# パート2：ビジネスロジックとエンティティ間ルール（10分）

## サービス層パターン（2分）

> 「お帰りなさいませ。パート1では基盤（モデル、マッパー、コントローラー）を構築いたしました。今回は、システムの知性である**サービス層**に取り組みます。
>
> サービスはビジネスルールが存在する場所でございます。コントローラーとマッパーの間に位置し、データの整合性を保つ制約を強制いたします。
>
> すべてのサービスに対して**インターフェースベースのパターン**に従っております：
> *   `PropertyService` / `PropertyServiceImpl`
> *   `TenantService` / `TenantServiceImpl`
> *   `LeaseService` / `LeaseServiceImpl`
>
> **なぜインターフェースか？** これにより依存性注入が可能になり、テストが容易になります。ユニットテストでサービスをモック化できます。
>
> **トランザクション管理：** すべての作成/更新/削除メソッドには`@Transactional`が付与されております。これにより、操作の途中で何か失敗した場合、トランザクション全体がロールバックされ、データベースが整合性のある状態に保たれます。」

---

## 物件管理ルール（2分30秒）

> 「まず**PropertyService**から始めさせていただきます。一見すると、単純なCRUD操作に見えます：
>
> *   `getAllProperties()` - すべての物件を取得
> *   `getPropertyById(Long id)` - 1件の物件を取得
> *   `createProperty(Property property)` - 新規物件を挿入
> *   `updateProperty(Property property)` - 既存物件を更新
> *   `deleteProperty(Long id)` - 物件を削除
>
> しかし、ここで興味深い点がございます：**削除は常に許可されるわけではございません**。
>
> ### **削除ガードルール**
> **ビジネスルール：** アクティブな賃貸契約が存在する物件は削除できません。
>
> **理由：** 誰かが実際に賃借している物件を削除することを想像してください。賃貸契約データが孤立し、重要な契約情報が失われます。
>
> **強制方法：**
> `PropertyController.delete()`をご覧ください：
> ```java
> if (leaseService.hasActiveLeases(id)) {
>     redirectAttributes.addFlashAttribute(\"error\", 
>         \"有効な賃貸契約が存在するため、物件を削除できません\");
>     return \"redirect:/properties/\" + id;
> }
> propertyService.deleteProperty(id);
> ```
>
> コントローラーは削除を許可する前に`LeaseService`に確認いたします。アクティブな賃貸契約が存在する場合、操作はブロックされ、ユーザーには分かりやすいエラーメッセージが表示されます。
>
> **重要な洞察：** サービス層は`hasActiveLeases()`メソッドを提供いたしますが、ルールを強制するのはコントローラーでございます。この分離により、関心事がクリーンに保たれます。」

---

## テナント管理ルール（2分）

> 「**TenantService**も同様のパターンに従っております：
>
> *   標準的なCRUD操作：`getAllTenants()`、`getTenantById()`、`createTenant()`、`updateTenant()`、`deleteTenant()`
>
> ### **削除ガードルール**
> **ビジネスルール：** 既存の賃貸契約（アクティブまたは履歴）を持つテナントは削除できません。
>
> **理由：** 賃貸契約は法的契約でございます。契約終了後であっても、誰がいつ何を賃借したかの履歴記録を維持する必要がございます。
>
> **強制方法：**
> `TenantController.delete()`をご覧ください：
> ```java
> if (!leaseService.getLeasesByTenantId(id).isEmpty()) {
>     redirectAttributes.addFlashAttribute(\"error\", 
>         \"賃貸契約が存在するため、入居者を削除できません\");
>     return \"redirect:/tenants\";
> }
> tenantService.deleteTenant(id);
> ```
>
> 再び、コントローラーは削除を許可する前に`LeaseService`に相談し、賃貸契約（アクティブなものだけでなく）の有無を確認いたします。
>
> **パターン認識：** PropertyとTenantの削除は両方とも同じガードパターンに従っております—まず依存関係を確認し、その後実行いたします。」

---

## 賃貸契約管理ルール（3分30秒）

> 「それでは最も複雑な部分、**LeaseService**についてご説明いたします。ここに重要なビジネスロジックが存在いたします。
>
> ### **ゴールデンルール：1物件につき1つのアクティブな賃貸契約**
> **ビジネスルール：** 物件は、任意の時点で「ACTIVE」ステータスの賃貸契約を1つのみ持つことができます。
>
> **理由：** 物件を2人の異なるテナントに同時に賃貸することはできません。これは物件管理における基本的な制約でございます。
>
> ### **強制方法**
> `LeaseServiceImpl.createLease()`をご覧ください：
> ```java
> public void createLease(Lease lease) {
>     if (!validateLease(lease)) {
>         throw new IllegalStateException(
>             \"賃貸契約を作成できません：物件には既にアクティブな賃貸契約が存在します\");
>     }
>     leaseMapper.insert(lease);
> }
> ```
>
> 魔法は`validateLease()`で起こります：
> ```java
> private boolean validateLease(Lease lease) {
>     if (!\"ACTIVE\".equals(lease.getStatus())) {
>         return true;  // 非アクティブな賃貸契約は競合しません
>     }
>     
>     List<Lease> activeLeases = 
>         leaseMapper.findActiveLeasesByPropertyId(lease.getPropertyId());
>     
>     return activeLeases.isEmpty() || 
>            (activeLeases.size() == 1 && 
>             activeLeases.get(0).getId().equals(lease.getId()));
> }
> ```
>
> **何が起こっているか：**
> 1.  賃貸契約のステータスが「ACTIVE」でない場合、バリデーションは通過いたします（複数のENDEDまたはNOTICE賃貸契約を持つことは可能）。
> 2.  「ACTIVE」の場合、同じ物件の他のアクティブな賃貸契約をデータベースに問い合わせます。
> 3.  バリデーションは以下の場合のみ通過いたします：
>     *   アクティブな賃貸契約が存在しない、または
>     *   唯一のアクティブな賃貸契約が現在編集中のもの（更新の場合）。
>
> ### **更新ロジック**
> `updateLease()`はさらに賢明でございます：
> ```java
> if (!existing.getPropertyId().equals(lease.getPropertyId()) ||
>     (\"ACTIVE\".equals(lease.getStatus()) && 
>      !\"ACTIVE\".equals(existing.getStatus()))) {
>     if (!validateLease(lease)) {
>         throw new IllegalStateException(
>             \"賃貸契約を更新できません：物件には既にアクティブな賃貸契約が存在します\");
>     }
> }
> ```
>
> 以下の場合のみ再バリデーションを実行いたします：
> *   物件が変更された場合（賃貸契約を別の物件に移動）、または
> *   ステータスが「ACTIVE」に変更された場合（以前は非アクティブだった賃貸契約を有効化）。
>
> ### **ステータス遷移**
> 典型的なライフサイクル：`ACTIVE` → `NOTICE` → `ENDED`
> *   **ACTIVE：** テナント様が現在居住中。
> *   **NOTICE：** テナント様が退去通知を提出済み。
> *   **ENDED：** 契約完了。
>
> ### **例外処理**
> バリデーションが失敗した場合、`IllegalStateException`をスローいたします。コントローラーはこれをキャッチし、フォーム上のユーザーフレンドリーなエラーメッセージに変換いたします。」

---

## まとめと次のステップ

> 「実装したビジネスルールをまとめさせていただきます：
>
> 1.  **物件削除ガード：** アクティブな賃貸契約が存在する場合は削除不可。
> 2.  **テナント削除ガード：** 賃貸契約が存在する場合は削除不可。
> 3.  **賃貸契約一意性ルール：** 1物件につき1つのACTIVE賃貸契約のみ。
>
> これらのルールは**サービス層**で強制されており、UIを回避しようとしても不可能でございます。
>
> 構造（パート1）と知性（パート2）をカバーいたしました。パート3では、最終層である**セキュリティ**を追加し、システム全体が連携して動作することをライブデモで実演いたします。」

