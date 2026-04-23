package com.digitalevidence.mvc.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalevidence.mvc.dto.AddEvidenceRequest;
import com.digitalevidence.mvc.dto.CreateCaseRequest;
import com.digitalevidence.mvc.dto.TransferCustodyRequest;
import com.digitalevidence.mvc.model.CaseFileEntity;
import com.digitalevidence.mvc.model.EvidenceEntity;
import com.digitalevidence.mvc.model.EvidenceType;
import com.digitalevidence.mvc.service.EvidenceManagementService;

@Controller
@Profile("!mongo")
public class WebDashboardController {
    private final EvidenceManagementService service;

    public WebDashboardController(EvidenceManagementService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String caseId,
                            @RequestParam(required = false) String searchEvidenceId,
                            Model model) {
        List<CaseFileEntity> cases = service.listCases();
        String selectedCaseId = caseId;
        if (selectedCaseId == null && !cases.isEmpty()) {
            selectedCaseId = cases.get(0).getCaseId();
        }

        String report = selectedCaseId == null ? "No cases available" : service.generateCaseReport(selectedCaseId);

        model.addAttribute("cases", cases);
        model.addAttribute("selectedCaseId", selectedCaseId);
        model.addAttribute("report", report);
        model.addAttribute("evidenceTypes", EvidenceType.values());

        if (searchEvidenceId != null && !searchEvidenceId.isBlank()) {
            try {
                EvidenceEntity evidence = service.findEvidenceById(searchEvidenceId.trim());
                model.addAttribute("searchResult", evidence);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("searchError", ex.getMessage());
            }
        }
        return "dashboard";
    }

    @PostMapping("/cases")
    public String createCase(CreateCaseRequest request, RedirectAttributes redirectAttributes) {
        try {
            service.createCase(request);
            redirectAttributes.addFlashAttribute("message", "Case created successfully");
            return "redirect:/?caseId=" + request.caseId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/evidence")
    public String addEvidence(AddEvidenceRequest request, RedirectAttributes redirectAttributes) {
        try {
            service.addEvidence(request);
            redirectAttributes.addFlashAttribute("message", "Evidence added successfully");
            return "redirect:/?caseId=" + request.caseId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/?caseId=" + request.caseId();
        }
    }

    @PostMapping("/custody")
    public String transferCustody(TransferCustodyRequest request, RedirectAttributes redirectAttributes) {
        try {
            service.transferCustody(request);
            redirectAttributes.addFlashAttribute("message", "Custody transferred successfully");
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/evidence/archive")
    public String archiveEvidence(@RequestParam String evidenceId, RedirectAttributes redirectAttributes) {
        try {
            service.archiveEvidence(evidenceId.trim());
            redirectAttributes.addFlashAttribute("message", "Evidence archived");
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/";
        }
    }
}
