package com.bibliotech.repository;

import com.bibliotech.model.Socio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocioRepository {

    private final List<Socio> socios = new ArrayList<>();

    public void guardar(Socio socio) {
        socios.add(socio);
    }

    public Optional<Socio> buscarPorId(int id) {
        return socios.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    public List<Socio> buscarTodos() {
        return socios;
    }
}