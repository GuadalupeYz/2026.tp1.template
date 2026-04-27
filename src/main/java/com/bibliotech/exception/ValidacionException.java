package com.bibliotech.exception;

// Para errores de validación de datos de entrada
public class ValidacionException extends BibliotecaException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
