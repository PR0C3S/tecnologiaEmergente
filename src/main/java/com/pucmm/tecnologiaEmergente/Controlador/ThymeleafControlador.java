package com.pucmm.tecnologiaEmergente.Controlador;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pucmm.tecnologiaEmergente.Models.*;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Almacen;
import com.pucmm.tecnologiaEmergente.Models.Complementos.DetalleInventario;
import com.pucmm.tecnologiaEmergente.Models.Complementos.Orden;
import com.pucmm.tecnologiaEmergente.Models.Complementos.VerComponenteOrden;
import com.pucmm.tecnologiaEmergente.Repositorios.*;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import javax.websocket.server.PathParam;
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
    @Autowired
    private DetalleOrdenRepositorio detalleOrdenRepositorio;

    @Autowired
    private TiempoEntregaSuplidorRepositorio tiempoEntregaSuplidorRepositorio;

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
            attr.addAttribute("alerta","No se puede procesar la orden, no hay componentes en la lista.");
            return "redirect:/crud/generarOrden";
        }
    
        LocalDate fechaDeseada = LocalDate.parse(fecha);
        if(fechaDeseada.isBefore(LocalDate.now().plusDays(2))){
            attr.addAttribute("alerta","No se puede utilizar esta fecha, la solicutd de un pedido debe ser minimo 2 dias despues de la fecha actual.");
            return "redirect:/crud/generarOrden";
        }

        Random aleatorio = new Random();

        ArrayList<VerComponenteOrden> listaVerOrdenes= new ArrayList<>();
        ArrayList<Ordenes> ordenes = new ArrayList<>();
        String mensaje="";
        for (Componente peticion: ordenesCarrito.keySet()){
    
            //calcular dias
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            LocalDate anterior = today.minusYears(1);
            AggregationResults<Document> output = movimientoInventarioRepositorio.findByTipoMovimientoAndFecha(7,anterior,today);
            Document dc = output.getUniqueMappedResult();
            int cantidad = (int) dc.get("totalConsumidos");
            int dias= (int) DAYS.between(anterior, today);

            int diasPedido = (int)DAYS.between(today, fechaDeseada);
            int consumoTotal = (cantidad/dias) * diasPedido;

            AggregationResults<Document> output1 = componenteRepositorio.findComponenteCantidad(peticion.getCodigoComponente());
            dc = output1.getUniqueMappedResult();
            int balanceTotal = (int) dc.get("balanceTotal");

            AggregationResults<Document> output2 = componenteRepositorio.findComponenteOrdendos(peticion.getCodigoComponente(), fechaDeseada);
            dc = output2.getUniqueMappedResult();
            int totalEntregados  = 0;
            if(dc != null){
                totalEntregados = (int) dc.get("totalEntregados");
            }

            int stockEsperado = balanceTotal+totalEntregados;
            int stockRealEsperado =  stockEsperado - consumoTotal;
    
            if (stockRealEsperado > ordenesCarrito.get(peticion)){ //si ya va haber suficiente stock, me salto esta peticion
               mensaje+= "No se genero orden de compra para producto : " + peticion.getDescripcion() + ", se proyecta que va haber suficiente en inventario.\n";

            }else{// si no va a haber suficiente stock, busco el mejor suplidor
        
                int cantidad_comprar =   Math.abs(stockRealEsperado - ordenesCarrito.get(peticion));
                AggregationResults<Document> output3 = tiempoEntregaSuplidorRepositorio.selectSuplidor(peticion.getCodigoComponente(), cantidad_comprar, diasPedido); //>> mongo //find_mejor_suplidor no retorna un Suplidor en si, si no un objeto Json con toda la info que necesito.
                Document mejor_suplidor = output3.getUniqueMappedResult();

                if (mejor_suplidor == null  || mejor_suplidor.isEmpty()){ //query no encontro suplidor que pueda cumplir requisitos.
                    mensaje+="No existe suplidor que cumpla con los requisitos para producto: " + peticion.getDescripcion()+".\n";
                }else{
                    DetalleOrden detalle = new DetalleOrden();
                    //detalle.setCodigoDetalle((int) mejor_suplidor.get("codigoDetalle"));//codigoDetalle
                    detalle.setCodigoAlmacen(aleatorio.nextInt(3)+1); //codigoAlmacen, se elige aleatoriamente
                    detalle.setCodigoComponente(peticion.getCodigoComponente()); //codigoComponente
                    detalle.setCantidadComprada(cantidad_comprar); //cantidadComprada
                    detalle.setPrecioCompra((int) mejor_suplidor.get("precio"));//precioCompra
                    detalle.setPorcientoDescuento((int) mejor_suplidor.get("descuento"));//porcientoDescuento
                    detalle.setMontoDetalle((Double) mejor_suplidor.get("montoTotal")); //montoDetalle
                    LocalDate fechaEntrega =  LocalDate.now().plusDays(Long.valueOf( ((int) mejor_suplidor.get("tiempoEntrega")))); //fechaEntrega // funcion suma la fecha actual + la cantidad de dias que diga mejor_suplidor.tiempoEntrega
                    detalle.setFechaEntrega(fechaEntrega);

                    Suplidor suplidor = suplidorRepositorio.findByCodigoSuplidor((int) mejor_suplidor.get("codigoSuplidor"));

                    //verificar si existe una orden para este suplidor en la que se esta creando
                    int index = verificarNumeroOrdenBySuplidor(ordenes, suplidor.getCodigoSuplidor());

                    //actualizar Componente.ordenados[] con el recien creado DetalleOrden
                    Optional<Componente> opt = componenteRepositorio.findById(peticion.getId()); //crud find
                    Componente actC = opt.get();
                    if (actC.getOrdenados() == null){
                        actC.setOrdenados(new ArrayList<Orden>());
                    }

                    if (index >=0){
                        //Se ha pedido, Se ha actualiza el monto total de esta orden porque se ha pedido a este suplidor anteriormente
                        ordenes.get(index).setMontoTotal(ordenes.get(index).getMontoTotal()+detalle.getMontoDetalle()); //actualizo monto total en Orden
                        String numeroOrden=ordenes.get(index).getNumeroOrden();

                        //Se utiliza el  numero de orden
                        detalle.setNumeroOrden(numeroOrden);   //relaciono DetalleOrden con Orden
                        actC.getOrdenados().add((new Orden( detalle.getCodigoAlmacen(), cantidad_comprar, fechaEntrega)));//almacen al que se pide es un numero random del 1 al 3.

                    }else{

                        //No se ha pedido nada a este suplidor
                        Ordenes orden = new Ordenes();
                        orden.setCodigoSuplidor(suplidor.getCodigoSuplidor());
                        orden.setFechaOrden(fechaEntrega); //fecha entrega
                        orden.setCiudadSuplidor(suplidor.getCiudad());//ciudadSuplidor
                        orden.setMontoTotal(detalle.getMontoDetalle());//montoTotal
                        ordenes.add(orden);

                        //se utiliza el nuevo numero de orden
                        detalle.setNumeroOrden(orden.getNumeroOrden());
                        actC.getOrdenados().add((new Orden(detalle.getCodigoAlmacen(), cantidad_comprar, fechaEntrega)));//almacen al que se pide es un numero random del 1 al 3.
                    }

                    //actualizo el componete
                    componenteRepositorio.save(actC);

                    //Guardo el detalle
                    mensaje+="Se genero la orden de compra para el suplidor: "+mejor_suplidor.get("nombre")+", se pidieron"+cantidad_comprar+ " para el componente: " + peticion.getDescripcion()+".\n";
                    detalleOrdenRepositorio.save(detalle);
                }
            }
        }
        

        if(ordenes.isEmpty()){
            attr.addAttribute("alerta",mensaje);
            return "redirect:/crud/generarOrden";
        }
        for (Ordenes orden : ordenes){
            ordenesRepositorio.save(orden);
        }
        ordenesCarrito = new HashMap<Componente, Integer>();

        attr.addAttribute("alerta",mensaje);
        return "redirect:/crud/verOrden";
    }

    private int verificarNumeroOrdenBySuplidor(ArrayList<Ordenes> ordenes, int codigoSuplidor) {

        for (int i=0; i< ordenes.size();  i++) {
            Ordenes act= ordenes.get(i);
            if(act.getCodigoSuplidor()==codigoSuplidor){
                return i;
            }
        }
        return -1;
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
