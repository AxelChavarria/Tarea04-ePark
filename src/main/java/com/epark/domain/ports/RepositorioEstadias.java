package com.epark.domain.ports;

import com.epark.domain.model.Estadia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RepositorioEstadias {
    void guardar(Estadia estadia);

    Optional<Estadia> buscarPorId(String idEstadia);

    List<Estadia> buscarActivas();

    List<Estadia> buscarProximasAVencer(LocalDateTime ahora, long minutosAnticipacion);
}
