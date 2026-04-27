package com.bibliotech.service;

import com.bibliotech.exception.ValidacionException;
import com.bibliotech.model.Socio;
import com.bibliotech.model.TipoSocio;
import com.bibliotech.repository.SocioRepository;

import java.util.List;
import java.util.Optional;

public class SocioService {

    private final SocioRepository socioRepository;
    private int contadorId = 1;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public void registrarSocio(String nombre, String dni, String email, TipoSocio tipo)
            throws ValidacionException {

        // Campos vacíos
        if (nombre.isBlank() || dni.isBlank() || email.isBlank()) {
            throw new ValidacionException("Todos los campos son obligatorios.");
        }

        // DNI: exactamente 8 dígitos numéricos
        if (!dni.matches("\\d{8}")) {
            throw new ValidacionException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        // Email: debe tener @ y al menos un punto después del @
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new ValidacionException("El email no tiene un formato válido (ej: usuario@gmail.com).");
        }

        // DNI duplicado
        boolean dniExiste = socioRepository.buscarTodos().stream()
                .anyMatch(s -> s.getDni().equals(dni));
        if (dniExiste) {
            throw new ValidacionException("Ya existe un socio con el DNI: " + dni);
        }

        Socio socio = new Socio(contadorId++, nombre, dni, email, tipo);
        socioRepository.guardar(socio);
        System.out.println("Socio registrado: " + nombre + " (ID: " + socio.getId() + ")");
    }

    public Optional<Socio> buscarPorId(int id) {
        return socioRepository.buscarPorId(id);
    }

    public List<Socio> listarTodos() {
        return socioRepository.buscarTodos();
    }
}
