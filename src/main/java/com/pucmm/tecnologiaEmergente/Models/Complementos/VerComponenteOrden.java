package com.pucmm.tecnologiaEmergente.Models.Complementos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerComponenteOrden {
    private int codigoComponente;
    private String descripcion;
    private int cantidad;
    private String unidad = "UND";
    private int precio;
    private float importe;
}
