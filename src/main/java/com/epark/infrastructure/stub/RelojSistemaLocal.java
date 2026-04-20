package com.epark.infrastructure.stub;

import com.epark.domain.ports.RelojSistema;

import java.time.LocalDateTime;

public class RelojSistemaLocal implements RelojSistema {
    @Override
    public LocalDateTime ahora() {
        return LocalDateTime.now();
    }
}
