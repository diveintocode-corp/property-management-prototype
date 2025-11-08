package com.example.app.mapper;

import com.example.app.model.Property;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 物件情報のデータベースアクセスを担当するマッパーインターフェース。
 * MyBatisを使用してデータベースとの連携を行います。
 */
@Mapper
public interface PropertyMapper {
    /**
     * すべての物件情報をデータベースから取得します。
     * 
     * @return 物件情報のリスト
     */
    List<Property> findAll();

    /**
     * 指定されたIDの物件情報をデータベースから取得します。
     * 
     * @param id 物件ID
     * @return 該当する物件情報。存在しない場合はnull
     */
    Property findById(Long id);

    /**
     * 新しい物件情報をデータベースに登録します。
     * 
     * @param property 登録する物件情報
     */
    void insert(Property property);

    /**
     * 既存の物件情報をデータベースで更新します。
     * 
     * @param property 更新する物件情報
     */
    void update(Property property);

    /**
     * 指定されたIDの物件情報をデータベースから削除します。
     * 
     * @param id 削除する物件ID
     */
    void delete(Long id);
}