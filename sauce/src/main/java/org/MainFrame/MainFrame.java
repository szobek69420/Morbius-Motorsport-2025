package main.java.org.MainFrame;

import main.java.org.InputManagement.InputManager;
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

    public void setCurrentState(STATE state){
        currentState=state;
    }

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public MainFrame(String name){
        super(name);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH=(int)screenSize.getWidth();
        SCREEN_HEIGHT=(int)screenSize.getHeight();

        this.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);

        this.setLayout(new GridLayout(1,1));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void startGame(){
        while(currentState!=STATE.QUIT){
            switch (currentState){
                case GAME:
                    GameScreen gs=new GameScreen(SCREEN_WIDTH,SCREEN_HEIGHT);
                    this.add(gs);

                    this.addKeyListener(new InputManager.KeyInput());
                    this.addMouseListener(new InputManager.MouseInput());
                    InputManager.hideCursor(this);

                    this.requestFocus();
                    this.setVisible(true);

                    double lastFrame=System.nanoTime()*0.000000001;
                    double lastChunkLoad=0;
                    while(currentState==STATE.GAME){
                        if(InputManager.ESCAPE){
                            currentState=STATE.QUIT;
                            System.out.println("broken");
                            break;
                        }
                        //try{Thread.sleep(100);}catch (Exception ex) {System.err.println("huh");}

                        if(System.nanoTime()*0.000000001-lastChunkLoad>0.05){
                            gs.frame(0,true);
                            lastChunkLoad=System.nanoTime()*0.000000001;
                            continue;
                        }

                        double deltaTime=System.nanoTime()*0.000000001-lastFrame;
                        if(deltaTime<0.0016){
                            continue;
                        }

                        lastFrame+=deltaTime;

                        gs.frame(deltaTime,false);
                    }
                    break;
            }
        }
    }
}
