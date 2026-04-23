package com.digitalevidence;

import java.util.List;
import java.util.Scanner;

import com.digitalevidence.bootstrap.DemoDataSeeder;
import com.digitalevidence.exception.ValidationException;
import com.digitalevidence.model.CaseFile;
import com.digitalevidence.model.EvidenceType;
import com.digitalevidence.repository.EvidenceRepository;
import com.digitalevidence.service.ChainOfCustodyService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EvidenceRepository repository = new EvidenceRepository();
        ChainOfCustodyService service = new ChainOfCustodyService(repository);
        if (repository.isEmpty()) {
            DemoDataSeeder.seed(service);
        }

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createCase(scanner, service);
                    case "2" -> addEvidence(scanner, service);
                    case "3" -> transferCustody(scanner, service);
                    case "4" -> viewReport(scanner, service);
                    case "5" -> listCases(service);
                    case "0" -> {
                        System.out.println("Exiting system.");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (ValidationException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Digital Evidence & Chain of Custody Management System");
        System.out.println("1. Create case");
        System.out.println("2. Add evidence");
        System.out.println("3. Transfer custody");
        System.out.println("4. View case report");
        System.out.println("5. List cases");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private static void createCase(Scanner scanner, ChainOfCustodyService service) {
        System.out.print("Case ID: ");
        String caseId = scanner.nextLine();
        System.out.print("Case Title: ");
        String title = scanner.nextLine();
        System.out.print("Investigator Name: ");
        String investigator = scanner.nextLine();
        service.createCase(caseId, title, investigator);
        System.out.println("Case created successfully.");
    }

    private static void addEvidence(Scanner scanner, ChainOfCustodyService service) {
        System.out.print("Case ID: ");
        String caseId = scanner.nextLine();
        System.out.print("Evidence ID: ");
        String evidenceId = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        EvidenceType type = readEvidenceType(scanner);
        System.out.print("Collected By: ");
        String collectedBy = scanner.nextLine();
        service.addEvidence(caseId, evidenceId, description, type, collectedBy);
        System.out.println("Evidence added successfully.");
    }

    private static EvidenceType readEvidenceType(Scanner scanner) {
        while (true) {
            System.out.print("Evidence Type (IMAGE, VIDEO, DOCUMENT, AUDIO, OTHER): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return EvidenceType.valueOf(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid evidence type. Please enter one of the listed values.");
            }
        }
    }

    private static void transferCustody(Scanner scanner, ChainOfCustodyService service) {
        System.out.print("Evidence ID: ");
        String evidenceId = scanner.nextLine();
        System.out.print("New Custodian: ");
        String newCustodian = scanner.nextLine();
        System.out.print("Reason for transfer: ");
        String reason = scanner.nextLine();
        service.transferCustody(evidenceId, newCustodian, reason);
        System.out.println("Custody transferred successfully.");
    }

    private static void viewReport(Scanner scanner, ChainOfCustodyService service) {
        System.out.print("Case ID: ");
        String caseId = scanner.nextLine();
        System.out.println();
        System.out.println(service.generateCaseReport(caseId));
    }

    private static void listCases(ChainOfCustodyService service) {
        List<CaseFile> cases = service.listCases();
        if (cases.isEmpty()) {
            System.out.println("No cases found.");
            return;
        }
        System.out.println("Cases:");
        for (CaseFile caseFile : cases) {
            System.out.println("- " + caseFile.getCaseId() + " | " + caseFile.getCaseTitle() + " | Investigator: " + caseFile.getInvestigatorName());
        }
    }
}
