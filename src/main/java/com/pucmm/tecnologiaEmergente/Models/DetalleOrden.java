package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "DetalleOrden")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DetalleOrden {

    @Id
    private String codigoDetalle;
    private String numeroOrden;
    private int codigoAlmacen;
    private int codigoComponente;
    private int cantidadComprada;
    private int precioCompra;
    private String unidadCompra ="UND";
    private int porcientoDescuento;
    private Double montoDetalle;
    private LocalDate fechaEntrega;

}
