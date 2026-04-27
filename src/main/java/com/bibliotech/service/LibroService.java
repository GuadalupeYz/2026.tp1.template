package com.bibliotech.service;

import com.bibliotech.model.Libro;
import com.bibliotech.repository.LibroRepository;
import java.util.List;
import java.util.Optional;

public class LibroService {

    // El servicio no crea el repo, lo recibe — eso es inyección de dependencias
    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void registrarLibro(String isbn, String titulo, String autor, int anio, String categoria) {
        Libro libro = new Libro(isbn, titulo, autor, anio, categoria);
        libroRepository.guardar(libro);
        System.out.println("Libro registrado: " + titulo);
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepository.buscarPorIsbn(isbn);
    }

    public List<Libro> buscar(String criterio) {
        return libroRepository.buscar(criterio);
    }

    public List<Libro> listarTodos() {
        return libroRepository.buscarTodos();
    }
}