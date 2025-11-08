package com.example.app.controller;

import com.example.app.model.Lease;
import com.example.app.service.LeaseService;
import com.example.app.service.PropertyService;
import com.example.app.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 賃貸契約の管理を担当するコントローラークラス。
 * 契約の登録、更新、削除などのWebインターフェースを提供します。
 * 物件と入居者の関連付けも行います。
 */
@Controller
@RequestMapping("/leases")
@RequiredArgsConstructor
public class LeaseController {
    
    private final LeaseService leaseService;
    private final PropertyService propertyService;
    private final TenantService tenantService;
    
    /**
     * 新規賃貸契約登録フォームを表示します。
     * 物件IDが指定された場合は、その物件情報を事前に設定します。
     * 
     * @param propertyId 物件ID（オプション）
     * @param model ビューに渡すモデル
     * @return 契約登録フォームのテンプレート名
     */
    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long propertyId, Model model) {
        Lease lease = new Lease();
        if (propertyId != null) {
            lease.setPropertyId(propertyId);
            lease.setProperty(propertyService.getPropertyById(propertyId));
        }
        
        setupFormModel(model, lease);
        return "leases/form";
    }
    
    /**
     * 賃貸契約編集フォームを表示します。
     * 
     * @param id 編集する契約のID
     * @param model ビューに渡すモデル
     * @return 契約編集フォームのテンプレート名
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Lease lease = leaseService.getLeaseById(id);
        if (lease == null) {
            return "redirect:/properties";
        }
        
        setupFormModel(model, lease);
        return "leases/form";
    }
    
    /**
     * 賃貸契約情報を保存します。
     * 新規登録または更新を行い、結果に応じてメッセージを表示します。
     * 
     * @param lease 保存する契約情報
     * @param bindingResult バリデーション結果
     * @param redirectAttributes リダイレクト時に使用する属性
     * @param model ビューに渡すモデル
     * @return リダイレクト先のURL
     */
    @PostMapping
    public String save(@Valid @ModelAttribute Lease lease,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, lease);
            return "leases/form";
        }
        
        try {
            if (lease.getId() == null) {
                leaseService.createLease(lease);
                redirectAttributes.addFlashAttribute("message", "Lease created successfully");
            } else {
                leaseService.updateLease(lease);
                redirectAttributes.addFlashAttribute("message", "Lease updated successfully");
            }
        } catch (Exception e) {
            bindingResult.reject("error", e.getMessage());
            setupFormModel(model, lease);
            return "leases/form";
        }
        
        return "redirect:/properties/" + lease.getPropertyId();
    }
    
    /**
     * 賃貸契約を削除します。
     * 
     * @param id 削除する契約のID
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Lease lease = leaseService.getLeaseById(id);
        if (lease == null) {
            return "redirect:/properties";
        }
        
        Long propertyId = lease.getPropertyId();
        leaseService.deleteLease(id);
        redirectAttributes.addFlashAttribute("message", "Lease deleted successfully");
        return "redirect:/properties/" + propertyId;
    }
    
    /**
     * 契約フォームのモデルを設定します。
     * 物件リスト、入居者リスト、契約ステータスの選択肢を設定します。
     * 
     * @param model ビューに渡すモデル
     * @param lease 設定対象の契約情報
     */
    private void setupFormModel(Model model, Lease lease) {
        model.addAttribute("lease", lease);
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("tenants", tenantService.getAllTenants());
        model.addAttribute("statusOptions", new String[]{"ACTIVE", "NOTICE", "ENDED"});
    }
}