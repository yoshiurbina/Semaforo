/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import kobytest.KobyTest;
import static semaforo.Controller.calculateColorLock;

/**
 *
 * @author fernando
 */
public class Settings {
    /*public final static String RANGO_1="rango1";
     public final static String RANGO_2="rango2";
     public final static String RANGO_3="rango3";
     public final static String REFRESH_TIME="refreshTime";*/

    private ArrayList<Ticker> tickers;
    private Hashtable<String, Integer> mapTickerID = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> variables = new Hashtable<String, Integer>();
    
    private int numPos = 0;
    private Double porcentCapital = 0.0;
    private Map valorPosiciones ;
    
    private Map valorCierres ;

    public Map getValorCierres() {
        return valorCierres;
    }

    public void setValorCierres(Map valorCierres) {
        this.valorCierres = valorCierres;
    }

    
    
    
    public Map getValorPosiciones() {
        return valorPosiciones;
    }

    public void setValorPosiciones(Map valorPosiciones) {
        this.valorPosiciones = valorPosiciones;
    }
    
    
    
    
    public int getNumPos() {
        return numPos;
    }

    public void setNumPos(int numPos) {
        this.numPos = numPos;
    }
    

    
    
    public Double getPorcentCapital() {
        return porcentCapital;
    }

    public void setPorcentCapital(Double porcentCapital) {
        this.porcentCapital = porcentCapital;
    }
    


    
    //  private int contador_tikers;

    public Settings() {
        
        valorCierres = new HashMap();
        
        tickers = new ArrayList<Ticker>();
        //    contador_tikers = 0;
        DDBB.setup(this);

    }

    public class Ticker {

        HashMap<Integer, HashMap<String, Double>> max_min_week = new HashMap<Integer, HashMap<String, Double>>();
        double current_price;
        String name;
        //int currentValue;

        public Ticker(String name) {
            this(null, 0.0f, name);
            this.current_price = 0.0f;
        }

        public Ticker(HashMap<Integer, HashMap<String, Double>> weeksValue, float current_price, String name) {
            this.max_min_week = weeksValue;
            this.current_price = current_price;
            this.name = name;
        }

        public void setHistioy(HashMap<Integer, HashMap<String, Double>> weeksValue) {
            this.max_min_week = weeksValue;
        }

        public boolean isHistory() {
            return max_min_week != null;
        }

        public double getCurrentPrice() {
            return this.current_price;
        }

        public void setCurretnPrice(double current_price) {
            this.current_price = current_price;
        }

        public double getMaxValue(int week) {
            double m = 0;

            if (max_min_week == null) {
                return 0.0;
            }
            if (max_min_week.get(week) == null) {
                return 0.0;
            }
            return max_min_week.get(week).get("high") == null ? 0.0 : max_min_week.get(week).get("high");
        }

        public void setMaxValue(double maxValue, int week) {

            if (max_min_week == null) {
                return;
            }
            if (max_min_week.get(week) != null) {
                this.max_min_week.get(week).put("high", maxValue);
            }
        }

        public double getMinValue(int week) {

            if (max_min_week == null) {
                return 0.0;
            }
            if (max_min_week.get(week) == null) {
                return 0.0;
            }
            return max_min_week.get(week).get("low") == null ? 0.0 : max_min_week.get(week).get("low");
        }

        public void setMinValue(double minValue, int week) {

            if (max_min_week == null) {
                return;
            }
            if (max_min_week.get(week) != null) {
                this.max_min_week.get(week).put("low", minValue);
            }

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {

            DDBB.updateTicker(this.name, name);
            this.name = name;
        }

    }

    public void addTicker(String nameTicker) {

        tickers.add(new Ticker(null, 0.0f, nameTicker));
        mapTickerID.put(nameTicker, mapTickerID.size());
        KobyTest.anyadir_simbolo(nameTicker);

    }

    public void addTicker(HashMap<Integer, HashMap<String, Double>> weeksValue, String nameTicker) {

        if (!mapTickerID.containsKey(nameTicker)) {
            mapTickerID.put(nameTicker, mapTickerID.size());
            tickers.add(new Ticker(weeksValue, 0.0f, nameTicker));
        } else {
            tickers.get(mapTickerID.get(nameTicker)).setHistioy(weeksValue);
        }

    }

    public int getTickerID(String name) {
        return mapTickerID.get(name);
    }

    public void updateTicker(int index, int valIndex, int min, int max, String name) {
        Ticker t = tickers.get(index);

        t.setMinValue(min, valIndex);
        t.setMaxValue(max, valIndex);
        t.setName(name);
    }

    public boolean existeTicker(String Ticker) {
        return mapTickerID.containsKey(Ticker);
    }

    public void removeTicker(int index) {

        synchronized (calculateColorLock) {
            Controller.isCalculatingColor = true;
        }

        DDBB.deleteTicker(tickers.get(index).getName());
        mapTickerID.remove(tickers.get(index).getName());
        for (String key : mapTickerID.keySet()) {

            int pos = mapTickerID.get(key);
            if (pos > index) {
                mapTickerID.put(key, pos - 1);
            }

        }

        synchronized (calculateColorLock) {
            Controller.isCalculatingColor = false;
        }

        tickers.remove(index);
    }

    public ArrayList<Ticker> getTickers() {
        return tickers;
    }

    public int getVaribable(String tag) {
        return (variables.get(tag) != null) ? variables.get(tag) : -1;
    }

    public void setVaribable(String tag, int value) {
        DDBB.updatePreference(tag, value + "");
        variables.put(tag, value);
    }
    
    public int getCapital(String name){
        return DDBB.requestTickerCapital(name);
    }
    
    public void setCapital(String name, int capital){
        DDBB.updateTickerCapital(name, capital);
    }

    public String[] getNameTickers() {
        String[] ticker_names = new String[mapTickerID.size()];

        Enumeration e = mapTickerID.keys();

        int i = 0;
        while (e.hasMoreElements()) {
            ticker_names[i++] = (String) e.nextElement();
        }

        return ticker_names;
    }
}
