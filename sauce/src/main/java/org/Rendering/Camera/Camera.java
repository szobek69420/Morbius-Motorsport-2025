package main.java.org.Rendering.Camera;

import main.java.org.LinearAlgebruh.Matrix3;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Rendering.Drawables.Drawable;
import main.java.org.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Camera {

    public static Camera main;
    public static float RENDER_DISTANCE=60;
    public static float FOG_START=50;
    public static float ONE_PER_FOG_LENGTH=0.1f;

    public static final Color CLEAR_COLOR=new Color(0,170,250);
    public static final int CLEAR_COLOR_INT=CLEAR_COLOR.getRGB();

    private int GAME_WIDTH,GAME_HEIGHT;

    private final float aspectYX;

    private final float aspectXY;

    private float fov;

    private float nearPlane;
    private float nearPlaneSquared;

    private float nearPlaneWidth;

    private float nearPlaneHeight;
    private float onePerNearPlaneWidth;
    private float onePerNearPlaneHeight;


    private ArrayList<Drawable> drawables;
    private Vector3 pos;
    private Vector3 renderPosition;

    private float pitch,yaw;
    private Vector3 left,up,forward;

    private Matrix3 viewMatrix;

    /**
     * Erzeugt eine neue Kamerainstanz
     * @param width Die Breite des Fensters
     * @param height Die Höhe des Fensters
     */
    public Camera(int width, int height){
        drawables=new ArrayList<>();

        pitch=-5;
        yaw=20;

        pos= new Vector3(-3,3,-10);
        renderPosition= new Vector3(-3,3,-10);

        GAME_WIDTH=width;
        GAME_HEIGHT=height;

        aspectXY=((float)GAME_WIDTH/GAME_HEIGHT);
        aspectYX=((float)GAME_HEIGHT/GAME_WIDTH);

        RENDER_DISTANCE=Settings.renderDistance*16-5;
        FOG_START=Settings.renderDistance*16-15;

        fov= 5;
        nearPlane=0.05f;
        calculateScreenDimensions();

        calculateOrientation();
    }


    public void render(BufferedImage image, float[][] depthBuffer) {
        calculateOrientation();

        for(int i=drawables.size()-1;i>=0;i--)
        {
            drawables.get(i).render(image,depthBuffer,this);
        }
    }

    public void renderUnregistered(Drawable unregisteredDrawable, BufferedImage image, float[][] depthBuffer){
        calculateOrientation();
        if(unregisteredDrawable!=null)
            unregisteredDrawable.render(image, depthBuffer,this);
    }

    /**
     * Nach den Werten der Umdrehung werden die Einheitsvektoren der Kamerabasis und die Viewmatrix kalkuliert
     */
    private void calculateOrientation(){
        forward=new Vector3(
                (float)(Math.cos(0.0174532925*pitch)*Math.sin(0.0174532925*yaw)),
                (float)Math.sin(0.0174532925*pitch),
                (float)(Math.cos(0.0174532925*pitch)*Math.cos(0.0174532925*yaw))
        );

        left=Vector3.crossProduct(Vector3.up,forward);
        Vector3.normalize(left);
        up=Vector3.crossProduct(forward,left);
        Vector3.normalize(up);

        viewMatrix=new Matrix3(left,up,forward);
        Matrix3.transpose(viewMatrix);

    }

    public void sortDrawables(){
        final int length=drawables.size();

        float[] distance = new float[length];

        for(int i=0;i<length;i++){
            distance[i]=Vector3.sqrMagnitude(Vector3.difference(drawables.get(i).getPositionByReference(),pos));
        }

        float temp;
        Drawable temp2;
        for(int i=0;i<length;i++){
            for(int j=0;j<length-i-1;j++){
                if(distance[j]>distance[j+1]){
                    temp=distance[j];
                    distance[j]=distance[j+1];
                    distance[j+1]=temp;

                    temp2=drawables.get(j);
                    drawables.set(j,drawables.get(j+1));
                    drawables.set(j+1,temp2);
                }
            }
        }
    }

    /**
     * Registrier ein Drawable-Objekt zu der Kamera
     * @param d
     */
    public void addDrawable(Drawable d){
        drawables.add(d);
    }

    /**
     * Lösch ein Drawable-Objekt von der Liste der Kamera
     * @param d Das zu löschene Objekt
     */
    public void removeDrawable(Drawable d){
        drawables.remove(d);
    }

    /**
     * Berechne die Projektionebene anhand dem fov
     */
    private void calculateScreenDimensions(){
        nearPlaneSquared=nearPlane*nearPlane;
        nearPlaneHeight=(float)Math.tan(0.0174532925*fov)*nearPlane;
        nearPlaneWidth=nearPlaneHeight*aspectXY;

        onePerNearPlaneWidth=1/nearPlaneWidth;
        onePerNearPlaneHeight=1/nearPlaneHeight;
    }

    //getter setters

    /**
     * Sagt die Kamera, wie groß der Bildschirm ist
     * @param width Bildschirmbreite
     * @param height Bildschirmhöhe
     */
    public void setScreenSize(int width, int height){
        GAME_WIDTH=width;
        GAME_HEIGHT=height;
    }

    /**
     * Zurückgibt den kopierten Wert der Kameraposition
     * @return der Wert der Kameraposition
     */
    public Vector3 getPosition(){
        return pos.copy();
    }

    /**
     * Stellt die Position der Kamera ein
     * @param pos die neue Position
     */
    public void setPosition(Vector3 pos){
        this.pos=pos;
    }

    public Vector3 getRenderPositionByReference(){return renderPosition;}
    public void setRenderPosition(Vector3 renderPosition){this.renderPosition=renderPosition;}

    /**
     * Zurückgibt den kopierten Wert des dritten Basisvektors des Kameraraumes
     * @return der Wert des dritten Basisvektors des Kameraraumes
     */
    public Vector3 getForward(){
        return forward.copy();
    }

    /**
     * Zurückgibt den kopierten Wert des zweiten Basisvektors des Kameraraumes
     * @return der Wert des zweiten Basisvektors des Kameraraumes
     */
    public Vector3 getUp() {
        return up.copy();
    }

    /**
     * Zurückgibt den kopierten Wert des ersten Basisvektors des Kameraraumes
     * @return der Wert des ersten Basisvektors des Kameraraumes
     */
    public Vector3 getLeft() {
        return left.copy();
    }

    /**
     * Zurückgibt die vertikale Umdrehung der Kamera
     * @return die vertikale Umdrehung der Kamera
     */
    public float getPitch(){
        return pitch;
    }

    /**
     * Stellt die vertikale Umdrehung der Kamera ein
     * @param pitch die neue vertikale Umdrehung
     */
    public void setPitch(float pitch){
        this.pitch=pitch;
    }

    /**
     * Zurückgibt die horizontale Umdrehung der Kamera
     * @return die horizontale Umdrehung der Kamera
     */
    public float getYaw(){
        return yaw;
    }

    /**
     * Stellt die horizontale Umdrehung der Kamera ein
     * @param yaw die neue horizontale Umdrehung
     */
    public void setYaw(float yaw){
        this.yaw=yaw;
    }

    /**
     * Zurückgibt die Klipdistanz der Kamera
     * @return die Klipdistanz der Kamera
     */
    public float getNearPlane(){
        return nearPlane;
    }

    /**
     * Zurückgibt die Breite der Projektionebene
     * @return die Breite der Projektionebene
     */
    public float getNearPlaneWidth(){
        return nearPlaneWidth;
    }
    /**
     * Zurückgibt die Höhe der Projektionebene
     * @return die Höhe der Projektionebene
     */
    public float getNearPlaneHeight(){
        return nearPlaneHeight;
    }

    /**
     * Zurückgibt die Breite des Fensters
     * @return die Breite des Fensters
     */
    public int getScreenWidth(){
        return GAME_WIDTH;
    }

    /**
     * Zurückgibt die Höhe des Fensters
     * @return die Höhe des Fensters
     */
    public int getScreenHeight(){
        return GAME_HEIGHT;
    }

    /**
     * Zurückgibt das vertikale Sichtfeld
     * @return das vertikale Sichtfeld
     */
    public float getFOV(){
        return fov;
    }

    /**
     * Stellt den Wert des vertikalen Sichtfeldes ein
     * @param fov der neue Wert von fov
     */
    public void setFOV(float fov){
        this.fov=fov;
        calculateScreenDimensions();
    }

    /**
     * Gibt die Viewmatrix der Kamera zurück. Sie wird nicht kopiert.
     * @return die Viewmatrix der Kamera
     */
    public Matrix3 getViewMatrix(){
        return viewMatrix;
    }
}

