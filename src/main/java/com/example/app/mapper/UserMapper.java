package com.example.app.mapper;

import com.example.app.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ユーザー情報のデータベースアクセスを担当するマッパーインターフェース。
 * MyBatisを使用してデータベースとの連携を行います。
 */
@Mapper
public interface UserMapper {
    /**
     * 指定されたユーザー名のユーザー情報をデータベースから取得します。
     * 
     * @param username ユーザー名
     * @return 該当するユーザー情報。存在しない場合はnull
     */
    User findByUsername(String username);

    /**
     * 新しいユーザー情報をデータベースに登録します。
     * 
     * @param user 登録するユーザー情報
     */
    void insert(User user);

    /**
     * 指定されたユーザー名が既に存在するかを確認します。
     * 
     * @param username 確認するユーザー名
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    boolean existsByUsername(String username);
}

