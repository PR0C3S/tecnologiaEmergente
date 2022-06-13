package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Almacen;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlmacenRepositorio extends MongoRepository<Almacen, Integer> {
}
