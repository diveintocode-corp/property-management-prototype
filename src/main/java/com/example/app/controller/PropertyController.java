package com.example.app.controller;

import com.example.app.model.Property;
import com.example.app.service.LeaseService;
import com.example.app.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 物件情報の管理を担当するコントローラークラス。
 * 物件の一覧表示、詳細表示、登録、更新、削除などの
 * Webインターフェースを提供します。
 */
@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {
    
    private final PropertyService propertyService;
    private final LeaseService leaseService;
    
    /**
     * 物件一覧を表示します。
     * 
     * @param model ビューに渡すモデル
     * @return 物件一覧画面のテンプレート名
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "properties/list";
    }
    
    /**
     * 物件の詳細情報を表示します。
     * 
     * @param id 表示する物件のID
     * @param model ビューに渡すモデル
     * @return 物件詳細画面のテンプレート名
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties";
        }
        
        model.addAttribute("property", property);
        model.addAttribute("leases", leaseService.getLeasesByPropertyId(id));
        return "properties/detail";
    }
    
    /**
     * 新規物件登録フォームを表示します。
     * 
     * @param model ビューに渡すモデル
     * @return 物件登録フォームのテンプレート名
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("property", new Property());
        return "properties/form";
    }
    
    /**
     * 物件編集フォームを表示します。
     * 
     * @param id 編集する物件のID
     * @param model ビューに渡すモデル
     * @return 物件編集フォームのテンプレート名
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties";
        }
        
        model.addAttribute("property", property);
        return "properties/form";
    }
    
    /**
     * 物件情報を保存します。
     * 新規登録または更新を行い、結果に応じてメッセージを表示します。
     * 
     * @param property 保存する物件情報
     * @param bindingResult バリデーション結果
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping
    public String save(@Valid @ModelAttribute Property property,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "properties/form";
        }
        
        try {
            if (property.getId() == null) {
                propertyService.createProperty(property);
                redirectAttributes.addFlashAttribute("message", "物件を登録しました");
            } else {
                propertyService.updateProperty(property);
                redirectAttributes.addFlashAttribute("message", "物件情報を更新しました");
            }
        } catch (Exception e) {
            bindingResult.reject("error", e.getMessage());
            return "properties/form";
        }
        
        return "redirect:/properties";
    }
    
    /**
     * 物件を削除します。
     * 有効な契約が存在する場合は削除を中止します。
     * 
     * @param id 削除する物件のID
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (leaseService.hasActiveLeases(id)) {
            redirectAttributes.addFlashAttribute("error", 
                "有効な賃貸契約が存在するため、物件を削除できません");
            return "redirect:/properties/" + id;
        }
        
        propertyService.deleteProperty(id);
        redirectAttributes.addFlashAttribute("message", "物件を削除しました");
        return "redirect:/properties";
    }
}