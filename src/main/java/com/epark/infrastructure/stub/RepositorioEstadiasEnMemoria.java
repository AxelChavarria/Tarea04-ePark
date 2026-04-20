package com.epark.infrastructure.stub;

import com.epark.domain.enums.EstadoEstadia;
import com.epark.domain.model.Estadia;
import com.epark.domain.ports.RepositorioEstadias;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RepositorioEstadiasEnMemoria implements RepositorioEstadias {
    private final Map<String, Estadia> base = new ConcurrentHashMap<>();

    @Override
    public void guardar(Estadia estadia) {
        base.put(estadia.getIdEstadia(), estadia);
    }

    @Override
    public Optional<Estadia> buscarPorId(String idEstadia) {
        return Optional.ofNullable(base.get(idEstadia));
    }

    @Override
    public List<Estadia> buscarActivas() {
        return base.values().stream()
                .filter(e -> e.getEstado() == EstadoEstadia.ACTIVA)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Estadia> buscarProximasAVencer(LocalDateTime ahora, long minutosAnticipacion) {
        return base.values().stream()
                .filter(e -> e.estaProximaAVencer(ahora, minutosAnticipacion))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
