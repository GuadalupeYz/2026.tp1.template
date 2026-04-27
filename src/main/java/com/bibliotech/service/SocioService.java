package com.bibliotech.service;

import com.bibliotech.model.Socio;
import com.bibliotech.model.TipoSocio;
import com.bibliotech.repository.SocioRepository;
import java.util.List;
import java.util.Optional;

public class SocioService {

    private final SocioRepository socioRepository;
    private int contadorId = 1; // ID autoincrementable simple

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public void registrarSocio(String nombre, String dni, String email, TipoSocio tipo) {
        // Validación básica de email
        if (!email.contains("@")) {
            System.out.println("Error: el email no tiene formato válido.");
            return;
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