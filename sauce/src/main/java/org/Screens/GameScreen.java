package main.java.org.Screens;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import main.java.org.InputManagement.InputManager;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Rendering.Drawables.SelectionCube;
import main.java.org.Updateable.Player;
import main.java.org.World.BlockTypes;
import main.java.org.World.Chunk;
import main.java.org.Rendering.Drawables.Cube;
import main.java.org.World.ChunkManager;

public class GameScreen extends JPanel {

    private int width;
    private int height;

    private Foreground fg;
    private Background bg;

    private ChunkManager chunkManager;

    private Player player;

    private SelectionCube selectionKuba;

    public GameScreen(int width, int height){
        super();

        this.width=width;
        this.height=height;

        this.setLayout(null);

        fg=new Foreground();
        bg=new Background(this.width,this.height);

        fg.setBounds(0,0,this.width,this.height);
        bg.setBounds(0,0,this.width,this.height);

        this.add(fg);
        this.add(bg);

        this.chunkManager=new ChunkManager();
        this.player=new Player();


        //---------------------------
        Camera cum=new Camera(this.width,this.height);
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

    public void frame(double deltaTime, boolean chunkLoad){
        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        if(chunkLoad){
            this.chunkManager.unloadChunk(chunkPos[0],chunkPos[1]);
            this.chunkManager.loadChunk(chunkPos[0],chunkPos[1], player);
        }
        else{
            InputManager.fetchMousePosition();

            player.update(deltaTime);
            this.chunkManager.calculatePhysics(deltaTime,chunkPos[0],chunkPos[1]);

            lastBlockBreak+=deltaTime;
            RaycastHit rh=this.chunkManager.gaycast(Camera.main.getPosition(),Camera.main.getForward(),5);
            if(rh!=null){
                selectionKuba.setPosition(new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z));

                if(InputManager.MOUSE_LEFT&&lastBlockBreak>0.1){
                    lastBlockBreak=0;
                    chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, BlockTypes.AIR);
                    chunkManager.reloadChunk(rh.chunkX,rh.chunkZ,player);
                }
            }
            else{
                selectionKuba.setPosition(new Vector3(0,-10000,0));
            }

            bg.repaint();
            fg.repaint();

            if((System.nanoTime()-lastFrame)*0.000000001>1){
                System.out.println(frameCount);

                lastFrame=System.nanoTime();
                frameCount=0;
            }
            frameCount++;
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


        private int width;
        private int height;

        private BufferedImage image;
        private Graphics imageGraphics;
        private float[][] depthBuffer;



        public Background(int width, int height){
            this.setBackground(new Color(0,0,0,255));

            this.width=width;
            this.height=height;

            image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            imageGraphics=image.getGraphics();

            depthBuffer=new float[this.width][this.height];
            for(int i=0;i<width;i++){
                for(int j=0;j<height;j++)
                    depthBuffer[i][j]=100;
            }
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);

            clearBuffers();

            Camera.main.render(image,depthBuffer);

            g.drawImage(image,0,0,this);
        }

        private void clearBuffers(){
            float renderDistance=Camera.RENDER_DISTANCE;

            clearDepthBuffer(renderDistance);

            imageGraphics.setColor(Camera.CLEAR_COLOR);
            imageGraphics.fillRect(0,0,this.width,this.height);
        }

        private void clearDepthBuffer(float clearValue){
            for(int i=0;i<this.width;i++){
                for(int j=0;j<this.height;j++)
                    depthBuffer[i][j]=clearValue;
            }
        }
    }
}
