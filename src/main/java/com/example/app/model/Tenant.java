package com.example.app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 入居者情報を表すモデルクラス。
 * 入居者の個人情報と連絡先情報を管理します。
 */
@Data
public class Tenant {
    /** 入居者ID */
    private Long id;
    
    /** 
     * 入居者の氏名
     * 必須項目として設定されています
     */
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    /** 
     * 電話番号
     * 任意項目として設定されています
     */
    private String phone;
    
    /** 
     * メールアドレス
     * 有効なメールアドレス形式である必要があります
     */
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}