package com.pucmm.tecnologiaEmergente.Controlador;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Almacen;
import com.pucmm.tecnologiaEmergente.Models.Complementos.DetalleInventario;
import com.pucmm.tecnologiaEmergente.Models.Componente;
import com.pucmm.tecnologiaEmergente.Models.MovimientoInventario;
import com.pucmm.tecnologiaEmergente.Repositorios.ComponenteRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.MovimientoInventarioRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.OrdenesRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.SuplidorRepositorio;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
@RequestMapping(path="/crud")
public class ThymeleafControlador {


    @Autowired
    private ComponenteRepositorio componenteRepositorio;
    @Autowired
    private OrdenesRepositorio ordenesRepositorio;
    @Autowired
    private MovimientoInventarioRepositorio movimientoInventarioRepositorio;

    @Autowired
    private SuplidorRepositorio suplidorRepositorio;

    private HashMap<Componente, Integer> ordenesCarrito = new HashMap<Componente, Integer>();

    @GetMapping("/generarOrden")
    public String  generarOrden(Model model, @RequestParam(value="alerta", required=false, defaultValue="") String alerta)
    {
        model.addAttribute("componentesSel",componenteRepositorio.findAll());
        if(ordenesCarrito==null){
            ordenesCarrito = new HashMap<Componente, Integer>();
        }

        model.addAttribute("lista",ordenesCarrito);
        if(!alerta.equals("")){
            model.addAttribute("alerta",alerta);
        }
        return "GenerarOrden";
    }

    @PostMapping("/generarOrden")
    public String generarOrdenSave(@RequestParam("fecha") String fecha, RedirectAttributes attr){
        if(ordenesCarrito.size() ==0){
            attr.addAttribute("alerta","No se puede procesar la orden, no hay componentes en la lista");
            return "redirect:/crud/generarOrden";
        }

        LocalDate act = LocalDate.parse(fecha);
        if(act.isBefore(LocalDate.now().plusDays(2))){
            attr.addAttribute("alerta","No se puede utilizar esta fecha, la solicutd de un pedido debe ser minimo 2 dias despues de la fecha actual.");
            return "redirect:/crud/generarOrden";
        }

        //calcular dias
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate anterior = today.minusYears(3);
        AggregationResults<Document> output = movimientoInventarioRepositorio.findByTipoMovimientoAndFecha(7,anterior,today);
        Document dc = output.getUniqueMappedResult();
        int cantidad = (int) dc.get("totalConsumidos");
        int dias= (int) DAYS.between(anterior, today);
        int consumoDiario =cantidad/dias;

        //VERIFICACION QUE NO SE HAYA GENERADO UN PEDIDO PARA ESA FECHA


        //FILTRO DE CALIDAD PRECIO CON AGGREGATES

        //ADD a la base de datos


        ordenesCarrito = new HashMap<Componente, Integer>();
        return "redirect:/crud/verOrden";
    }

