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

@Controller
@RequestMapping("/leases")
@RequiredArgsConstructor
public class LeaseController {
    
    private final LeaseService leaseService;
    private final PropertyService propertyService;
    private final TenantService tenantService;
    
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
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Lease lease = leaseService.getLeaseById(id);
        if (lease == null) {
            return "redirect:/properties";
        }
        
        setupFormModel(model, lease);
        return "leases/form";
    }
    
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
    
    private void setupFormModel(Model model, Lease lease) {
        model.addAttribute("lease", lease);
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("tenants", tenantService.getAllTenants());
        model.addAttribute("statusOptions", new String[]{"ACTIVE", "NOTICE", "ENDED"});
    }
}