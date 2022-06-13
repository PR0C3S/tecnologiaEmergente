package com.pucmm.tecnologiaEmergente.Models;

import org.springframework.data.annotation.Id;

public class Suplidor {

    @Id
    private int codigoSuplidor;
    private String nombre;
    private String rnc;
    private String ciudad;
    private String direccion;
}
