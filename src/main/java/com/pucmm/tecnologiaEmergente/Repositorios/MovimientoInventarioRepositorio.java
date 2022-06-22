package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.MovimientoInventario;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface MovimientoInventarioRepositorio extends MongoRepository<MovimientoInventario, String> {


    @Aggregation(pipeline = {
            "{$unwind: {path: \"$detalle\"}}",
            "{$match: {\n" +
                    "      tipoMovimiento: \"SALIDA\",\n" +
                    "      \"detalle.codigoComponente\": ?0,\n" +
                    "      \"fechaMovimiento\": { $gte: ?1, $lte: ?2 }\n" +
                    "    }}",
            " {$group: {\n" +
                    "     _id: {codigoComponente:\"$detalle.codigoComponente\"},\n" +
                    "     numMovimiento: {$sum: 1},\n" +
                    "     totalConsumidos: {$sum: \"$detalle.cantidadMovimiento\"}\n" +
                    "    }}",
            "{$project: {\n" +
                    "       _id:0,\n" +
                    "       codigoComponente: \"$_id.codigoComponente\",\n" +
                    "       numMovimiento: \"$numMovimiento\",\n" +
                    "       totalConsumidos: \"$totalConsumidos\",\n" +
                    "    }}"
    })
    AggregationResults<Document> findByTipoMovimientoAndFecha(int codigoComponente, LocalDate fechaInf, LocalDate fechaSup);

}
