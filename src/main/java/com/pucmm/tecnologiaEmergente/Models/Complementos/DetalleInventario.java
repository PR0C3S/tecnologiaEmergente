package com.pucmm.tecnologiaEmergente.Models.Complementos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleInventario {

    private int codigoComponente;
    private int cantidadMovimiento;
    private String unidad="UND";
}
