package main.java.org.MainFrame;

import main.java.org.Screens.GameScreen;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    public static enum STATE{
        MAIN_MENU,
        GAME,
        QUIT
    };

    private STATE currentState=STATE.GAME;

    private int width;
    private int height;

    public MainFrame(String name){
        super(name);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.width=(int)screenSize.getWidth();
        this.height=(int)screenSize.getHeight();

        this.setSize(this.width,this.height);

        this.setLayout(new GridLayout(1,1));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void startGame(){
        while(currentState!=STATE.QUIT){
            switch (currentState){
                case GAME:
                    GameScreen gs=new GameScreen(this.width,this.height);
                    this.add(gs);
                    this.setVisible(true);
                    while(currentState==STATE.GAME){
                        try{Thread.sleep(100);}catch (Exception ex) {System.err.println("huh");}
                        gs.frame();
                    }
                    break;
            }
        }
    }
}
