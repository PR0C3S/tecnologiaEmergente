package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.DetalleOrden;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DetalleOrdenRepositorio extends MongoRepository<DetalleOrden, String> {
}
