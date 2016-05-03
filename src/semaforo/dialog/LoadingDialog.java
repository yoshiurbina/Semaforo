/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo.dialog;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.Box;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.CENTER;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;;

/**
 *
 * @author fernando
 */
public class LoadingDialog extends JDialog {

    public LoadingDialog(Frame parent, String title, String text) {
        super(parent);

        initUI(title, text);
    }

    private void initUI(String title, String text) {

        URL hj = getClass().getResource("wait2.gif");
        //setIconImage(Toolkit.getDefaultToolkit().getImage(hj));
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(hj));
        JLabel label = new JLabel(icon);

        JLabel name = new JLabel(text);
        //name.setFont(new Font("Serif", Font.BOLD, 13));

        
        
        
        
        
//        JButton btn = new JButton("OK");
//        btn.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent event) {
//                dispose();
//            }
//        });
        
        
        
        
        

        createLayout(name, label);

        setModalityType(ModalityType.MODELESS);

        setTitle(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setLocationRelativeTo(getParent());
        

    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(CENTER)
                .addComponent(arg[0])
                .addComponent(arg[1])
               // .addComponent(arg[2])
                .addGap(200)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(30)
                .addComponent(arg[0])
                .addGap(20)
                .addComponent(arg[1])
                .addGap(20)
               // .addComponent(arg[2])
              //  .addGap(30)
        );

        pack();
    }
}
