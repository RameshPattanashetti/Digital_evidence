package com.digitalevidence.web;

import java.util.List;

import com.digitalevidence.model.CaseFile;

public final class WebPages {
    private WebPages() {
    }

    public static String dashboardPage(List<CaseFile> cases, String report, String selectedCaseId, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append(head());
        html.append("<body>");
        html.append("<div class='backdrop'></div>");
        html.append("<main class='shell'>");
        html.append(header());
        if (message != null && !message.isBlank()) {
            html.append("<section class='message'>").append(escape(message)).append("</section>");
        }
        html.append(metrics(cases));
        html.append("<section class='workspace'>");
        html.append(forms());
        html.append(reportPanel(report, selectedCaseId));
        html.append("</section>");
        html.append(casesPanel(cases));
        html.append("</main>");
        html.append("<script src='/static/app.js'></script>");
        html.append("</body></html>");
        return html.toString();
    }

    private static String head() {
        return "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Digital Evidence Manager</title>"
                + "<link rel='stylesheet' href='/static/styles.css'>"
                + "</head>";
    }

    private static String header() {
        return "<header class='hero'>"
                + "<div>"
                + "<p class='eyebrow'>OOAD Mini Project</p>"
                + "<h1>Digital Evidence & Chain-of-Custody Management System</h1>"
                + "<p class='lead'>Track evidence, preserve custody history, and generate audit-ready case reports in one Java web app.</p>"
                + "</div>"
                + "<div class='hero-badge'>Java Backend + Browser Frontend</div>"
                + "</header>";
    }

    private static String metrics(List<CaseFile> cases) {
        int evidenceCount = cases.stream().mapToInt(caseFile -> caseFile.getEvidenceIds().size()).sum();
        return "<section class='metrics'>"
                + metric("Cases", String.valueOf(cases.size()))
                + metric("Evidence Items", String.valueOf(evidenceCount))
            + metric("Mode", "File persistence")
                + "</section>";
    }

    private static String metric(String label, String value) {
        return "<article class='metric-card'><span>" + escape(label) + "</span><strong>" + escape(value) + "</strong></article>";
    }

    private static String forms() {
        return "<section class='panel forms-panel'>"
                + "<h2>Actions</h2>"
                + formCreateCase()
                + formAddEvidence()
                + formTransferCustody()
                + "</section>";
    }

    private static String formCreateCase() {
        return formShell("Create Case", "/cases/create", "grid-2", "Create Case",
                input("caseId", "Case ID", "CASE-002")
                + input("title", "Case Title", "Phishing Investigation")
                + input("investigator", "Investigator", "Inspector Khan"));
    }

    private static String formAddEvidence() {
        return formShell("Add Evidence", "/evidence/add", "grid-2", "Add Evidence",
                input("caseId", "Case ID", "CASE-001")
                + input("evidenceId", "Evidence ID", "EV-003")
                + input("description", "Description", "USB drive image")
                + select("type", "Type", new String[] {"IMAGE", "VIDEO", "DOCUMENT", "AUDIO", "OTHER"})
                + input("collectedBy", "Collected By", "Inspector Khan"));
    }

    private static String formTransferCustody() {
        return formShell("Transfer Custody", "/custody/transfer", "grid-2", "Transfer Custody",
                input("evidenceId", "Evidence ID", "EV-002")
                + input("newCustodian", "New Custodian", "Forensic Lab")
                + input("reason", "Reason", "Analysis and verification"));
    }

    private static String formShell(String title, String action, String layoutClass, String buttonLabel, String fields) {
        return "<form class='action-card' method='post' action='" + action + "'>"
                + "<h3>" + escape(title) + "</h3>"
                + "<div class='" + layoutClass + "'>" + fields + "</div>"
                + "<button type='submit'>" + escape(buttonLabel) + "</button>"
                + "</form>";
    }

    private static String input(String name, String label, String placeholder) {
        return "<label><span>" + escape(label) + "</span><input name='" + name + "' placeholder='" + escape(placeholder) + "' required></label>";
    }

    private static String select(String name, String label, String[] options) {
        StringBuilder html = new StringBuilder();
        html.append("<label><span>").append(escape(label)).append("</span><select name='").append(name).append("'>");
        for (String option : options) {
            html.append("<option value='").append(option).append("'>").append(option).append("</option>");
        }
        html.append("</select></label>");
        return html.toString();
    }

    private static String reportPanel(String report, String selectedCaseId) {
        return "<section class='panel report-panel'>"
                + "<div class='panel-header'><h2>Case Report</h2><span>Selected case: " + escape(selectedCaseId == null ? "None" : selectedCaseId) + "</span></div>"
                + "<pre class='report-box'>" + escape(report) + "</pre>"
                + "</section>";
    }

    private static String casesPanel(List<CaseFile> cases) {
        StringBuilder html = new StringBuilder();
        html.append("<section class='panel cases-panel'>");
        html.append("<div class='panel-header'><h2>Stored Cases</h2><span>Click a case to inspect</span></div>");
        html.append("<div class='case-grid'>");
        for (CaseFile caseFile : cases) {
            html.append("<a class='case-card' href='/?caseId=").append(escape(caseFile.getCaseId())).append("'>")
                .append("<strong>").append(escape(caseFile.getCaseId())).append("</strong>")
                .append("<p>").append(escape(caseFile.getCaseTitle())).append("</p>")
                .append("<span>").append(escape(caseFile.getInvestigatorName())).append("</span>")
                .append("<span>").append(caseFile.getEvidenceIds().size()).append(" evidence item(s)</span>")
                .append("</a>");
        }
        html.append("</div></section>");
        return html.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
