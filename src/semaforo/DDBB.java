 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.util.Random;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kobytest.KobyTest.isDebugConsoleMode;

/**
 *
 * @author fernando
 */
public class DDBB {

    public static String RANGO_1 = "rango_1";
    public static String RANGO_2 = "rango_2";
    public static String RANGO_3 = "rango_3";
    public static String RATIO_REFRESCO = "ratio_refresco";
    public static String VALUE = "value";

    private static Connection conn = null;
    
    private final static String GET_RANGO_1 = "select * from PREFERENCIAS where name='" + RANGO_1 + "'";
    private final static String GET_RANGO_2 = "select * from PREFERENCIAS where name='" + RANGO_2 + "'";
    private final static String GET_RANGO_3 = "select * from PREFERENCIAS where name='" + RANGO_3 + "'";
    private final static String GET_REFRESH_TIME = "select * from PREFERENCIAS where name='" + RATIO_REFRESCO + "'";

    private final static String url = "jdbc:h2:./ticker_database";
    public static boolean loadData = true;
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void getConection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, "sa", "sa");
            }
        } catch (SQLException ex) {
            //closeConection();
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void closeConection() {

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ResultSet query(String query) {
        try {

            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException ex) {
            //  closeConection();
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private static void createrDB() {
        final String CREATE_TABLE_PREFERENCE = "CREATE TABLE `preferencias` ( "
                + " `id` INT NOT NULL AUTO_INCREMENT,"
                + " `name` VARCHAR(50) NULL,  "
                + "`value` VARCHAR(50) NULL,"
                + "PRIMARY KEY (`id`));";

        final String CREATE_TABLE_TICKERS = "CREATE TABLE `tickers` (\n"
                + "  `id` INT NOT NULL AUTO_INCREMENT,\n"
                + "  `name` VARCHAR(15) NOT NULL,"
                + "  `capital` INT,"
                + "PRIMARY KEY (`id`)); "
                + "CREATE UNIQUE INDEX `simbolo` ON `tickers` (`name`)";
        
        
        final String CREATE_COLUMN_CAPITAL = "ALTER TABLE `tickers` ADD `capital` INT";

        final String INSERT_DEFAULT_PREFERENCES_RANGO_1 = "insert INTO preferencias VALUES(null,'" + RANGO_1 + "', '13')";
        final String INSERT_DEFAULT_PREFERENCES_RANGO_2 = "insert INTO preferencias VALUES(null,'" + RANGO_2 + "', '26')";
        final String INSERT_DEFAULT_PREFERENCES_RANGO_3 = "insert INTO preferencias VALUES(null,'" + RANGO_3 + "', '52')";
        final String INSERT_DEFAULT_PREFERENCES_RATIO = "insert INTO preferencias VALUES(null,'" + RATIO_REFRESCO + "',1000)";

        Statement stmt;
        try {
            stmt = conn.createStatement();

            // Creation of the tables
            stmt.executeUpdate(CREATE_TABLE_TICKERS);
            stmt.executeUpdate(CREATE_TABLE_PREFERENCE);

            // TICKERS
            stmt.executeUpdate(INSERT_DEFAULT_PREFERENCES_RANGO_1);
            stmt.executeUpdate(INSERT_DEFAULT_PREFERENCES_RANGO_2);
            stmt.executeUpdate(INSERT_DEFAULT_PREFERENCES_RANGO_3);
            stmt.executeUpdate(INSERT_DEFAULT_PREFERENCES_RATIO);

        } catch (SQLException ex) {
            
            if (semaforo.Semaforo.isDebugMode) System.out.println("################################################    ENTRANDO EN ERROR DDBB " + ex.getErrorCode() );
            if (ex.getErrorCode() != 42101) {
                closeConection();
            }
        }
        
        try{
            stmt = conn.createStatement();
             stmt.executeUpdate(CREATE_COLUMN_CAPITAL);
        } catch (SQLException ex) {
            
            if (semaforo.Semaforo.isDebugMode) System.out.println("################################################    ENTRANDO EN ERROR COLUMNA " + ex.getErrorCode() );
            if (ex.getErrorCode() != 42101) {
                closeConection();
            }
        }

    }

    public static void setup(final Settings settings) {

        getConection();
        createrDB();

         try {

                    getConection();
                    ResultSet res1 = query(GET_RANGO_1);
                    res1.next();
                    settings.setVaribable(RANGO_1, Integer.parseInt(res1.getString(VALUE)));

                    getConection();
                    ResultSet res2 = query(GET_RANGO_2);
                    res2.next();
                    settings.setVaribable(RANGO_2, Integer.parseInt(res2.getString(VALUE)));

                    getConection();
                    ResultSet res3 = query(GET_RANGO_3);
                    res3.next();
                    settings.setVaribable(RANGO_3, Integer.parseInt(res3.getString(VALUE)));

                    getConection();
                    ResultSet res4 = query(GET_REFRESH_TIME);
                    res4.next();
                    settings.setVaribable(RATIO_REFRESCO, Integer.parseInt(res4.getString(VALUE)));
                    //  closeConection();
                } catch (SQLException ex) {
                    closeConection();
                    Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
                }

        loadData = true;
        Thread thread = new Thread() {
            public void run() {
                ResultSet resTicekrs = DDBB.Tickers();
                try {
                    while (resTicekrs.next()) {

                        String nameTicker = resTicekrs.getString("name");

                        synchronized (Controller.countLock) {
                            Controller.finish = false;
                        }

                         while (!Controller.conectado) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                        settings.addTicker(nameTicker);

                       
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    loadData = false;
                }

                closeConection();
            }
        };
        thread.start();

    }

    public static void deleteTicker(String ticker) {
        getConection();

        String deleteTicker = "DELETE FROM TICKERS WHERE name = '" + ticker + "'";

        try {
            conn.createStatement().executeUpdate(deleteTicker);
        } catch (SQLException ex) {
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConection();
        }

    }

    public static void insert(String sql) {
        update(sql);
    }

    public static void update(String sql) {
        try {

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            closeConection();
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateTicker(String name, String new_name) {
        final String UPDATE_PREFERENCE = "UPDATE tickers SET name='" + new_name + "' WHERE name='" + name + "'";
        getConection();
        update(UPDATE_PREFERENCE);
        closeConection();
    }
    
//$$$    
    public static void updateTickerCapital(String name, int capital) {
        final String UPDATE_TICKER_CAPITAL = "UPDATE tickers SET capital=" + capital + " WHERE name='" + name + "'";
        getConection();
        update(UPDATE_TICKER_CAPITAL);
        closeConection();
        
        if (semaforo.Semaforo.isDebugMode) System.out.println("################################################################");
        if (semaforo.Semaforo.isDebugMode) System.out.println("################################################################");
        if (semaforo.Semaforo.isDebugMode) System.out.println("################################ updateTickerCapital: Name: " + name + " Capital: " + capital);
    }
    
    public static int requestTickerCapital(String name){
        int capital = 0;
        final String SELECT_TICKER = "SELECT capital from TICKERS WHERE name='" + name + "'";
        
        try {
            getConection();
            ResultSet x = conn.createStatement().executeQuery(SELECT_TICKER);          
            if (x.next()) {
                capital = x.getInt("capital");               
            }
        } catch (SQLException ex) {
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConection();
        }
        System.out.println("################################################################");
        System.out.println("################################################################");
        System.out.println("################################ requestTickerCapital: " + capital);

        return capital;
    }


    public static void updatePreference(String name, String value) {
        final String UPDATE_TICKER = "UPDATE PREFERENCIAS SET value='" + value + "' WHERE name='" + name + "'";
        getConection();
        update(UPDATE_TICKER);
        closeConection();
    }

    
    public static Map<String, ElementoCapitalDB> queryCapital() {
        String SELECT_CAPITAL = "select * from tickers;";
         Map<String, ElementoCapitalDB> valoresTickerCapital = new HashMap<>() ;
         ElementoCapitalDB elemCapitalDB = new ElementoCapitalDB();
         
        try {
            getConection();
            ResultSet result = conn.createStatement().executeQuery(SELECT_CAPITAL);
           // if (result.first()) {
          //      valoresTickerCapital.put(result.getString("name"), result.getInt("capital"));
                while(result.next()){
                     elemCapitalDB = new ElementoCapitalDB();
                     elemCapitalDB.setCapital(result.getInt("capital"));
                     if (semaforo.Semaforo.isDebugMode) System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ "+result.getString("name") + " " + elemCapitalDB.getCapital());
                     elemCapitalDB.setIsChequeado(false);
                     valoresTickerCapital.put(result.getString("name"), elemCapitalDB);
                }
           // }
            
            return valoresTickerCapital;
            
        } catch (SQLException ex) {
            closeConection();
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } 
    }
    
    public static void insertTicker(String name) {
        String INSERT_TICKER = "insert INTO tickers VALUES(null, '" + name + "',0);";
        getConection();
        insert(INSERT_TICKER);
        closeConection();
    }

    public static ResultSet Tickers() {
        String sql = "select name from tickers";

        try {
            getConection();
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException ex) {
            closeConection();
            Logger.getLogger(DDBB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static float getTickerValue(String nameTicker, float ini, float fin) {
        Random r = new Random(System.nanoTime());

        return (r.nextFloat() * (fin - ini)) + ini;
    }

    public static float getTickerHistory(String nameTicker, int weeksAgo) {
        Random r = new Random(System.nanoTime());

        return (float) ((r.nextInt(weeksAgo) * 1.0f) + r.nextFloat() * (r.nextInt(weeksAgo) * 1.0f));
    }
}
