/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import static semaforo.ConsultaIndices.INDICE_DJI;
import static semaforo.ConsultaIndices.INDICE_NASDAQ;
import static semaforo.ConsultaIndices.INDICE_SandP;

import static semaforo.Controller.calculateColorLock;
import semaforo.Settings.Ticker;
import semaforo.dialog.LoadingDialog;
import view.Synchronizer;
import java.lang.String;
import java.sql.ResultSet;

import semaforo.LogFile;



/**
 *
 * @author fernando
 */
public class Semaforo extends javax.swing.JFrame {
    
    
  
    public static Boolean isDebugMode = false;
    public Boolean isDBCapitalLeida = false;
    public int lecturasCapital = 0;
    public Map<String, ElementoCapitalDB> valoresTickerCapital = new HashMap<>();

    int timerMilisegundosIndicesRefresh = 30000;
    int timerRecomendaciones = 15000;

    Font letraTituloTablas = new java.awt.Font("Arial", 1, 16);
    Color colorTituloTablas = java.awt.Color.BLACK;
    float alineacionTituloTablas = CENTER_ALIGNMENT;
    Font tipoLetraWeeks = new java.awt.Font("ARIAL", 0, 18);

    static Color colorFontCeldaTickers = Color.white;
    static Color colorFontCeldaWeeks = Color.white;

    static Color colorImparCeldaTicker = java.awt.Color.BLACK;
    static Color colorParCeldaTicker = java.awt.Color.DARK_GRAY;
    static Color colorImparCeldaWeeks = java.awt.Color.BLACK;
    static Color colorParCeldaWeeks = java.awt.Color.DARK_GRAY;

    static Color colorBotonesPaneles = Color.RED.darker();

    int porcentajeCasillaD = 19;
    int porcentajeCasillaC = 23 + porcentajeCasillaD;
    int porcentajeCasillaB = 27 + porcentajeCasillaC; //% de CFD
    int porcentajeCasillaA = 31 + porcentajeCasillaB; //% de CFD (acciones que se pueden comprar)

    int[] porcentajes = {porcentajeCasillaA, porcentajeCasillaB, porcentajeCasillaC, porcentajeCasillaD}; //int[4];

    private static final int WEEK1 = 0;
    private static final int WEEK2 = 1;
    private static final int WEEK3 = 2;

    private boolean isload = false;
    // private boolean isloadHistory = true;

    UpdateTableListener listener = null;
    Timer timer = null;

    private static final Color[] colors = new Color[]{
        new Color(0, 130, 0),   // Green
        new Color(80, 190, 0),  // Light Green
        new Color(110, 180, 0), // Yellow
        new Color(150, 170, 0), // Light Yellow
        // VERDES HASTA ACA
        new Color(255, 150, 0), // Light Pink
        new Color(244, 130, 0), // Pink 
        new Color(198, 89, 17), // Brown
        new Color(255, 70, 70), // Red
        new Color(255, 40, 40), // Red CLARO
        new Color(255, 0, 0),   // Red
        new Color(255, 255, 50)
    };

    private int num_positions = 0;

    Settings settings = null;
    //Controller controller = null;

    /**
     * Creates new form Semaforo
     */
    int Mcol = 0;
    int Mrow = 0;

    public class ColumnHeaderRenderer extends JLabel implements TableCellRenderer {

        public ColumnHeaderRenderer() {
            setFont(new Font("Arial", Font.BOLD, 16));
            //setFont(tipoLetraWeeks);
            //setAlignmentX(CENTER);
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setForeground(Color.lightGray);
            setBackground(Color.black);
            //setBorder(BorderFactory.createEtchedBorder());
        }

        public ColumnHeaderRenderer(Color color) {
            setFont(new Font("Arial", Font.BOLD, 16));
            //setFont(tipoLetraWeeks);
            //setAlignmentX(CENTER);
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setForeground(color);
            setBackground(Color.black);
            //setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    //Custom renderer - do what the natural renderer would do, just add a border
    public static class CustomRenderer implements TableCellRenderer {

        TableCellRenderer render;
        Border b;

        public CustomRenderer(TableCellRenderer r, Color top, Color left, Color bottom, Color right) {
            render = r;

            //It looks funky to have a different color on each side - but this is what you asked
            //You can comment out borders if you want too. (example try commenting out top and left borders)
//$$$ Elimina bordes 
//            b = BorderFactory.createCompoundBorder();
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(1, 0, 0, 0, top));
////            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 1, 0, 0, left));
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 1, 0, bottom));
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 0, 1, right));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JComponent result = (JComponent) render.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            result.setForeground(colorFontCeldaTickers);

            result.setBackground(Color.black);
            if (row % 2 == 0) {
                result.setBackground(java.awt.Color.DARK_GRAY);

            }

            ((JLabel) result).setHorizontalAlignment(SwingConstants.CENTER);
//$$$ Elimina bordes resultados weeks
//            result.setBorder(b);

            return result;
        }

    }

// ############################################################################     
// #                        CLASS MY CELL RENDERER       
// linea roja recomendacion, sugerencia    
// ############################################################################     
    public class MyCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

        TableCellRenderer render;
        int positions_render[];
        Border b;
        int index;

        public MyCellRenderer(TableCellRenderer r, int index, Color top, Color left, Color bottom, Color right, int _position[], int numColumn) {
            render = r;
            this.index = index;

            this.positions_render = _position;
            //It looks funky to have a different color on each side - but this is what you asked
            //You can comment out borders if you want too. (example try commenting out top and left borders)

//$$$ ELIMINADO BORDES WEEKS
//            b = BorderFactory.createCompoundBorder();
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(1, 0, 0, 0, top));
////            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 1, 0, 0, left));
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 1, 0, bottom));
            // index 0 es porque la linea roja solo se prende en week 1 
            if (numColumn == 4 && index == 0) {
                b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 0, 1, Color.RED));
            }

        }

//$$$ RENDERER COMPONENT DE LAS WEEKS
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Double valorCFD;
            Double valorDiferencia;
            Double division;

            cellComponent.setForeground(colorFontCeldaWeeks);

            if (row % 2 == 0) {
                cellComponent.setBackground(colorParCeldaWeeks);
            } else {
                cellComponent.setBackground(colorImparCeldaWeeks);

            }

            if (this.positions_render.length > row && this.positions_render.length > 0 && this.positions_render[row] == column /*row == Mrow && Mcol == column*/) {
                if (column > 0) {
                    if (column <= 4 & index == 0) {

                       // if (((System.currentTimeMillis() - tiempoInicio) > timerRecomendaciones) & (cuenta < 200)) {

                            for (int i = 1; i < 5; i++) {
                                //if (i != column)
                                {
                                    table.setValueAt("", row, i);
                                }
                            }

                            cellComponent.setForeground(Color.RED.darker());

//########################################
//$$$ ESTA ES LA RECOMENDACION
                            valorCFD = 0.0;
                            valorDiferencia = 0.0;
                            String tempStrValCFD = "";
                            String tempStrValPosicion = "";
                            try {

                                //CFD
                                tempStrValCFD = TableTicker.getModel().getValueAt(row, 3).toString().replace(",", ".");

                                //POSICION
                                Object obj;
                                obj = TableTicker.getModel().getValueAt(row, 4); //.toString().replace(",", ".");

                                if (!tempStrValCFD.isEmpty()) {

                                    valorCFD = Double.parseDouble(tempStrValCFD);

                                    //RECOMENDACION
                                    division = porcentajes[column - 1 ] * (1.0 * valorCFD) / 100;

                                    if (obj != null) {
                                        tempStrValPosicion = obj.toString().replace(",", ".");

                                        if (!tempStrValPosicion.isEmpty()) {
                                            division -= Double.parseDouble(tempStrValPosicion);
                                        }

                                    }

                                    if (division < 0) {
                                        division = 0.0;
                                    }

                                    table.setValueAt(division.intValue(), row, column);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                       // }

                    }

                    cellComponent.setBackground(colors[this.positions_render[row] - 1]);

                } else {
                    cellComponent.setBackground(colors[0]);
                }
            } else {
                    if ( index == 0 && column > 0 && (cellComponent.getBackground().equals(colorImparCeldaWeeks) || cellComponent.getBackground().equals(colorParCeldaWeeks)) ) {                    
                    table.setValueAt("", row, column); 
                }
            }

            ((JComponent) cellComponent).setBorder(b);

            ((JLabel) cellComponent).setHorizontalAlignment(SwingConstants.CENTER);

            return cellComponent;
        }
    }

    public class ResetCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

        TableCellRenderer render;
        Border b;

        public ResetCellRenderer(TableCellRenderer r, Color top, Color left, Color bottom, Color right, int numColumn) {
            render = r;

            //It looks funky to have a different color on each side - but this is what you asked
            //You can comment out borders if you want too. (example try commenting out top and left borders)
//$$$ Elimina borders            
//            b = BorderFactory.createCompoundBorder();
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(1, 0, 0, 0, top));
////            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 1, 0, 0, left));
//            b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 1, 0, bottom));
//            if (numColumn == 4) {
//               // b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0, 0, 0, 1, Color.RED));
//            }
        }

        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            return cellComponent;
        }
    }


