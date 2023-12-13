package main.java.org.Screens;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import main.java.org.InputManagement.InputManager;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.MainFrame.MainFrame;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Rendering.Drawables.SelectionCube;
import main.java.org.UI.MyChangeListener;
import main.java.org.UI.RoundedBorder;
import main.java.org.Updateable.Player;
import main.java.org.World.BlockColours;
import main.java.org.World.BlockTypes;
import main.java.org.World.Chunk;
import main.java.org.Rendering.Drawables.Cube;
import main.java.org.World.ChunkManager;

public class GameScreen extends JPanel {

    private MainFrame mainFrame;

    private Foreground fg;
    private Background bg;

    private ChunkManager chunkManager;

    private Player player;
    public Player getPlayer(){return player;}

    private SelectionCube selectionKuba;

    private static int lastFrameCount=0;
    public static int getFrameCount(){return lastFrameCount;}
    private static int lastPendingChunks=0;
    public static int getPendingCount(){return lastPendingChunks;}
    private static int lastChunkUpdates=0;
    public static int getLastChunkUpdates(){return lastChunkUpdates;}

    //states
    private boolean paused=false;

    public GameScreen(MainFrame mf){
        super();

        this.setLayout(null);

        this.mainFrame=mf;

        fg=new Foreground();
        bg=new Background(this);

        fg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);
        bg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);

        this.add(bg);
        this.add(fg);

        this.chunkManager=new ChunkManager();
        this.player=new Player();


        //---------------------------
        Camera cum=new Camera(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);
        cum.setYaw(30);
        cum.setPitch(-20);
        cum.setFOV(50);
        cum.setPosition(new Vector3(-10,20,-10));
        Camera.main=cum;

        this.selectionKuba=new SelectionCube(new Color(255,255,255));
        this.selectionKuba.setScale(new Vector3(0.5f, 0.5f, 0.5f));
        Camera.main.addDrawable(selectionKuba);
    }

    private int frameCount=0;
    private long lastFrame=0;

    private double lastBlockBreak=0;
    private double lastBlockPlace=0;

    public void frame(double deltaTime){
        if(paused)
            return;

        if(InputManager.ESCAPE){
            pause();
            mainFrame.setVisible(true);
        }


        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        this.repaint();

        if((System.nanoTime()-lastFrame)*0.000000001>1){
            lastFrameCount=frameCount;

            lastChunkUpdates=this.chunkManager.getChunkUpdates();
            this.chunkManager.setChunkUpdates(0);

            lastFrame=System.nanoTime();
            frameCount=0;
        }
        frameCount++;
        lastPendingChunks=this.chunkManager.getPendingCount();
    }

    public void physicsFrame(double deltaTime, boolean chunkLoad){
        if(paused)
            return;

        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        if(chunkLoad){
            this.chunkManager.unloadChunk(chunkPos[0],chunkPos[1]);
            this.chunkManager.loadChunk(chunkPos[0],chunkPos[1], player);
            this.chunkManager.updateChunks(player);
            Vector3 tempVector69=selectionKuba.getPositionByReference().copy();
            selectionKuba.setPosition(Camera.main.getPosition());
            Camera.main.sortDrawables();
            selectionKuba.setPosition(tempVector69);
            return;
        }

        InputManager.fetchMousePosition();

        player.update(deltaTime);
        this.chunkManager.calculatePhysics(deltaTime,chunkPos[0],chunkPos[1]);

        if(!InputManager.MOUSE_LEFT)
            lastBlockBreak=69;
        if(!InputManager.MOUSE_RIGHT)
            lastBlockPlace=69;

        lastBlockBreak+=deltaTime;
        lastBlockPlace+=deltaTime;
        RaycastHit rh=this.chunkManager.gaycast(Camera.main.getPosition(),Camera.main.getForward(),5);
        if(rh!=null){
            Vector3 selectionPosition=new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z);
            selectionKuba.setPosition(selectionPosition);
            int kubaColorInt= BlockColours.blockColours[6*rh.blockType.ordinal()+4].brighter().getRGB();
            kubaColorInt&=0x00FFFFFF;
            kubaColorInt|=0x88000000;
            selectionKuba.setColor(new Color(kubaColorInt));

            if(InputManager.MOUSE_LEFT&&lastBlockBreak>0.2&&rh.blockType!=BlockTypes.BEDROCK){
                lastBlockBreak=0;
                chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, BlockTypes.AIR);
                Chunk chomk;

                if(rh.x==0){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX-1,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.x==15){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX+1,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.z==0){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ-1);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.z==15){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ+1);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }

                chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ);
                if(chomk!=null)
                    chunkManager.reloadChunk(chomk);
            }
            else if(InputManager.MOUSE_RIGHT&&lastBlockPlace>0.2){
                lastBlockPlace=0;
                Vector3 tempDeltaPos=new Vector3(
                        rh.point.get(0)-selectionPosition.get(0),
                        rh.point.get(1)-selectionPosition.get(1),
                        rh.point.get(2)-selectionPosition.get(2)
                );

                float tempMax=Math.abs(tempDeltaPos.get(0));
                int tempMaxIndex=0;
                if(tempMax<Math.abs(tempDeltaPos.get(1))){
                    tempMax=Math.abs(tempDeltaPos.get(1));
                    tempMaxIndex=1;
                }
                if(tempMax<Math.abs(tempDeltaPos.get(2))){
                    tempMax=Math.abs(tempDeltaPos.get(2));
                    tempMaxIndex=2;
                }

                switch(tempMaxIndex){
                    case 0:
                        if(tempDeltaPos.get(0)<0) {
                            rh.x--;
                            if (rh.x < 0) {
                                rh.x = 15;
                                rh.chunkX--;
                            }
                        }
                        else{
                            rh.x++;
                            if (rh.x >15) {
                                rh.x = 0;
                                rh.chunkX++;
                            }
                        }
                        break;

                    case 1:
                        if(tempDeltaPos.get(1)<0) {
                            rh.y--;
                            if (rh.y < 0) {
                                rh.y = 0;
                            }
                        }
                        else{
                            rh.y++;
                            if (rh.y >49) {
                                rh.y = 49;
                            }
                        }
                        break;

                    case 2:
                        if(tempDeltaPos.get(2)<0) {
                            rh.z--;
                            if (rh.z < 0) {
                                rh.z = 15;
                                rh.chunkZ--;
                            }
                        }
                        else{
                            rh.z++;
                            if (rh.z >15) {
                                rh.z = 0;
                                rh.chunkZ++;
                            }
                        }
                        break;
                }

                if(!player.isBlockInPlayer(new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z))){
                        chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, player.getHeldBlock());
                    Chunk chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
            }
        }
        else{
            selectionKuba.setPosition(new Vector3(0,-10000,0));
        }
    }

    public void pause(){
        if(!paused){
            InputManager.showCursor(mainFrame);
            bg.setVisible(false);
            fg.pause(mainFrame,this);
            paused=true;
        }
    }

    public void unpause(){
        if(paused){
            InputManager.hideCursor(mainFrame);
            fg.removeAll();
            fg.setVisible(true);
            bg.setVisible(true);
            InputManager.fetchMousePosition();//visszarakja a kurzort a helyére
            InputManager.fetchMousePosition();//törli az előző visszarakás adatait
            mainFrame.requestFocus();
            paused=false;
        }
    }

    private static class Foreground extends JPanel{

        public Foreground(){
            this.setBackground(new Color(0,0,0,0));
            this.setLayout(new GridLayout(1,1));
            this.setDoubleBuffered(true);
        }

        public void pause(MainFrame mainFrame, GameScreen gameScreen){
            this.removeAll();
            this.add(new PauseMenu(mainFrame,gameScreen));
            this.setVisible(true);
        }

        private static class PauseMenu extends JPanel{
            public PauseMenu(MainFrame mf, GameScreen gameScreen){
                this.setBackground(new Color(0,0,0,100));

                this.setLayout(null);

                Font titleFont=new Font("Arial",Font.BOLD,50);
                Font normalFont=new Font("Arial",Font.BOLD,20);
                int centerX= MainFrame.SCREEN_WIDTH/2;
                int centerY=MainFrame.SCREEN_HEIGHT/2;


                JLabel title=new JLabel("paused",SwingConstants.CENTER);
                title.setBackground(new Color(0,0,0,0));
                title.setForeground(new Color(255,209,0));
                title.setFont(titleFont);
                title.setBounds(centerX-400,centerY-100,800,50);
                this.add(title);

                JButton startButton = new JButton("proceed");
                startButton.setBackground(Color.black);
                startButton.setForeground(Color.white);
                startButton.setFont(normalFont);
                startButton.setContentAreaFilled(false);
                startButton.setBorder(new RoundedBorder(30));
                startButton.setBounds(centerX-100,centerY-5,200,50);
                startButton.addActionListener((a)->{gameScreen.unpause();});
                startButton.addChangeListener(new MyChangeListener(Color.white,new Color(0,255,255),new Color(0,150,150)));
                this.add(startButton);

                JButton settingsButton = new JButton("not proceed");
                settingsButton.setBackground(Color.black);
                settingsButton.setForeground(Color.white);
                settingsButton.setFont(normalFont);
                settingsButton.setContentAreaFilled(false);
                settingsButton.setBorder(new RoundedBorder(30));
                settingsButton.setBounds(centerX-100,centerY+55,200,50);
                settingsButton.addActionListener((a)->{mf.setCurrentState(MainFrame.STATE.MAIN_MENU);});
                settingsButton.addChangeListener(new MyChangeListener(Color.white,new Color(0,255,255),new Color(0,150,150)));
                this.add(settingsButton);
            }

            @Override
            public void paint(Graphics g){
                int centerX= MainFrame.SCREEN_WIDTH/2;
                int centerY=MainFrame.SCREEN_HEIGHT/2;

                Color orgColor=g.getColor();
                g.setColor(Color.black);
                g.fillRoundRect(centerX-200,centerY-150,400,300,40,40);
                g.setColor(Color.white);
                g.drawRoundRect(centerX-200,centerY-150,400,300,40,40);
                g.setColor(orgColor);

                super.paint(g);
            }
        }
    }

    private static class Background extends JPanel{

        private BufferedImage image;
        private Graphics imageGraphics;
        private float[][] depthBuffer;

        private Font font;
        private FontMetrics fontMetrics;

        private GameScreen gameScreen;


        public Background(GameScreen gameScreen){
            this.setBackground(new Color(0,0,0,255));

            this.gameScreen=gameScreen;

            this.font=new Font("Arial",Font.PLAIN,10);

            image=new BufferedImage(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT,BufferedImage.TYPE_INT_RGB);
            imageGraphics=image.getGraphics();
            imageGraphics.setFont(font);

            this.fontMetrics= imageGraphics.getFontMetrics();

            depthBuffer=new float[MainFrame.FRAME_BUFFER_WIDTH][MainFrame.FRAME_BUFFER_HEIGHT];
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=Camera.RENDER_DISTANCE;
            }
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            clearBuffers();

            Camera.main.render(image,depthBuffer);
            gameScreen.player.renderHeldBlock(image,depthBuffer,Camera.main);

            for(int i=MainFrame.FRAME_BUFFER_WIDTH/2-2;i<=MainFrame.FRAME_BUFFER_WIDTH/2;i++){
                for(int j=MainFrame.FRAME_BUFFER_HEIGHT/2-2;j<=MainFrame.FRAME_BUFFER_HEIGHT/2;j++){
                    int temp=image.getRGB(i,j);
                    temp^=0xFFFFFFFF;
                    image.setRGB(i,j,temp);
                }
            }

            //text
            imageGraphics.setColor(Color.white);

            //left side
            Vector3 pos;
            float pitch=0,yaw=0;
            if(Camera.main!=null){
                pos=Camera.main.getPosition();
                pos.set(1,pos.get(1)-0.8f);
                pitch=Camera.main.getPitch();
                yaw=Camera.main.getYaw();
            }
            else
                pos=Vector3.zero;

            String posString="Pos: "+(int)pos.get(0) +", "+(int)pos.get(1) +", "+(int)pos.get(2);
            imageGraphics.drawString(posString,5,10);
            String lookString="Rot: "+((int)(yaw*100))/100.0f +", "+((int)(pitch*100))/100.0f;
            imageGraphics.drawString(lookString,5,20);

            //right side
            String updateString="Updates: "+GameScreen.getLastChunkUpdates();
            imageGraphics.drawString(updateString,MainFrame.FRAME_BUFFER_WIDTH-fontMetrics.stringWidth(updateString)-10,10);
            String pendingString="Pending: "+GameScreen.getPendingCount();
            imageGraphics.drawString(pendingString,MainFrame.FRAME_BUFFER_WIDTH-fontMetrics.stringWidth(pendingString)-10,20);


            //hotbar
            paintHotbar(imageGraphics);

            //draw screen
            g.drawImage(image, 0, 0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, null);
        }

        private void clearBuffers(){
            float renderDistance=Camera.RENDER_DISTANCE;

            clearDepthBuffer(renderDistance);

            imageGraphics.setColor(new Color(Camera.CLEAR_COLOR.getRed(),Camera.CLEAR_COLOR.getGreen(),Camera.CLEAR_COLOR.getBlue(),255));
            imageGraphics.fillRect(0,0,MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);
        }

        private void clearDepthBuffer(float clearValue){
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=clearValue;
            }
        }

        private void paintHotbar(Graphics imageGraphics){
            int slotWidth=40;
            int startX=MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2;
            imageGraphics.setColor(Color.BLACK);
            for(int i=0;i<Player.HOTBAR_SIZE;i++){
                imageGraphics.fillRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-19,slotWidth,slotWidth);
                startX+=slotWidth;
            }
            imageGraphics.setColor(Color.RED);
            imageGraphics.fillRect(
                    MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2+gameScreen.getPlayer().getSelectedHotbarSlot()*slotWidth-1,
                    MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-20,
                    slotWidth+2,
                    slotWidth+2);

            startX=MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2+4;
            for(int i=0;i<Player.HOTBAR_SIZE;i++){
                imageGraphics.setColor(BlockColours.blockColours[6*gameScreen.getPlayer().getHotbarBlock(i).ordinal()]);
                imageGraphics.fillRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-15,slotWidth-8,slotWidth-8);
                startX+=slotWidth;
            }
        }
    }

}
