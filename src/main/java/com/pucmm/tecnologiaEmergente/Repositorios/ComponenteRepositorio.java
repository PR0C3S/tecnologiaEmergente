package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Componente;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComponenteRepositorio extends MongoRepository<Componente, String> {
}