//############################################################
//                         UPDATE PANEL SEMAFORO
//############################################################

    public void updatePanelSemaforo() {
        
        if (semaforo.Semaforo.isDebugMode) System.out.println("* * * * * * * * * * * * * UPDATE PANEL SEMAFORO * * * * * * * * * * * * * *");
        if (semaforo.Semaforo.isDebugMode) System.out.println("");
        
        try {
            jLabelNumPos.setText("" + Controller.getSettings().getNumPos());
        } catch (Exception e) {
            e.printStackTrace();
        }

        jLabelInvested.setText("" + String.format("%.2f", Controller.getSettings().getPorcentCapital()) + " %");


//############################################################
//                         POSICIONES
//############################################################

        
        
        kobytest.KobyTest.posiciones();
        kobytest.KobyTest.miPortfolioUpdates();

    }

    class RealTimeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Random r = new Random(System.currentTimeMillis());
                    //Mcol = r.nextInt(8) + 1;

                    while (update == 2) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Semaforo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    synchronized (updateLock) {
                        update = 0;
                    }

                    Settings settings = Controller.getSettings();
                    Hashtable<String, List<Integer>> ht = Controller.getTickersValue();

                    while (Controller.isCalculatingColor) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    synchronized (calculateColorLock) {
                        Controller.isCalculatingColor = true;
                    }

                    for (int t = 0; t < settings.getTickers().size(); t++) {
                        Controller.calcular_color(settings.getTickers().get(t).getName(), settings.getTickers().get(t).getCurrentPrice());
                    }

                    synchronized (Controller.calculateColorLock) {
                        Controller.isCalculatingColor = false;
                    }

//Aquí se aplica el ordenamiento
                    Map<String, List<Integer>> map = sortByValues(ht);
                    TableModel temp = TableTicker.getModel();

//              
                    updatePanelSemaforo();

