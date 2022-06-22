package com.pucmm.tecnologiaEmergente.Models.Complementos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class Almacen {
    private int codigoAlmacen;
    private int balancaeAlmacen;
}
