package com.bibliotech.exception;

public class SocioNoEncontradoException extends BibliotecaException {
    public SocioNoEncontradoException(int id) {
        super("No se encontró el socio con ID: " + id);
    }
}
