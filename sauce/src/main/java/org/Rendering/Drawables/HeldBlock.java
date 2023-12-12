package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.World.BlockColours;
import main.java.org.World.BlockTypes;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Ein Würfel, der ans Bildschirm gezeichnet werden kann.
 * Vererbt von Drawable.
 */
public class HeldBlock extends Drawable{

    public HeldBlock(){
        vertices=new Vector3[8];
        vertices[0]=new Vector3(1,-1,-1);
        vertices[1]=new Vector3(-1, -1, -1);
        vertices[2]=new Vector3(-1, -1,1);
        vertices[3]=new Vector3(1, -1, 1);
        vertices[4]=new Vector3(1,1,-1);
        vertices[5]=new Vector3(-1, 1, -1);
        vertices[6]=new Vector3(-1, 1 ,1);
        vertices[7]=new Vector3(1, 1, 1);

        indices=new int[]{
                0,1,5,
                0,5,4,
                1,2,6,
                1,6,5,
                2,3,7,
                2,7,6,
                3,0,4,
                3,4,7,
                0,2,1,
                0,3,2,
                4,5,6,
                4,6,7
        };

        faceColors=new Color[]{
                BlockColours.blockColours[0],
                BlockColours.blockColours[0],
                BlockColours.blockColours[1],
                BlockColours.blockColours[1],
                BlockColours.blockColours[2],
                BlockColours.blockColours[2],
                BlockColours.blockColours[3],
                BlockColours.blockColours[3],
                BlockColours.blockColours[5],
                BlockColours.blockColours[5],
                BlockColours.blockColours[4],
                BlockColours.blockColours[4]
        };

        scale=new Vector3(1,1,1);
        pos=new Vector3(0,0,0);

        calculateModelMatrix();

        this.setName("amogus");
    }

    public void render(BufferedImage image, float[][] depthBuffer, Camera cam){

        Graphics g=image.getGraphics();

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
        Color orgColor=g.getColor();
        for(int i=0;i<faceCount;i++){

            g.setColor(faceColors[i]);

            switch (clipCount[i]){
                case 0:
                    g.fillPolygon(
                            new int[]{x[indices[3*i]],x[indices[3*i+1]],x[indices[3*i+2]]},
                            new int[]{y[indices[3*i]],y[indices[3*i+1]],y[indices[3*i+2]]},
                            3
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

                    g.fillPolygon(new int[]{modX2[0],modX2[1],modX2[2]},new int[]{modY2[0],modY2[1],modY2[2]},3);
                    g.fillPolygon(new int[]{modX2[1],modX2[2],modX2[3]},new int[]{modY2[1],modY2[2],modY2[3]},3);
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
                    g.fillPolygon(new int[]{modX[0],modX[1],modX[2]},new int[]{modY[0],modY[1],modY[2]},3);
                    break;
            }
        }

        g.setColor(orgColor);
    }

    public void setBlockType(BlockTypes blockType){
        faceColors=new Color[]{
                BlockColours.blockColours[blockType.ordinal()],
                BlockColours.blockColours[blockType.ordinal()],
                BlockColours.blockColours[blockType.ordinal()+1],
                BlockColours.blockColours[blockType.ordinal()+1],
                BlockColours.blockColours[blockType.ordinal()+2],
                BlockColours.blockColours[blockType.ordinal()+2],
                BlockColours.blockColours[blockType.ordinal()+3],
                BlockColours.blockColours[blockType.ordinal()+3],
                BlockColours.blockColours[blockType.ordinal()+5],
                BlockColours.blockColours[blockType.ordinal()+5],
                BlockColours.blockColours[blockType.ordinal()+4],
                BlockColours.blockColours[blockType.ordinal()+4]
        };
    }
}
