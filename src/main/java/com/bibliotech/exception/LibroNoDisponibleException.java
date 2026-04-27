package com.bibliotech.exception;

// Se lanza cuando un libro ya está prestado
public class LibroNoDisponibleException extends BibliotecaException {
    public LibroNoDisponibleException(String isbn) {
        super("El libro con ISBN " + isbn + " no está disponible.");
    }
}
