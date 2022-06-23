package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.TiempoEntregaSuplidor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface TiempoEntregaSuplidorRepositorio extends MongoRepository<TiempoEntregaSuplidor, String> {

    @Aggregation(pipeline = {
          "{$match: {codigoComponente: ?0, \"activo\": \"S\", \"tiempoEntrega\": { $lte: ?2 } } }",
            "{$set: {montoTotal:  {$multiply : [?1, {$divide : [\"$precio\", {$add:[1, {$divide : ['$descuento',100] } ] } ] } ] } } }",
            "{$sort: {montoTotal:1} }",
            "{$limit : 1}"
    })
    AggregationResults<Document> selectSuplidor( int componente, int cantidad_comprar, int diasPedido);
}
