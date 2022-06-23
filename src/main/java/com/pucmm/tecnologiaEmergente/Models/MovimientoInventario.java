package com.pucmm.tecnologiaEmergente.Models;

import com.pucmm.tecnologiaEmergente.Models.Complementos.DetalleInventario;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "MovimientoInventario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovimientoInventario {

    @Id
    private int codigoMovimiento;
    private LocalDate fechaMovimiento = LocalDate.now();
    private int codigoAlmacen;
    private String tipoMovimiento; //ENTREGA/SALIDA
    private List<DetalleInventario> detalle;

}
