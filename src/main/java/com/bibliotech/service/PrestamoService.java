package com.bibliotech.service;

import com.bibliotech.exception.*;
import com.bibliotech.model.Prestamo;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.LibroRepository;
import com.bibliotech.repository.PrestamoRepository;
import com.bibliotech.repository.SocioRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PrestamoService {

    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;
    private int contadorId = 1;

    public PrestamoService(LibroRepository libroRepository,
                           SocioRepository socioRepository,
                           PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.socioRepository = socioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void realizarPrestamo(String isbn, int socioId) throws BibliotecaException {

        libroRepository.buscarPorIsbn(isbn)
                .orElseThrow(() -> new BibliotecaException("Libro con ISBN " + isbn + " no encontrado."));

        if (prestamoRepository.estaPresado(isbn)) {
            throw new LibroNoDisponibleException(isbn);
        }

        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new SocioNoEncontradoException(socioId));

        if (socio.getCantidadPrestamos() >= socio.getLimitePrestamos()) {
            throw new LimitePrestamosException(socio.getNombre());
        }

        Prestamo prestamo = new Prestamo(
                contadorId++,
                isbn,
                socioId,
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );
        prestamoRepository.guardar(prestamo);
        socio.incrementarPrestamos();

        System.out.println("Préstamo registrado. Devolver antes del: " + prestamo.fechaDevolucionEsperada());
    }

    public void registrarDevolucion(String isbn, int socioId) throws BibliotecaException {

        Prestamo prestamo = prestamoRepository.buscarTodos().stream()
                .filter(p -> p.isbn().equals(isbn) && p.socioId() == socioId)
                .findFirst()
                .orElseThrow(() -> new BibliotecaException("No se encontró un préstamo activo para ese libro y socio."));

        long diasRetraso = ChronoUnit.DAYS.between(prestamo.fechaDevolucionEsperada(), LocalDate.now());

        prestamoRepository.eliminar(isbn);

        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new SocioNoEncontradoException(socioId));
        socio.decrementarPrestamos();

        if (diasRetraso > 0) {
            System.out.println("Devolución registrada con " + diasRetraso + " día(s) de retraso.");
        } else {
            System.out.println("Devolución registrada a tiempo. ¡Gracias!");
        }
    }

    // Nuevo: lista todos los préstamos activos
    public List<Prestamo> listarPrestamosActivos() {
        return prestamoRepository.buscarTodos();
    }
}
