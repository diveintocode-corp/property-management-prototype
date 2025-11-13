package com.example.app.service;

import com.example.app.model.User;

/**
 * ユーザー情報の管理に関するビジネスロジックを提供するサービスインターフェース。
 * ユーザーの登録、認証などの操作を定義します。
 */
public interface UserService {
    /**
     * 新しいユーザーを登録します。
     * 
     * @param user 登録するユーザーの情報
     * @throws IllegalStateException ユーザー名が既に存在する場合
     */
    void register(User user);

    /**
     * 指定されたユーザー名のユーザー情報を取得します。
     * 
     * @param username ユーザー名
     * @return 該当するユーザー情報。存在しない場合はnull
     */
    User findByUsername(String username);
}

