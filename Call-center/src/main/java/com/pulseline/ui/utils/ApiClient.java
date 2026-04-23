package com.pulseline.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente HTTP liviano para consumir la API REST de PulseLine.
 * Usa java.net.http.HttpClient (disponible desde Java 11).
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8081/api";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    public static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ── GET ──────────────────────────────────────────────────────────────────

    public static JsonNode get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkError(response);
        return mapper.readTree(response.body());
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    public static JsonNode post(String path, Object body) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkError(response);
        return mapper.readTree(response.body());
    }

    // ── PATCH ────────────────────────────────────────────────────────────────

    public static JsonNode patch(String path, Object body) throws IOException, InterruptedException {
        String json = body != null ? mapper.writeValueAsString(body) : "{}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkError(response);
        return mapper.readTree(response.body());
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public static void delete(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkError(response);
    }

    // ── ERROR HANDLING ───────────────────────────────────────────────────────

    private static void checkError(HttpResponse<String> response) throws IOException {
        if (response.statusCode() >= 400) {
            String mensaje = "Error del servidor";
            try {
                JsonNode node = mapper.readTree(response.body());
                if (node.has("mensaje")) mensaje = node.get("mensaje").asText();
            } catch (Exception ignored) {}
            throw new IOException("HTTP " + response.statusCode() + ": " + mensaje);
        }
    }
}
