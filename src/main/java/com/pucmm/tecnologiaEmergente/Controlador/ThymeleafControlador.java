package com.pucmm.tecnologiaEmergente.Controlador;

import com.pucmm.tecnologiaEmergente.Models.Componente;
import com.pucmm.tecnologiaEmergente.Models.DetalleOrden;
import com.pucmm.tecnologiaEmergente.Models.Ordenes;
import com.pucmm.tecnologiaEmergente.Repositorios.ComponenteRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.DetalleRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.OrdenesRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(path="/crud")
public class ThymeleafControlador {

    @Autowired
    private DetalleRepositorio repositorioDetalle;

    @Autowired
    private ComponenteRepositorio repositorioComponente;
    @Autowired
    private OrdenesRepositorio repositorioOrdenes;

    private HashMap<Componente, Integer> ordenesCarrito = new HashMap<Componente, Integer>();

    @GetMapping("/generarOrden")
    public String  generarOrden(Model model, @RequestParam(value="alerta", required=false, defaultValue="") String alerta)
    {
       //model.addAttribute("componentesSel",repositorioComponente.findAll());
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

        //VERIFICACION QUE NO SE HAYA GENERADO UN PEDIDO PARA ESA FECHA

        //FILTRO DE CALIDAD PRECIO CON AGGREGATES

        //ADD a la base de datos


        ordenesCarrito = new HashMap<Componente, Integer>();
        return "redirect:/crud/verOrden";
    }

    @PostMapping("/AddElement")
    public String addElement(
            @RequestParam("componente") String componente,
            @RequestParam("cantidad") int cantidad, RedirectAttributes attr
    ){
        Optional<Componente> actComponente = repositorioComponente.findById(componente);
        ordenesCarrito.put(actComponente.get(),cantidad);
        attr.addAttribute("alerta","Se ha agregado el elemento: "+actComponente.get().getDescripcion()+" a la lista.");
        return "redirect:/crud/generarOrden";
    }

    @PostMapping("/deleteElement")
    public String deleteElement(
            @RequestParam("descripcionID") String descripcionID, RedirectAttributes attr
    ){
        Optional<Componente> actComponente = repositorioComponente.findById(descripcionID);
        ordenesCarrito.remove(actComponente.get());
        attr.addAttribute("alerta","Se ha eliminado el elemento: "+actComponente.get().getDescripcion()+" de la lista.");
        return "redirect:/crud/generarOrden";
    }

    @GetMapping("/listarOrdenes")
    public String  listarOrdenes(Model model)
    {
        model.addAttribute("lista",repositorioOrdenes.findAll());
        return "VerOrdenes";
    }




}
