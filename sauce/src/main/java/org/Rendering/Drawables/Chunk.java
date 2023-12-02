package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.Vector3;
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
                    if(y<(x+z)/2)
                        blocks[y][x][z]=BlockTypes.STONE;
                    else
                        blocks[y][x][z]=BlockTypes.AIR;
                }
            }
        }

        List<Vector3> vertices=new ArrayList<>();
        List<Integer> indices=new ArrayList<>();
        List<Color> faceColours=new ArrayList<>();

        float baseX=chunkX*16;
        float baseZ=chunkZ*16;
        int indexBase=0;
        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    if(blocks[y][x][z]==BlockTypes.AIR)
                        continue;

                    //-z
                    if(z==0||blocks[y][x][z-1]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z - 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(200,200,200),
                                new Color(200,200,200),
                        }));

                        indexBase+=4;
                    }

                    //-x
                    if(x==0||blocks[y][x-1][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(160,160,160),
                                new Color(160,160,160),
                        }));

                        indexBase+=4;
                    }

                    //+z
                    if(z==15||blocks[y][x][z+1]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(110,110,110),
                                new Color(110,110,110),
                        }));

                        indexBase+=4;
                    }

                    //+x
                    if(x==15||blocks[y][x+1][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z - 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(140,140,140),
                                new Color(140,140,140),
                        }));

                        indexBase+=4;
                    }

                    //+y
                    if(y==49||blocks[y+1][x][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y + 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x + 0.5f, y + 0.5f, baseZ + z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+1,indexBase+2,indexBase+2,indexBase+3,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(230,230,230),
                                new Color(230,230,230),
                        }));

                        indexBase+=4;
                    }

                    //+-y
                    if(y==0||blocks[y-1][x][z]==BlockTypes.AIR){
                        vertices.addAll(List.of(new Vector3[]{
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z - 0.5f),
                                new Vector3(baseX + x - 0.5f, y - 0.5f, baseZ + z + 0.5f),
                                new Vector3(baseX + x + 0.5f, y - 0.5f, baseZ + z + 0.5f),
                        }));
                        indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                        faceColours.addAll(List.of(new Color[]{
                                new Color(80,80,80),
                                new Color(80,80,80),
                        }));

                        indexBase+=4;
                    }
                }
            }
        }

        int length=vertices.size();
        this.vertices=new Vector3[length];
        for(int i=0;i<length;i++)
            this.vertices[i]=vertices.get(i);

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
        System.out.println(this.vertices.length+" "+this.indices.length+" "+this.faceColors.length);
    }
}
