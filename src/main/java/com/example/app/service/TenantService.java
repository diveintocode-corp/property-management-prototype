package com.example.app.service;

import com.example.app.model.Tenant;
import java.util.List;

/**
 * 入居者情報の管理に関するビジネスロジックを提供するサービスインターフェース。
 * 入居者の登録、更新、削除、照会などの操作を定義します。
 */
public interface TenantService {
    /**
     * すべての入居者情報を取得します。
     * 
     * @return 登録されているすべての入居者のリスト
     */
    List<Tenant> getAllTenants();

    /**
     * 指定されたIDの入居者情報を取得します。
     * 
     * @param id 取得したい入居者のID
     * @return 該当する入居者情報。存在しない場合はnull
     */
    Tenant getTenantById(Long id);

    /**
     * 新しい入居者を登録します。
     * 
     * @param tenant 登録する入居者の情報
     */
    void createTenant(Tenant tenant);

    /**
     * 既存の入居者情報を更新します。
     * 
     * @param tenant 更新する入居者の情報
     */
    void updateTenant(Tenant tenant);

    /**
     * 指定されたIDの入居者を削除します。
     * 
     * @param id 削除する入居者のID
     */
    void deleteTenant(Long id);
}