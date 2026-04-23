package com.pulseline.infrastructure.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.sql.*;
import java.util.UUID;

/**
 * Maneja la base de datos SQLite externa para autenticación.
 * Independiente de H2 — persiste entre reinicios.
 */
@Component
public class AuthDbManager {

    @Value("${app.sqlite.path}")
    private String dbPath;

    private Connection connection;

    @PostConstruct
    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            crearTablas();
            System.out.println("SQLite Auth DB iniciada en: " + dbPath);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo iniciar SQLite: " + e.getMessage(), e);
        }
    }

    private void crearTablas() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS workspaces (
                id           TEXT PRIMARY KEY,
                nombre       TEXT NOT NULL,
                codigo       TEXT NOT NULL UNIQUE,
                fecha_creado TEXT NOT NULL
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id           TEXT PRIMARY KEY,
                username     TEXT NOT NULL,
                password     TEXT NOT NULL,
                rol          TEXT NOT NULL,
                workspace_id TEXT NOT NULL,
                FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
                UNIQUE(username, workspace_id)
            )
        """);

        stmt.close();
    }

    // ── WORKSPACES ────────────────────────────────────────────────────────────

    public String crearWorkspace(String nombre, String adminUsername, String adminPassword) throws SQLException {
        String workspaceId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String codigo      = generarCodigo();

        PreparedStatement wsStmt = connection.prepareStatement(
            "INSERT INTO workspaces (id, nombre, codigo, fecha_creado) VALUES (?, ?, ?, datetime('now'))"
        );
        wsStmt.setString(1, workspaceId);
        wsStmt.setString(2, nombre);
        wsStmt.setString(3, codigo);
        wsStmt.executeUpdate();
        wsStmt.close();

        // Crear el usuario ADMIN automáticamente
        crearUsuario(adminUsername, adminPassword, "ADMIN", workspaceId);

        return codigo; // Devuelve el código para que el admin lo comparta
    }

    public WorkspaceInfo buscarPorCodigo(String codigo) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT id, nombre, codigo FROM workspaces WHERE codigo = ?"
        );
        stmt.setString(1, codigo.toUpperCase().trim());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            WorkspaceInfo info = new WorkspaceInfo(rs.getString("id"), rs.getString("nombre"), rs.getString("codigo"));
            rs.close(); stmt.close();
            return info;
        }
        rs.close(); stmt.close();
        return null;
    }

    // ── USUARIOS ──────────────────────────────────────────────────────────────

    public void crearUsuario(String username, String password, String rol, String workspaceId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO usuarios (id, username, password, rol, workspace_id) VALUES (?, ?, ?, ?, ?)"
        );
        stmt.setString(1, UUID.randomUUID().toString().substring(0, 8));
        stmt.setString(2, username.trim());
        stmt.setString(3, hashPassword(password)); // hash simple
        stmt.setString(4, rol);
        stmt.setString(5, workspaceId);
        stmt.executeUpdate();
        stmt.close();
    }

    public UsuarioInfo login(String username, String password, String workspaceCodigo) throws SQLException {
        WorkspaceInfo ws = buscarPorCodigo(workspaceCodigo);
        if (ws == null) return null;

        PreparedStatement stmt = connection.prepareStatement(
            "SELECT id, username, rol FROM usuarios WHERE username = ? AND password = ? AND workspace_id = ?"
        );
        stmt.setString(1, username.trim());
        stmt.setString(2, hashPassword(password));
        stmt.setString(3, ws.id());
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            UsuarioInfo u = new UsuarioInfo(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("rol"),
                ws.id(),
                ws.nombre()
            );
            rs.close(); stmt.close();
            return u;
        }
        rs.close(); stmt.close();
        return null;
    }

    public boolean existeUsernameEnWorkspace(String username, String workspaceId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT COUNT(*) FROM usuarios WHERE username = ? AND workspace_id = ?"
        );
        stmt.setString(1, username.trim());
        stmt.setString(2, workspaceId);
        ResultSet rs = stmt.executeQuery();
        boolean existe = rs.next() && rs.getInt(1) > 0;
        rs.close(); stmt.close();
        return existe;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private String generarCodigo() {
        // Genera código tipo: PLS-A3F2-9K1B
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 8);
        return "PLS-" + uuid.substring(0, 4) + "-" + uuid.substring(4, 8);
    }

    private String hashPassword(String password) {
        // Hash SHA-256 simple
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    // ── RECORDS (DTOs internos) ───────────────────────────────────────────────

    public record WorkspaceInfo(String id, String nombre, String codigo) {}
    public record UsuarioInfo(String id, String username, String rol, String workspaceId, String workspaceNombre) {}
}
