package com.example.app.mapper;

import com.example.app.model.Tenant;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 入居者情報のデータベースアクセスを担当するマッパーインターフェース。
 * MyBatisを使用してデータベースとの連携を行います。
 */
@Mapper
public interface TenantMapper {
    /**
     * すべての入居者情報をデータベースから取得します。
     * 
     * @return 入居者情報のリスト
     */
    List<Tenant> findAll();

    /**
     * 指定されたIDの入居者情報をデータベースから取得します。
     * 
     * @param id 入居者ID
     * @return 該当する入居者情報。存在しない場合はnull
     */
    Tenant findById(Long id);

    /**
     * 新しい入居者情報をデータベースに登録します。
     * 
     * @param tenant 登録する入居者情報
     */
    void insert(Tenant tenant);

    /**
     * 既存の入居者情報をデータベースで更新します。
     * 
     * @param tenant 更新する入居者情報
     */
    void update(Tenant tenant);

    /**
     * 指定されたIDの入居者情報をデータベースから削除します。
     * 
     * @param id 削除する入居者ID
     */
    void delete(Long id);
}