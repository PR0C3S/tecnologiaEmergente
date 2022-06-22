package com.pucmm.tecnologiaEmergente.Models;

import com.pucmm.tecnologiaEmergente.Models.Complementos.Almacen;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Orden;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Componente")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Componente {

    @Id
    private String id;
    private String codigoComponente;
    private String descripcion;
    private String unidad="UND";
    private List<Almacen> almacenes;
    private List<Orden> ordenados;


}
