package com.pulseline.infrastructure.web.controllers;

import com.pulseline.infrastructure.auth.AuthDbManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthDbManager authDbManager;

    public AuthController(AuthDbManager authDbManager) {
        this.authDbManager = authDbManager;
    }

    /** POST /api/auth/registro — Crear nuevo workspace + admin */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> body) {
        try {
            String nombre   = body.get("nombreWorkspace");
            String username = body.get("username");
            String password = body.get("password");

            if (nombre == null || username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son requeridos."));
            }
            if (password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 6 caracteres."));
            }

            String codigo = authDbManager.crearWorkspace(nombre.trim(), username.trim(), password);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Workspace creado exitosamente.",
                "codigo",  codigo
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/auth/login — Iniciar sesión */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String codigo   = body.get("codigoWorkspace");

            AuthDbManager.UsuarioInfo usuario = authDbManager.login(username, password, codigo);
            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas o workspace inválido."));
            }

            return ResponseEntity.ok(Map.of(
                "usuarioId",        usuario.id(),
                "username",         usuario.username(),
                "rol",              usuario.rol(),
                "workspaceId",      usuario.workspaceId(),
                "workspaceNombre",  usuario.workspaceNombre()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/auth/agregar-agente — Admin agrega agente a su workspace */
    @PostMapping("/agregar-agente")
    public ResponseEntity<?> agregarAgente(@RequestBody Map<String, String> body) {
        try {
            String username    = body.get("username");
            String password    = body.get("password");
            String workspaceId = body.get("workspaceId");

            if (authDbManager.existeUsernameEnWorkspace(username, workspaceId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un usuario con ese nombre en este workspace."));
            }

            authDbManager.crearUsuario(username.trim(), password, "AGENTE", workspaceId);
            return ResponseEntity.ok(Map.of("mensaje", "Agente registrado exitosamente."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}