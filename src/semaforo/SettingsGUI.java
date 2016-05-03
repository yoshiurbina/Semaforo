/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaforo;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import semaforo.dialog.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import kobytest.KobyTest;

/**
 *
 * @author fernando
 */
public class SettingsGUI extends javax.swing.JFrame {

    private final int RANGE_1 = 0;
    private final int RANGE_2 = 1;
    private final int RANGE_3 = 2;
    private final int REFRESH_TIME = 3;
    private boolean[] canEditTickers = new boolean[]{false};
    UpdateTableListener listener = null;
    private static boolean open = true;
    private LoadingDialog loadingDialog = null;

    /**
     * Creates new form SettingsGUI
     */
    public static class CustomRenderer implements TableCellRenderer {

        TableCellRenderer render;
        Border bWhite;
        Border bGrey;

        public CustomRenderer(TableCellRenderer r, Color top, Color left, Color bottom, Color right) {
            render = r;

            bWhite = BorderFactory.createCompoundBorder();
            bWhite = BorderFactory.createCompoundBorder(bWhite, BorderFactory.createMatteBorder(0, 10, 0, 0, Color.WHITE));

            bGrey = BorderFactory.createCompoundBorder();
            bGrey = BorderFactory.createCompoundBorder(bGrey, BorderFactory.createMatteBorder(0, 10, 0, 0, new Color(232, 232, 232)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JComponent result = (JComponent) render.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (table.isCellSelected(row, column)) {
                result.setBackground(Color.blue);
            } else {
                result.setBackground(new Color(232, 232, 232));
                if (row % 2 == 0) {
                    result.setBackground(java.awt.Color.WHITE);
                }
            }

            if (row % 2 == 0) {
                result.setBorder(bWhite);
            } else {
                result.setBorder(bGrey);
            }

            return result;
        }

    }

    public SettingsGUI(UpdateTableListener listener) {
        this.listener = listener;
        initComponents();
        setup();
    }

    public static boolean openWindow() {
        if (open == true) {
            open = false;
            return true;
        }

        return false;
    }

    TableModelListener lmodel = null;

    public void setup() {
        // Controller.setup();
        Settings settings = Controller.getSettings();

        this.setTitle("Settings");
        URL hj = getClass().getResource("resources/settings.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(hj));

        ButtonSaveTicker.setEnabled(false);
        ButtonDeleteTicker.setEnabled(false);
        ButtonAddTicker.setEnabled(true);
        ButtonCancelTicker.setEnabled(false);
        ButtonSaveChangesVars.setEnabled(false);

        CustomRenderer cr = new CustomRenderer(TableTickers.getDefaultRenderer(Object.class
        ), Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
        TableTickers.setDefaultRenderer(Object.class, cr);
        DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();

        canEditTickers = new boolean[settings.getTickers().size()];

        int tam = model.getRowCount();

        for (int j = tam - 1; j >= 0; j--) {
            model.removeRow(j);
        }

        Object[] o = new Object[1];
        model.getRowCount();
        for (int i = 0;
                i < settings.getTickers()
                .size(); i++) {

            o[0] = settings.getTickers().get(i).getName();

            model.addRow(o);
        }

        //TableTickers.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        TableTickers.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                TableTickers.setCellSelectionEnabled(false);
                if (me.getClickCount() == 2 && (action & EDIT) != EDIT && (action & ADD) != ADD) {
                    // editableTickers(row);
                    selectedRow = row;
                    action = EDIT;
                    TableTickers.setCellSelectionEnabled(true);

                    ButtonCancelTicker.setEnabled(true);
                    ButtonSaveTicker.setEnabled(false);
                    ButtonAddTicker.setEnabled(false);
                    ButtonDeleteTicker.setEnabled(true);
                    // your valueChanged overridden method 
                }

                if (selectedRow > -1) {
                    TableTickers.setCellSelectionEnabled(true);
                    TableTickers.setRowSelectionInterval(selectedRow, selectedRow);
                    TableTickers.getRowCount();
                }
            }

        });

        final JFrame frame = this;

        lmodel = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                if (e.getType() == TableModelEvent.UPDATE && selectedRow >= 0) {
                    // selectedRow = TableTickers.getEditingRow();

                    if ((action & SAVE) == SAVE) {
                        return;
                    }

                    if ((action & ADD) != ADD) {
                        ButtonAddTicker.setEnabled(false);
                        ButtonDeleteTicker.setEnabled(false);
                        ButtonCancelTicker.setEnabled(false);
                        ButtonSaveTicker.setEnabled(true);
                    }
                    /* 
                     JOptionPane.showMessageDialog(frame,
                     "Please wait a moment, Validating ticker ...");*/

                    final String tickerName = (String) TableTickers.getValueAt(selectedRow, 0);

                    boolean error = false;

                    if (tickerName.trim().length() <= 0) {
                        return;
                    }

                    loadingDialog = new LoadingDialog(frame, "Please wait", "Please wait a moment, Validating ticker ...");

                    new Thread() {

                        @Override
                        public void run() {
                            loadingDialog.setVisible(true);
                        }
                    }.start();

                    if (Controller.getSettings().existeTicker(tickerName.toUpperCase().trim())) {
                        JFrame frame2 = new JFrame();
                        JOptionPane.showMessageDialog(frame2, "The ticker " + tickerName + " has already been inserted.");

                        error = true;
                    }

                    if (error) {
                        DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();
                        model.removeRow(selectedRow);
                        // }
                        loadingDialog.setVisible(false);
                        action = NOTHING;
                        selectedRow = -1;
                        TableTickers.setCellSelectionEnabled(false);

                        ButtonCancelTicker.setEnabled(false);
                        ButtonSaveTicker.setEnabled(false);
                        ButtonAddTicker.setEnabled(true);
                        ButtonDeleteTicker.setEnabled(false);
                        noEditableTickers();
                        return;
                    }

                    KobyTest.simbolo_existe(tickerName.toUpperCase().trim(), new KobyTest.ListenerComprobarSimbolo() {
                        String ticker = tickerName;
                        
                        
                        private void ShowMessage(final String message) {
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(null, message);
                                }
                            });
                        }
                        
                        
                        @Override
                        public void callback_simbolo_existe(String simbolo, boolean existe) {
//                            try {
//                                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                                Thread.sleep(1500);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(SettingsGUI.class.getName()).log(Level.SEVERE, null, ex);
//                            }

                            if (ticker.toUpperCase().trim().equals(simbolo.toUpperCase().trim())) {
                                if (existe && (action & EDIT) != EDIT) {
                                    // ButtonEditTicker.setEnabled(false);
                                    loadingDialog.setVisible(false);
                                    ButtonSaveTicker.setEnabled(true);
                                    ButtonAddTicker.setEnabled(false);
                                    ButtonDeleteTicker.setEnabled(false);
                                    ticker = "";
                                } else if (!existe) {
                                    //JFrame frame = new JFrame();
                                    loadingDialog.setVisible(false);
                                    ShowMessage("Sorry, the ticker " + ticker + " has not been found.");
//                                    JOptionPane.showMessageDialog(frame,
//                                            "Sorry, the ticker " + ticker + " has not been found.");
                                    System.out.println("HOLLLLLLLLLLLLLLAAAAAX PAPAX");
                                    //  ButtonEditTicker.setEnabled(true);
                                    ButtonSaveTicker.setEnabled(false);
                                    ButtonAddTicker.setEnabled(true);
                                    ButtonDeleteTicker.setEnabled(false);
                                    ButtonCancelTicker.setEnabled(false);
                                    if ((action & ADD) == ADD) {
                                        DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();
                                        model.removeRow(selectedRow);
                                    }
                                }
                            }
                        }
                    });

