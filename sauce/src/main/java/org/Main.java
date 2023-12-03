package main.java.org;

import main.java.org.MainFrame.MainFrame;

import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        MainFrame mf=new MainFrame("Morbius Motorsport 2025 GOTY Edition");
        mf.startGame();
        mf.dispatchEvent(new WindowEvent(mf, WindowEvent.WINDOW_CLOSING));
    }
}