    @RequestMapping("/view/Element")
    public String verElementoMovimiento(){

        return "";
    }

//    entrada: (codigoProducto, cantidad_stock_que_quiero, fecha_para_la_que_se_quiere) List<Peticion> //Peticion seria un nuevo objeto disenado para recibir este tipo de peticiones // fecha seria igual para todos los productos
//
//new List<OrdenCompra>;//lista vacia
//
//    for peticion in List<Peticion>{
//
//        detalle = new DetallesOrden();
//        consumo_diario = find_consumo_diario(codigoComponente); >> mongo
//                consumo_total = dias_hasta_fecha_objetivo * consumo //se podria conseguir consumo total directo desde el query en mongo.
//
//        if ((stockEsperado - consumo_total) > peticion.cantidad_stock_que_quiero){
//            //ya va haber suficiente stock, me salto esta peticion
//
//        }else{// si no va a haber suficiente stock, busco el mejor suplidor
//            mejor_suplidor = find_mejor_suplidor(peticion.codigoComponente, peticion.fecha) >> mongo //find_mejor_suplidor no retorna un Suplidor en si, si no un objeto Json con toda la info que necesito.
//
//            llenar parametros de detalle;//con mejor_suplidor lleno los parametros de detalle(codigoSuplidor, precio, descuento...), almacen al que se pide es un numero random del 1 al 3.
//
//            //falta manejo de caso cuando no encuentra un suplidor.
//            if find_mejor_suplidor == null{
//                //saltar a siguiente peticion.
//            }
//
//            guardar(detalle);
//            //actualizar Componente.ordenados[] con el recien creado DetalleOrden
//            componente = find_Componente(peticion.codigoComponente);
//            componente.ordenados.append(new Obj(codigoAlmacen, cantidadComprada, mejor_suplidor.fechaEntrega)); //almacen al que se pide es un numero random del 1 al 3.
//
//            if (mejor_suplidor.codigoSuplidor == orden.codigoSuplidor){//si ya hay una orden creada para mejor_suplidor, agrego ese numeroOrden al recien creado DetalleOrden
//
//            }else{//si no, creo una nueva Orden y agrego ese numeroOrden al recien creado DetalleOrden
//                nueva_orden = new Orden();
//                //lleno los parametros de nueva_orden;
//                List<OrdenCompra>.append(nueva_orden);
//            }
//        }
//    }
//
//    for orden in List<OrdenCompra>{
//            guardar orden;
//        }
//
//    return List<OrdenCompra>; // o void
//        salida: void

//    @PostMapping("/generarMovimiento")
//    public String generarMovimiento(
//            @RequestParam("tipoMovimiento") String tipoMovimiento,
//            @RequestParam("id") String id,
//            @RequestParam("cantidad") int cantidad,
//            RedirectAttributes attr
//    ){
//
//        for (DetalleInventario:) {
//
//        }
//        for peticion in List<Peticion>{
//
//
//            detalle = new DetallesOrden();
//            consumo_diario = find_consumo_diario(codigoComponente); >> mongo
//                    consumo_total = dias_hasta_fecha_objetivo * consumo //se podria conseguir consumo total directo desde el query en mongo.
//
//            if ((stockEsperado - consumo_total) > peticion.cantidad_stock_que_quiero){
//                //ya va haber suficiente stock, me salto esta peticion
//
//            }else{// si no va a haber suficiente stock, busco el mejor suplidor
//                mejor_suplidor = find_mejor_suplidor(peticion.codigoComponente, peticion.fecha) >> mongo //find_mejor_suplidor no retorna un Suplidor en si, si no un objeto Json con toda la info que necesito.
//
//                llenar parametros de detalle;//con mejor_suplidor lleno los parametros de detalle(codigoSuplidor, precio, descuento...), almacen al que se pide es un numero random del 1 al 3.
//
//                //falta manejo de caso cuando no encuentra un suplidor.
//
//                guardar(detalle);
//                //actualizar Componente.ordenados[] con el recien creado DetalleOrden
//                componente = find_Componente(peticion.codigoComponente);
//                componente.ordenados.append(new Obj(codigoAlmacen, cantidadComprada, mejor_suplidor.fechaEntrega)); //almacen al que se pide es un numero random del 1 al 3.
//
//                if (mejor_suplidor.codigoSuplidor == orden.codigoSuplidor){//si ya hay una orden creada para mejor_suplidor, agrego ese numeroOrden al recien creado DetalleOrden
//
//                }else{//si no, creo una nueva Orden y agrego ese numeroOrden al recien creado DetalleOrden
//                    nueva_orden = new Orden();
//                    List<OrdenCompra>.append(nueva_orden);
//                }
//            }
//        }
//
//        for orden in List<OrdenCompra>{
//            guardar orden;
//        }
//
//        return List<OrdenCompra>; // o void
//        salida: void
//
//        MovimientoInventario act = new MovimientoInventario();
//        act.setTipoMovimiento(tipoMovimiento);
//        act.setCodigoAlmacen(codigoAlmacen);
//        if(tipoMovimiento.equals("ENTRADA")){
//            for (DetalleInventario detalleAct: listaInventario) {
//                Componente actComponente = componenteRepositorio.findByCodigoComponenteAndAlmacenesCodigoAlmacen(detalleAct.getCodigoComponente(), codigoAlmacen);
//                for (Almacen almacenAct: actComponente.getAlmacenes()) {
//                    if(almacenAct.getCodigoAlmacen() == codigoAlmacen){
//                        almacenAct.setBalancaeAlmacen(almacenAct.getBalancaeAlmacen()+cantidad);
//                    }
//                }
//                componenteRepositorio.save(actComponente);
//            }
//        }else{
//            for (DetalleInventario detalleAct: listaInventario) {
//                Componente actComponente = componenteRepositorio.findByCodigoComponenteAndAlmacenesCodigoAlmacen(detalleAct.getCodigoComponente(), codigoAlmacen);
//                for (Almacen almacenAct: actComponente.getAlmacenes()) {
//                    if(almacenAct.getCodigoAlmacen() == codigoAlmacen){
//                        if(almacenAct.getBalancaeAlmacen()-cantidad == 0){
//
//                        }
//                        almacenAct.setBalancaeAlmacen();
//                    }
//                }
//                componenteRepositorio.save(actComponente);
//            }
//        }
//        Componente actComponente = findComponentebyId(descripcionID);
//        if (actComponente==null){
//            attr.addAttribute("alerta","Error no se encontro el elemento: "+actComponente.getDescripcion()+" de la lista.");
//            return "redirect:/crud/generarOrden";
//        }
//        ordenesCarrito.remove(actComponente);
//        attr.addAttribute("alerta","Se ha eliminado el elemento: "+actComponente.getDescripcion()+" de la lista.");
//        return "redirect:/crud/generarOrden";
//    }
//
    @GetMapping("/listarOrdenes")
    public String  listarOrdenes(Model model)
    {
        model.addAttribute("lista",suplidorRepositorio.findAll());
        return "VerOrdenes";
    }
    @PostMapping("/AddElement")
    public String addElement(
            @RequestParam("componente") String componente,
            @RequestParam("cantidad") int cantidad, RedirectAttributes attr
    ){
        Optional<Componente> actComponente = componenteRepositorio.findById(componente);
        ordenesCarrito.put(actComponente.get(),cantidad);
        attr.addAttribute("alerta","Se ha agregado el elemento: "+actComponente.get().getDescripcion()+" a la lista.");
        return "redirect:/crud/generarOrden";
    }

    @PostMapping("/deleteElement/{id}")
    public String deleteElement(
            @PathVariable("id") String id, RedirectAttributes attr
    ){
        Optional<Componente> actComponente = componenteRepositorio.findById(id);
        if (actComponente.isEmpty()){
            attr.addAttribute("alerta","Error no se encontro el elemento: "+id+" de la lista.");
            return "redirect:/crud/generarOrden";
        }
        ordenesCarrito.remove(actComponente.get());
        attr.addAttribute("alerta","Se ha eliminado el elemento: "+actComponente.get().getDescripcion()+" de la lista.");
        return "redirect:/crud/generarOrden";
    }






}
