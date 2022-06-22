package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Componente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ComponenteRepositorio extends MongoRepository<Componente, String> {

    @Query("{ 'codigoComponente' : ?0, 'almacenes.codigoAlmacen' : ?1, }")
    Componente findByCodigoComponenteAndAlmacenesCodigoAlmacen(String componente, String almacen);

    @Query("{ 'codigoComponente' : ?0")
    Componente findByCodigoComponente(String componente);
}
