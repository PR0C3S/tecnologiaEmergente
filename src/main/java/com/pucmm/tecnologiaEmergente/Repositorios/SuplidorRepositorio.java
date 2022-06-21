package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Suplidor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SuplidorRepositorio extends MongoRepository<Suplidor, String> {

    @Query("{ 'rnc' : ?0 }")
    List<Suplidor> findByRnc(String rnc);
}
