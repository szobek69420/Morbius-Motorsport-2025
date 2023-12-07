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
import main.java.org.Updateable.Player;
import main.java.org.World.BlockTypes;
import main.java.org.World.Chunk;
import main.java.org.Rendering.Drawables.Cube;
import main.java.org.World.ChunkManager;

public class GameScreen extends JPanel {

    private Foreground fg;
    private Background bg;

    private ChunkManager chunkManager;

    private Player player;

    private SelectionCube selectionKuba;



    public GameScreen(){
        super();

        this.setLayout(null);

        fg=new Foreground();
        bg=new Background();

        fg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);
        bg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);

        this.add(fg);
        this.add(bg);

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
        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        bg.repaint();
        fg.repaint();

        if((System.nanoTime()-lastFrame)*0.000000001>1){
            System.out.println("fps: "+frameCount);

            lastFrame=System.nanoTime();
            frameCount=0;
        }
        frameCount++;
    }

    public void physicsFrame(double deltaTime, boolean chunkLoad){
        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        if(chunkLoad){
            this.chunkManager.unloadChunk(chunkPos[0],chunkPos[1]);
            this.chunkManager.loadChunk(chunkPos[0],chunkPos[1], player);
            this.chunkManager.updateChunks(player);
            Vector3 tempVector69=selectionKuba.getPositionByReference().copy();
            selectionKuba.setPosition(Vector3.zero);
            Camera.main.sortDrawables();
            selectionKuba.setPosition(tempVector69);
            return;
        }

        InputManager.fetchMousePosition();

        player.update(deltaTime);
        this.chunkManager.calculatePhysics(deltaTime,chunkPos[0],chunkPos[1]);

        lastBlockBreak+=deltaTime;
        lastBlockPlace+=deltaTime;
        RaycastHit rh=this.chunkManager.gaycast(Camera.main.getPosition(),Camera.main.getForward(),5);
        if(rh!=null){
            Vector3 selectionPosition=new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z);
            selectionKuba.setPosition(selectionPosition);

            if(InputManager.MOUSE_LEFT&&lastBlockBreak>0.1){
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
                    chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, BlockTypes.YELLOW);
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

    private static class Foreground extends JPanel{
        public Foreground(){
            this.setBackground(new Color(0,0,0,0));
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);
        }
    }

    private static class Background extends JPanel{

        private BufferedImage image;
        private Graphics imageGraphics;
        private float[][] depthBuffer;

        private Font font;


        public Background(){
            this.setBackground(new Color(0,0,0,255));

            this.font=new Font("Arial",Font.PLAIN,20);

            image=new BufferedImage(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT,BufferedImage.TYPE_INT_RGB);
            imageGraphics=image.getGraphics();
            imageGraphics.setFont(font);

            depthBuffer=new float[MainFrame.FRAME_BUFFER_WIDTH][MainFrame.FRAME_BUFFER_HEIGHT];
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=Camera.RENDER_DISTANCE;
            }
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);

            clearBuffers();

            Camera.main.render(image,depthBuffer);

            for(int i=MainFrame.FRAME_BUFFER_WIDTH/2-2;i<=MainFrame.FRAME_BUFFER_WIDTH/2;i++){
                for(int j=MainFrame.FRAME_BUFFER_HEIGHT/2-2;j<=MainFrame.FRAME_BUFFER_HEIGHT/2;j++){
                    int temp=image.getRGB(i,j);
                    temp^=0xFFFFFFFF;
                    image.setRGB(i,j,temp);
                }
            }

            imageGraphics.setColor(Color.black);
            imageGraphics.drawString("Press ESC to quit",10,20);

            g.drawImage(image, 0, 0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, null);
        }

        private void clearBuffers(){
            float renderDistance=Camera.RENDER_DISTANCE;

            clearDepthBuffer(renderDistance);

            imageGraphics.setColor(new Color(Camera.CLEAR_COLOR.getRed(),Camera.CLEAR_COLOR.getGreen(),Camera.CLEAR_COLOR.getBlue(),200));
            imageGraphics.fillRect(0,0,MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);
        }

        private void clearDepthBuffer(float clearValue){
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=clearValue;
            }
        }
    }

}
