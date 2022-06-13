package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.DetalleInventario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DetalleInventarioRepositorio extends MongoRepository<DetalleInventario, String> {
}
