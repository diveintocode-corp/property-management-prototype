package com.example.app.service;

import com.example.app.model.Lease;
import java.util.List;

/**
 * 賃貸契約の管理に関するビジネスロジックを提供するサービスインターフェース。
 * 契約の作成、更新、削除、照会などの操作を定義します。
 */
public interface LeaseService {
    /**
     * すべての賃貸契約情報を取得します。
     * 
     * @return 登録されているすべての契約のリスト
     */
    List<Lease> getAllLeases();

    /**
     * 指定されたIDの賃貸契約情報を取得します。
     * 
     * @param id 取得したい契約のID
     * @return 該当する契約情報。存在しない場合はnull
     */
    Lease getLeaseById(Long id);

    /**
     * 指定された物件IDに関連するすべての賃貸契約を取得します。
     * 
     * @param propertyId 物件ID
     * @return 該当する物件のすべての契約リスト
     */
    List<Lease> getLeasesByPropertyId(Long propertyId);

    /**
     * 指定された入居者IDに関連するすべての賃貸契約を取得します。
     * 
     * @param tenantId 入居者ID
     * @return 該当する入居者のすべての契約リスト
     */
    List<Lease> getLeasesByTenantId(Long tenantId);

    /**
     * 新しい賃貸契約を登録します。
     * 
     * @param lease 登録する契約の情報
     * @throws IllegalStateException 物件が既に有効な契約を持っている場合
     */
    void createLease(Lease lease);

    /**
     * 既存の賃貸契約情報を更新します。
     * 
     * @param lease 更新する契約の情報
     * @throws IllegalStateException 物件が既に有効な契約を持っている場合
     * @throws IllegalArgumentException 契約が存在しない場合
     */
    void updateLease(Lease lease);

    /**
     * 指定されたIDの賃貸契約を削除します。
     * 
     * @param id 削除する契約のID
     */
    void deleteLease(Long id);

    /**
     * 指定された物件が有効な契約を持っているかを確認します。
     * 
     * @param propertyId 確認する物件のID
     * @return 有効な契約が存在する場合はtrue、それ以外はfalse
     */
    boolean hasActiveLeases(Long propertyId);
}