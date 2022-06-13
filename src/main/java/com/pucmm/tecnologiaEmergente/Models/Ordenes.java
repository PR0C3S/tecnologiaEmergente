package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "DetalleOrden")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Ordenes {

    @Id
    private int numeroOrden;
    private int codigoSuplidor;
    private String ciudadSuplidor;
    private LocalDate fechaOrden;
    private float montoTotal;
}
