/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Este objeto es utilizado para la sincronia entre barras de desplazamiento
 *
 * @author Jose Leonardo Jerez
 */
public final class Synchronizer implements AdjustmentListener {

    private JScrollBar firstVerticalScroll;
    private JScrollBar firstHorizontalScroll;
    private JScrollBar secondVerticalScroll;
    private JScrollBar secondHorizontalScroll;
    private JScrollBar thirdVerticalScroll;
    private JScrollBar thirdHorizontalScroll;
    private JScrollBar fourthVerticalScroll;
    private JScrollBar fourthHorizontalScroll;

    public Synchronizer(JScrollPane firstScrollPanel, JScrollPane secondScrollPanel, JScrollPane thirdScrollPanel, JScrollPane fourthScrollPanel) {
        setFirstVerticalScroll(firstScrollPanel.getVerticalScrollBar());
        setFirstHorizontalScroll(firstScrollPanel.getHorizontalScrollBar());
        setSecondVerticalScroll(secondScrollPanel.getVerticalScrollBar());
        setSecondHorizontalScroll(secondScrollPanel.getHorizontalScrollBar());
        setThirdVerticalScroll(thirdScrollPanel.getVerticalScrollBar());
        setThirdHorizontalScroll(thirdScrollPanel.getHorizontalScrollBar());
        setFourthVerticalScroll(fourthScrollPanel.getVerticalScrollBar());
        setFourthHorizontalScroll(fourthScrollPanel.getHorizontalScrollBar());
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent event) {
        JScrollBar scrollBar = (JScrollBar) event.getSource();

        if (scrollBar != null) {
            JScrollBar target = null;
            JScrollBar target1 = null;
            JScrollBar target2 = null;
            if (scrollBar == getFirstVerticalScroll()) {
                target = getSecondVerticalScroll();
                target1 = getThirdVerticalScroll();
                target2 = getFourthVerticalScroll();
            }
            if (scrollBar == getFirstHorizontalScroll()) {
                target = getSecondHorizontalScroll();
                target1 = getThirdHorizontalScroll();
                target2 = getFourthHorizontalScroll();
            }
            if (scrollBar == getSecondVerticalScroll()) {
                target = getFirstVerticalScroll();
                target1 = getThirdVerticalScroll();
                target2 = getFourthVerticalScroll();
            }
            if (scrollBar == getSecondHorizontalScroll()) {
                target = getFirstHorizontalScroll();
                target1 = getThirdHorizontalScroll();
                target2 = getFourthHorizontalScroll();
            }
            if (scrollBar == getThirdVerticalScroll()) {
                target = getFirstVerticalScroll();
                target1 = getSecondVerticalScroll();
                target2 = getFourthVerticalScroll();
                
            }
            if (scrollBar == getThirdHorizontalScroll()) {
                target = getFirstHorizontalScroll();
                target1 = getSecondHorizontalScroll();
                target2 = getFourthHorizontalScroll();
            }
            if (scrollBar == getFourthVerticalScroll()) {
                target = getFirstVerticalScroll();
                target1 = getSecondVerticalScroll();
                target2 = getThirdVerticalScroll();
            }
            if (scrollBar == getFourthHorizontalScroll()) {
                target = getFirstHorizontalScroll();
                target1 = getSecondHorizontalScroll();
                target2 = getThirdHorizontalScroll();
            }
            if (scrollBar.getValue() != 0) {
                target.setValue(scrollBar.getValue());
                target1.setValue(scrollBar.getValue());
                target2.setValue(scrollBar.getValue());
            }
        }
    }

    public JScrollBar getFirstVerticalScroll() {
        return firstVerticalScroll;
    }

    public void setFirstVerticalScroll(JScrollBar firstVerticalScroll) {
        this.firstVerticalScroll = firstVerticalScroll;
    }

    public JScrollBar getFirstHorizontalScroll() {
        return firstHorizontalScroll;
    }

    public void setFirstHorizontalScroll(JScrollBar firstHorizontalScroll) {
        this.firstHorizontalScroll = firstHorizontalScroll;
    }

    public JScrollBar getSecondVerticalScroll() {
        return secondVerticalScroll;
    }

    public void setSecondVerticalScroll(JScrollBar secondVerticalScroll) {
        this.secondVerticalScroll = secondVerticalScroll;
    }

    public JScrollBar getSecondHorizontalScroll() {
        return secondHorizontalScroll;
    }

    public void setSecondHorizontalScroll(JScrollBar secondHorizontalScroll) {
        this.secondHorizontalScroll = secondHorizontalScroll;
    }

    public JScrollBar getThirdVerticalScroll() {
        return thirdVerticalScroll;
    }

    public void setThirdVerticalScroll(JScrollBar thirdVerticalScroll) {
        this.thirdVerticalScroll = thirdVerticalScroll;
    }

    public JScrollBar getThirdHorizontalScroll() {
        return thirdHorizontalScroll;
    }

    public void setThirdHorizontalScroll(JScrollBar thirdHorizontalScroll) {
        this.thirdHorizontalScroll = thirdHorizontalScroll;
    }

    public JScrollBar getFourthVerticalScroll() {
        return fourthVerticalScroll;
    }

    public void setFourthVerticalScroll(JScrollBar fourthVerticalScroll) {
        this.fourthVerticalScroll = fourthVerticalScroll;
    }

    public JScrollBar getFourthHorizontalScroll() {
        return fourthHorizontalScroll;
    }

    public void setFourthHorizontalScroll(JScrollBar fourthHorizontalScroll) {
        this.fourthHorizontalScroll = fourthHorizontalScroll;
    }

}
