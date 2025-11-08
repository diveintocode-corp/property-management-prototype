package com.example.app.service;

import com.example.app.model.Property;
import java.util.List;

/**
 * 物件情報の管理に関するビジネスロジックを提供するサービスインターフェース。
 * 物件の登録、更新、削除、照会などの操作を定義します。
 */
public interface PropertyService {
    /**
     * すべての物件情報を取得します。
     * 
     * @return 登録されているすべての物件のリスト
     */
    List<Property> getAllProperties();

    /**
     * 指定されたIDの物件情報を取得します。
     * 
     * @param id 取得したい物件のID
     * @return 該当する物件情報。存在しない場合はnull
     */
    Property getPropertyById(Long id);

    /**
     * 新しい物件を登録します。
     * 
     * @param property 登録する物件の情報
     */
    void createProperty(Property property);

    /**
     * 既存の物件情報を更新します。
     * 
     * @param property 更新する物件の情報
     */
    void updateProperty(Property property);

    /**
     * 指定されたIDの物件を削除します。
     * 
     * @param id 削除する物件のID
     */
    void deleteProperty(Long id);
}