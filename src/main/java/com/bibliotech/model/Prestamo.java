package com.bibliotech.model;

import java.time.LocalDate;

public record Prestamo(int id, String isbn, int socioId, LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada) {
}