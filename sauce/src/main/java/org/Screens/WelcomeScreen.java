package main.java.org.Screens;

import main.java.org.MainFrame.MainFrame;
import main.java.org.Resizable.Resizable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class WelcomeScreen extends JPanel implements Resizable {

    private Font titleFont;
    private Font numberFont;
    private Font normalFont;

    private MainFrame mainFrame;

    public WelcomeScreen(MainFrame mf){
        this.titleFont=new Font("Arial",Font.BOLD,50);
        this.numberFont=new Font("Arial",Font.BOLD,150);
        this.normalFont=new Font("Arial",Font.BOLD,20);

        this.mainFrame=mf;

        this.setLayout(null);

        this.setBackground(new Color(0,0,0));

        fillScreen();

        this.setVisible(true);
    }

    private void fillScreen(){
        this.removeAll();

        int centerX= MainFrame.SCREEN_WIDTH/2;
        int centerY=MainFrame.SCREEN_HEIGHT/2;

        Color transparent =new Color(0,0,0,0);


        JLabel title=new JLabel("MORBIJUS MODORSBOR",SwingConstants.CENTER);
        title.setBackground(transparent);
        title.setForeground(new Color(255,209,0));
        title.setFont(titleFont);
        title.setBounds(centerX-400,centerY-300,800,50);
        this.add(title);

        JLabel titleNumber=new JLabel("2025",SwingConstants.CENTER);
        titleNumber.setBackground(transparent);
        titleNumber.setForeground(new Color(0,255,255));
        titleNumber.setFont(numberFont);
        titleNumber.setBounds(centerX-400,centerY-335,800,120);
        this.add(titleNumber);

        JButton startButton = new JButton("proceed");
        startButton.setBackground(transparent);
        startButton.setForeground(Color.white);
        startButton.setFont(normalFont);
        startButton.setContentAreaFilled(false);
        startButton.setBorder(new RoundedBorder(30));
        startButton.setBounds(centerX-150,centerY+50,300,50);
        startButton.addActionListener((a)->{mainFrame.setCurrentState(MainFrame.STATE.GAME);});
        this.add(startButton);

        JButton settingsButton = new JButton("morb");
        settingsButton.setBackground(transparent);
        settingsButton.setForeground(Color.white);
        settingsButton.setFont(normalFont);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorder(new RoundedBorder(30));
        settingsButton.setBounds(centerX-150,centerY+110,300,50);
        settingsButton.addActionListener((a)->{mainFrame.setCurrentState(MainFrame.STATE.GAME);});
        this.add(settingsButton);

        JButton quitButton = new JButton("not proceed");
        quitButton.setBackground(transparent);
        quitButton.setForeground(Color.white);
        quitButton.setFont(normalFont);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorder(new RoundedBorder(30));
        quitButton.setBounds(centerX-150,centerY+170,300,50);
        quitButton.addActionListener((a)->{mainFrame.setCurrentState(MainFrame.STATE.QUIT);});
        this.add(quitButton);
    }

    @Override
    public void onResize() {
        fillScreen();
    }

    private static class RoundedBorder implements Border {

        private int radius;


        RoundedBorder(int radius) {
            this.radius = radius;
        }


        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }


        public boolean isBorderOpaque() {
            return true;
        }


        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
}
