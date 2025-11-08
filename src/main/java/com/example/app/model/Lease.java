package com.example.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 賃貸契約を表すモデルクラス。
 * 物件と入居者の契約関係、契約条件、契約期間などの情報を管理します。
 */
@Data
public class Lease {
    /** 賃貸契約ID */
    private Long id;
    
    /** 
     * 物件ID
     * 必須項目として設定されています
     */
    @NotNull(message = "Property is required")
    private Long propertyId;
    
    /** 
     * 入居者ID
     * 必須項目として設定されています
     */
    @NotNull(message = "Tenant is required")
    private Long tenantId;
    
    /** 
     * 月額賃料
     * 必須項目として設定され、正の値である必要があります
     */
    @NotNull(message = "Rent amount is required")
    @Positive(message = "Rent must be greater than 0")
    private Integer rent;
    
    /** 
     * 契約開始日
     * 必須項目として設定されています
     */
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    /** 
     * 契約終了日
     * 任意項目として設定されています
     */
    private LocalDate endDate;
    
    /** 
     * 契約状態
     * 必須項目として設定されています
     * 値: ACTIVE（有効）、NOTICE（解約予告）、ENDED（終了）
     */
    @NotNull(message = "Status is required")
    private String status;
    
    /** 
     * 敷金
     * 正の値である必要があります
     */
    @Positive(message = "Deposit must be greater than 0")
    private Integer deposit;
    
    /** レコード作成日時 */
    private LocalDateTime createdAt;
    /** レコード更新日時 */
    private LocalDateTime updatedAt;
    
    // ナビゲーションプロパティ
    /** 関連する物件情報 */
    private Property property;
    /** 関連する入居者情報 */
    private Tenant tenant;
}