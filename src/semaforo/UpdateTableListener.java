/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

/**
 *
 * @author fernando
 */
interface UpdateTableListener {
    public void updateTickers();
    public void updateVariables();
    public void reomveTicker(int index);
    public void addTickers();
    public boolean canUpdate();
    public void stopThread();
}