                    action |= SAVE;

                } else {
                    return;
                }
            }
        };

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                open = true;
            }
        });

        TableTickers.setRowHeight(30);
        TableTickers.setFont(new Font("Arial", Font.BOLD, 14));
        TableTickers.setCellSelectionEnabled(false);

        TableTickers.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SaveACtion");
        TableTickers.getActionMap().put("SaveACtion", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if ((action & SAVE) != 0) {
                    save();
                }
            }
        });

        rangesEdit = false;
        TableRanges.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Update Time", null}
            },
            new String [] {
                "Variable", "Valor" //, "inv", "cfd", "bought", "rem"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
//        TableRanges.setValueAt(settings.getVaribable(DDBB.RANGO_1), RANGE_1, 1);
//        TableRanges.setValueAt(settings.getVaribable(DDBB.RANGO_2), RANGE_2, 1);
//        TableRanges.setValueAt(settings.getVaribable(DDBB.RANGO_3), RANGE_3, 1);
//        TableRanges.setValueAt(settings.getVaribable(DDBB.RATIO_REFRESCO), REFRESH_TIME, 1);
        TableRanges.setValueAt(settings.getVaribable(DDBB.RATIO_REFRESCO), 0, 1);
        TableRanges.setEnabled(false);
        TableRanges.setRowHeight(30);
        TableRanges.setFont(new Font("Arial", Font.BOLD, 14));
        TableRanges.setCellSelectionEnabled(false);

        
        
        TableRanges.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (rangesEdit) {
                    ButtonSaveChangesVars.setEnabled(true);
                }
            }
        });

        rangesEdit = true;
    }

    boolean rangesEdit = false;

    public SettingsGUI() {
        this(null);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelTickers = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableTickers = new javax.swing.JTable();
        ButtonDeleteTicker = new javax.swing.JButton();
        ButtonSaveTicker = new javax.swing.JButton();
        ButtonAddTicker = new javax.swing.JButton();
        ButtonCancelTicker = new javax.swing.JButton();
        PanelRanges = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableRanges = new javax.swing.JTable();
        ButtonSaveChangesVars = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        PanelTickers.setBackground(new java.awt.Color(0, 0, 0));
        PanelTickers.setBorder(javax.swing.BorderFactory.createTitledBorder("Tickers"));
        PanelTickers.setForeground(new java.awt.Color(204, 204, 204));

        TableTickers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ticker"
            }
        ) {

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEditTickers [rowIndex];
            }
        });
        TableTickers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(TableTickers);

        ButtonDeleteTicker.setText("Remove Ticker");
        ButtonDeleteTicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonDeleteTickerActionPerformed(evt);
            }
        });

        ButtonSaveTicker.setBackground(new java.awt.Color(0, 0, 0));
        ButtonSaveTicker.setText("Save Changes");
        ButtonSaveTicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonSaveTickerActionPerformed(evt);
            }
        });

        ButtonAddTicker.setBackground(new java.awt.Color(0, 0, 0));
        ButtonAddTicker.setForeground(new java.awt.Color(204, 204, 204));
        ButtonAddTicker.setText("Add Ticker");
        ButtonAddTicker.setToolTipText("Add a new ticker");
        ButtonAddTicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonAddTickerActionPerformed(evt);
            }
        });

        ButtonCancelTicker.setText("Cancel");
        ButtonCancelTicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCancelTickerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelTickersLayout = new javax.swing.GroupLayout(PanelTickers);
        PanelTickers.setLayout(PanelTickersLayout);
        PanelTickersLayout.setHorizontalGroup(
            PanelTickersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTickersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ButtonSaveTicker, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonAddTicker, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonDeleteTicker, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonCancelTicker, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        PanelTickersLayout.setVerticalGroup(
            PanelTickersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTickersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelTickersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonSaveTicker)
                    .addComponent(ButtonAddTicker)
                    .addComponent(ButtonCancelTicker)
                    .addComponent(ButtonDeleteTicker))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        PanelRanges.setBorder(javax.swing.BorderFactory.createTitledBorder("Rangos"));

        TableRanges.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Range 1", ""},
                {"Range 2", null},
                {"Range 3", null},
                {"Update Time", null}
            },
            new String [] {
                "Variable", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TableRanges.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(TableRanges);

        ButtonSaveChangesVars.setText("Save Changes");
        ButtonSaveChangesVars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonSaveChangesVarsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelRangesLayout = new javax.swing.GroupLayout(PanelRanges);
        PanelRanges.setLayout(PanelRangesLayout);
        PanelRangesLayout.setHorizontalGroup(
            PanelRangesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRangesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRangesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(ButtonSaveChangesVars, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelRangesLayout.setVerticalGroup(
            PanelRangesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRangesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ButtonSaveChangesVars)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelTickers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(PanelRanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PanelRanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelTickers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void noEditableTickers() {
        canEditTickers = new boolean[TableTickers.getRowCount()];

    }

    private void editableTickers(int pos) {
        canEditTickers[pos] = true;
    }
    int selectedRow = -1;

    private void modifyTicker(int pos) {

        if (TableTickers.isEditing()) {
            TableTickers.getCellEditor().stopCellEditing();
        }

        String ticker_name = (String) TableTickers.getModel().getValueAt(pos, 0);

        Settings settings = Controller.getSettings();
        settings.getTickers().get(pos).setName(ticker_name);

        Thread thread = new Thread() {
            public void run() {
                if (listener != null) {
                    while (!listener.canUpdate()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SettingsGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    listener.stopThread();

                    listener.updateTickers();
                }
            }
        };

        thread.start();
    }

    private void addTicker(int pos) {
        final DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();

        if (TableTickers.isEditing()) {
            TableTickers.getCellEditor().stopCellEditing();
        }

        final String ticker_name = (String) TableTickers.getModel().getValueAt(pos, 0);
        final Settings settings = Controller.getSettings();

        Thread thread = new Thread() {
            public void run() {
                if (listener != null) {
                    while (!listener.canUpdate()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SettingsGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    listener.stopThread();

                    settings.addTicker(ticker_name.toUpperCase().trim());
                    DDBB.insertTicker(ticker_name.toUpperCase().trim());
                    listener.addTickers();
                }
            }
        };

        thread.start();
    }

    private final int NOTHING = 0;
    private final int EDIT = 1;
    private final int ADD = 2;
    private final int MODIFY = 4;
    private final int REMOVE = 8;
    private final int SAVE = 16;

    private int action = NOTHING;

    private void save() {
        int auxPos = 0;
        //selectedRow = ((auxPos = TableTickers.getEditingRow()) > -1) ? auxPos : selectedRow;

        final String tickerName = (String) TableTickers.getValueAt(selectedRow, 0);

        if ((action & ADD) == ADD) {
            addTicker(selectedRow);
        } else if ((action & EDIT) == EDIT) {
            modifyTicker(selectedRow);
        }

        TableTickers.setValueAt(tickerName.toUpperCase().trim(), selectedRow, 0);
        action = SAVE;
        TableTickers.setCellSelectionEnabled(false);
        noEditableTickers();

        ButtonCancelTicker.setEnabled(false);
        ButtonSaveTicker.setEnabled(false);
        ButtonAddTicker.setEnabled(true);
        ButtonDeleteTicker.setEnabled(false);
    }

    private void ButtonSaveTickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSaveTickerActionPerformed
        // TODO add your handling code here:
        //  Controller.setup();
        save();

        noEditableTickers();
        selectedRow = -1;
    }//GEN-LAST:event_ButtonSaveTickerActionPerformed

    private void ButtonAddTickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAddTickerActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();
        int numRows = TableTickers.getRowCount();
        canEditTickers = new boolean[numRows + 1];
        editableTickers(numRows);
        TableTickers.setEditingRow(numRows);
        selectedRow = numRows;
        // TableTickers.setE
        action = ADD;
        Object[] o = new Object[1];
        o[0] = "";
        //o[1] = new jB

        model.addRow(o);

        TableTickers.requestFocus();
        TableTickers.editCellAt(model.getRowCount() - 1, 0);

        ButtonCancelTicker.setEnabled(true);
        ButtonSaveTicker.setEnabled(false);
        ButtonAddTicker.setEnabled(false);
        ButtonDeleteTicker.setEnabled(false);
        TableTickers.getModel().addTableModelListener(lmodel);

    }//GEN-LAST:event_ButtonAddTickerActionPerformed

    private void ButtonSaveChangesVarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSaveChangesVarsActionPerformed
        Settings settings = Controller.getSettings();
        int r1 = 0, r2 = 0, r3 = 0, time = 0;

        if (TableRanges.isEditing()) {
            TableRanges.getCellEditor().stopCellEditing();
        }

        Object range1 = TableRanges.getValueAt(RANGE_1, 1);
        Object range2 = TableRanges.getValueAt(RANGE_2, 1);
        Object range3 = TableRanges.getValueAt(RANGE_3, 1);
        Object refresh_time = TableRanges.getValueAt(REFRESH_TIME, 1);

        Frame frame = new Frame();

        try {
            if (range1 instanceof String) {
                r1 = Integer.parseInt((String) range1);
            } else {
                r1 = (Integer) range1;
            }

            if ((r1 <= 0) || (r1 > 52)) {
                JOptionPane.showMessageDialog(frame,
                        "Range 1 must be bigger than 0 and lower than 53 weeeks.",
                        "Bounds Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Range 1 must a number between 1 and 52",
                    "Type Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (range2 instanceof String) {
                r2 = Integer.parseInt((String) range2);
            } else {
                r2 = (Integer) range2;
            }

            if ((r2 <= 0) || (r2 > 52)) {
                JOptionPane.showMessageDialog(frame,
                        "Range 2 must be bigger than 0 and lower than 53 weeeks.",
                        "Bounds Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Range 2 must a number between 1 and 52",
                    "Type Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (range3 instanceof String) {
                r3 = Integer.parseInt((String) range3);
            } else {
                r3 = (Integer) range3;
            }

            if ((r3 <= 0) || (r3 > 52)) {
                JOptionPane.showMessageDialog(frame,
                        "Range 3 must be bigger than 0 and lower than 53 weeeks.",
                        "Bounds Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Range 3 must a number between 1 and 52",
                    "Type Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (refresh_time instanceof String) {
                time = Integer.parseInt((String) refresh_time);
            } else {
                time = (Integer) refresh_time;
            }

            if (time <= 0) {
                JOptionPane.showMessageDialog(frame,
                        "Update Time must be bigger than 0 miliseconds.",
                        "Bounds Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Update Time must a number bigger than 0",
                    "Type Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (r1 != 0 && r2 != 0 && r3 != 0 && time
                != 0) {
            settings.setVaribable(DDBB.RANGO_1, r1);
            settings.setVaribable(DDBB.RANGO_2, r2);
            settings.setVaribable(DDBB.RANGO_3, r3);
            settings.setVaribable(DDBB.RATIO_REFRESCO, time);

            listener.updateVariables();
            ButtonSaveChangesVars.setEnabled(false);
        }
    }//GEN-LAST:event_ButtonSaveChangesVarsActionPerformed

    private void ButtonDeleteTickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonDeleteTickerActionPerformed
        // final int selectedRow = TableTickers.getSelectedRow();
        final DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();
        action = NOTHING;
        TableTickers.setCellSelectionEnabled(false);

        if (selectedRow > -1) {
            model.removeRow(selectedRow);
//$$$ rrefresca el modelo por remover la fila
            //model.fireTableDataChanged();
            model.fireTableRowsDeleted(selectedRow, selectedRow);
            
            
            Settings settings = Controller.getSettings();
//            KobyTest.getHistorico_valores_cumulativo().remove(settings.getTickers().get(selectedRow).getName());
            KobyTest.cancel_market_data_wrapper(settings.getTickers().get(selectedRow).getName());
            settings.removeTicker(selectedRow);
            noEditableTickers();
            Thread thread = new Thread() {
                public void run() {
                    if (listener != null) {
                        while (!listener.canUpdate()) {
                            try {
                                Thread.sleep(50);

                            } catch (InterruptedException ex) {
                                Logger.getLogger(SettingsGUI.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        listener.stopThread();

                        listener.reomveTicker(selectedRow);
                        selectedRow = -1;
                    }
                }
            };

            thread.start();
        }

        // ButtonEditTicker.setEnabled(true);
        ButtonSaveTicker.setEnabled(false);
        ButtonAddTicker.setEnabled(true);
        ButtonDeleteTicker.setEnabled(false);
        ButtonCancelTicker.setEnabled(false);
    }//GEN-LAST:event_ButtonDeleteTickerActionPerformed

    private void ButtonCancelTickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCancelTickerActionPerformed

        DefaultTableModel model = (DefaultTableModel) TableTickers.getModel();
        model.removeTableModelListener(lmodel);
        TableTickers.setCellSelectionEnabled(false);
        //TableTickers. editCellAt(model.getRowCount() - 1, 0);
        if ((action & ADD) == ADD) {

         
            model.removeRow(TableTickers.getRowCount() - 1);

        }

        TableTickers.repaint();
        action = NOTHING;
        selectedRow = -1;
        TableTickers.setCellSelectionEnabled(false);

        ButtonCancelTicker.setEnabled(false);
        ButtonSaveTicker.setEnabled(false);
        ButtonAddTicker.setEnabled(true);
        ButtonDeleteTicker.setEnabled(false);
        noEditableTickers();

        if (loadingDialog != null) {
            loadingDialog.setVisible(false);
        }
    }//GEN-LAST:event_ButtonCancelTickerActionPerformed

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
     
     java.awt.EventQueue.invokeLater(new Runnable() {
     public void run() {
     new SettingsGUI().setVisible(true);
     }
     });
     }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonAddTicker;
    private javax.swing.JButton ButtonCancelTicker;
    private javax.swing.JButton ButtonDeleteTicker;
    private javax.swing.JButton ButtonSaveChangesVars;
    private javax.swing.JButton ButtonSaveTicker;
    private javax.swing.JPanel PanelRanges;
    private javax.swing.JPanel PanelTickers;
    private javax.swing.JTable TableRanges;
    private javax.swing.JTable TableTickers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
