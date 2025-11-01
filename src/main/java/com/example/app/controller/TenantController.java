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

@Controller
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {
    
    private final TenantService tenantService;
    private final LeaseService leaseService;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tenants", tenantService.getAllTenants());
        return "tenants/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("tenant", new Tenant());
        return "tenants/form";
    }
    
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