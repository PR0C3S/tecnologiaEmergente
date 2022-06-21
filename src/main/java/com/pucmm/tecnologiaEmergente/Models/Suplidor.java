package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Suplidor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suplidor {

    @Id
    private String id;
    private int codigoSuplidor;
    private String nombre;
    private String rnc;
    private String ciudad;
    private String direccion;
}
