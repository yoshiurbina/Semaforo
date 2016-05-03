/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

/**
 *
 * @author LCAAMAÑO
 */

    public class Indice { 

        public static Boolean ALZA = true;

        public static Boolean BAJA = false;

        public String ticker;

        public Double valor;

        public Double cierre;
        
        public Boolean tendencia;

        public Double getCierre() {
            return cierre;
        }

        public void setCierre(Double cierre) {
            this.cierre = cierre;
        }

//        public Indice(String ticker, Double valor, String tendencia) {
//            this.ticker = ticker;
//            this.valor = valor;
//            this.tendencia = tendencia;
//        }

        Indice(String ticker, Double valorActual, Boolean tendencia, Double cierreAnterior) {
            this.ticker = ticker;
            this.valor = valorActual;
            this.tendencia = tendencia;
            this.cierre = cierreAnterior;
           // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public Boolean getTendencia() {
            return tendencia;
        }

        public void setTendencia(Boolean tendencia) {
            this.tendencia = tendencia;
        }

        public void setTendenciaAlza() {
            this.tendencia = ALZA;
        }

        public void setTendenciaBaja() {
            this.tendencia = BAJA;
        }

    }

    