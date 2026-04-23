package com.digitalevidence.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.digitalevidence.bootstrap.DemoDataSeeder;
import com.digitalevidence.repository.EvidenceRepository;
import com.digitalevidence.service.ChainOfCustodyService;

import com.sun.net.httpserver.HttpServer;

public class WebApplication {
    public static void main(String[] args) throws IOException {
        EvidenceRepository repository = new EvidenceRepository();
        ChainOfCustodyService service = new ChainOfCustodyService(repository);
        if (repository.isEmpty()) {
            DemoDataSeeder.seed(service);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new WebController(service));
        server.createContext("/static", new StaticAssetController());
        server.setExecutor(null);
        server.start();

        System.out.println("Digital Evidence web app started.");
        System.out.println("Open http://localhost:8080 in your browser.");
    }
}
