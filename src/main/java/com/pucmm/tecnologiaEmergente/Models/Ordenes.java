package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "Ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ordenes {

    @Id
    private String id;
    private String numeroOrden;
    private int codigoSuplidor;
    private String ciudadSuplidor;
    private LocalDate fechaOrden;
    private Double montoTotal;
}

