package main.java.org.MainFrame;

import main.java.org.InputManagement.InputManager;
import main.java.org.Resizable.Resizable;
import main.java.org.Screens.GameScreen;
import main.java.org.Screens.SettingsScreen;
import main.java.org.Screens.WelcomeScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame {

    public static enum STATE{
        MAIN_MENU,
        SETTINGS,
        GAME,
        QUIT
    };

    private STATE currentState=STATE.MAIN_MENU;
    private Resizable currentContent=null;

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
        this.setSize((int)screenSize.getWidth()-100,(int)screenSize.getHeight()-100);

        SCREEN_WIDTH=this.getWidth();
        SCREEN_HEIGHT=this.getHeight();

        FRAME_BUFFER_WIDTH=SCREEN_WIDTH/2;
        FRAME_BUFFER_HEIGHT=SCREEN_HEIGHT/2;

        InputManager.setMouseAnchor(this.getX()+SCREEN_WIDTH/2,this.getY()+SCREEN_HEIGHT/2);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                SCREEN_WIDTH=componentEvent.getComponent().getWidth();
                SCREEN_HEIGHT=componentEvent.getComponent().getHeight();

                FRAME_BUFFER_WIDTH=SCREEN_WIDTH/2;
                FRAME_BUFFER_HEIGHT=SCREEN_HEIGHT/2;

                InputManager.setMouseAnchor(componentEvent.getComponent().getX()+SCREEN_WIDTH/2,componentEvent.getComponent().getY()+SCREEN_HEIGHT/2);

                if(currentContent!=null)
                    currentContent.onResize();
            }
        });


        this.setLayout(new GridLayout(1,1));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void startGame(){
        while(currentState!=STATE.QUIT){
            switch (currentState){
                case MAIN_MENU:
                    WelcomeScreen ws=new WelcomeScreen(this);
                    this.currentContent=ws;
                    this.add(ws);
                    this.setVisible(true);

                    try{
                        while(currentState==STATE.MAIN_MENU)
                            Thread.sleep(50);
                    }
                    catch(InterruptedException ie){
                        this.remove(ws);
                        this.setVisible(true);
                    }

                    this.currentContent=null;
                    this.remove(ws);
                    this.setVisible(true);

                    break;

                case SETTINGS:
                    SettingsScreen ss=new SettingsScreen(this);
                    this.currentContent=ss;
                    this.add(ss);
                    this.setVisible(true);

                    try{
                        while(currentState==STATE.SETTINGS)
                            Thread.sleep(50);
                    }
                    catch(InterruptedException ie){
                        this.remove(ss);
                        this.setVisible(true);
                    }

                    this.currentContent=null;
                    this.remove(ss);
                    this.setVisible(true);

                    break;

                case GAME:
                    this.setResizable(false);

                    GameScreen gs=new GameScreen(this);
                    this.add(gs);

                    KeyListener kl= new InputManager.KeyInput();
                    this.addKeyListener(kl);
                    MouseListener ml=new InputManager.MouseInput();
                    this.addMouseListener(ml);
                    MouseWheelListener mwl=new InputManager.MouseWheelInput();
                    this.addMouseWheelListener(mwl);

                    FocusListener fl=new FocusListener() {
                        public void focusLost(FocusEvent e) {gs.pause(true);}
                        public void focusGained(FocusEvent e) {}
                    };
                    this.addFocusListener(fl);

                    InputManager.hideCursor(this);

                    this.requestFocus();
                    this.setVisible(true);


                    Thread renderThread=new Thread(()->{
                        double lastFrame=System.nanoTime()*0.000000001;

                        while(currentState==STATE.GAME){
                            //try{Thread.sleep(100);}catch (Exception ex) {System.err.println("huh");}


                            double deltaTime=System.nanoTime()*0.000000001-lastFrame;
                            if(deltaTime<0.0167){
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

                    this.remove(gs);

                    this.removeKeyListener(kl);
                    this.removeMouseListener(ml);
                    this.removeMouseWheelListener(mwl);
                    this.removeFocusListener(fl);

                    this.setResizable(true);
                    this.setVisible(true);

                    break;
            }
        }
    }
}
