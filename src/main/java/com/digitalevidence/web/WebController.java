package com.digitalevidence.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.digitalevidence.exception.ValidationException;
import com.digitalevidence.model.CaseFile;
import com.digitalevidence.model.DigitalEvidence;
import com.digitalevidence.model.EvidenceType;
import com.digitalevidence.service.ChainOfCustodyService;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebController implements HttpHandler {
    private final ChainOfCustodyService service;

    public WebController(ChainOfCustodyService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equalsIgnoreCase(method)) {
                renderDashboard(exchange, null, getQueryParam(exchange.getRequestURI(), "caseId"));
                return;
            }

            if (!"POST".equalsIgnoreCase(method)) {
                sendText(exchange, 405, "Method Not Allowed");
                return;
            }

            Map<String, String> form = parseForm(exchange);
            if (path.endsWith("/cases/create")) {
                service.createCase(form.get("caseId"), form.get("title"), form.get("investigator"));
                renderDashboard(exchange, "Case created successfully.", form.get("caseId"));
                return;
            }

            if (path.endsWith("/evidence/add")) {
                EvidenceType type = EvidenceType.valueOf(form.get("type").toUpperCase());
                service.addEvidence(form.get("caseId"), form.get("evidenceId"), form.get("description"), type, form.get("collectedBy"));
                renderDashboard(exchange, "Evidence added successfully.", form.get("caseId"));
                return;
            }

            if (path.endsWith("/custody/transfer")) {
                DigitalEvidence evidence = service.transferCustody(form.get("evidenceId"), form.get("newCustodian"), form.get("reason"));
                renderDashboard(exchange, "Custody transferred successfully.", evidence.getCaseId());
                return;
            }

            sendText(exchange, 404, "Not Found");
        } catch (ValidationException | IllegalArgumentException ex) {
            renderDashboard(exchange, ex.getMessage(), getQueryParam(exchange.getRequestURI(), "caseId"));
        }
    }

    private void renderDashboard(HttpExchange exchange, String message, String selectedCaseId) throws IOException {
        List<CaseFile> cases = service.listCases();
        String activeCaseId = selectedCaseId;
        if (activeCaseId == null && !cases.isEmpty()) {
            activeCaseId = cases.get(0).getCaseId();
        }

        String report = activeCaseId == null ? "No case available." : service.generateCaseReport(activeCaseId);
        String html = WebPages.dashboardPage(cases, report, activeCaseId, message);
        sendHtml(exchange, 200, html);
    }

    private Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return parseQueryString(body);
    }

    private Map<String, String> parseQueryString(String input) {
        Map<String, String> values = new LinkedHashMap<>();
        if (input == null || input.isBlank()) {
            return values;
        }
        String[] pairs = input.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            values.put(key, value);
        }
        return values;
    }

    private String getQueryParam(URI uri, String key) {
        Map<String, String> params = parseQueryString(uri.getRawQuery());
        return params.get(key);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }

    private void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}
