package com.bibliotech.service;

import com.bibliotech.exception.*;
import com.bibliotech.model.Prestamo;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.LibroRepository;
import com.bibliotech.repository.PrestamoRepository;
import com.bibliotech.repository.SocioRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrestamoService {

    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;
    private int contadorId = 1;

    // Inyección por constructor — recibe los 3 repositorios que necesita
    public PrestamoService(LibroRepository libroRepository,
                           SocioRepository socioRepository,
                           PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.socioRepository = socioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void realizarPrestamo(String isbn, int socioId) throws BibliotecaException {
        // 1. Verificar que el libro existe
        libroRepository.buscarPorIsbn(isbn)
                .orElseThrow(() -> new BibliotecaException("Libro con ISBN " + isbn + " no encontrado."));

        // 2. Verificar que el libro esté disponible
        if (prestamoRepository.estaPresado(isbn)) {
            throw new LibroNoDisponibleException(isbn);
        }

        // 3. Verificar que el socio existe
        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new SocioNoEncontradoException(socioId));

        // 4. Verificar límite de préstamos del socio
        if (socio.getCantidadPrestamos() >= socio.getLimitePrestamos()) {
            throw new LimitePrestamosException(socio.getNombre());
        }

        // 5. Registrar el préstamo (14 días de plazo)
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
        // Buscar el préstamo activo
        Prestamo prestamo = prestamoRepository.buscarTodos().stream()
                .filter(p -> p.isbn().equals(isbn) && p.socioId() == socioId)
                .findFirst()
                .orElseThrow(() -> new BibliotecaException("No se encontró un préstamo activo para ese libro y socio."));

        // Calcular días de retraso
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
}