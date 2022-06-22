package com.pucmm.tecnologiaEmergente.Repositorios;

import com.pucmm.tecnologiaEmergente.Models.Complementos.DetalleInventario;
import com.pucmm.tecnologiaEmergente.Models.MovimientoInventario;
import com.pucmm.tecnologiaEmergente.Models.Suplidor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MovimientoInventarioRepositorio extends MongoRepository<MovimientoInventario, String> {


}
