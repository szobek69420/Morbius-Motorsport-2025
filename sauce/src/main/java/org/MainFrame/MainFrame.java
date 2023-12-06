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

    public static int FRAME_BUFFER_WIDTH;
    public static int FRAME_BUFFER_HEIGHT;

    public MainFrame(String name){
        super(name);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH=(int)screenSize.getWidth();
        SCREEN_HEIGHT=(int)screenSize.getHeight();

        FRAME_BUFFER_WIDTH=SCREEN_WIDTH/2;
        FRAME_BUFFER_HEIGHT=SCREEN_HEIGHT/2;

        this.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);

        this.setLayout(new GridLayout(1,1));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void startGame(){
        while(currentState!=STATE.QUIT){
            switch (currentState){
                case GAME:
                    GameScreen gs=new GameScreen();
                    this.add(gs);

                    this.addKeyListener(new InputManager.KeyInput());
                    this.addMouseListener(new InputManager.MouseInput());
                    InputManager.hideCursor(this);

                    this.requestFocus();
                    this.setVisible(true);


                    Thread renderThread=new Thread(()->{
                        double lastFrame=System.nanoTime()*0.000000001;

                        while(currentState==STATE.GAME){
                            if(InputManager.ESCAPE){
                                currentState=STATE.QUIT;
                                System.out.println("gone");
                                break;
                            }
                            //try{Thread.sleep(100);}catch (Exception ex) {System.err.println("huh");}


                            double deltaTime=System.nanoTime()*0.000000001-lastFrame;
                            if(deltaTime<0.05){
                                continue;
                            }

                            lastFrame=System.nanoTime()*0.000000001;
                            if(deltaTime>1.0)
                                System.out.println("render: "+deltaTime);

                            gs.frame(deltaTime);
                        }
                    });

                    Thread physicsThread=new Thread(()->{
                        double lastPhysicsFrame=System.nanoTime()*0.000000001;
                        double lastChunkLoad=0;
                        while(currentState==STATE.GAME){
                            if(InputManager.ESCAPE){
                                currentState=STATE.QUIT;
                                System.out.println("gone2");
                                break;
                            }

                            if(System.nanoTime()*0.000000001-lastChunkLoad>0.05){
                                gs.physicsFrame(0,true);
                                lastChunkLoad=System.nanoTime()*0.000000001;
                                continue;
                            }

                            double deltaTime=System.nanoTime()*0.000000001-lastPhysicsFrame;
                            if(deltaTime<0.016){
                                continue;
                            }

                            lastPhysicsFrame=System.nanoTime()*0.000000001;

                            if(deltaTime>0.2)
                                System.out.println("physics: "+deltaTime);

                            gs.physicsFrame(deltaTime,false);
                        }
                    });

                    renderThread.start();
                    physicsThread.start();

                    try{
                        renderThread.join();
                        physicsThread.join();
                    }
                    catch(InterruptedException ie){
                        System.err.println("something wong with the gaym thredz");
                        System.exit(-420);
                    }

                    break;
            }
        }
    }
}
