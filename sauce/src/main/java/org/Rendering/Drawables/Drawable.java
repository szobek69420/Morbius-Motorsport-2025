package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.*;
import main.java.org.Rendering.Camera.Camera;

import java.awt.*;

/**
 * Eine Basisklasse für alle Objekte, die von einer Kamera gezeichnet werden sollen.
 */
public abstract class Drawable {

    /**
     * Der Name des Drawables
     */
    private String name="amogus";

    /**
     * Die Knoten des Drawables
     */
    protected Vector3[] vertices;

    /**
     * Die Indizen des Drawables
     */
    protected int[] indices;

    /**
     * Die Seitenfarben des Drawables
     */
    protected Color[] faceColors;

    /**
     * Eine Matrix, mit deren die lokalen Knotenpositionen mit berücksichtigung der Größe des Objektes berechnet werden können.
     * (die ist nicht eine echte Modelmatrix, weil es die Knotenpositionen nicht zu Weltraumpositionen konvertiert)
     */
    protected Matrix3 modelMatrix;

    /**
     * Die Position des Objektes
     */
    protected Vector3 pos;
    /**
     * Die Größe des Objektes
     */
    protected Vector3 scale;


    /**
     * Berechnet die Modelmatrix anhand der Größe
     */
    protected void calculateModelMatrix(){
        modelMatrix=new Matrix3(new float[][]{{scale.get(0),0,0},{0,scale.get(1),0},{0,0,scale.get(2)}});
    }

