package com.example.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 不動産物件を表すモデルクラス。
 * 物件の基本情報（名称、住所、面積など）を管理します。
 */
@Data
public class Property {
    /** 物件ID */
    private Long id;
    
    /** 
     * 物件名
     * 必須項目として設定されています
     */
    @NotBlank(message = "Name is required")
    private String name;
    
    /** 
     * 物件の所在地住所
     * 必須項目として設定されています
     */
    @NotBlank(message = "Address is required")
    private String address;
    
    /** 
     * 物件の面積
     * 必須項目として設定されています
     */
    @NotBlank(message = "Area is required")
    private String area;
    
    /** 
     * 物件の間取り
     * 任意項目として設定されています
     */
    private String plan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}