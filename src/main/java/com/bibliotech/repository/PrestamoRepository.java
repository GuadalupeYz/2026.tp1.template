package com.bibliotech.repository;

import com.bibliotech.model.Prestamo;
import java.util.ArrayList;
import java.util.List;

public class PrestamoRepository {

    private final List<Prestamo> prestamos = new ArrayList<>();

    public void guardar(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    public List<Prestamo> buscarTodos() {
        return prestamos;
    }

    // Busca si hay un préstamo activo (sin devolución) para un ISBN
    public boolean estaPresado(String isbn) {
        return prestamos.stream()
                .anyMatch(p -> p.isbn().equals(isbn));
    }

    public void eliminar(String isbn) {
        prestamos.removeIf(p -> p.isbn().equals(isbn));
    }
}