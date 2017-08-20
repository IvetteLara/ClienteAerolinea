package com.aerolinea.control;

import com.aerolinea.entidad.Avion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.xml.ws.WebServiceRef;

@ManagedBean
@SessionScoped
public class ControlAvion implements Serializable {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WebServices/wsAvion.wsdl")
    private com.aerolinea.cliente.WsAvion_Service service;
    
    private List<Avion> aviones;
    private Avion avion;
    
    @PostConstruct
    public void init(){
        avion=new Avion();
        aviones = new ArrayList<>();
    }
    
    public ControlAvion() {
    }

    public List<Avion> getAviones() {
        List<com.aerolinea.cliente.Avion> lista = 
                consultarAviones();
        aviones.clear();
        for(com.aerolinea.cliente.Avion a: lista){
            aviones.add(new Avion(a.getIdavion(), 
                    a.getCapacidad(), 
                    a.getDescripcion()));
        }
        return aviones;
    }

    public void setAviones(List<Avion> aviones) {
        this.aviones = aviones;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    } 
    
    public String preparaNuevo(){
        avion=new Avion();
        return "AvionForm.xhtml?faces-redirect=true";
    }
    
    public String guardar(){
        if (avion.getIdavion()==null){
            com.aerolinea.cliente.Avion a = 
                    new com.aerolinea.cliente.Avion();
            a.setCapacidad(avion.getCapacidad());
            a.setDescripcion(avion.getDescripcion());
            create(a);
        }
        return "AvionLista.xhtml?faces-redirect=true";
    }
    
    private void create(com.aerolinea.cliente.Avion a){
        
        try {
            com.aerolinea.cliente.WsAvion port = service.getWsAvionPort();
            port.create(a);
        } catch (Exception ex) {
        }
    }
    
    private List<com.aerolinea.cliente.Avion> consultarAviones(){
        
        try { 
            com.aerolinea.cliente.WsAvion port = service.getWsAvionPort();
            java.util.List<com.aerolinea.cliente.Avion> result = port.findAll();
            return result;
        } catch (Exception ex) {
            return null;
        }
    }
}

