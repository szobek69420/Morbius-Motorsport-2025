package main.java.org;

import main.java.org.MainFrame.MainFrame;

import java.awt.*;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        Color color=new Color(234,255,21);
        int r=(color.getRGB()&0x00FF0000)>>16;
        int g=(color.getRGB()&0x0000FF00)>>8;
        int b=(color.getRGB()&0x000000FF);

        System.out.println(r+" "+g+" "+b);

        MainFrame mf=new MainFrame("Morbius Motorsport 2025 GOTY Edition");
        mf.startGame();
        mf.dispatchEvent(new WindowEvent(mf, WindowEvent.WINDOW_CLOSING));
    }
}