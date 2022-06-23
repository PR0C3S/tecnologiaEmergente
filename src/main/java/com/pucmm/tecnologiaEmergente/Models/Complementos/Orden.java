package com.pucmm.tecnologiaEmergente.Models.Complementos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class Orden {
    private int codigoAlmacen;
    private int cantidadComprada;
    private LocalDate fechaEntrega;
}
