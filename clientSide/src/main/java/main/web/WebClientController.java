package main.web;

import jakarta.servlet.http.HttpServletRequest;
import main.data.Contenedor;
import main.data.Estado;
import main.data.Planta;
import main.proxies.IServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Controller
public class WebClientController {
    @Autowired
    private IServiceProxy serviceProxy;

    private String token;

    @ModelAttribute
    public void addAtributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl); // Makes current URL available in all templates
        model.addAttribute("token", token); // Makes token available in all templates
    }

    @GetMapping("/")
    public String home(@RequestParam(name = "token",required = false) String token,
            Model model){
        model.addAttribute("token", token);
        return "index";
    }

    @GetMapping("/plantas")
    public String plantas(@RequestParam(name = "token", required = false) String token,
                          Model model){

        if (token == null || token.isEmpty()) {
            model.addAttribute("errorMessage", "Sesión no válida.");
            return "error";
        }

        try {
            List<Planta> plantas = serviceProxy.getPlantas(token);
            model.addAttribute("plantas", plantas);
            model.addAttribute("token", token);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error: " + ex.getMessage());
            model.addAttribute("token", token);
        }
        return "plantas";
    }

    @GetMapping("/plantas/capacidad")
    public String consultarCapacidad(@RequestParam String token,
                                     @RequestParam String plantaId,
                                     @RequestParam String fecha,
                                     Model model)
    {
        try{
            List<Planta> plantas = serviceProxy.getPlantas(token);
            model.addAttribute("plantas", plantas);
            model.addAttribute("token", token);

            try{
                Double capacidad = serviceProxy.consultarCapacidadFecha(token, plantaId, fecha);

                model.addAttribute("capacidadResultado", capacidad);
                model.addAttribute("plantaConsultada", plantaId);
                model.addAttribute("fechaConsultada", fecha);
                model.addAttribute("token", token);
            }catch (Exception ex){
                model.addAttribute("errorCapacidad", "No hay capacidad disponible para la fecha " + fecha + ". Prueba con otra fecha.");
                model.addAttribute("plantaConsultada", plantaId);
            }
        }catch (Exception ex){
            model.addAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "plantas";
    }

    @GetMapping("/plantas/capacidad/api")
    @ResponseBody
    public Double consultarCapacidadAPI(
            @RequestParam String plantaId,
            @RequestParam String fecha,
            @RequestParam String token) {
        try {
            return serviceProxy.consultarCapacidadFecha(token, plantaId, fecha);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @GetMapping("/contenedores")
    public String contenedores(@RequestParam(name = "token", required = false) String token,
                               Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("errorMessage", "Sesión no válida.");
            return "error";
        }

        try {
            List<Contenedor> contenedores = serviceProxy.getContenedores(token);
            model.addAttribute("contenedores", contenedores);
            model.addAttribute("token", token);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "contenedores";
    }

    @GetMapping("/contenedores/historial")
    public String consultarHistorial(
            @RequestParam String token,
            @RequestParam Long contenedorId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            Model model) {

        try {
            List<Contenedor> contenedores = serviceProxy.getContenedores(token);
            model.addAttribute("contenedores", contenedores);
            model.addAttribute("token", token);

            try {
                List<Estado> historial = serviceProxy.getHistorialContenedor(
                        contenedorId, fechaInicio, fechaFin, token);

                if (historial == null || historial.isEmpty()) {
                    // Lista vacía
                    model.addAttribute("errorHistorial", "Rango erróneo: No hay datos disponibles para ese período.");
                    model.addAttribute("contenedorConsultado", contenedorId);
                } else {
                    model.addAttribute("historialResultado", historial);
                    model.addAttribute("contenedorConsultado", contenedorId);
                    model.addAttribute("fechaInicioConsultada", fechaInicio);
                    model.addAttribute("fechaFinConsultada", fechaFin);
                }
            } catch (Exception ex) {
                model.addAttribute("errorHistorial", "Rango erróneo: No hay datos disponibles para ese período.");
                model.addAttribute("contenedorConsultado", contenedorId);
            }

        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "contenedores";
    }

    @GetMapping("/camiones")
    public String camiones(@RequestParam(name = "token", required = false) String token,
                           Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("errorMessage", "Sesión no válida.");
            return "error";
        }

        try {
            List<Planta> plantas = serviceProxy.getPlantas(token);
            List<Contenedor> contenedores = serviceProxy.getContenedores(token);

            model.addAttribute("plantas", plantas);
            model.addAttribute("contenedores", contenedores);
            model.addAttribute("token", token);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "camiones";
    }

    @PostMapping("/camiones/crear")
    public String crearCamion(
            @RequestParam String token,
            @RequestParam String planta,
            @RequestParam String fecha,
            @RequestParam List<Long> contenedores,
            Model model) {

        try {
            serviceProxy.crearNuevoCamion(contenedores, planta, fecha, token);
            model.addAttribute("successMessage", "Camión creado exitosamente");

            // Recargar datos
            List<Planta> plantas = serviceProxy.getPlantas(token);
            List<Contenedor> contenedoresLista = serviceProxy.getContenedores(token);
            model.addAttribute("plantas", plantas);
            model.addAttribute("contenedores", contenedoresLista);
            model.addAttribute("token", token);

        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error al crear camión: " + ex.getMessage());
        }
        return "camiones";
    }

}
