package com.aerolinea.control;

import com.aerolinea.reportes.ClienteVuelos;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.json.JsonArray;
import javax.json.JsonObject;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean
@ViewScoped
public class controlReporteVuelos implements Serializable{
    private Date fecha1;
    private Date fecha2;
    JasperPrint jasperPrint;
    HttpServletResponse httpServletResponse;
  
    public controlReporteVuelos() {
    }
    public Date getFecha1() {
        return fecha1;
    }
    public void setFecha1(Date fecha1) {
        this.fecha1 = fecha1;
    }
    public Date getFecha2() {
        return fecha2;
    }
    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }
    
    public void generarReporte() throws JRException, IOException{
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String reportePath = sc.getRealPath("reportes/ReporteVuelosRest.jasper");
        Map<String, Object> parametros = new HashMap<>();
        String f1 = new SimpleDateFormat("dd-MM-yyyy").format(fecha1);
        String f2 = new SimpleDateFormat("dd-MM-yyyy").format(fecha2);
        JRBeanCollectionDataSource coll = new JRBeanCollectionDataSource(consultar(f1, f2));
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("es", "ES"));
        parametros.put("fecha1", f1);
        parametros.put("fecha2", f2);
        jasperPrint = JasperFillManager.fillReport(reportePath, parametros, coll);
        httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setHeader("Content-Disposition", "inline;filename=ReporteVuelosRest.pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();
    }
    
    private List<AeropuertoRest> consultar(String f1, String f2){
        ClienteVuelos c = new ClienteVuelos();
        JsonArray listaA = c.consultarVuelos(JsonArray.class, f1, f2);
        List<AeropuertoRest> listaB = new ArrayList<>();
        for (int i=0;i<listaA.size();i++){
            JsonObject o = listaA.getJsonObject(i);
            JsonObject origen = o.getJsonObject("idorigen");
            JsonObject destino = o.getJsonObject("iddestino");
            AeropuertoRest a = new AeropuertoRest();
            a.setIdvuelo(o.getInt("idvuelo"));
            a.setOrigen(origen.getString("aeropuerto"));
            a.setDestino(destino.getString("aeropuerto"));
            a.setFecha(o.getString("fecha"));
            listaB.add(a);
        }
        c.close();
        return listaB;
    }
    
    public class AeropuertoRest{
        Integer idvuelo;
        String origen;
        String destino;
        String fecha;

        public Integer getIdvuelo() {
            return idvuelo;
        }

        public void setIdvuelo(Integer idvuelo) {
            this.idvuelo = idvuelo;
        }

        public String getOrigen() {
            return origen;
        }

        public void setOrigen(String origen) {
            this.origen = origen;
        }

        public String getDestino() {
            return destino;
        }

        public void setDestino(String destino) {
            this.destino = destino;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }
}