    /**
     * Eine abstrakte Funktion, die dann wird aufgerufen, falls das Objekt gezeichnet werden soll.
     * @param g Die Graphics-Kontext, in der das Objekt gezeichnet werden soll.
     * @param cam Die Kamera, nach deren Orientation die Bildschirmpositionen berechnet werden sollen.
     */
    public void render(Graphics g, Camera cam){
        g.setColor(Color.red);

        Vector3 cameraPos=cam.getPosition();
        Vector3 forward=cam.getForward();
        Vector3 left=cam.getLeft();
        Vector3 up=cam.getUp();
        float nearPlane=cam.getNearPlane();
        float onePerNearPlaneHeight=1/cam.getNearPlaneHeight();
        float onePerNearPlaneWidth=1/cam.getNearPlaneWidth();
        int GAME_WIDTH=cam.getScreenWidth();
        int GAME_HEIGHT=cam.getScreenHeight();

        int vertexCount=vertices.length;
        int indexCount=indices.length;

        Vector3[] transformedVertices=new Vector3[vertexCount];

        //view space
        for(int i=0;i< vertexCount;i++){
            Vector3 temp= Vector3.difference(
                    Vector3.sum(
                            new Vector3(scale.get(0)*vertices[i].get(0),scale.get(1)*vertices[i].get(1),scale.get(2)*vertices[i].get(2)),
                            this.pos)
                    ,cameraPos);

            /*transformedVertices[i]=new Vector3(
                    Vector3.dotProduct(left,temp),
                    Vector3.dotProduct(up,temp),
                    Vector3.dotProduct(forward,temp)
            );*/
            transformedVertices[i]=Vector3.multiplyWithMatrix(cam.getViewMatrix(),temp);
        }

        //backface cull
        int faceCount=indexCount/3;
        boolean isEverythingBehind=true;
        boolean[] isBehindView=new boolean[faceCount*3];
        int[] clipCount=new int[faceCount];
        for(int i=0;i<faceCount;i++){
            Vector3[] face=new Vector3[]{transformedVertices[indices[i*3]],transformedVertices[indices[i*3+1]],transformedVertices[indices[i*3+2]]};

            if(Vector3.dotProduct(
                    Vector3.avg(face,3),
                    Vector3.crossProduct(
                            Vector3.difference(face[2],face[1]),
                            Vector3.difference(face[0],face[1])
                    )) <0
            ){
                isBehindView[i*3]=false;
                isBehindView[i*3+1]=false;
                isBehindView[i*3+2]=false;
                clipCount[i]=0;


                if(face[0].get(2)<0){
                    isBehindView[i*3]=true;
                    clipCount[i]++;
                }
                if(face[1].get(2)<0){
                    isBehindView[i*3+1]=true;
                    clipCount[i]++;
                }
                if(face[2].get(2)<0){
                    isBehindView[i*3+2]=true;
                    clipCount[i]++;
                }

                //check if anything is visible
                if(clipCount[i]<3)
                    isEverythingBehind=false;
            }
            else {
                isBehindView[i*3]=true;
                isBehindView[i*3+1]=true;
                isBehindView[i*3+2]=true;
                clipCount[i]=3;
            }
        }

        if(isEverythingBehind)//object not visible -> yeet
            return;

        //screen positions
        int[] x=new int[vertexCount];
        int[] y=new int[vertexCount];

        for(int i=0;i< vertexCount;i++){
            float distanceRatio=(float)(nearPlane/Math.abs(transformedVertices[i].get(2)));

            x[i]=(int)((0.5f*GAME_WIDTH-(transformedVertices[i].get(0)*distanceRatio*onePerNearPlaneWidth)*0.5f*GAME_WIDTH));
            y[i]=(int)(0.5f*GAME_HEIGHT-(transformedVertices[i].get(1)*distanceRatio*onePerNearPlaneHeight)*0.5f*GAME_HEIGHT);
        }

        //draw
        Color orgColor=g.getColor();
        for(int i=0;i<faceCount;i++){

            switch (clipCount[i]){
                case 0:
                    g.setColor(faceColors[i]);
                    //System.out.println("guter: "+x[indices[3*i]]+" "+y[indices[3*i]]+" "+x[indices[3*i+1]]+" "+y[indices[3*i+1]]+" "+x[indices[3*i+2]]+" "+y[indices[3*i+2]]);
                    g.fillPolygon(new int[]{x[indices[3*i]],x[indices[3*i+1]],x[indices[3*i+2]]},new int[]{y[indices[3*i]],y[indices[3*i+1]],y[indices[3*i+2]]},3);
                    break;

                case 1:
                    int[] modX2=new int[4];
                    int[] modY2=new int[4];
                    Vector3[] jok2=new Vector3[2];
                    int index3=0;
                    Vector3 rossz=null;
                    for(int j=0;j<3;j++){
                        if(!isBehindView[i*3+j])
                        {
                            //System.out.println(x[indices[i*3+j]]+" "+y[indices[i*3+j]]);
                            modX2[index3]=x[indices[i*3+j]];
                            modY2[index3]=y[indices[i*3+j]];
                            jok2[index3]=transformedVertices[indices[i*3+j]];
                            index3++;
                        }
                        else
                            rossz=transformedVertices[indices[i*3+j]];
                    }

                    for(int j=0;j<2;j++){
                        float rosszDist=(nearPlane-rossz.get(2));//a kamera mogotti pont tavolsaga a vaszontol
                        float joDist=(jok2[j].get(2)-nearPlane);//a kamera elotti pont tavolsaga a vaszontol

                        if(joDist<0.005f)
                            joDist=0.005f;

                        Vector3 temp=new Vector3(
                                joDist*rossz.get(0)+rosszDist*jok2[j].get(0),
                                joDist*rossz.get(1)+rosszDist*jok2[j].get(1),
                                joDist*rossz.get(2)+rosszDist*jok2[j].get(2)
                        );
                        temp=Vector3.multiplyWithScalar(1/(rosszDist+joDist),temp);

                        modX2[2+j]=(int)((0.5f*GAME_WIDTH-(temp.get(0)*onePerNearPlaneWidth)*0.5f*GAME_WIDTH));
                        modY2[2+j]=(int)(0.5f*GAME_HEIGHT-(temp.get(1)*onePerNearPlaneHeight)*0.5f*GAME_HEIGHT);
                    }

                    g.setColor(faceColors[i]);
                    //g.fillPolygon(modX2,modY2,4);
                    g.fillPolygon(new int[]{modX2[0],modX2[1],modX2[2]},new int[]{modY2[0],modY2[1],modY2[2]},3);
                    g.fillPolygon(new int[]{modX2[1],modX2[2],modX2[3]},new int[]{modY2[1],modY2[2],modY2[3]},3);
                    break;

                case 2:
                    int[] modX=new int[3];
                    int[] modY=new int[3];
                    Vector3 jo=null;
                    for(int j=0;j<3;j++){
                        if(!isBehindView[i*3+j])
                        {
                            //System.out.println(x[indices[i*3+j]]+" "+y[indices[i*3+j]]);
                            modX[0]=x[indices[i*3+j]];
                            modY[0]=y[indices[i*3+j]];
                            jo=transformedVertices[indices[i*3+j]];
                            break;
                        }
                    }

                    int index2=1;
                    //System.out.println(joZ+" "+modX[0]+" "+modY[0]);
                    for(int j=0;j<3;j++){
                        if(!isBehindView[i*3+j])
                            continue;

                        Vector3 rossz2=transformedVertices[indices[i*3+j]];

                        float rosszDist=(nearPlane-rossz2.get(2));//a kamera mogotti pont tavolsaga a vaszontol
                        float joDist=(jo.get(2)-nearPlane);//a kamera elotti pont tavolsaga a vaszontol
                        if(joDist<0.005f)
                            joDist=0.005f;

                        Vector3 temp=new Vector3(
                                joDist*rossz2.get(0)+rosszDist*jo.get(0),
                                joDist*rossz2.get(1)+rosszDist*jo.get(1),
                                joDist*rossz2.get(2)+rosszDist*jo.get(2)
                        );
                        temp=Vector3.multiplyWithScalar(1/(rosszDist+joDist),temp);

                        modX[index2]=(int)((0.5f*GAME_WIDTH-(temp.get(0)*onePerNearPlaneWidth)*0.5f*GAME_WIDTH));
                        modY[index2]=(int)(0.5f*GAME_HEIGHT-(temp.get(1)*onePerNearPlaneHeight)*0.5f*GAME_HEIGHT);

                        index2++;
                    }

                    //System.out.println("skipped: "+x[indices[3*i]]+" "+y[indices[3*i]]+" "+x[indices[3*i+1]]+" "+y[indices[3*i+1]]+" "+x[indices[3*i+2]]+" "+y[indices[3*i+2]]);
                    g.setColor(faceColors[i]);
                    g.fillPolygon(modX,modY,3);
                    break;
            }
        }
        g.setColor(orgColor);
    }


    /**
     * Gibt die Position des Objektes zurück. Der Wert der Position wird nicht in eine andere Vector3-Instanz kopiert.
     * @return die Position des Objektes
     */
    public final Vector3 getPositionByReference(){
        return pos;
    }

    /**
     * Stellt die Position des Objektes
     * @param pos die neue Position
     */
    public final void setPosition(Vector3 pos){
        this.pos=pos;
    }
    /**
     * Stellt die Größe des Objektes
     * @param scale die neue Größe
     */
    public final void setScale(Vector3 scale){
        this.scale=scale;
        calculateModelMatrix();
    }
    /**
     * Gibt die Größe des Objektes zurück. Der Wert der Größe wird nicht in eine andere Vector3-Instanz kopiert.
     * @return die Position des Objektes
     */
    public final Vector3 getScaleByReference(){
        return this.scale;
    }

    /**
     * Stellt den Name des Objektes ein
     * @param name der neue Name
     */
    public final void setName(String name){
        this.name=name;
    }

    /**
     * Gibt der Name des Objektes zurück
     * @return der Name des Objektes
     */
    public final String getName(){
        return this.name;
    }
}
