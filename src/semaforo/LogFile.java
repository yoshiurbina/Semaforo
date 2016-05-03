/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author LCAAMAÑO
 */
public class LogFile {
    
    public static void logFile(String args) {
        /*
        BufferedWriter out = null;
        try {
            java.util.Date fecha = new Date();
            //System.out.println (fecha);
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            //File logFile = new File(timeLog);
            FileWriter fstream;
            fstream = new FileWriter("ARCHIVO_LOG.TXT",true);
            out = new BufferedWriter(fstream);
            out.write("LOG: => " + fecha + " :: " + args );
            //writer.append("Hello world!");
            out.newLine();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                if(out != null){
                    out.close();
                }
            } catch (Exception e) {
            }
        }
        */
    }
}