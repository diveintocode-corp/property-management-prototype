package com.example.app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ユーザー情報を表すモデルクラス。
 * 認証に必要なユーザー情報を管理します。
 */
@Data
public class User {
    /** ユーザーID */
    private Long id;
    
    /** 
     * ユーザー名
     * 必須項目として設定されています
     */
    @NotBlank(message = "ユーザー名を入力してください")
    private String username;
    
    /** 
     * パスワード
     * 必須項目として設定されています
     */
    @NotBlank(message = "パスワードを入力してください")
    private String password;
    
    /** 
     * メールアドレス
     * 任意項目として設定されています
     */
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

