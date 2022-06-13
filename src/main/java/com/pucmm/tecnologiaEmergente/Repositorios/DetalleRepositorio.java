package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.DetalleOrden;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleRepositorio extends MongoRepository<DetalleOrden, String> {
    @Aggregation(pipeline = {
            "{'$match':{'numeroOrden':?0 }}",
    })
    List<DetalleOrden> findByNumeroOrden(int numero);
}
