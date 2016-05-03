package semaforo;

import com.ib.client.EClientSocket;
//import com.tws.EmptyWrapper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Positions {
    public static void main(String[] args){

        // create connection object for to communicate with TWS
        //EClientSocket eClientSocket = new EClientSocket(new TWSClientInterface());

        // try to connect to TWS
        //eClientSocket.eConnect("127.0.0.1", 4001, 0);

        
        
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Positions.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // request account summary
        kobytest.KobyTest.posiciones();
        //kobytest.Kobytest.posiciones();
    }
}
