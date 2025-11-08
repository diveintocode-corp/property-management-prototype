package com.example.app.controller;

import com.example.app.model.Tenant;
import com.example.app.service.LeaseService;
import com.example.app.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 入居者情報の管理を担当するコントローラークラス。
 * 入居者の一覧表示、登録、更新、削除などの
 * Webインターフェースを提供します。
 */
@Controller
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {
    
    private final TenantService tenantService;
    private final LeaseService leaseService;
    
    /**
     * 入居者一覧を表示します。
     * 
     * @param model ビューに渡すモデル
     * @return 入居者一覧画面のテンプレート名
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tenants", tenantService.getAllTenants());
        return "tenants/list";
    }
    
    /**
     * 新規入居者登録フォームを表示します。
     * 
     * @param model ビューに渡すモデル
     * @return 入居者登録フォームのテンプレート名
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("tenant", new Tenant());
        return "tenants/form";
    }
    
    /**
     * 入居者編集フォームを表示します。
     * 編集対象の入居者情報と関連する契約情報も合わせて表示します。
     * 
     * @param id 編集する入居者のID
     * @param model ビューに渡すモデル
     * @return 入居者編集フォームのテンプレート名
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Tenant tenant = tenantService.getTenantById(id);
        if (tenant == null) {
            return "redirect:/tenants";
        }
        
        model.addAttribute("tenant", tenant);
        model.addAttribute("leases", leaseService.getLeasesByTenantId(id));
        return "tenants/form";
    }
    
    /**
     * 入居者情報を保存します。
     * 新規登録または更新を行い、結果に応じてメッセージを表示します。
     * 
     * @param tenant 保存する入居者情報
     * @param bindingResult バリデーション結果
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping
    public String save(@Valid @ModelAttribute Tenant tenant,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tenants/form";
        }
        
        try {
            if (tenant.getId() == null) {
                tenantService.createTenant(tenant);
                redirectAttributes.addFlashAttribute("message", "Tenant created successfully");
            } else {
                tenantService.updateTenant(tenant);
                redirectAttributes.addFlashAttribute("message", "Tenant updated successfully");
            }
        } catch (Exception e) {
            bindingResult.reject("error", e.getMessage());
            return "tenants/form";
        }
        
        return "redirect:/tenants";
    }
    
    /**
     * 入居者を削除します。
     * 関連する契約が存在する場合は削除を中止します。
     * 
     * @param id 削除する入居者のID
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!leaseService.getLeasesByTenantId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", 
                "Cannot delete tenant: They have associated leases");
            return "redirect:/tenants";
        }
        
        tenantService.deleteTenant(id);
        redirectAttributes.addFlashAttribute("message", "Tenant deleted successfully");
        return "redirect:/tenants";
    }
}