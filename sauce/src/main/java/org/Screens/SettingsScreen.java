package main.java.org.Screens;

import main.java.org.MainFrame.MainFrame;
import main.java.org.Resizable.Resizable;
import main.java.org.Settings;
import main.java.org.UI.MyChangeListener;
import main.java.org.UI.RoundedBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SettingsScreen extends JPanel implements Resizable {

    private Font titleFont;
    private Font numberFont;
    private Font normalFont;

    private MainFrame mainFrame;

    public SettingsScreen(MainFrame mf){
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

        //title
        JLabel title=new JLabel("sedigs",SwingConstants.CENTER);
        title.setBackground(transparent);
        title.setForeground(Color.white);
        title.setFont(titleFont);
        title.setBounds(centerX-400,centerY-300,800,60);
        this.add(title);

        //fov
        JLabel fovLabel=new JLabel("fov",SwingConstants.LEFT);
        fovLabel.setBackground(transparent);
        fovLabel.setForeground(Color.white);
        fovLabel.setFont(normalFont);
        fovLabel.setBounds(centerX-200,centerY-100,400,20);
        this.add(fovLabel);

        JLabel fovValue=new JLabel(""+Settings.fieldOfView,SwingConstants.RIGHT);
        fovValue.setBackground(transparent);
        fovValue.setForeground(Color.white);
        fovValue.setFont(normalFont);
        fovValue.setBounds(centerX+150,centerY-65,50,20);
        this.add(fovValue);

        JSlider fovSlider=new JSlider(1,89, Settings.fieldOfView){
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this, new Color(0,255,255), new Color(255, 209,0)));
            }
        };
        fovSlider.addChangeListener((e)->{
                Settings.fieldOfView=fovSlider.getValue();
                fovValue.setText(""+Settings.fieldOfView);
            });
        fovSlider.setBackground(new Color(transparent.getRGB()|0xFF000000));
        fovSlider.setForeground(Color.white);
        fovSlider.setDoubleBuffered(true);//kell, hogy az átlátszó háttér ne basszon el mindent
        fovSlider.setBounds(centerX-200,centerY-80,350,50);
        this.add(fovSlider);

        //render distance
        JLabel renderDistanceLabel=new JLabel("render distance",SwingConstants.LEFT);
        renderDistanceLabel.setBackground(transparent);
        renderDistanceLabel.setForeground(Color.white);
        renderDistanceLabel.setFont(normalFont);
        renderDistanceLabel.setBounds(centerX-200,centerY,400,20);
        this.add(renderDistanceLabel);

        JLabel renderDistanceValue=new JLabel(""+Settings.renderDistance,SwingConstants.RIGHT);
        renderDistanceValue.setBackground(transparent);
        renderDistanceValue.setForeground(Color.white);
        renderDistanceValue.setFont(normalFont);
        renderDistanceValue.setBounds(centerX+150,centerY+35,50,20);
        this.add(renderDistanceValue);

        JSlider renderDistanceSlider=new JSlider(2,16, Settings.renderDistance){
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this, new Color(0,255,255), new Color(255, 209,0)));
            }
        };
        renderDistanceSlider.addChangeListener((e)-> {
            Settings.renderDistance=renderDistanceSlider.getValue();
            renderDistanceValue.setText(""+Settings.renderDistance);
        });
        renderDistanceSlider.setBackground(new Color(transparent.getRGB()|0xFF000000));
        renderDistanceSlider.setForeground(Color.white);
        renderDistanceSlider.setDoubleBuffered(true);//kell, hogy az átlátszó háttér ne basszon el mindent
        renderDistanceSlider.setBounds(centerX-200,centerY+20,350,50);
        this.add(renderDistanceSlider);


        //save
        JButton saveButton = new JButton("back");
        saveButton.setBackground(transparent);
        saveButton.setForeground(Color.white);
        saveButton.setFont(normalFont);
        saveButton.setContentAreaFilled(false);
        saveButton.setBorder(new RoundedBorder(30));
        saveButton.setBounds(centerX-150,centerY+170,300,50);
        saveButton.addActionListener((a)->{mainFrame.setCurrentState(MainFrame.STATE.MAIN_MENU);});
        saveButton.addChangeListener(new MyChangeListener(Color.white,new Color(0,255,255),new Color(0,150,150)));
        this.add(saveButton);

        //notiz
        JLabel note=new JLabel("NOTE: closing the game resets the settings",SwingConstants.LEFT);
        note.setBackground(transparent);
        note.setForeground(Color.white);
        note.setFont(new Font("Arial", Font.ITALIC|Font.BOLD,15));
        note.setBounds(5,MainFrame.SCREEN_HEIGHT-60,800,15);
        this.add(note);
    }

    @Override
    public void onResize() {
        fillScreen();
    }

    private static class CustomSliderUI extends BasicSliderUI {

        private static final int TRACK_HEIGHT = 8;
        private static final int TRACK_WIDTH = 8;
        private static final int TRACK_ARC = 5;
        private static final Dimension THUMB_SIZE = new Dimension(20, 20);
        private final RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float();

        private final Color trackColor;
        private final Color handleColor;

        public CustomSliderUI(final JSlider b, Color trackColor, Color handleColor) {
            super(b);

            this.trackColor=trackColor;
            this.handleColor=handleColor;
        }

        @Override
        protected void calculateTrackRect() {
            super.calculateTrackRect();
            if (isHorizontal()) {
                trackRect.y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
                trackRect.height = TRACK_HEIGHT;
            } else {
                trackRect.x = trackRect.x + (trackRect.width - TRACK_WIDTH) / 2;
                trackRect.width = TRACK_WIDTH;
            }
            trackShape.setRoundRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height, TRACK_ARC, TRACK_ARC);
        }

        @Override
        protected void calculateThumbLocation() {
            super.calculateThumbLocation();
            if (isHorizontal()) {
                thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
            } else {
                thumbRect.x = trackRect.x + (trackRect.width - thumbRect.width) / 2;
            }
        }

        @Override
        protected Dimension getThumbSize() {
            return THUMB_SIZE;
        }

        private boolean isHorizontal() {
            return slider.getOrientation() == JSlider.HORIZONTAL;
        }

        @Override
        public void paint(final Graphics g, final JComponent c) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paint(g, c);
        }

        @Override
        public void paintTrack(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Shape clip = g2.getClip();

            boolean horizontal = isHorizontal();
            boolean inverted = slider.getInverted();

            // Paint shadow.
            g2.setColor(new Color(50,50,50));
            g2.fill(trackShape);

            // Paint track background.
            g2.setColor(trackColor.darker());
            g2.setClip(trackShape);
            trackShape.y += 1;
            g2.fill(trackShape);
            trackShape.y = trackRect.y;

            g2.setClip(clip);

            // Paint selected track.
            if (horizontal) {
                boolean ltr = slider.getComponentOrientation().isLeftToRight();
                if (ltr) inverted = !inverted;
                int thumbPos = thumbRect.x + thumbRect.width / 2;
                if (inverted) {
                    g2.clipRect(0, 0, thumbPos, slider.getHeight());
                } else {
                    g2.clipRect(thumbPos, 0, slider.getWidth() - thumbPos, slider.getHeight());
                }

            } else {
                int thumbPos = thumbRect.y + thumbRect.height / 2;
                if (inverted) {
                    g2.clipRect(0, 0, slider.getHeight(), thumbPos);
                } else {
                    g2.clipRect(0, thumbPos, slider.getWidth(), slider.getHeight() - thumbPos);
                }
            }
            g2.setColor(trackColor);
            g2.fill(trackShape);
            g2.setClip(clip);
        }

        @Override
        public void paintThumb(final Graphics g) {
            g.setColor(handleColor);
            g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        }

        @Override
        public void paintFocus(final Graphics g) {}
    }
}

