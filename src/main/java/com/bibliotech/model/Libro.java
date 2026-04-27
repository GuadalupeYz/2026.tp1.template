package com.bibliotech.model;

// 'record' = clase de solo datos, sin setters, inmutable
public record Libro(String isbn, String titulo, String autor, int anio, String categoria) {
}
