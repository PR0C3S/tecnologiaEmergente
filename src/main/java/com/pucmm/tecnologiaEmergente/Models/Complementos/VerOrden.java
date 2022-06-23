package com.pucmm.tecnologiaEmergente.Models.Complementos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerOrden {
    private String noOrden;
    private LocalDate fechaOrden;
    private String suplidor;
    private float montoTotal;
    private List<VerComponenteOrden> componenteOrden;
}
