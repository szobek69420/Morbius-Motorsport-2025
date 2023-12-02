package main.java.org.Screens;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import main.java.org.Rendering.Camera.Camera;

public class GameScreen extends JPanel {

    private int width;
    private int height;

    private Foreground fg;
    private Background bg;

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

        //---------------------------

        Camera cum=new Camera(this.width,this.height);
        Camera.main=cum;
    }

    public void frame(){
        bg.repaint();
        fg.repaint();
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

        private static Color CLEAR_COLOR=new Color(255,0,0);

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

            g.drawImage(image,0,0,this);
        }

        private void clearBuffers(){
            float renderDistance=Camera.RENDER_DISTANCE;

            for(int i=0;i<this.width;i++){
                for(int j=0;j<this.height;j++)
                    depthBuffer[i][j]=renderDistance;
            }

            imageGraphics.setColor(CLEAR_COLOR);
            imageGraphics.fillRect(0,0,this.width,this.height);
        }
    }
}
