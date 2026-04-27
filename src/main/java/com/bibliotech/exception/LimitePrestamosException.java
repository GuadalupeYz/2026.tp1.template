package com.bibliotech.exception;

// Se lanza cuando el socio ya llegó a su límite de libros
public class LimitePrestamosException extends BibliotecaException {
    public LimitePrestamosException(String nombre) {
        super("El socio " + nombre + " alcanzó su límite de préstamos.");
    }
}