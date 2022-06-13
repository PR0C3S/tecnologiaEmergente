package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Almacen")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Almacen {

    @Id
    private int codigoAlmacen;
    private float balancaeAlmacen;
}
