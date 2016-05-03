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
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import semaforo.Indice;

public class ConsultaIndices {
    
    
    public static final String INDICE_NASDAQ = "NASDAQ";
    public static final String INDICE_SandP = "S&P";
    public static final String INDICE_DJI = "DJI";
    
    public static final int IMG_INDEX_WIDTH = 200;
    public static final int IMG_INDEX_HEIGHT = 120;


    String URL_NASDAQ   = "http://chartapi.finance.yahoo.com/instrument/1.0/%5EIXIC/chartdata;type=quote;range=1d/csv";
    Double peso_NASDAQ  = 1.0;

    String URL_DJI      = "http://chartapi.finance.yahoo.com/instrument/1.0/%5EDJX/chartdata;type=quote;range=1d/csv";
    Double peso_DJI     = 100.0;

    String URL_SAndP    = "http://chartapi.finance.yahoo.com/instrument/1.0/%5EGSPC/chartdata;type=quote;range=1d/csv";
    Double peso_SAndP   = 1.0;

    // IMAGEN GRANDE
    String pathImagenGrandeDJI      = "http://chart.finance.yahoo.com/t?s=%5eDJI&amp;lang=en-US&amp;region=US&amp;width=" + IMG_INDEX_WIDTH + "&amp;height=" + IMG_INDEX_HEIGHT;
    String pathImagenGrandeNASDAQ   = "http://chart.finance.yahoo.com/t?s=%5eIXIC&amp;lang=en-US&amp;region=US&amp;width=" + IMG_INDEX_WIDTH + "&amp;height=" + IMG_INDEX_HEIGHT;
    String pathImagenGrandeSandP    = "http://chart.finance.yahoo.com/t?s=%5eGSPC&amp;lang=en-US&amp;region=US&amp;width=" + IMG_INDEX_WIDTH + "&amp;height=" + IMG_INDEX_HEIGHT;

    // IMAGEN REGULAR
    String pathImagenRegularDJI     = "http://chart.finance.yahoo.com/instrument/1.0/^IXIC/chart;range=1d/image;size=126x60?region=US&amp;lang=en-US&amp;scheme=gsbeta";
    String pathImagenRegularNASDAQ  = "http://chart.finance.yahoo.com/instrument/1.0/^DJI/chart;range=1d/image;size=126x60?region=US&amp;lang=en-US&amp;scheme=gsbeta";
    String pathImagenRegularSandP   = "http://chart.finance.yahoo.com/instrument/1.0/^GSPC/chart;range=1d/image;size=126x60?region=US&amp;lang=en-US&amp;scheme=gsbeta";

    String tamanyoImgIndice = "GRANDE"; // REGULAR
    
    
    


    public Indice ObtenerValorIndice(String indice) {
        String urlIndice = "";
        Double pesoIndice = 0.0;

        switch (indice) {
            case "NASDAQ":
                urlIndice   = URL_NASDAQ;
                pesoIndice  = peso_NASDAQ;
                break;
            case "S&P":
                urlIndice   = URL_SAndP;
                pesoIndice  = peso_SAndP;
                break;
            case "DJI":
                urlIndice   = URL_DJI;
                pesoIndice  = peso_DJI;
                break;
            default:
                System.out.println("Indice no conocido");
                break;
        }
        return ObtenerValorIndiceURL(urlIndice, pesoIndice);
    }

    public Indice ObtenerValorIndiceURL(String URL_Indice, Double peso) {

        URL indiceURL = null;

        try {
            indiceURL = new URL(URL_Indice);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ConsultaIndices.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader br = null;

        try {
            br = new BufferedReader(
                    new InputStreamReader(indiceURL.openStream()));
        } catch (IOException ex) {
            Logger.getLogger(ConsultaIndices.class.getName()).log(Level.SEVERE, null, ex);
        }

        String line = "";
        String cvsSplitBy = ",";
        String[] indice = null;
        String nombre = null;
        String[] cierre = null;
        String cierreStr = null;

        try {

            try {
                for (int i = 0; i < 17; i++) {
                    if ((line = br.readLine()) != null & i == 2) {
                        if (semaforo.Semaforo.isDebugMode) System.out.println("= = = " + line);
                        nombre = line;
                    }
                    if ((i == 8)) {
                        if (semaforo.Semaforo.isDebugMode) System.out.println("= = = " + line);
                       // cierre = line.split(cvsSplitBy);
                        cierre = line.split(":");
                    }
                }
            } catch (Exception e) {
                System.out.println("ObtenerValorIndiceURL ");
                e.printStackTrace();
            }
           // cierreStr = cierre[0].split(":")[1];
                cierreStr = cierre[1];
            
            
            while ((line = br.readLine()) != null) {

                // use comma as separator
                indice = line.split(cvsSplitBy);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Double valorActual = null;
        Double cierreAnterior = null;
        Boolean tendencia;

        try {
            valorActual = peso * Double.parseDouble(indice[4]);
        } catch (Exception e) {
            System.out.println("* * * ERROR PARSEANDO valorActual");
        }
        try {
            cierreAnterior = peso * Double.parseDouble(cierreStr);
        } catch (Exception e) {
            System.out.println("* * * ERROR PARSEANDO cierreAnterior");
        }
        if (valorActual > cierreAnterior) {
            tendencia = Indice.ALZA;
        } else {
            tendencia = Indice.BAJA;
        }
        //System.out.println("ACTUAL " + valorActual + "CIERRE " + cierreAnterior + "TEND " + tendencia);
        Indice indiceNuevo = new Indice("", valorActual, tendencia, cierreAnterior);
        return indiceNuevo;

    }

    public ImageIcon ObtenerImagenIndice(String indice) {
        if (semaforo.Semaforo.isDebugMode) System.out.println("**************  obtener img ind");
        String urlIndice = "";
        switch (indice) {
            case "NASDAQ":
                urlIndice = pathImagenGrandeNASDAQ;
                break;
            case "S&P":
                urlIndice = pathImagenGrandeSandP;
                break;
            case "DJI":
                urlIndice = pathImagenGrandeDJI;
                break;
            default:
                System.out.println("Indice no conocido");
                break;
        }
        return ObtenerImgIndiceURL(urlIndice, tamanyoImgIndice);
    }

    public ImageIcon ObtenerImgIndiceURL(String URL_indice, String tamanyo) {

        ImageIcon imagencita = null;
        BufferedImage image = null;
        BufferedImage image2 = null;
        try {
            URL url = new URL(URL_indice);
            image = ImageIO.read(url);
            //image2 = createResizedCopy(image, 252, 120, false);
            imagencita = new ImageIcon(image);
            //JLabel label = new JLabel(new ImageIcon(image));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return imagencita;
    }

    public BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        if (semaforo.Semaforo.isDebugMode) System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

}
