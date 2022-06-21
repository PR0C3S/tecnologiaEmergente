package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.MovimientoInventario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovimientoInventarioRepositorio extends MongoRepository<MovimientoInventario, String> {
}
