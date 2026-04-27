package com.bibliotech.model;

public class Socio {
    private final int id;
    private final String nombre;
    private final String dni;
    private final String email;
    private final TipoSocio tipo;
    private int cantidadPrestamos; // va cambiando

    public Socio(int id, String nombre, String dni, String email, TipoSocio tipo) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.tipo = tipo;
        this.cantidadPrestamos = 0;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }
    public TipoSocio getTipo() { return tipo; }
    public int getCantidadPrestamos() { return cantidadPrestamos; }

    // Devuelve el límite según el tipo de socio
    public int getLimitePrestamos() {
        return tipo == TipoSocio.ESTUDIANTE ? 3 : 5;
    }

    public void incrementarPrestamos() { cantidadPrestamos++; }
    public void decrementarPrestamos() { cantidadPrestamos--; }
}