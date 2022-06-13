package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DetalleOrden")
@Data @Getter @Setter @NoArgsConstructor @AllArgsConstructor

public class DetalleOrden {

    @Id
    private int codigoDetalle;
    private int numeroOrden;
    private int codigoAlmacen;
    private String codigoComponente;
    private float cantidadComprada;
    private float precioCompra;
    private String unidadCompra ="UND";
    private float porcientoDescuento;
    private float montoDetalle;
}
