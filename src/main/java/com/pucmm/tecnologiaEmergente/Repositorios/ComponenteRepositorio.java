package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Componente;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;

public interface ComponenteRepositorio extends MongoRepository<Componente, String> {

    @Aggregation(pipeline = {
            "{$match: {\n" +
                    "        codigoComponente: ?0\n" +
                    "    }}",
            "{$unwind: {\n" +
                    "        path: \"$almacenes\"\n" +
                    "    }}",
            "{$group: {\n" +
                    "        _id: {codigoComponente: \"$codigoComponente\",descripcion: \"$descripcion\"},\n" +
                    "        balanceTotal:{$sum: \"$almacenes.balanceAlmacen\"}\n" +
                    "    }}",
            "{$project: {\n" +
                    "        _id:0,\n" +
                    "        codigoComponente: \"$_id.codigoComponente\",\n" +
                    "        descripcion: \"$_id.descripcion\",\n" +
                    "        balanceTotal: \"$balanceTotal\"\n" +
                    "    }}"
    })
    AggregationResults<Document> findComponenteCantidad(int codigoComponente);



    @Aggregation(pipeline = {
        "{$unwind: {\n" +
                "        path: \"$ordenados\"\n" +
                "    }}",
            " {$match: {\n" +
                    "        codigoComponente: 5,\n" +
                    "        \"ordenados.fechaEntrega\":{$lte: limSupFecha}\n" +
                    "    }}",
            "{$group: {\n" +
                    "        _id: {codigoComponente: \"$codigoComponente\",descripcion: \"$descripcion\"},\n" +
                    "        totalEntregados:{$sum: \"$ordenados.cantidadComprada\"}\n" +
                    "    }}",
            "{$project: {\n" +
                    "        _id:0,\n" +
                    "        codigoComponente: \"$_id.codigoComponente\",\n" +
                    "        descripcion: \"$_id.descripcion\",\n" +
                    "        totalEntregados: \"$totalEntregados\"\n" +
                    "    }}"
    })
    AggregationResults<Document> findComponenteOrdendos(int codigoComponente, LocalDate fecha);

}
