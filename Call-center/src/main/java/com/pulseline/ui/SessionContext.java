package com.pulseline.ui;

/**
 * Singleton que guarda la sesión activa del usuario logueado.
 * Accesible desde cualquier panel de la UI.
 */
public class SessionContext {

    private static SessionContext instance;

    private String usuarioId;
    private String nombreUsuario;
    private String rol; // "ADMIN" o "AGENTE"
    private String workspaceId;
    private String workspaceNombre;

    private SessionContext() {}

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public void iniciarSesion(String usuarioId, String nombreUsuario,
                               String rol, String workspaceId, String workspaceNombre) {
        this.usuarioId       = usuarioId;
        this.nombreUsuario   = nombreUsuario;
        this.rol             = rol;
        this.workspaceId     = workspaceId;
        this.workspaceNombre = workspaceNombre;
    }

    public void cerrarSesion() {
        this.usuarioId       = null;
        this.nombreUsuario   = null;
        this.rol             = null;
        this.workspaceId     = null;
        this.workspaceNombre = null;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(rol);
    }

    public boolean isLogueado() {
        return usuarioId != null;
    }

    // Getters
    public String getUsuarioId()       { return usuarioId; }
    public String getNombreUsuario()   { return nombreUsuario; }
    public String getRol()             { return rol; }
    public String getWorkspaceId()     { return workspaceId; }
    public String getWorkspaceNombre() { return workspaceNombre; }
}