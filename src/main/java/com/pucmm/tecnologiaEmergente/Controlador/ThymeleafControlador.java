package com.pucmm.tecnologiaEmergente.Controlador;

import com.pucmm.tecnologiaEmergente.Models.Componente;
import com.pucmm.tecnologiaEmergente.Repositorios.ComponenteRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.OrdenesRepositorio;
import com.pucmm.tecnologiaEmergente.Repositorios.SuplidorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(path="/crud")
public class ThymeleafControlador {


    @Autowired
    private ComponenteRepositorio componenteRepositorio;
    @Autowired
    private OrdenesRepositorio ordenesRepositorio;

    @Autowired
    private SuplidorRepositorio suplidorRepositorio;

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


    @PostMapping("/deleteElement")
    public String deleteElement(
            @RequestParam("descripcionID") String descripcionID, RedirectAttributes attr
    ){
        Componente actComponente = findComponentebyId(descripcionID);
        if (actComponente==null){
            attr.addAttribute("alerta","Error no se encontro el elemento: "+actComponente.getDescripcion()+" de la lista.");
            return "redirect:/crud/generarOrden";
        }
        ordenesCarrito.remove(actComponente);
        attr.addAttribute("alerta","Se ha eliminado el elemento: "+actComponente.getDescripcion()+" de la lista.");
        return "redirect:/crud/generarOrden";
    }

    private Componente findComponentebyId(String descripcionID) {
        for (Componente act: ordenesCarrito.keySet()) {
            if(act.getId().equals(descripcionID)){
                return act;
            }
        }
        return null;
    }

    @GetMapping("/listarOrdenes")
    public String  listarOrdenes(Model model)
    {
        model.addAttribute("lista",suplidorRepositorio.findAll());
        return "VerOrdenes";
    }




}