//$$$ SE SETEA LAS COLUMNAS DE TICKER
                    int capital = 0;
                    Object obje = null;

                    String columnsTitle[] = {"Ticker", "Price", "To Invest", "CFD", "Bought", "Remain"};
                    Object rows[][] = new Object[settings.getTickers().size()][6];
                    //int size = TableTicker.getRowCount();
                    int fila = 0;

                    int cuentaTickersBaja = 0;
                    int cuentaTickersValidos = 0;

                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();
                        for (Ticker ticker : settings.getTickers()) {
                            if (ticker.getName().equalsIgnoreCase(entry.getKey().toString())) {
//$$$ 0 Nombre del Ticker
                                rows[fila][0] = ticker.getName();
//$$$ 1 Valor del Ticker
                                rows[fila][1] = String.format("%.2f", ticker.getCurrentPrice());
//$$$ 2 CAPITAL

//$$$ rutina para precarga de DDBB
                                //int capitalDB = Controller.getSettings().getCapital(ticker.getName());
                                //valoresTickerCapital.put(ticker.getName(), capitalDB);
                                //System.out.println("***************************** CAPITAL HASHMAP  " + valoresTickerCapital.size() + "  " + valoresTickerCapital.toString());
                                int tempFila = getFilaTickerFrontEnd(temp, ticker.getName());
                                if (tempFila > -1) {
                                    obje = temp.getValueAt(tempFila, 2);
                                }

                                try {
                                    capital = Integer.parseInt((obje == null || obje.toString().isEmpty()) ? "0" : obje.toString());

                                    ElementoCapitalDB elemCapitalDB = valoresTickerCapital.get(ticker.getName());

                                    if ((elemCapitalDB != null) && !(valoresTickerCapital.get(ticker.getName()).isChequeado)) {

                                        if (semaforo.Semaforo.isDebugMode) System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Lectura de DDBB  " + elemCapitalDB);
                                        if (semaforo.Semaforo.isDebugMode) System.out.println("");
                                        if (semaforo.Semaforo.isDebugMode) System.out.println("");

                                        rows[fila][2] = valoresTickerCapital.get(ticker.getName()).getCapital();
                                        valoresTickerCapital.get(ticker.getName()).setIsChequeado(true);

                                    } else {
                                        rows[fila][2] = (capital == 0) ? "" : capital;
                                    }

                                    if (semaforo.Semaforo.isDebugMode) System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ROWS FILA " + fila + " COLUMNA  2 VALE: " + rows[fila][2]);

                                    elemCapitalDB = valoresTickerCapital.get(ticker.getName());
                                    if (elemCapitalDB == null) {
                                        elemCapitalDB = new ElementoCapitalDB();
                                        elemCapitalDB.setCapital(0);
                                        elemCapitalDB.setIsChequeado(true);
                                    }

                                    int capitalDB = elemCapitalDB.getCapital();
                                    //rows[fila][2] = capitalDB;

                                    if ((capitalDB != capital)) {
                                        Controller.getSettings().setCapital(ticker.getName(), capital);
                                        elemCapitalDB.setCapital(capital);
                                        elemCapitalDB.setIsChequeado(true);
                                        valoresTickerCapital.put(ticker.getName(), elemCapitalDB);
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                    rows[fila][2] = "";
                                }

                                //rows[fila][2]
                                //.....
//$$$ 3 CFD                               
                                rows[fila][3] = String.format("", Math.round((capital / ticker.getCurrentPrice())));

                                if ( /*!rows[fila][2].equals(null) ||*/!rows[fila][2].toString().isEmpty()) {
                                    rows[fila][3] = String.format("%.0f", Math.floor(capital / ticker.getCurrentPrice()));
                                }
//$$$ Bought

                                Map misPosiciones = Controller.getSettings().getValorPosiciones();

                                if (misPosiciones != null) {
                                    Iterator iterator2 = misPosiciones.keySet().iterator();
                                    
                                    while (iterator2.hasNext()) {
                                        String key = iterator2.next().toString();
                                        int value = (Integer) misPosiciones.get(key);

                                        if (ticker.getName().equals(key)) {
                                            rows[fila][4] = value;
                                        }

                                        //System.out.println(key + " " + value);
                                    }

                                }

                                //rows[fila][4] = ticker.getCurrentPrice();         
                                try {
                                    //if(rows[fila][3] != "" || rows[fila][4] != ""){
                                    if (rows[fila][3] != null && !rows[fila][3].toString().isEmpty()) {
                                        if (rows[fila][4] != null && !rows[fila][4].toString().isEmpty()) {

                                            //tempStr = TableTicker.getModel().getValueAt(row, 3).toString().replace(",", ".");
// Remain
                                            rows[fila][5] = Integer.parseInt(rows[fila][3].toString()) - Integer.parseInt(rows[fila][4].toString());

                                        } else {
                                            rows[fila][5] = rows[fila][3];
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //System.out.println("INIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIITTTTT");
                                Map misCierres = Controller.getSettings().getValorCierres();

                                if (misCierres != null) {

                                    try {
                                        Double restaCierre = (Double) misCierres.get(ticker.getName()) - ticker.getCurrentPrice();
                                        //System.out.println("RESTAAAA #################  " + restaCierre);
                                        cuentaTickersValidos++;

                                        cuentaTickersBaja = cuentaTickersBaja + (restaCierre > 0 ? 1 : 0);

//                                        System.out.println("TICKER " + ticker.getName() + " CIERRE " + misCierres.get(ticker.getName()) + " PRECIO: " + ticker.getCurrentPrice() );
//                                        System.out.println("#################### PROMEDIO " + ((1.00 * cuentaTickersBaja) / (1.00 * cuentaTickersValidos)) *100 + " VALIDOS " + cuentaTickersValidos + " BAJAS " + cuentaTickersBaja);
                                    } catch (Exception e) {
                                    }

//                                    Iterator iterator3 = misCierres.keySet().iterator();
//
//                                    while (iterator3.hasNext()) {
//                                        String key = iterator3.next().toString();
//                                        Double valueDouble =  (Double)misCierres.get(key);
//                                        
//
//                                        
//                                        System.out.println(key + " " + valueDouble);
//                                    }
                                }

                                //System.out.println("FIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIINN");
                                fila++;
                            }
                        }
                    }

                    Double promedioBajasTickers = ((1.00 * cuentaTickersBaja) / (1.00 * cuentaTickersValidos)) * 100;

                    if (semaforo.Semaforo.isDebugMode) System.out.println("#################### PROMEDIO " + promedioBajasTickers);

                    if (promedioBajasTickers < 50.0) {
                        jLabelSemaphore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/SemVERDE_HOR.png")));

                    } else if (promedioBajasTickers < 70.0) {
                        jLabelSemaphore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/SemAMARILLO_HOR.png")));

                    } else {
                        jLabelSemaphore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/SemROJO_HOR.png")));
                    }

// $$$ GENERACION DEL MODELO CON LA MEJORA DE NO EDICION DE LAS OTRAS COLUMNAS                    
                    TableModel newModel = new DefaultTableModel(rows, columnsTitle) {
                        boolean[] canEdit = new boolean[]{
                            false, false, true, false, false, false
                        };

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit[columnIndex];
                        }
                    };

//$$$ RUTINA ESPECIAL PARA EL MANEJO DEL MODELO QUE SE BORRA Y LA EDICION DEL CAMPO CASH
                    if (!TableTicker.isEditing()) {

                        TableTicker.setModel(newModel);

                        formateaCabeceroTicker();
                    }

                    //tickerContainer.getViewport().setBackground(Color.black);
                    updateTableTickers();

                    updateTableWeek(TableWeek1, settings.getVaribable("rango_1"));
                    loadTableCells(TableWeek1, WEEK1, map);

                    updateTableWeek(TableWeek2, settings.getVaribable("rango_2"));
                    loadTableCells(TableWeek2, WEEK2, map);

                    updateTableWeek(TableWeek3, settings.getVaribable("rango_3"));
                    loadTableCells(TableWeek3, WEEK3, map);

                    //Redraw the window
                    synchronized (updateLock) {
                        update = 1;
                    }

                    validate();

                    repaint();
                }

                private int getFilaTickerFrontEnd(TableModel temp, String name) {

                    int i = 0;

                    String tickerName = "";
                    Object objTicker = null;

                    while (i < temp.getRowCount()) {
                        objTicker = temp.getValueAt(i, 0);
                        if (objTicker != null) {
                            tickerName = objTicker.toString();
                            if (tickerName.compareTo(name) == 0) {
                                break;
                            }

                        }
                        i++;

                    }

                    return (i == temp.getRowCount()) ? -1 : i;
                }
            }
            );

        }
    }

    public void formateaCabeceroTicker() {
        for (int i = 0; i < TableTicker.getColumnCount(); i++) {

            TableTicker.getColumnModel().getColumn(i).setHeaderRenderer(new ColumnHeaderRenderer());
        }
    }

    public void formateaCabeceroWeeks(JTable table) {

        table.getColumnModel().getColumn(0).setHeaderRenderer(new ColumnHeaderRenderer());
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setHeaderRenderer(new ColumnHeaderRenderer());

        for (int i = 1; i < table.getColumnCount() - 1; i++) {

            table.getColumnModel().getColumn(i).setHeaderRenderer(new ColumnHeaderRenderer(new Color(240, 240, 120)));
        }
    }

    public static <K extends Comparable, V extends Comparable> Map<K, List<Integer>> sortByValues(Map<K, List<Integer>> map) {
        List<Map.Entry<K, List<Integer>>> entries = new LinkedList<Map.Entry<K, List<Integer>>>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, List<Integer>>>() {

            @Override
            public int compare(Map.Entry<K, List<Integer>> o1, Map.Entry<K, List<Integer>> o2) {
                int comparation = o1.getValue().get(0).compareTo(o2.getValue().get(0));
                if (comparation == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return comparation;
            }
        });
        Map<K, List<Integer>> sortedMap = new LinkedHashMap<K, List<Integer>>();

        for (Map.Entry<K, List<Integer>> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public void loadTableCells(JTable TableWeek, int index, Map<String, List<Integer>> ht) {

        Settings settings = Controller.getSettings();

        for (int i = 0; i < 10; i++) {
            TableWeek.getColumnModel().getColumn(i).setCellRenderer(new ResetCellRenderer(TableWeek.getDefaultRenderer(Object.class
            ), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, i));

        }

        int[] my_positions = new int[num_positions];
        boolean[] paint = new boolean[num_positions];

        for (int j = 0; j < num_positions; j++) {
            my_positions[j] = -1;
            paint[j] = false;
        }

        //int num = settings.getTickers().size();
        // if (TableWeek.getModel().getRowCount() > 0) {
        int num = Math.min(settings.getTickers().size(), TableWeek.getModel().getRowCount());
     //   }

        // synchronized (Controller.positionLock) {
        for (int row = 0; row < num; row++) {

            if (ht.get(TableTicker.getValueAt(row, 0)) != null) {

                List<Integer> listInt = ht.get(TableTicker.getValueAt(row, 0) /*settings.getTickers().get(row).getName()*/);
                my_positions[row] = -1;
                if (!listInt.isEmpty() && settings.getTickers().get(row).isHistory()) {
                    int col = ht.get(TableTicker.getValueAt(row, 0)).get(index) + 1;
                    my_positions[row] = col;

                }

            }
        }

        // }
        //Modify the cell
        // if (num > 0) {
        for (int col = 0; col < 11/*settings.getTickers().size()*/; col++) {

            //     if (paint[col]) {
            TableWeek.getColumnModel().getColumn(col).setCellRenderer(new MyCellRenderer(TableWeek.getDefaultRenderer(Object.class
            ), index, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, my_positions, col));
            //     }

        }
        //  }
    }

    Long tiempoInicio; // Inicio de Ejecucion del Programa
    Boolean recomentacionHecha = false;
    int cuenta = 0;

    public Semaforo() {

        semaforo.LogFile.logFile("");
        semaforo.LogFile.logFile("************** Inicio ejecucion semaforo ****************");
        
        tiempoInicio = System.currentTimeMillis();  // Inicio de Ejecucion del Programa
        initComponents();                           // Inicializa los componentes de Swing
        setUp();                                    // Inicia el setup de la aplicacion
        precargaCapitalDB();                        // Lee la base de datos para precargar la tabla de Capital de la BBDD
        realTime();                                 // Inicio de threads de lectura de datos del TWS
        ajustaDetallesFrame();                      // Modificacion de bordes y otros elementos de look and feel
        syncScroll();                               // Crea los listeners para la sincronizacion del Scroll
        hiloConsultaIndices();                      // Carga los valores e imagenes de indices de Yahoo Financials

    }

    public void precargaCapitalDB() {

        valoresTickerCapital = DDBB.queryCapital();

        if (semaforo.Semaforo.isDebugMode) System.out.println("***************************** CAPITAL HASHMAP  " + valoresTickerCapital.size() + "  " + valoresTickerCapital.toString());

//        int numFilasSettings = Controller.getSettings().getTickers().size();
//        
//        for (int i = 0; i < numFilasSettings; i++) {
//            String nombreTicker = Controller.getSettings().getTickers().get(i).name;
//            valoresTickerCapital.put(nombreTicker, Controller.getSettings().getCapital(nombreTicker) );
//
//        }
//        System.out.println("***************************** CAPITAL HASHMAP  " + valoresTickerCapital.size() + "  " + valoresTickerCapital.toString());
    }

    /**
     * THREAD PARA ACTUALIZAR LOS INDICES
     */
    public void hiloConsultaIndices() {
        try {

            long startTime = System.currentTimeMillis();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // LLAMADA A LOS METODOS DE CONSULTA
                        do {
                            refrescaIndices();
                            actualizaImagenesIndices();
                            Thread.sleep(timerMilisegundosIndicesRefresh);
                        } while (true);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Semaforo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            t.start();

        } catch (Exception ex) {
            Logger.getLogger(Semaforo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * ACTUALIZA LOS VALORES DE PANTALLA CON LOS VALORES DE INDICE CARGADOS
     */
    public void refrescaIndices() throws InterruptedException {

        ConsultaIndices indicesUtil = new ConsultaIndices();

        Indice indiceNASDAQ = indicesUtil.ObtenerValorIndice(INDICE_NASDAQ);
        Indice indiceSandP = indicesUtil.ObtenerValorIndice(INDICE_SandP);
        Indice indiceDJI = indicesUtil.ObtenerValorIndice(INDICE_DJI);

        Double valorIndiceNASDAQ = indiceNASDAQ.valor;
        Double valorIndiceSandP = indiceSandP.valor;
        Double valorIndiceDJI = indiceDJI.valor;

        Double valorCierreNASDAQ = indiceNASDAQ.cierre;
        Double valorCierreSandP = indiceSandP.cierre;
        Double valorCierreDJI = indiceDJI.cierre;

        jLabelImagenNASDAQ.setText("NASDAQ: " + String.format("%.2f (%.2f%%)", valorIndiceNASDAQ, 100 * (valorIndiceNASDAQ - valorCierreNASDAQ) / valorCierreNASDAQ));
        jLabelImagenSandP.setText("S&P: " + String.format("%.2f (%.2f%%)", valorIndiceSandP, 100 * (valorIndiceSandP - valorCierreSandP) / valorCierreSandP));
        jLabelImagenDJI.setText("DJI: " + String.format("%.2f (%.2f%%)", valorIndiceDJI, 100 * (valorIndiceDJI - valorCierreDJI) / valorCierreDJI));

        jLabelImagenNASDAQ.setForeground((indiceNASDAQ.tendencia.equals(Indice.ALZA)) ? Color.GREEN : Color.RED);
        jLabelImagenSandP.setForeground((indiceSandP.tendencia.equals(Indice.ALZA)) ? Color.GREEN : Color.RED);
        jLabelImagenDJI.setForeground((indiceDJI.tendencia.equals(Indice.ALZA)) ? Color.GREEN : Color.RED);

        // PRENDE SEMAFORO
        if (semaforo.Semaforo.isDebugMode) System.out.println("TENDENCIAS : " + indiceNASDAQ.tendencia + " " + indiceSandP.tendencia + " " + indiceDJI.tendencia);

        if ((indiceNASDAQ.tendencia.equals(Indice.BAJA)) & (indiceSandP.tendencia.equals(Indice.BAJA)) & (indiceDJI.tendencia.equals(Indice.BAJA))) {
            jLabelSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/semaforoRojo.png")));
        } else {
            jLabelSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/semaforoVerde.png")));
        }

    }

    /**
     * ***** IMAGENES INDICE
     */
    public void actualizaImagenesIndices() {

        ConsultaIndices indicesUtil = new ConsultaIndices();
        jLabelImagenNASDAQ.setIcon(indicesUtil.ObtenerImagenIndice(INDICE_NASDAQ));
        jLabelImagenSandP.setIcon(indicesUtil.ObtenerImagenIndice(INDICE_SandP));
        jLabelImagenDJI.setIcon(indicesUtil.ObtenerImagenIndice(INDICE_DJI));
    }
//$$$ Este era el codigo que traia peos     
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//        }

    public void ajustaDetallesFrame() {

        tickerContainer.getViewport().setBackground(Color.black);
        firstWeeksContainer.getViewport().setBackground(Color.black);
        secondWeeksContainer.getViewport().setBackground(Color.black);
        thirdWeeksContainer.getViewport().setBackground(Color.black);

// PARA OCULTAR LOS WEEKS 2 Y 3 AL INICIAR LA APLICACION   
        PanelWeek1.setVisible(true);
        PanelWeek2.setVisible(false);
        PanelWeek3.setVisible(false);
        jButton13Weeks.setForeground(colorBotonesPaneles);
//        jButton26Weeks.setForeground(Color.BLACK);
//        jButton52Weeks.setForeground(Color.BLACK);

// PARA PONER EL FRAME EN COLOR NEGRO
        getContentPane().setBackground(Color.BLACK);
//        firstWeeksContainer.setBackground(Color.BLACK);
//        TableWeek1.setBackground(Color.BLACK);
//        PanelWeek1.setBackground(Color.BLACK);

// ELIMINA LA DECORACION DEL BORDE, USANDO EL BORDER EMPTY 
        TitledBorder title;
        Border empty = BorderFactory.createEmptyBorder();

//        TableTicker.getTableHeader().setForeground(colorTituloTablas);
//        TableTicker.getTableHeader().setFont(letraTituloTablas);
        TableWeek1.getTableHeader().setForeground(colorTituloTablas);
        TableWeek1.getTableHeader().setFont(letraTituloTablas);
        //TableWeek1.getColumnModel().getColumn(0).setHeaderRenderer(new ColumnHeaderRenderer());
        formateaCabeceroTicker();

        formateaCabeceroWeeks(TableWeek1);
        formateaCabeceroWeeks(TableWeek2);
        formateaCabeceroWeeks(TableWeek3);

// $$$WEEKS23  
        TableWeek2.getTableHeader().setForeground(colorTituloTablas);
        TableWeek2.getTableHeader().setFont(letraTituloTablas);

        TableWeek3.getTableHeader().setForeground(colorTituloTablas);
        TableWeek3.getTableHeader().setFont(letraTituloTablas);

        TableWeek3.getTableHeader().setAlignmentY(alineacionTituloTablas);

        //$$$ MOD
        TableTicker.setFont(tipoLetraWeeks); // NOI18N
        TableTicker.setAlignmentX(LEFT_ALIGNMENT);

        TableWeek1.setFont(tipoLetraWeeks); // NOI18N        
// $$$WEEKS23        
        TableWeek2.setFont(tipoLetraWeeks); // NOI18N
        TableWeek3.setFont(tipoLetraWeeks); // NOI18N
    }

    public void syncScroll() {
        //$$$ SINCRONIZA SCROLL BARS
        Synchronizer synchronizer = new Synchronizer(tickerContainer, firstWeeksContainer, secondWeeksContainer, thirdWeeksContainer);
        tickerContainer.getVerticalScrollBar().addAdjustmentListener(synchronizer);
        //tickerContainer.getHorizontalScrollBar().addAdjustmentListener(synchronizer);
        firstWeeksContainer.getVerticalScrollBar().addAdjustmentListener(synchronizer);
        //firstWeeksContainer.getHorizontalScrollBar().addAdjustmentListener(synchronizer);
        secondWeeksContainer.getVerticalScrollBar().addAdjustmentListener(synchronizer);
        //secondWeeksContainer.getHorizontalScrollBar().addAdjustmentListener(synchronizer);
        thirdWeeksContainer.getVerticalScrollBar().addAdjustmentListener(synchronizer);
        //thirdWeeksContainer.getHorizontalScrollBar().addAdjustmentListener(synchronizer);
    }

    public void realTime() {
        timer = new Timer(Controller.getSettings().getVaribable(DDBB.RATIO_REFRESCO), new RealTimeListener());
        timer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelWeek1 = new javax.swing.JPanel();
        firstWeeksContainer = new javax.swing.JScrollPane();
        TableWeek1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        PanelTicker = new javax.swing.JPanel();
        tickerContainer = new javax.swing.JScrollPane();
        TableTicker = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        PanelWeek2 = new javax.swing.JPanel();
        secondWeeksContainer = new javax.swing.JScrollPane();
        TableWeek2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        PanelWeek3 = new javax.swing.JPanel();
        thirdWeeksContainer = new javax.swing.JScrollPane();
        TableWeek3 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        PanelIndex = new javax.swing.JPanel();
        jLabelSemaforo = new javax.swing.JLabel();
        jLabelImagenNASDAQ = new javax.swing.JLabel();
        jLabelImagenSandP = new javax.swing.JLabel();
        jLabelImagenDJI = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton13Weeks = new javax.swing.JButton();
        jButton26Weeks = new javax.swing.JButton();
        jButton52Weeks = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelSemaphore = new javax.swing.JLabel();
        jLabelInvested = new javax.swing.JLabel();
        jLabelNumPos = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1366, 768));

        PanelWeek1.setBackground(new java.awt.Color(0, 0, 0));
        PanelWeek1.setToolTipText("");
        PanelWeek1.setName("Week 15"); // NOI18N

        firstWeeksContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //TableWeek1.setTableHeader(null);
        TableWeek1.setBackground(new java.awt.Color(51, 51, 51));
        TableWeek1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Low", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "High"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        /*
        TableWeek1.getTableHeader().setReorderingAllowed(false);
        */
        firstWeeksContainer.setViewportView(TableWeek1);
        if (TableWeek1.getColumnModel().getColumnCount() > 0) {
            TableWeek1.getColumnModel().getColumn(0).setPreferredWidth(70);
            TableWeek1.getColumnModel().getColumn(1).setPreferredWidth(60);
            TableWeek1.getColumnModel().getColumn(2).setPreferredWidth(60);
            TableWeek1.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableWeek1.getColumnModel().getColumn(4).setPreferredWidth(60);
            TableWeek1.getColumnModel().getColumn(5).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(6).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(7).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(8).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(9).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(10).setPreferredWidth(30);
            TableWeek1.getColumnModel().getColumn(11).setPreferredWidth(75);
        }

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("13 WEEKS");

        javax.swing.GroupLayout PanelWeek1Layout = new javax.swing.GroupLayout(PanelWeek1);
        PanelWeek1.setLayout(PanelWeek1Layout);
        PanelWeek1Layout.setHorizontalGroup(
            PanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWeek1Layout.createSequentialGroup()
                .addGap(222, 222, 222)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(firstWeeksContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        PanelWeek1Layout.setVerticalGroup(
            PanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWeek1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstWeeksContainer)
                .addContainerGap())
        );

        PanelTicker.setBackground(new java.awt.Color(0, 0, 0));
        PanelTicker.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PanelTicker.setForeground(new java.awt.Color(255, 255, 255));
        PanelTicker.setFocusable(false);
        PanelTicker.setMaximumSize(new java.awt.Dimension(500, 32767));

        tickerContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tickerContainer.setMaximumSize(new java.awt.Dimension(500, 32767));

        //TableTicker.setTableHeader(null);
        TableTicker.setBackground(new java.awt.Color(0, 0, 0));
        TableTicker.setForeground(new java.awt.Color(255, 255, 255));
        TableTicker.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ticker", "Price", "to Invest", "CFD", "Bought", "Remain"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TableTicker.setIntercellSpacing(new java.awt.Dimension(0, 0));
        TableTicker.setMaximumSize(new java.awt.Dimension(500, 0));
        TableTicker.getTableHeader().setReorderingAllowed(false);
        tickerContainer.setViewportView(TableTicker);
        if (TableTicker.getColumnModel().getColumnCount() > 0) {
            TableTicker.getColumnModel().getColumn(0).setPreferredWidth(90);
            TableTicker.getColumnModel().getColumn(0).setMaxWidth(90);
            TableTicker.getColumnModel().getColumn(1).setPreferredWidth(90);
            TableTicker.getColumnModel().getColumn(1).setMaxWidth(90);
            TableTicker.getColumnModel().getColumn(2).setPreferredWidth(120);
            TableTicker.getColumnModel().getColumn(2).setMaxWidth(120);
            TableTicker.getColumnModel().getColumn(3).setPreferredWidth(90);
            TableTicker.getColumnModel().getColumn(3).setMaxWidth(90);
            TableTicker.getColumnModel().getColumn(4).setPreferredWidth(90);
            TableTicker.getColumnModel().getColumn(4).setMaxWidth(90);
            TableTicker.getColumnModel().getColumn(5).setPreferredWidth(90);
            TableTicker.getColumnModel().getColumn(5).setMaxWidth(90);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TICKERS");

        javax.swing.GroupLayout PanelTickerLayout = new javax.swing.GroupLayout(PanelTicker);
        PanelTicker.setLayout(PanelTickerLayout);
        PanelTickerLayout.setHorizontalGroup(
            PanelTickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tickerContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
            .addGroup(PanelTickerLayout.createSequentialGroup()
                .addGap(223, 223, 223)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelTickerLayout.setVerticalGroup(
            PanelTickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTickerLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tickerContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tickerContainer.getAccessibleContext().setAccessibleParent(PanelWeek1);

        PanelWeek2.setBackground(new java.awt.Color(0, 0, 0));

        secondWeeksContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //TableWeek2.setTableHeader(null);
        TableWeek2.setBackground(new java.awt.Color(51, 51, 51));
        TableWeek2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Low", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "High"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        /*
        */
        secondWeeksContainer.setViewportView(TableWeek2);
        if (TableWeek2.getColumnModel().getColumnCount() > 0) {
            TableWeek2.getColumnModel().getColumn(0).setPreferredWidth(70);
            TableWeek2.getColumnModel().getColumn(1).setPreferredWidth(60);
            TableWeek2.getColumnModel().getColumn(2).setPreferredWidth(60);
            TableWeek2.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableWeek2.getColumnModel().getColumn(4).setPreferredWidth(60);
            TableWeek2.getColumnModel().getColumn(5).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(6).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(7).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(8).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(9).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(10).setPreferredWidth(30);
            TableWeek2.getColumnModel().getColumn(11).setPreferredWidth(75);
        }

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("26 WEEKS");

        javax.swing.GroupLayout PanelWeek2Layout = new javax.swing.GroupLayout(PanelWeek2);
        PanelWeek2.setLayout(PanelWeek2Layout);
        PanelWeek2Layout.setHorizontalGroup(
            PanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWeek2Layout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(jLabel3))
            .addComponent(secondWeeksContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        PanelWeek2Layout.setVerticalGroup(
            PanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelWeek2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secondWeeksContainer)
                .addContainerGap())
        );

        PanelWeek3.setBackground(new java.awt.Color(0, 0, 0));

        thirdWeeksContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //TableWeek3.setTableHeader(null);
        TableWeek3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Low", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "High"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        /*
        TableWeek3.getTableHeader().setReorderingAllowed(false);
        */
        thirdWeeksContainer.setViewportView(TableWeek3);
        if (TableWeek3.getColumnModel().getColumnCount() > 0) {
            TableWeek3.getColumnModel().getColumn(0).setPreferredWidth(70);
            TableWeek3.getColumnModel().getColumn(1).setPreferredWidth(60);
            TableWeek3.getColumnModel().getColumn(2).setPreferredWidth(60);
            TableWeek3.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableWeek3.getColumnModel().getColumn(4).setPreferredWidth(60);
            TableWeek3.getColumnModel().getColumn(5).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(6).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(7).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(8).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(9).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(10).setPreferredWidth(30);
            TableWeek3.getColumnModel().getColumn(11).setPreferredWidth(75);
        }

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("52 WEEKS");

        javax.swing.GroupLayout PanelWeek3Layout = new javax.swing.GroupLayout(PanelWeek3);
        PanelWeek3.setLayout(PanelWeek3Layout);
        PanelWeek3Layout.setHorizontalGroup(
            PanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWeek3Layout.createSequentialGroup()
                .addGroup(PanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelWeek3Layout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addComponent(jLabel4))
                    .addComponent(thirdWeeksContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        PanelWeek3Layout.setVerticalGroup(
            PanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelWeek3Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thirdWeeksContainer)
                .addContainerGap())
        );

        PanelIndex.setBackground(new java.awt.Color(0, 0, 0));

        jLabelSemaforo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/semaforoOLD.png"))); // NOI18N
        jLabelSemaforo.setText("jLabel5");

        jLabelImagenNASDAQ.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelImagenNASDAQ.setForeground(new java.awt.Color(102, 255, 102));
        jLabelImagenNASDAQ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImagenNASDAQ.setToolTipText("");
        jLabelImagenNASDAQ.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabelImagenNASDAQ.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.gray, null));
        jLabelImagenNASDAQ.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelImagenNASDAQ.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabelImagenSandP.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelImagenSandP.setForeground(new java.awt.Color(102, 255, 102));
        jLabelImagenSandP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImagenSandP.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabelImagenSandP.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.gray, null));
        jLabelImagenSandP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelImagenSandP.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabelImagenDJI.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelImagenDJI.setForeground(new java.awt.Color(102, 255, 102));
        jLabelImagenDJI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImagenDJI.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabelImagenDJI.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.gray, null));
        jLabelImagenDJI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelImagenDJI.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("INDEX:");

        javax.swing.GroupLayout PanelIndexLayout = new javax.swing.GroupLayout(PanelIndex);
        PanelIndex.setLayout(PanelIndexLayout);
        PanelIndexLayout.setHorizontalGroup(
            PanelIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIndexLayout.createSequentialGroup()
                .addGroup(PanelIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelIndexLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelImagenSandP, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelImagenNASDAQ, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelImagenDJI, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1, Short.MAX_VALUE))
        );
        PanelIndexLayout.setVerticalGroup(
            PanelIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIndexLayout.createSequentialGroup()
                .addGroup(PanelIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelIndexLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelIndexLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel5)))
                .addGap(95, 95, 95)
                .addComponent(jLabelImagenNASDAQ, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelImagenSandP, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelImagenDJI, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jButton13Weeks.setText("13 WEEKS");
        jButton13Weeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13Weeks3WeeksActionPerformed(evt);
            }
        });

        jButton26Weeks.setText("26 WEEKS");
        jButton26Weeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26Weeks6WeeksActionPerformed(evt);
            }
        });

        jButton52Weeks.setText("52 WEEKS");
        jButton52Weeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52WeeksActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("NUM POSITIONS - (MAX: 12)");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("% INVESTED CAPITAL ");

        jLabelSemaphore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/semaforo/resources/SemVERDE_HOR.png"))); // NOI18N
        jLabelSemaphore.setText("jLabel8");

        jLabelInvested.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelInvested.setForeground(new java.awt.Color(240, 240, 120));
        jLabelInvested.setText("46%");

        jLabelNumPos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelNumPos.setForeground(new java.awt.Color(240, 240, 120));
        jLabelNumPos.setText("9");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 0, 0));
        jLabel12.setText("70 - 100%");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 0));
        jLabel14.setText("  50 - 70%");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 255, 0));
        jLabel15.setText("    0 - 50%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton13Weeks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton26Weeks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton52Weeks)
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelNumPos)
                                .addGap(123, 123, 123)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelInvested)
                                .addGap(88, 88, 88)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jLabel7)
                                .addGap(33, 33, 33)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSemaphore, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton13Weeks)
                        .addComponent(jButton26Weeks)
                        .addComponent(jButton52Weeks))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabelInvested)
                            .addComponent(jLabelNumPos))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addComponent(jLabelSemaphore, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));

        jMenu2.setBackground(new java.awt.Color(153, 153, 153));
        jMenu2.setText("Settings");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenu2MousePressed(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelTicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelWeek1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelWeek2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelWeek3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelWeek3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelWeek1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelWeek2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelTicker, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(PanelIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jMenu2MouseClicked


    private void jMenu2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MousePressed
        // TODO add your handling code here:
        //  if (SettingsGUI.openWindow()) {
        settingsGUI.setup();
        settingsGUI.setVisible(true);
        //  }
    }//GEN-LAST:event_jMenu2MousePressed

    private void jButton26Weeks6WeeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26Weeks6WeeksActionPerformed
        // TODO add your handling code here:
        jButton13Weeks.setForeground(Color.BLACK);
        jButton26Weeks.setForeground(colorBotonesPaneles);
        jButton52Weeks.setForeground(Color.BLACK);
        PanelWeek1.setVisible(false);
        PanelWeek2.setVisible(true);
        PanelWeek3.setVisible(false);
    }//GEN-LAST:event_jButton26Weeks6WeeksActionPerformed

    private void jButton13Weeks3WeeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13Weeks3WeeksActionPerformed
        // TODO add your handling code here:
        jButton13Weeks.setForeground(colorBotonesPaneles);
        jButton26Weeks.setForeground(Color.BLACK);
        jButton52Weeks.setForeground(Color.BLACK);
        PanelWeek1.setVisible(true);
        PanelWeek2.setVisible(false);
        PanelWeek3.setVisible(false);
    }//GEN-LAST:event_jButton13Weeks3WeeksActionPerformed

    private void jButton52WeeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52WeeksActionPerformed
        // TODO add your handling code here:
        jButton13Weeks.setForeground(Color.BLACK);
        jButton26Weeks.setForeground(Color.BLACK);
        jButton52Weeks.setForeground(colorBotonesPaneles);
        PanelWeek1.setVisible(false);
        PanelWeek2.setVisible(false);
        PanelWeek3.setVisible(true);
    }//GEN-LAST:event_jButton52WeeksActionPerformed

    public int numPosiciones = 0;

    int update = 0;
    Object updateLock = new Object();
    LoadingDialog loadingDialog = null;
    SettingsGUI settingsGUI = null;
    Features features = null;

    public void setUp() {

        this.setTitle("Semaforo");
        URL hj = getClass().getResource("resources/semaforo.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(hj));

        listener = new UpdateTableListener() {

            @Override
            public void addTickers() {

                // Add in Tickers Table
                Settings settings = Controller.getSettings();
                synchronized (updateLock) {
                    update = 2;

                }
                CustomRenderer cr = new CustomRenderer(TableTicker.getDefaultRenderer(Object.class
                ), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

                TableTicker.setDefaultRenderer(Object.class, cr);
                DefaultTableModel model = (DefaultTableModel) TableTicker.getModel();

                Object[] o = new Object[2];
                for (int i = model.getRowCount();
                        i < settings.getTickers()
                        .size(); i++) {

                    o[0] = settings.getTickers().get(i).getName();
                    o[1] = 0;
                    model.addRow(o);
                }

                // Resize the vector of values
                num_positions = Controller.getSettings().getTickers().size();

                num_positions++;

                // Add in Week Tables
                CustomRenderer cr1 = new CustomRenderer(TableWeek1.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
                CustomRenderer cr2 = new CustomRenderer(TableWeek2.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
                CustomRenderer cr3 = new CustomRenderer(TableWeek3.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

                // Table Week 1
                TableWeek1.setDefaultRenderer(Object.class, cr1);
                DefaultTableModel model1 = (DefaultTableModel) TableWeek1.getModel();

                Object[] o1 = new Object[10];

                // Table Week 2
                TableWeek2.setDefaultRenderer(Object.class, cr2);
                DefaultTableModel model2 = (DefaultTableModel) TableWeek2.getModel();

                Object[] o2 = new Object[10];

                // Table Week 3
                TableWeek3.setDefaultRenderer(Object.class, cr3);
                DefaultTableModel model3 = (DefaultTableModel) TableWeek3.getModel();

                Object[] o3 = new Object[10];

                for (int i = model1.getRowCount();
                        i < settings.getTickers()
                        .size(); i++) {

                    model1.addRow(o1);

                    model2.addRow(o2);

                    model3.addRow(o3);
                }

                synchronized (updateLock) {
                    update = 0;
                }

            }

            @Override
            public boolean canUpdate() {
                return update == 1;
            }

            @Override
            public void stopThread() {
                synchronized (updateLock) {
                    update = 2;
                }
            }

            @Override
            public void updateTickers() {
                synchronized (updateLock) {
                    Settings settings = Controller.getSettings();
                    update = 2;

                    CustomRenderer cr = new CustomRenderer(TableTicker.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

                    TableTicker.setDefaultRenderer(Object.class, cr);

                    for (int i = 0; i < settings.getTickers().size(); i++) {

                        TableTicker.setValueAt(settings.getTickers().get(i).getName(), i, 0);
                    }

                    update = 0;
                }
            }

            @Override
            public void updateVariables() {
                Settings settings = Controller.getSettings();
                validate();
                repaint();

                timer.setDelay(settings.getVaribable(DDBB.RATIO_REFRESCO));
            }

            @Override
            public void reomveTicker(int index) {
                synchronized (updateLock) {
                    // Add in Tickers Table

                    update = 2;

                    DefaultTableModel model = (DefaultTableModel) TableTicker.getModel();
                    model.removeRow(index);

                    // Remove row in Week Tables
                    // Table Week 1
                    DefaultTableModel model1 = (DefaultTableModel) TableWeek1.getModel();

                    // Table Week 2
                    DefaultTableModel model2 = (DefaultTableModel) TableWeek2.getModel();

                    // Table Week 3
                    DefaultTableModel model3 = (DefaultTableModel) TableWeek3.getModel();

                    // Table Week 1
                    model1.removeRow(index);

                    // Table Week 2
                    model2.removeRow(index);

                    // Table Week 3
                    model3.removeRow(index);

                    num_positions = Controller.getSettings().getTickers().size();

                    update = 0;

                }
            }
        };

        Controller.setup(listener);/* = new Controller();*/

        Settings settings = Controller.getSettings();

        num_positions = Controller.getSettings().getTickers().size();

        loadTableTickers();

        loadTableWeek(TableWeek1, WEEK1);

        loadTableWeek(TableWeek2, WEEK2);

        loadTableWeek(TableWeek3, WEEK3);

        String cad = settings.getVaribable(DDBB.RANGO_1) > 1 ? "weeks" : "week";

        cad = settings.getVaribable(DDBB.RANGO_2) > 1 ? "weeks" : "week";

        cad = settings.getVaribable(DDBB.RANGO_3) > 1 ? "weeks" : "week";

        TableTicker.setCellSelectionEnabled(
                false);
        TableWeek1.setCellSelectionEnabled(
                false);
        TableWeek2.setCellSelectionEnabled(
                false);
        TableWeek3.setCellSelectionEnabled(
                false);
        /*validate();
         repaint();*/
        settingsGUI = new SettingsGUI(listener);
        features = new Features();
        features.setBackground(java.awt.Color.BLACK);

    }

    public synchronized void loadTableTickers() {
        Settings settings = Controller.getSettings();

        CustomRenderer cr = new CustomRenderer(TableTicker.getDefaultRenderer(Object.class
        ), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        TableTicker.setDefaultRenderer(Object.class, cr);
        DefaultTableModel model = (DefaultTableModel) TableTicker.getModel();
        TableTicker.setRowHeight(20); //40
        TableTicker.setFont(new Font("Arial", Font.BOLD, 12)); //18
        TableTicker.getColumnModel().getColumn(0).setPreferredWidth(10);
        TableTicker.getColumnModel().getColumn(1).setPreferredWidth(30);

        JTableHeader header = TableTicker.getTableHeader();
        header.setPreferredSize(new Dimension(100, 30));

        TableTicker.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        Object[] o = new Object[2];
        for (int i = 0;
                i < settings.getTickers()
                .size(); i++) {

            o[0] = settings.getTickers().get(i).getName();
            o[1] = 0;
            model.addRow(o);
        }
    }

    public synchronized void updateTableTickers() {
        Settings settings = Controller.getSettings();

        //  TableWeek.getColumnModel().getColumn(0).setPreferredWidth(120);
        //  TableWeek.getColumnModel().getColumn(9).setPreferredWidth(120);
        // CustomRenderer cr = new CustomRenderer(TableWeek.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        //  TableWeek.setDefaultRenderer(Object.class, cr);
        DefaultTableModel model = (DefaultTableModel) TableTicker.getModel();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        int num = Math.min(settings.getTickers().size(), model.getRowCount());
        if (TableTicker.getModel().getRowCount() > 0) {
            num = Math.min(settings.getTickers().size(), TableTicker.getModel().getRowCount());
        }

        isload = false;

        for (int i = 0; i < num; i++) {
            if (settings.getTickers().get(i).getCurrentPrice() <= 0) {
                isload = true;
            }
        }

        new Thread() {

            @Override
            public void run() {
                if (loadingDialog == null) {
                    JFrame frame = new JFrame();
                    loadingDialog = new LoadingDialog(null, "Please Wait", "Loading Data");
                    loadingDialog.setEnabled(false);
                    loadingDialog.setAlwaysOnTop(false);

                }

                if (loadingDialog.isShowing() && isload == false /*&& isloadHistory == false*/) {
                    loadingDialog.setVisible(false);
                } else if (!loadingDialog.isShowing() && isload == true /*&& isloadHistory == true*/) {
                    loadingDialog.setVisible(true);
                }

            }
        }.start();

    }

    public synchronized void loadTableWeek(JTable TableWeek, int index) {
        Settings settings = Controller.getSettings();

        //TableWeek.getColumnModel().getColumn(0).setPreferredWidth(350); //150
        TableWeek.setRowHeight(20); //40
        //TableWeek.getColumnModel().getColumn(9).setPreferredWidth(350);//150
        TableWeek.setFont(new Font("Arial", Font.BOLD, 12));//18

        CustomRenderer cr = new CustomRenderer(TableWeek.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        TableWeek.setDefaultRenderer(Object.class, cr);
        DefaultTableModel model = (DefaultTableModel) TableWeek.getModel();
        Object[] o = new Object[10];

        JTableHeader header = TableWeek.getTableHeader();
        header.setPreferredSize(new Dimension(10, 30)); //100
        TableWeek.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        /* DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
         TableWeek.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
         TableWeek.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
         TableWeek.setDefaultRenderer(String.class, centerRenderer);*/
        int num = settings.getTickers().size();

        /*   while(DDBB.loadData) {
         try {
         Thread.sleep(500);
         } catch (InterruptedException ex) {
         Logger.getLogger(Semaforo.class.getName()).log(Level.SEVERE, null, ex);
         }
         }*/
        if (TableWeek.getModel().getRowCount() > 0) {
            num = Math.min(settings.getTickers().size(), TableWeek.getModel().getRowCount());
        }

        for (int i = 0; i < num; i++) {
            o[0] = settings.getTickers().get(i).getMinValue(index);
            o[9] = settings.getTickers().get(i).getMaxValue(index);

            model.addRow(o);
        }
    }

    public synchronized void updateTableWeek(JTable TableWeek, int index) {
        Settings settings = Controller.getSettings();

        //  TableWeek.getColumnModel().getColumn(0).setPreferredWidth(120);
        //  TableWeek.getColumnModel().getColumn(9).setPreferredWidth(120);
        // CustomRenderer cr = new CustomRenderer(TableWeek.getDefaultRenderer(Object.class), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        //  TableWeek.setDefaultRenderer(Object.class, cr);
        DefaultTableModel model = (DefaultTableModel) TableWeek.getModel();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);

        int num = Math.min(settings.getTickers().size(), model.getRowCount());
        if (TableWeek.getModel().getRowCount() > 0) {
            num = Math.min(settings.getTickers().size(), TableWeek.getModel().getRowCount());
        }

        for (int i = 0; i < num; i++) {
            for (int m = 0; m < TableTicker.getModel().getRowCount(); m++) {
                if (TableTicker != null && TableTicker.getModel() != null) {
                    if (TableTicker.getModel().getValueAt(m, 0) != null) {
                        if (TableTicker.getModel().getValueAt(m, 0).equals(settings.getTickers().get(i).getName())) {
                            model.setValueAt(String.format("%.2f", settings.getTickers().get(i).getMinValue(index)), m, 0);
                            model.setValueAt(String.format("%.2f", settings.getTickers().get(i).getMaxValue(index)), m, 11); //TODO: Parametrizar
                        }
                    }
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
  
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Semaforo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Semaforo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Semaforo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Semaforo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new Semaforo().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelIndex;
    private javax.swing.JPanel PanelTicker;
    private javax.swing.JPanel PanelWeek1;
    private javax.swing.JPanel PanelWeek2;
    private javax.swing.JPanel PanelWeek3;
    private javax.swing.JTable TableTicker;
    private javax.swing.JTable TableWeek1;
    private javax.swing.JTable TableWeek2;
    private javax.swing.JTable TableWeek3;
    private javax.swing.JScrollPane firstWeeksContainer;
    private javax.swing.JButton jButton13Weeks;
    private javax.swing.JButton jButton26Weeks;
    private javax.swing.JButton jButton52Weeks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelImagenDJI;
    private javax.swing.JLabel jLabelImagenNASDAQ;
    private javax.swing.JLabel jLabelImagenSandP;
    private javax.swing.JLabel jLabelInvested;
    private javax.swing.JLabel jLabelNumPos;
    private javax.swing.JLabel jLabelSemaforo;
    private javax.swing.JLabel jLabelSemaphore;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane secondWeeksContainer;
    private javax.swing.JScrollPane thirdWeeksContainer;
    private javax.swing.JScrollPane tickerContainer;
    // End of variables declaration//GEN-END:variables
}

    //$$$ DEPRECATED 
/*    
 public void initComponents2(){
            
 jMenu2 = new JMenu();

        
 jMenuBar1 = new JMenuBar();
 jMenu2.setText("Settings");

        
 jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
 public void mousePressed(java.awt.event.MouseEvent evt) {
 jMenu2MousePressed(evt);
 }

 public void mouseClicked(java.awt.event.MouseEvent evt) {
 jMenu2MouseClicked(evt);
 }
 });
        

        
 jMenuBar1.add(jMenu2);


 setJMenuBar(jMenuBar1);

 //--------> Creación TableTickers
 TableTicker = new JTable();
 PanelTicker = new JPanel();
 PanelTicker.setBackground(Color.black);
 PanelTicker.setForeground(Color.white);
 PanelTicker.setBorder(javax.swing.BorderFactory.createTitledBorder  (null, "TICKERS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18),Color.WHITE)); // NOI18N
 PanelTicker.setLayout(new BorderLayout());

     
 PanelIndex = new JPanel();
 PanelIndex.setLayout(new BorderLayout());

        
 TableTicker = new JTable();
 TableTicker.setModel(new javax.swing.table.DefaultTableModel(
 new Object[][]{},
 new String[]{
 "Ticker", "Price", "To Invest", "CFD", "Bought"
 }
 ) {

 boolean[] canEdit = new boolean[]{
 false, false, true, false, false
 };

 public boolean isCellEditable(int rowIndex, int columnIndex) {
 return canEdit[columnIndex];
 }
 });

        
        
        
        
        
        
 TableTicker.setIntercellSpacing(new java.awt.Dimension(1, 1));
 TableTicker.getTableHeader().setReorderingAllowed(false);
 JScrollPane tickerContainer = new JScrollPane(TableTicker);
 //----------> Fin creación TableTickers

 //$$$ WEEK1
 //-------------- Creación de TableWeek1
 TableWeek1 = new JTable();
 PanelWeek1 = new JPanel();
 PanelWeek1.setBackground(Color.black);
 PanelWeek1.setForeground(Color.white);
 PanelWeek1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "WEEKS 13", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18),Color.WHITE)); // NOI18N
 PanelWeek1.setLayout(new BorderLayout());
 PanelWeek1.setToolTipText("");
 PanelWeek1.setName("AG");

 TableWeek1.setModel(new javax.swing.table.DefaultTableModel(
 new Object[][]{},
 new String[]{
 "LOW", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "HIGH"
 }
 ) {
 boolean[] canEdit = new boolean[]{
 false, false, false, false, false, false, false, false, false, false
 };

 public boolean isCellEditable(int rowIndex, int columnIndex) {
 return canEdit[columnIndex];
 }
 });
 if (TableWeek1.getColumnModel().getColumnCount() > 0) {
 TableWeek1.getColumnModel().getColumn(0).setResizable(false);
 TableWeek1.getColumnModel().getColumn(1).setResizable(false);
 TableWeek1.getColumnModel().getColumn(2).setResizable(false);
 TableWeek1.getColumnModel().getColumn(3).setResizable(false);
 TableWeek1.getColumnModel().getColumn(4).setResizable(false);
 TableWeek1.getColumnModel().getColumn(5).setResizable(false);
 TableWeek1.getColumnModel().getColumn(6).setResizable(false);
 TableWeek1.getColumnModel().getColumn(7).setResizable(false);
 TableWeek1.getColumnModel().getColumn(8).setResizable(false);
 TableWeek1.getColumnModel().getColumn(9).setResizable(false);
 TableWeek1.getColumnModel().getColumn(10).setResizable(false);
 TableWeek1.getColumnModel().getColumn(11).setResizable(false);
 }
 TableWeek1.getColumnModel().getColumn(0).setPreferredWidth(350);
 TableWeek1.getColumnModel().getColumn(11).setPreferredWidth(350);
 TableWeek1.getTableHeader().setReorderingAllowed(false);
 JScrollPane firstWeeksContainer = new JScrollPane(TableWeek1);
 //---> Fin TableWeek1
 //----------->Creación TableWeek2
 PanelWeek2 = new JPanel();
 TableWeek2 = new JTable();
 PanelWeek2.setBackground(Color.black);
 PanelWeek2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "WEEKS 26", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 0, 18), Color.white)); // NOI18N
 PanelWeek2.setLayout(new BorderLayout());

 TableWeek2.setModel(new javax.swing.table.DefaultTableModel(
 new Object[][]{},
 new String[]{
 "LOW", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "HIGH"
 }
 ) {
 boolean[] canEdit = new boolean[]{
 false, false, false, false, false, false, false, false, false, false
 };

 public boolean isCellEditable(int rowIndex, int columnIndex) {
 return canEdit[columnIndex];
 }
 });
 if (TableWeek2.getColumnModel().getColumnCount() > 0) {
 TableWeek2.getColumnModel().getColumn(0).setResizable(false);
 TableWeek2.getColumnModel().getColumn(1).setResizable(false);
 TableWeek2.getColumnModel().getColumn(2).setResizable(false);
 TableWeek2.getColumnModel().getColumn(3).setResizable(false);
 TableWeek2.getColumnModel().getColumn(4).setResizable(false);
 //            TableWeek2.getColumnModel().getColumn(4)
 TableWeek2.getColumnModel().getColumn(5).setResizable(false);
 TableWeek2.getColumnModel().getColumn(6).setResizable(false);
 TableWeek2.getColumnModel().getColumn(7).setResizable(false);
 TableWeek2.getColumnModel().getColumn(8).setResizable(false);
 TableWeek2.getColumnModel().getColumn(9).setResizable(false);
 TableWeek2.getColumnModel().getColumn(10).setResizable(false);
 TableWeek2.getColumnModel().getColumn(11).setResizable(false);
 }
 TableWeek2.getColumnModel().getColumn(0).setPreferredWidth(350);
 TableWeek2.getColumnModel().getColumn(11).setPreferredWidth(350);
 TableWeek2.getTableHeader().setReorderingAllowed(false);
 JScrollPane secondWeeksContainer = new JScrollPane(TableWeek2);
 //----------->Fin TableWeek2
 //----------->Creación TableWeek3
 PanelWeek3 = new JPanel();
 TableWeek3 = new JTable();
 PanelWeek3.setBackground(Color.black);
 PanelWeek3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "WEEKS 52", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 0, 18), Color.white)); // NOI18N
 PanelWeek3.setLayout(new BorderLayout());
 TableWeek3.setModel(new javax.swing.table.DefaultTableModel(
 new Object[][]{},
 new String[]{
 "LOW", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "HIGH"
 }
 ) {
 boolean[] canEdit = new boolean[]{
 false, false, false, false, false, false, false, false, false, false
 };

 public boolean isCellEditable(int rowIndex, int columnIndex) {
 return canEdit[columnIndex];
 }
 });
 if (TableWeek3.getColumnModel().getColumnCount() > 0) {
 TableWeek3.getColumnModel().getColumn(0).setResizable(false);
 TableWeek3.getColumnModel().getColumn(1).setResizable(false);
 TableWeek3.getColumnModel().getColumn(2).setResizable(false);
 TableWeek3.getColumnModel().getColumn(3).setResizable(false);
 TableWeek3.getColumnModel().getColumn(4).setResizable(false);
 TableWeek3.getColumnModel().getColumn(5).setResizable(false);
 TableWeek3.getColumnModel().getColumn(6).setResizable(false);
 TableWeek3.getColumnModel().getColumn(7).setResizable(false);
 TableWeek3.getColumnModel().getColumn(8).setResizable(false);
 TableWeek3.getColumnModel().getColumn(9).setResizable(false);
 TableWeek3.getColumnModel().getColumn(10).setResizable(false);
 TableWeek3.getColumnModel().getColumn(11).setResizable(false);     
 }
 TableWeek3.getColumnModel().getColumn(0).setPreferredWidth(350);
 TableWeek3.getColumnModel().getColumn(11).setPreferredWidth(350);
 TableWeek3.getTableHeader().setReorderingAllowed(false);
 JScrollPane thirdWeeksContainer = new JScrollPane(TableWeek3);
 //-----------> Fin TableWeek3
       


 syncScroll();

 //        PanelTicker.add(tickerContainer);
 //        PanelTicker.add(firstWeeksContainer);
 //        PanelTicker.add(secondWeeksContainer);
 //        PanelTicker.add(thirdWeeksContainer);
 JPanel mainPanel = new JPanel();
 mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
 //-----------> agregar a la ventana
 //        mainPanel.add(thirdWeeksContainer, BoxLayout.X_AXIS);
 //        mainPanel.add(secondWeeksContainer, BoxLayout.X_AXIS);
 //        mainPanel.add(firstWeeksContainer, BoxLayout.X_AXIS);
 //        mainPanel.add(tickerContainer, BoxLayout.X_AXIS);
 PanelTicker.add(tickerContainer);
 PanelWeek1.add(firstWeeksContainer);
 PanelWeek2.add(secondWeeksContainer);
 PanelWeek3.add(thirdWeeksContainer);
        
 mainPanel.add(PanelWeek3, BoxLayout.X_AXIS);
 mainPanel.add(PanelWeek2, BoxLayout.X_AXIS);
 mainPanel.add(PanelWeek1, BoxLayout.X_AXIS);
 mainPanel.add(PanelTicker, BoxLayout.X_AXIS);
 mainPanel.add(PanelIndex, BoxLayout.X_AXIS);

 this.getContentPane().add(mainPanel);
 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
 this.pack();
 this.setVisible(true);
 }
 */
