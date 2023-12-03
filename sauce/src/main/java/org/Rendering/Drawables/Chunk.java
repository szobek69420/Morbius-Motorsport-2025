package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.World.BlockColours;
import main.java.org.World.BlockTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Chunk extends Drawable{
    private final int chunkX;
    private final int chunkZ;

    private BlockTypes[][][] blocks;

    public Chunk(int chunkX,int chunkZ){
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;

        this.pos=new Vector3(chunkX*16,0,chunkZ*16);
        this.scale=new Vector3(1,1,1);

        blocks=new BlockTypes[50][16][16];

        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    int level=(x+z)/2;
                    if(y>level)
                        blocks[y][x][z]=BlockTypes.AIR;
                    else if(y==level)
                        blocks[y][x][z]=BlockTypes.GRASS;
                    else if(y>level-3)
                        blocks[y][x][z]=BlockTypes.DIRT;
                    else
                        blocks[y][x][z]=BlockTypes.STONE;
                }
            }
        }

        List<Vector3> vertices=new ArrayList<>();
        List<Integer> indices=new ArrayList<>();
        List<Color> faceColours=new ArrayList<>();

        int indexBase=0;
        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    if(blocks[y][x][z]==BlockTypes.AIR)
                        continue;

                    //-z
                    if(z==0||blocks[y][x][z-1]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x - 0.5f, y - 0.5f, z - 0.5f),
                                new Vector3(x + 0.5f, y - 0.5f, z - 0.5f),
                                new Vector3(x + 0.5f, y + 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z - 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()]
                        }));

                        indexBase+=4;
                    }

                    //-x
                    if(x==0||blocks[y][x-1][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x - 0.5f, y - 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z + 0.5f),
                                new Vector3(x - 0.5f, y - 0.5f, z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+1],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+1]
                        }));

                        indexBase+=4;
                    }

                    //+z
                    if(z==15||blocks[y][x][z+1]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x + 0.5f, y - 0.5f, z + 0.5f),
                                new Vector3(x - 0.5f, y - 0.5f, z + 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z + 0.5f),
                                new Vector3(x + 0.5f, y + 0.5f, z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+2],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+2]
                        }));

                        indexBase+=4;
                    }

                    //+x
                    if(x==15||blocks[y][x+1][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x + 0.5f, y - 0.5f, z + 0.5f),
                                new Vector3(x + 0.5f, y + 0.5f, z + 0.5f),
                                new Vector3(x + 0.5f, y + 0.5f, z - 0.5f),
                                new Vector3(x + 0.5f, y - 0.5f, z - 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+3],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+3]
                        }));

                        indexBase+=4;
                    }

                    //+y
                    if(y==49||blocks[y+1][x][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x + 0.5f, y + 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y + 0.5f, z + 0.5f),
                                new Vector3(x + 0.5f, y + 0.5f, z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+1,indexBase+2,indexBase+2,indexBase+3,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+4],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+4]
                        }));

                        indexBase+=4;
                    }

                    //+-y
                    if(y==0||blocks[y-1][x][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(x + 0.5f, y - 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y - 0.5f, z - 0.5f),
                                new Vector3(x - 0.5f, y - 0.5f, z + 0.5f),
                                new Vector3(x + 0.5f, y - 0.5f, z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+5],
                                BlockColours.blockColours[6*blocks[y][x][z].ordinal()+5]
                        }));

                        indexBase+=4;
                    }
                }
            }
        }

        int length=vertices.size();
        this.vertices=new Vector3[length];
        float minX=900;
        float maxX=-900;
        for(int i=0;i<length;i++) {
            this.vertices[i] = vertices.get(i);
            if(vertices.get(i).get(0)<minX)
                minX=vertices.get(i).get(0);
            if(vertices.get(i).get(0)>maxX)
                maxX=vertices.get(i).get(0);
        }
        System.out.println(minX+" "+maxX);

        length=indices.size();
        this.indices=new int[length];
        for(int i=0;i<length;i++)
            this.indices[i]=indices.get(i);

        length=faceColours.size();
        this.faceColors=new Color[length];
        for(int i=0;i<length;i++)
            this.faceColors[i]=faceColours.get(i);

        calculateModelMatrix();

        this.setName("amogus");
    }
}
