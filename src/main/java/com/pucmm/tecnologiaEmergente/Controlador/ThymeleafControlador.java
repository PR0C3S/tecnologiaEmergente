package com.pucmm.tecnologiaEmergente.Controlador;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Almacen;
import com.pucmm.tecnologiaEmergente.Models.Complementos.DetalleInventario;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Orden;
import com.pucmm.tecnologiaEmergente.Models.Componente;
import com.pucmm.tecnologiaEmergente.Models.DetalleOrden;
import com.pucmm.tecnologiaEmergente.Models.MovimientoInventario;
import com.pucmm.tecnologiaEmergente.Models.Ordenes;
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
        
        ArrayList<Ordenes> ordenes = new ArrayList();

        for (Map.Entry<Componente,Integer> peticion: ordenesCarrito.entrySet()){

            if(ordenesCarrito.size() ==0){
                attr.addAttribute("alerta","No se puede procesar la orden, no hay componentes en la lista");
                return "redirect:/crud/generarOrden";
            }
    
            LocalDate fechaDeseada = LocalDate.parse(fecha);
            if(fechaDeseada.isBefore(LocalDate.now().plusDays(2))){
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
            int consumoTotal = consumoDiario * (int)DAYS.between(today, fechaDeseada);
    
            int stockEsperado = findStock(peticion.getKey().getId(), fechaDeseada); //suma de inventarios de componentes y los que vienen ordenados[]
            int stockRealEsperado = stockEsperado - consumoTotal; 
    
            if (stockEsperado > peticion.getValue()){ //si ya va haber suficiente stock, me salto esta peticion
                printf("No se genero orden de compra para producto de codigo: " + peticion.getKey() + ". \n se proyecta que va haber suficiente stock");
                break;

            }else{// si no va a haber suficiente stock, busco el mejor suplidor
        
                int cantidad_comprar = stockEsperado - peticion.getValue();
                mejor_suplidor = find_mejor_suplidor(fechaDeseada, peticion.getKey().getId(), cantidad_comprar); //>> mongo //find_mejor_suplidor no retorna un Suplidor en si, si no un objeto Json con toda la info que necesito.
                
                if (mejor_suplidor == null){ //query no encontro suplidor que pueda cumplir requisitos.
                    printf("No se existe suplidor que cumpla con los requisitos para producto de codigo: " + peticion.getKey());
                    break;
                }
        
                DetalleOrden detalle = new DetalleOrden(
                _, //codigoDetalle
                 _, //numeroOrden, se queda vacio, se agrega mas adelante
                random(1,3), //codigoAlmacen, se elige aleatoriamente
                peticion.getKey(), //codigoComponente 
                cantidad_comprar, //cantidadComprada 
                mejor_suplidor.precio, //precioCompra 
                "UND", //unidadCompra = 
                mejor_suplidor.descuento, //porcientoDescuento = 
                mejor_suplidor.montoTotal //montoDetalle = 
                calcular_fecha(mejor_suplidor.tiempoEntrega); //fechaEntrega // funcion suma la fecha actual + la cantidad de dias que diga mejor_suplidor.tiempoEntrega 
                );

                
                int bandera = 0; //buscar manera mas eficiente de hacer esta parte
                for (int i = 0;i < ordenes.size(); i++){
                    if (ordenes.get(i).codigoSuplidor == mejor_suplidor.codigoSuplidor){
                        bandera = i;
                    }
                }
                if (bandera != 0){
                    Ordenes orden = ordenes.get(bandera);
                    detalle.numeroOrden = orden.numeroOrden; //relaciono DetalleOrden con Orden
                    orden.montoTotal += detalle.montoDetalle; //actualizo monto total en Orden
        
                }else{
        
                    Ordenes nueva_orden = new Ordenes(
                    _, //numeroOrden
                    mejor_suplidor.ciudad, //ciudadSuplidor = 
                    LocalDate.now(), // fechaOrden
                    detalle.montoTotal //montoTotal = 
                    );

                    detalle.numeroOrden = nueva_orden.numeroOrden;
                    ordenes.add(nueva_orden);
                }

                //actualizar Componente.ordenados[] con el recien creado DetalleOrden
                componente = find_Componente(peticion.codigoComponente); //crud find
                componente.ordenados.append(new Obj(detalle.codigoAlmacen, detalle.cantidadComprada, detalle.fechaEntrega)); //almacen al que se pide es un numero random del 1 al 3.
                repo_update(componen);
                //guardar detalle
                repo_guardar(detalle);
            }
        }
        

        for (Ordenes orden : ordenes){
            repo_guardar(orden);
        }
        ordenesCarrito = new HashMap<Componente, Integer>();
        return "redirect:/crud/verOrden";
    }

    @RequestMapping("/view/Element")
    public String verElementoMovimiento(){

        return "";
    }



    //==================================encontrar mejor suplidor===============================

// id = 1;
// cntDias = 30;
// cnt_producto = 50;
// db.TiempoEntregaSuplidor.aggregate([ 
//     {$match: {codigoComponente: id, "activo": "S", "tiempoEntrega": { $lte: cntDias } } },
//     {$set: {montoTotal:  {$multiply : [cnt_producto, {$divide : ["$precio", {$add:[1, {$divide : ['$descuento',100] } ] } ] } ] } } },
//     {$sort: {montoTotal:1} }, 
//     {$limit : 1}
// ]);
//=============================================================================================

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
