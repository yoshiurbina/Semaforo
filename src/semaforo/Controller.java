/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import kobytest.KobyTest;
import static kobytest.KobyTest.isDebugConsoleMode;

/**
 *
 * @author fernando
 */
public class Controller {

    public static boolean finish = false;
    public static final Object countLock = new Object();
    public static final Object calculateColorLock = new Object();

    public static boolean conectado = false;
    public static final Object conectadoLock = new Object();
    public static final Object positionLock = new Object();
    public static boolean isCalculatingColor = false;

    private static Settings settings = null;
    private static Hashtable<String, List<Integer>> tickersValues = new Hashtable<String, List<Integer>>();
    private static Controller controler;
    private static int maxWeeks = 52;
    private static UpdateTableListener listener = null;

    /**
     * Si precioActual <= Low entonces verdeOscuro
     * Si precioActual >= HIgh entonces Rojo
     * Si no es ninguna de las anteriores entonces se realiza la siguiente fórmula:
     * ((precioActual - Low) / (High - Low)) * 7
     * Este último valor se redondea hacia donde esté más cerca, por ejemplo: 6.5 será 7 y 6.3 será 6
     * 
     * @param simbolo
     * @param precio 
     */
    public static void calcular_color(String simbolo, /*int tipo,*/ final double precio) {
        if (!settings.existeTicker(simbolo) || precio <= 0) {
            return;
        }

        final int tikcerID = settings.getTickerID(simbolo);
        final String nameTicker = simbolo;
        if ((settings.getTickers().size() - 1) < tikcerID || settings.getTickers().size() <= 0) {
            return;
        }

        settings.getTickers().get(tikcerID).setCurretnPrice(precio);

        //   Thread thread = new Thread() {
        //      public void run() {
        while (!Controller.finish) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double max = 0;//settings.getTickers().get(tikcerID).getMaxValue(0);
        double min = 0;//settings.getTickers().get(tikcerID).getMinValue(0);
        int pos = 0;
        //float currentValue = DDBB.getTickerValue(nameTicker, min - 0.5f, max + 0.5f);

        ArrayList<Integer> positions = new ArrayList<Integer>();

        int j = 1;
        int week = 0;
        while ((week = (settings.getVaribable("rango_" + j))) >= 0) {

            max = settings.getTickers().get(tikcerID).getMaxValue(week);
            min = settings.getTickers().get(tikcerID).getMinValue(week);

            // settings.getTickers().get(tikcerID).setMinValue(-10, week);
            if (precio <= min) {
                settings.getTickers().get(tikcerID).setMinValue(precio, week);
                pos = 0;
            } else if (precio >= max) {
                settings.getTickers().get(tikcerID).setMaxValue(precio, week);
                pos = 9;
            } else {
                pos = normalize(min, max, precio);
            }

            //  synchronized (positionLock) {
            positions.add(pos);
            //    }
            j++;
        }

        tickersValues.put(nameTicker, positions);
      //      }
        //  };

        //  thread.start();
    }

    public static void setup(UpdateTableListener _listener) {
        Controller.listener = _listener;
        if (settings == null) {
            KobyTest.init(maxWeeks, new String[]{}, new KobyTest.ListenerConnectionConsole() {

                @Override
                public void callback_valor(final String simbolo, int tipo, final double precio) {

                    if(tipo == 9)   // CIERRE
                    {
                                                
                     Map valorCierres = Controller.getSettings().getValorCierres();
                     valorCierres.put(simbolo, precio);
                     return;
                        
                    }
                    
                            
                            
                    Thread thread = new Thread() {
                        public void run() {

                            while(Controller.isCalculatingColor) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            synchronized (calculateColorLock) {
                                Controller.isCalculatingColor = true;
                            }

                            Controller.calcular_color(simbolo, precio);
                            
                            synchronized (calculateColorLock) {
                                Controller.isCalculatingColor = false;
                            }
                        }

                    };

                    thread.start();

                }

                @Override
                public void callback_historico(String simbolo, int weeks_ago, double high, double low, double open, double close
                ) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void callback_error(int Code, String error, String simbolo
                ) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                
                @Override
                public void callback_historico_cumulativo(String Valor, HashMap<Integer, HashMap<String, Double>> historico_valores_cumulativo
                ) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                    settings.addTicker(historico_valores_cumulativo, Valor);

                    listener.addTickers();
                    synchronized (countLock) {
                        Controller.finish = true;
                    }

                }

                @Override
                public void callback_historico_cumulativo(HashMap<String, HashMap<Integer, HashMap<String, Double>>> historico_valores_cumulativo
                ) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void callback_conexion_aceptada() {
                    
                    
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    synchronized (conectadoLock) {
                        conectado = true;
                    }
                }
                
                
                @Override
                public void guardaNumPos(int numPos) {
                    if (isDebugConsoleMode) System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  guardaNumPos   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    settings.setNumPos(numPos);
                }

                @Override
                public void guardaPorcentCapital(Double porcentCapital) {
                    if (isDebugConsoleMode) System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  guardaPorcentCapital   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    settings.setPorcentCapital(porcentCapital);                
                }

                @Override
                public void guardaPosiciones(Map valorPosiciones) {
                    
                    if (isDebugConsoleMode) {System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  guardaPosiciones   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");}
                    settings.setValorPosiciones(valorPosiciones);
                    
                }
                
                
 
                
            });

            settings = new Settings();

        }
    }

    public static Settings getSettings() {
        return settings;
    }

    public static Hashtable<String, List<Integer>> getTickersValue() {

        return tickersValues;
    }

    private static int normalize(double min, double max, double value) {
        long d = Math.round(((value - min) / (max - min)) * 9);  //$$$ todo: cUSTOMIZAR COLUMNAS(9 = 10 - 1)

        return (int) d;
    }

}
