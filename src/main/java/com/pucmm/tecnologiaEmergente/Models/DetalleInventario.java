package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DetalleInventario")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleInventario {

    @Id
    private String codigoComponente;
    private float cantidadMovimiento;
    private String unidad="UND";
}
