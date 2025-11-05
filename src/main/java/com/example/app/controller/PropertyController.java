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

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {
    
    private final PropertyService propertyService;
    private final LeaseService leaseService;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "properties/list";
    }
    
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
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("property", new Property());
        return "properties/form";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties";
        }
        
        model.addAttribute("property", property);
        return "properties/form";
    }
    
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
                redirectAttributes.addFlashAttribute("message", "Property created successfully");
            } else {
                propertyService.updateProperty(property);
                redirectAttributes.addFlashAttribute("message", "Property updated successfully");
            }
        } catch (Exception e) {
            bindingResult.reject("error", e.getMessage());
            return "properties/form";
        }
        
        return "redirect:/properties";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (leaseService.hasActiveLeases(id)) {
            redirectAttributes.addFlashAttribute("error", 
                "Cannot delete property: It has active leases");
            return "redirect:/properties/" + id;
        }
        
        propertyService.deleteProperty(id);
        redirectAttributes.addFlashAttribute("message", "Property deleted successfully");
        return "redirect:/properties";
    }
}