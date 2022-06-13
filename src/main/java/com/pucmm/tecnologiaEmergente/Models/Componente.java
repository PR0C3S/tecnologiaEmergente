package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Componente")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Componente {

    @Id
    private String codigoComponente;
    private String descripcion;
    private String unidad="UND";
    private List<Almacen> almacen;

}
