package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Noice.OpenSimplex2S;
import main.java.org.Physics.AABB;
import main.java.org.Physics.CollisionDetection;
import main.java.org.Rendering.Drawables.Drawable;
import main.java.org.Updateable.Player;
import main.java.org.World.BlockColours;
import main.java.org.World.BlockTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Chunk extends Drawable {
    public final int chunkX;
    public final int chunkZ;

    private BlockTypes[][][] blocks;

    private int[][] heightMap;

    private CollisionDetection cd;

    public Chunk(int chunkX,int chunkZ, Player player){
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;

        this.pos=new Vector3(chunkX*16,0,chunkZ*16);
        this.scale=new Vector3(1,1,1);

        int basedX=chunkX*16;
        int basedZ=chunkZ*16;

        cd=new CollisionDetection();
        player.addToPhysics(cd);

        blocks=new BlockTypes[50][16][16];

        heightMap=new int[18][18];

        for(int i=0;i<18;i++){
            for(int j=0;j<18;j++){
                heightMap[i][j]=30+(int)(19*OpenSimplex2S.noise3_ImproveXY(0, (basedX+i-1) * 0.01, (basedZ+j-1) * 0.023, 0.0));
            }
        }

        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    int level= heightMap[x+1][z+1];
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

        boolean shouldHaveCollider;
        int indexBase=0;
        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    if(blocks[y][x][z]==BlockTypes.AIR)
                        continue;

                    shouldHaveCollider=false;
                    //-z
                    if((z==0&&heightMap[x+1][0]<y)||(z!=0&&blocks[y][x][z-1]==BlockTypes.AIR)){
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
                        shouldHaveCollider=true;
                    }

                    //-x
                    if((x==0&&heightMap[0][z+1]<y)||(x!=0&&blocks[y][x-1][z]==BlockTypes.AIR)){
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
                        shouldHaveCollider=true;
                    }

                    //+z
                    if((z==15&&heightMap[x+1][17]<y)||(z!=15&&blocks[y][x][z+1]==BlockTypes.AIR)){
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
                        shouldHaveCollider=true;
                    }

                    //+x
                    if((x==15&&heightMap[17][z+1]<y)||(x!=15&&blocks[y][x+1][z]==BlockTypes.AIR)){
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
                        shouldHaveCollider=true;
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
                        shouldHaveCollider=true;
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
                        shouldHaveCollider=true;
                    }

                    if(shouldHaveCollider)
                        cd.addAABB(new AABB(new Vector3(basedX+x,y,basedZ+z),new Vector3(0.5f,0.5f,0.5f),true,"amogus"));
                }
            }
        }

        int length=vertices.size();
        this.vertices=new Vector3[length];
        for(int i=0;i<length;i++) {
            this.vertices[i] = vertices.get(i);
        }

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

    public void calculatePhysics(double deltaTime){
        cd.CalculatePhysics(deltaTime);
    }
}