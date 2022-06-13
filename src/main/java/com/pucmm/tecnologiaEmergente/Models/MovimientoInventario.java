package com.pucmm.tecnologiaEmergente.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "MovimientoInventario")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MovimientoInventario {

    @Id
    private int codigoMovimiento;
    private LocalDate fechaMovimiento;
    private int codigoAlmacen;
    private String tipoMovimiento; //ENTREGA/SALIDA
    private List<DetalleInventario> detalle;

}
