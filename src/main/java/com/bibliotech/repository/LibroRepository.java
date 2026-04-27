package com.bibliotech.repository;

import com.bibliotech.model.Libro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    // Lista en memoria — no hay base de datos
    private final List<Libro> libros = new ArrayList<>();

    public void guardar(Libro libro) {
        libros.add(libro);
    }

    // Optional evita retornar null — buena práctica moderna
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libros.stream()
                .filter(l -> l.isbn().equals(isbn))
                .findFirst();
    }

    public List<Libro> buscarTodos() {
        return libros;
    }

    // Búsqueda por título, autor o categoría (contiene el texto)
    public List<Libro> buscar(String criterio) {
        String c = criterio.toLowerCase();
        return libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(c)
                        || l.autor().toLowerCase().contains(c)
                        || l.categoria().toLowerCase().contains(c))
                .toList();
    }
}
