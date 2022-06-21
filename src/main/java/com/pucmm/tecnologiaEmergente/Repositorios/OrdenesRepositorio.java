package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Ordenes;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdenesRepositorio extends MongoRepository<Ordenes, String> {
}
