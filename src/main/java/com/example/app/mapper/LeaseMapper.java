package com.example.app.mapper;

import com.example.app.model.Lease;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 賃貸契約情報のデータベースアクセスを担当するマッパーインターフェース。
 * MyBatisを使用してデータベースとの連携を行います。
 * 契約情報の取得時には、関連する物件情報と入居者情報も合わせて取得します。
 */
@Mapper
public interface LeaseMapper {
    /**
     * 指定されたIDの賃貸契約情報をデータベースから取得します。
     * 関連する物件情報と入居者情報も合わせて取得します。
     * 
     * @param id 契約ID
     * @return 該当する契約情報。存在しない場合はnull
     */
    Lease findById(Long id);

    /**
     * 指定された物件IDに関連するすべての賃貸契約を取得します。
     * 関連する物件情報と入居者情報も合わせて取得します。
     * 
     * @param propertyId 物件ID
     * @return 該当する物件の契約リスト
     */
    List<Lease> findByPropertyId(Long propertyId);

    /**
     * 指定された入居者IDに関連するすべての賃貸契約を取得します。
     * 関連する物件情報と入居者情報も合わせて取得します。
     * 
     * @param tenantId 入居者ID
     * @return 該当する入居者の契約リスト
     */
    List<Lease> findByTenantId(Long tenantId);

    /**
     * 新しい賃貸契約情報をデータベースに登録します。
     * 
     * @param lease 登録する契約情報
     */
    void insert(Lease lease);

    /**
     * 既存の賃貸契約情報をデータベースで更新します。
     * 
     * @param lease 更新する契約情報
     */
    void update(Lease lease);

    /**
     * 指定されたIDの賃貸契約情報をデータベースから削除します。
     * 
     * @param id 削除する契約ID
     */
    void delete(Long id);

    /**
     * 指定された物件IDの有効な（アクティブな）賃貸契約を取得します。
     * 契約の重複チェックなどに使用されます。
     * 
     * @param propertyId 物件ID
     * @return アクティブな契約のリスト
     */
    List<Lease> findActiveLeasesByPropertyId(Long propertyId);
}