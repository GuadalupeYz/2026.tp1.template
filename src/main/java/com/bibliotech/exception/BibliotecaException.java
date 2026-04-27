package com.bibliotech.exception;

// Clase padre de todos nuestros errores personalizados
public class BibliotecaException extends Exception {
    public BibliotecaException(String mensaje) {
        super(mensaje);
    }
}
