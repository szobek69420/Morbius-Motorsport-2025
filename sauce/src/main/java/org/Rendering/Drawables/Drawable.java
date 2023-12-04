package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.*;
import main.java.org.MainFrame.MainFrame;
import main.java.org.Rendering.Camera.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

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
     * @param image Die Graphics-Kontext, in der das Objekt gezeichnet werden soll.
     * @param depthBuffer Der Tiefenpuffer
     * @param cam Die Kamera, nach deren Orientation die Bildschirmpositionen berechnet werden sollen.
     */
    public void render(BufferedImage image, float[][] depthBuffer, Camera cam){

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
        float[] z=new float[vertexCount];

        for(int i=0;i< vertexCount;i++){
            float distanceRatio=(float)(nearPlane/Math.abs(transformedVertices[i].get(2)));

            x[i]=(int)((0.5f*GAME_WIDTH-(transformedVertices[i].get(0)*distanceRatio*onePerNearPlaneWidth)*0.5f*GAME_WIDTH));
            y[i]=(int)(0.5f*GAME_HEIGHT-(transformedVertices[i].get(1)*distanceRatio*onePerNearPlaneHeight)*0.5f*GAME_HEIGHT);
            z[i]=transformedVertices[i].get(2);
        }

        //draw
        for(int i=0;i<faceCount;i++){

            switch (clipCount[i]){
                case 0:
                    drawTriangle(
                            new int[]{x[indices[3*i]],x[indices[3*i+1]],x[indices[3*i+2]]},
                            new int[]{y[indices[3*i]],y[indices[3*i+1]],y[indices[3*i+2]]},
                            new float[]{z[indices[3*i]],z[indices[3*i+1]],z[indices[3*i+2]]},
                            image,
                            depthBuffer,
                            faceColors[i].getRGB()
                    );
                    break;

                case 1:
                    int[] modX2=new int[4];
                    int[] modY2=new int[4];
                    float[] modZ2=new float[4];
                    Vector3[] jok2=new Vector3[2];
                    int index3=0;
                    Vector3 rossz=null;
                    for(int j=0;j<3;j++){
                        if(!isBehindView[i*3+j])
                        {
                            //System.out.println(x[indices[i*3+j]]+" "+y[indices[i*3+j]]);
                            modX2[index3]=x[indices[i*3+j]];
                            modY2[index3]=y[indices[i*3+j]];
                            modZ2[index3]=z[indices[i*3+j]];

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
                        modZ2[2+j]=nearPlane;
                    }


                    drawTriangle(new int[]{modX2[0],modX2[1],modX2[2]},new int[]{modY2[0],modY2[1],modY2[2]},new float[]{modZ2[0],modZ2[1],modZ2[2]},image,depthBuffer,faceColors[i].getRGB());
                    drawTriangle(new int[]{modX2[1],modX2[2],modX2[3]},new int[]{modY2[1],modY2[2],modY2[3]},new float[]{modZ2[1],modZ2[2],modZ2[3]},image,depthBuffer,faceColors[i].getRGB());
                    break;

                case 2:
                    int[] modX=new int[3];
                    int[] modY=new int[3];
                    float[] modZ=new float[3];
                    Vector3 jo=null;
                    for(int j=0;j<3;j++){
                        if(!isBehindView[i*3+j])
                        {
                            //System.out.println(x[indices[i*3+j]]+" "+y[indices[i*3+j]]);
                            modX[0]=x[indices[i*3+j]];
                            modY[0]=y[indices[i*3+j]];
                            modZ[0]=z[indices[i*3+j]];
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
                        modZ[index2]=nearPlane;

                        index2++;
                    }

                    //System.out.println("skipped: "+x[indices[3*i]]+" "+y[indices[3*i]]+" "+x[indices[3*i+1]]+" "+y[indices[3*i+1]]+" "+x[indices[3*i+2]]+" "+y[indices[3*i+2]]);
                    /*g.setColor(faceColors[i]);
                    g.fillPolygon(modX,modY,3);*/
                    drawTriangle(new int[]{modX[0],modX[1],modX[2]},new int[]{modY[0],modY[1],modY[2]},new float[]{modZ[0],modZ[1],modZ[2]},image,depthBuffer,faceColors[i].getRGB());
                    break;
            }
        }
    }

    private void drawTriangle(int[] x,int[] y, float[] z,BufferedImage image, float[][] depthBuffer,int colorARGB){
        int temp;
        float tempf;

        //sort
        {
            if (x[0] > x[2]) {
                temp = x[0];
                x[0] = x[2];
                x[2] = temp;

                temp = y[0];
                y[0] = y[2];
                y[2] = temp;

                tempf = z[0];
                z[0] = z[2];
                z[2] = tempf;
            }
            if (x[1] > x[2]) {
                temp = x[1];
                x[1] = x[2];
                x[2] = temp;

                temp = y[1];
                y[1] = y[2];
                y[2] = temp;

                tempf = z[1];
                z[1] = z[2];
                z[2] = tempf;
            }
            if (x[0] > x[1]) {
                temp = x[0];
                x[0] = x[1];
                x[1] = temp;

                temp = y[0];
                y[0] = y[1];
                y[1] = temp;

                tempf = z[0];
                z[0] = z[1];
                z[1] = tempf;
            }
        }


        boolean baseIsBased=false;//ha a kozepso csucs a ket szelso csucs kozotti oldal felett van, akkor true

        float middleY=1;
        float middleZ=Camera.RENDER_DISTANCE;
        if(x[0]==x[1]){
            middleY=y[0];
            middleZ=z[0];
        }
        else if(x[1]==x[2]){
            middleY=y[2];
            middleZ=z[2];
        }
        else{
            middleY=y[0]+(y[2]-y[0])*(x[1]-x[0])/(float)(x[2]-x[0]);
            middleZ=z[0]+(z[2]-z[0])*(x[1]-x[0])/(float)(x[2]-x[0]);
        }

        if(y[1]<middleY)
            baseIsBased=true;


        int minY;
        int maxY;
        float currentY1;
        float currentY2;
        float deltaY1;
        float deltaY2;
        float currentZ1;
        float currentZ2;
        float deltaZ1;
        float deltaZ2;
        float ziterator;
        float deltaZiterator;

        //elso egyenes
        if(x[0]!=x[1]){
            currentY1=y[0]+0.51f;
            currentY2=y[0]+0.51f;
            currentZ1=z[0];
            currentZ2=z[0];

            if(baseIsBased){
                deltaY1=(y[1]-y[0])/(float)(x[1]-x[0]);
                deltaY2=(y[2]-y[0])/(float)(x[2]-x[0]);
                deltaZ1=(z[1]-z[0])/(float)(x[1]-x[0]);
                deltaZ2=(z[2]-z[0])/(float)(x[2]-x[0]);
            }
            else{
                deltaY2=(y[1]-y[0])/(float)(x[1]-x[0]);
                deltaY1=(y[2]-y[0])/(float)(x[2]-x[0]);
                deltaZ2=(z[1]-z[0])/(float)(x[1]-x[0]);
                deltaZ1=(z[2]-z[0])/(float)(x[2]-x[0]);
            }

            for(int i=x[0];i<=x[1]&&i<MainFrame.FRAME_BUFFER_WIDTH;i++){

                if(i>=0){
                    minY=(int)currentY1;
                    maxY=(int)currentY2;


                    ziterator=currentZ1;
                    deltaZiterator=(currentZ2-currentZ1)/(maxY-minY);
                    for(;minY<=maxY&&minY<MainFrame.FRAME_BUFFER_HEIGHT;minY++){
                        if(minY>=0){
                            if(ziterator<depthBuffer[i][minY]){
                                depthBuffer[i][minY]=ziterator;
                                if(ziterator>Camera.FOG_START)
                                    image.setRGB(i,minY,colorLerp(colorARGB,Camera.CLEAR_COLOR_INT,(ziterator-Camera.FOG_START)*Camera.ONE_PER_FOG_LENGTH));
                                else
                                    image.setRGB(i,minY,colorARGB);
                            }
                        }
                        ziterator+=deltaZiterator;
                    }
                }

                currentY1+=deltaY1;
                currentY2+=deltaY2;
                currentZ1+=deltaZ1;
                currentZ2+=deltaZ2;
            }
        }

        //masodik egyenes
        if(x[1]!=x[2]){

            if(baseIsBased){
                currentY1=y[1]+0.51f;
                currentY2=middleY+0.51f;
                deltaY1=(y[2]-y[1])/(float)(x[2]-x[1]);
                deltaY2=(y[2]-y[0])/(float)(x[2]-x[0]);
                currentZ1=z[1];
                currentZ2=middleZ;
                deltaZ1=(z[2]-z[1])/(float)(x[2]-x[1]);
                deltaZ2=(z[2]-z[0])/(float)(x[2]-x[0]);
            }
            else{
                currentY2=y[1]+0.51f;
                currentY1=middleY+0.51f;
                deltaY2=(y[2]-y[1])/(float)(x[2]-x[1]);
                deltaY1=(y[2]-y[0])/(float)(x[2]-x[0]);
                currentZ2=z[1];
                currentZ1=middleZ;
                deltaZ2=(z[2]-z[1])/(float)(x[2]-x[1]);
                deltaZ1=(z[2]-z[0])/(float)(x[2]-x[0]);
            }

            for(int i=x[1];i<=x[2]&&i<MainFrame.FRAME_BUFFER_WIDTH;i++){

                if(i>=0){
                    minY=(int)currentY1;
                    maxY=(int)currentY2;


                    ziterator=currentZ1;
                    deltaZiterator=(currentZ2-currentZ1)/(maxY-minY);
                    for(;minY<=maxY&&minY<MainFrame.FRAME_BUFFER_HEIGHT;minY++){
                        if(minY>=0){
                            if(ziterator<depthBuffer[i][minY]){
                                depthBuffer[i][minY]=ziterator;
                                if(ziterator>Camera.FOG_START)
                                    image.setRGB(i,minY,colorLerp(colorARGB,Camera.CLEAR_COLOR_INT,(ziterator-Camera.FOG_START)*Camera.ONE_PER_FOG_LENGTH));
                                else
                                    image.setRGB(i,minY,colorARGB);
                            }
                        }
                        ziterator+=deltaZiterator;
                    }
                }

                currentY1+=deltaY1;
                currentY2+=deltaY2;
                currentZ1+=deltaZ1;
                currentZ2+=deltaZ2;
            }
        }
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

    public static int colorLerp(int a, int b, float i){
        int colour=0xFF000000;
        int temp;
        //r
        temp=a&0x00FF0000;
        temp=(int)(((b&0x00FF0000)-temp)*i)+temp;
        temp&=0x00FF0000;
        colour|=temp;

        //g
        temp=a&0x0000FF00;
        temp=(int)(((b&0x0000FF00)-temp)*i)+temp;
        temp&=0x0000FF00;
        colour|=temp;

        //b
        temp=a&0x000000FF;
        temp=(int)(((b&0x000000FF)-temp)*i)+temp;
        temp&=0x000000FF;
        colour|=temp;

        return colour;
    }

}
