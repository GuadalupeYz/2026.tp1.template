package com.bibliotech.service;

import com.bibliotech.exception.ValidacionException;
import com.bibliotech.model.Libro;
import com.bibliotech.repository.LibroRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void registrarLibro(String isbn, String titulo, String autor, int anio, String categoria)
            throws ValidacionException {

        // Campo vacío
        if (isbn.isBlank() || titulo.isBlank() || autor.isBlank() || categoria.isBlank()) {
            throw new ValidacionException("Todos los campos son obligatorios.");
        }

        // ISBN: exactamente 13 dígitos numéricos
        if (!isbn.matches("\\d{13}")) {
            throw new ValidacionException("El ISBN debe tener exactamente 13 dígitos numéricos.");
        }

        // Año: no negativo y no mayor al actual
        int anioActual = LocalDate.now().getYear();
        if (anio < 0 || anio > anioActual) {
            throw new ValidacionException("El año debe estar entre 0 y " + anioActual + ".");
        }

        // ISBN duplicado
        if (libroRepository.buscarPorIsbn(isbn).isPresent()) {
            throw new ValidacionException("Ya existe un libro con el ISBN: " + isbn);
        }

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