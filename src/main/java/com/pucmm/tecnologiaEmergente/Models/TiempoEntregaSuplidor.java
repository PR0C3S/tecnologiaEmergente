package com.pucmm.tecnologiaEmergente.Models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TiempoEntregaSuplidor")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TiempoEntregaSuplidor {

    @Id
    private String id;
    private int codigoSuplidor;
    private String codigoComponente;
    private float tiempoEntrega;
    private float precio;
    private float descuento;
    private String activo; //"S/N"
}
