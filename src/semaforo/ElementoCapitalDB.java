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
public class ElementoCapitalDB {
    public int capital;
    public Boolean isChequeado;
    
    public ElementoCapitalDB(){
        
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public Boolean getIsChequeado() {
        return isChequeado;
    }

    public void setIsChequeado(Boolean isChequeado) {
        this.isChequeado = isChequeado;
    }
    
    @Override
    public String toString(){
        return "{Capital: " + this.capital + " Es Chequeado: " + this.isChequeado + "}";
    }
}
