package main.java.org.UI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MyChangeListener implements ChangeListener {

    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;

    public MyChangeListener(Color normalColor, Color hoverColor, Color pressedColor){
        this.normalColor=normalColor;
        this.hoverColor=hoverColor;
        this.pressedColor=pressedColor;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JButton owner=(JButton) e.getSource();
        ButtonModel model=owner.getModel();

        if (model.isRollover()) {
            owner.setForeground(hoverColor);
        } else if (model.isPressed()||model.isArmed()) {
            owner.setForeground(pressedColor);
        } else {
            owner.setForeground(normalColor);
        }
    }
}
