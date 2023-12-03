package main.java.org.InputManagement;

import main.java.org.Screens.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Eine Klasse, die die Benutzereingaben detektiert
 */
public class InputManager  {
    /**
     * Die Robotinstanz, die die Mausbewegungen verwaltet
     */
    private static final Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @hidden
     */
    private static int basedMouseX=Toolkit.getDefaultToolkit().getScreenSize().width/2;
    /**
     * @hidden
     */
    private static int basedMouseY=Toolkit.getDefaultToolkit().getScreenSize().height/2;
    /**
     * Die horizontale Mausbewegung
     */
    public static int deltaMouseX=0;
    /**
     * Die vertikale Mausbewegung
     */
    public static int deltaMouseY=0;

    /**
     * Speichert wie viel die Maus sich bewegt ist und stellt die Mausposition zurück
     */
    public static void fetchMousePosition(){
        Point p=MouseInfo.getPointerInfo().getLocation();
        deltaMouseX=p.x-basedMouseX;
        deltaMouseY=p.y-basedMouseY;

        robot.mouseMove(basedMouseX,basedMouseY);
    }

    /**
     * Macht den Kursor unsehbar
     * @param frame das JFrame, in dem das Kursor unsehbar sein soll
     */
    public static void hideCursor(JFrame frame){
        frame.setCursor(frame.getToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "null"));
    }

    /**
     * Macht den Kursor sehbar
     * @param frame das JFrame, in dem das Kursor sehbar sein soll
     */
    public static void showCursor(JFrame frame){
        frame.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * @hidden
     */
    public static boolean W=false;
    /**
     * @hidden
     */
    public static boolean A=false;
    /**
     * @hidden
     */
    public static boolean S=false;
    /**
     * @hidden
     */
    public static boolean D=false;
    /**
     * @hidden
     */
    public static boolean C=false;
    /**
     * @hidden
     */
    public static boolean SPACE=false;
    /**
     * @hidden
     */
    public static boolean L_SHIT=false;
    /**
     * @hidden
     */
    public static boolean CONTROL=false;
    /**
     * @hidden
     */
    public static boolean UP=false;
    /**
     * @hidden
     */
    public static boolean DOWN=false;
    /**
     * @hidden
     */
    public static boolean LEFT=false;
    /**
     * @hidden
     */
    public static boolean RIGHT=false;
    /**
     * @hidden
     */
    public static boolean ESCAPE=false;

    /**
     * Eine Keyadapterklasse, die die Tasteneingaben in einem JFrame detektiert und stellt die dementsprechenden Variablen ein.
     * Detektiert nur solche Tasten, die für das Spiel relevant sind.
     */
    public static class KeyInput extends KeyAdapter{
        /**
         * Überschreibt die keyPressed-Funktion der KeyAdapter-Klasse
         * @param e das Ereignis
         */
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> W = true;
                case KeyEvent.VK_A -> A = true;
                case KeyEvent.VK_S -> S = true;
                case KeyEvent.VK_D -> D = true;
                case KeyEvent.VK_C -> C = true;
                case KeyEvent.VK_SPACE->SPACE=true;
                case KeyEvent.VK_SHIFT->L_SHIT=true;
                case KeyEvent.VK_CONTROL->CONTROL=true;
                case KeyEvent.VK_UP->UP=true;
                case KeyEvent.VK_DOWN->DOWN=true;
                case KeyEvent.VK_LEFT->LEFT=true;
                case KeyEvent.VK_RIGHT->RIGHT=true;
                case KeyEvent.VK_ESCAPE->ESCAPE=true;
            }
        }

        /**
         * Überschreibt die keyReleased-Funktion der KeyAdapter-Klasse
         * @param e das Ereignis
         */
        @Override
        public void keyReleased(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> W = false;
                case KeyEvent.VK_A -> A = false;
                case KeyEvent.VK_S -> S = false;
                case KeyEvent.VK_D -> D = false;
                case KeyEvent.VK_C -> C = false;
                case KeyEvent.VK_SPACE->SPACE=false;
                case KeyEvent.VK_SHIFT->L_SHIT=false;
                case KeyEvent.VK_CONTROL->CONTROL=false;
                case KeyEvent.VK_UP->UP=false;
                case KeyEvent.VK_DOWN->DOWN=false;
                case KeyEvent.VK_LEFT->LEFT=false;
                case KeyEvent.VK_RIGHT->RIGHT=false;
                case KeyEvent.VK_ESCAPE->ESCAPE=false;
            }
        }
    }
}
