package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Noice.OpenSimplex2S;
import main.java.org.Physics.AABB;
import main.java.org.Physics.CollisionDetection;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Drawables.Drawable;
import main.java.org.Updateable.Player;
import main.java.org.World.BlockColours;
import main.java.org.World.BlockTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Chunk extends Drawable {
    public static final int SEED=new Random().nextInt();

    public final int chunkX;
    public final int chunkZ;

    private BlockTypes[][][] blocks;

    private Biomes.BiomeTypes[][] biomeMap;
    private float[][] terrainMap;
    private int[][] mountainLineMap;
    private int[][] heightMap;

    private final CollisionDetection cd;

    public Chunk(int chunkX, int chunkZ, Player player, Map<ChangedBlockKey,List<ChangedBlock>> changedBlocks){
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;

        this.pos=new Vector3(chunkX*16,0,chunkZ*16);
        this.scale=new Vector3(1,1,1);

        this.blocks=new BlockTypes[50][16][16];

        this.biomeMap=new Biomes.BiomeTypes[18][18];
        this.terrainMap=new float[18][18];
        this.mountainLineMap=new int[18][18];
        this.heightMap=new int[18][18];

        cd=new CollisionDetection();
        player.addToPhysics(cd);

        this.regenerateChunk(changedBlocks);

        calculateModelMatrix();

        this.setName("amogus");
    }

    public void regenerateChunk(Map<ChangedBlockKey,List<ChangedBlock>> changedBlocks){
        this.cd.clear(true);

        int basedX=chunkX*16;
        int basedZ=chunkZ*16;


        for(int i=0;i<18;i++){
            for(int j=0;j<18;j++){
                float tempBiome=OpenSimplex2S.noise3_ImproveXY(SEED, (basedX+i-1) * 0.0017, (basedZ+j-1) * 0.0019, 0.0)+0.1f*OpenSimplex2S.noise3_ImproveXY(SEED, (basedX+i-1) * 0.031, (basedZ+j-1) * 0.017, 0.0);
                biomeMap[i][j]=tempBiome>0? Biomes.BiomeTypes.GRASSY: Biomes.BiomeTypes.SANDY;

                mountainLineMap[i][j]=(int)(35+3*OpenSimplex2S.noise3_ImproveXY(SEED, (basedX+i-1) * 0.017+0.23, (basedZ+j-1) * 0.023+0.83, 0.0));

                terrainMap[i][j]=0.5f+0.4f*OpenSimplex2S.noise3_ImproveXY(SEED, (basedX+i-1) * 0.007, (basedZ+j-1) * 0.013, 0.0)+0.1f*OpenSimplex2S.noise3_ImproveXY(SEED, (basedX+i-1) * 0.037, (basedZ+j-1) * 0.031, 0.0);
                terrainMap[i][j]*=terrainMap[i][j];

                heightMap[i][j]=5+(int)(43*terrainMap[i][j]);
            }
        }

        //terrain
        for(int y=0;y<50;y++){
            for(int x=0;x<16;x++){
                for(int z=0;z<16;z++){
                    int level= heightMap[x+1][z+1];
                    int biomeIndex=biomeMap[x+1][z+1].ordinal();

                    if(y>level)
                        blocks[y][x][z]=BlockTypes.AIR;
                    else if(y==level){
                        if(level>mountainLineMap[x+1][z+1])
                            blocks[y][x][z]= Biomes.upperLayerMountainous[biomeIndex];
                        else
                            blocks[y][x][z]=Biomes.upperLayer[biomeIndex];
                    }
                    else if(y>level-3){
                        if(level>mountainLineMap[x+1][z+1]){
                            blocks[y][x][z]=Biomes.middleLayerMountainous[biomeIndex];
                        }
                        else
                            blocks[y][x][z]=Biomes.middleLayer[biomeIndex];
                    }
                    else if(y==0)
                        blocks[y][x][z]=BlockTypes.BEDROCK;
                    else
                        blocks[y][x][z]=Biomes.lowerLayer[biomeIndex];
                }
            }
        }

        //trees and cockti
        Random random=new Random(chunkX*10000+chunkZ);
        for(int x=2;x<14;x++){
            for(int z=2;z<14;z++){
                //tree can spawn
                int y=heightMap[x+1][z+1];
                if(y>43)
                    continue;

                if(random.nextFloat()>0.99f){
                    switch (blocks[y][x][z]){
                        case GRASS:
                            blocks[y+1][x][z]=BlockTypes.LOG;
                            blocks[y+2][x][z]=BlockTypes.LOG;
                            blocks[y+3][x][z]=BlockTypes.LOG;
                            blocks[y+4][x][z]=BlockTypes.LOG;
                            blocks[y+4][x][z+1]=BlockTypes.LEAVES;
                            blocks[y+4][x][z+2]=BlockTypes.LEAVES;
                            blocks[y+4][x][z-1]=BlockTypes.LEAVES;
                            blocks[y+4][x][z-2]=BlockTypes.LEAVES;
                            blocks[y+4][x+1][z]=BlockTypes.LEAVES;
                            blocks[y+4][x+2][z]=BlockTypes.LEAVES;
                            blocks[y+4][x-1][z]=BlockTypes.LEAVES;
                            blocks[y+4][x-2][z]=BlockTypes.LEAVES;
                            blocks[y+5][x][z]=BlockTypes.LEAVES;
                            blocks[y+6][x][z]=BlockTypes.LEAVES;
                            break;

                        case SAND:
                            if(random.nextFloat()>0.8f){
                                int height=6;
                                height=(int)(height*random.nextFloat());
                                for(int i=1;i<=height&&i<50;i++)
                                    blocks[y+i][x][z]=BlockTypes.COCKTUS;
                            }
                            break;
                    }
                }
            }
        }

        //changed blocks
        List<ChangedBlock> changedInThisChunk=changedBlocks.get(new ChangedBlockKey(chunkX, chunkZ));
        for(ChangedBlock cbt : changedInThisChunk)
            blocks[cbt.y][cbt.x][cbt.z]=cbt.block;

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

                    //-y
                    if(y!=0&&blocks[y-1][x][z]==BlockTypes.AIR){
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

        //neighbouring changed blocks
        List<ChangedBlock> changed;

        //-z
        changed=changedBlocks.get(new ChangedBlockKey(chunkX, chunkZ-1));
        if(changed!=null){
            for(ChangedBlock cbt : changed) {
                if(cbt.z!=15)
                    continue;

                if(blocks[cbt.y][cbt.x][0]==BlockTypes.AIR)
                    continue;

                vertices.addAll(List.of(new Vector3[]{
                        new Vector3(cbt.x - 0.5f, cbt.y - 0.5f, -0.5f),
                        new Vector3(cbt.x + 0.5f, cbt.y - 0.5f, -0.5f),
                        new Vector3(cbt.x + 0.5f, cbt.y + 0.5f, -0.5f),
                        new Vector3(cbt.x - 0.5f, cbt.y + 0.5f, -0.5f),
                }));
                indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                faceColours.addAll(List.of(new Color[]{
                        BlockColours.blockColours[6*blocks[cbt.y][cbt.x][0].ordinal()],
                        BlockColours.blockColours[6*blocks[cbt.y][cbt.x][0].ordinal()]
                }));

                indexBase+=4;

                cd.addAABB(new AABB(new Vector3(basedX+cbt.x,cbt.y,basedZ),new Vector3(0.5f,0.5f,0.5f),true,"amogus"));
            }
        }

        //-x
        changed=changedBlocks.get(new ChangedBlockKey(chunkX-1, chunkZ));
        if(changed!=null){
            for(ChangedBlock cbt : changed) {
                if(cbt.x!=15)
                    continue;

                if(blocks[cbt.y][0][cbt.z]==BlockTypes.AIR)
                    continue;

                vertices.addAll(List.of(new Vector3[]{
                        new Vector3(-0.5f, cbt.y - 0.5f, cbt.z - 0.5f),
                        new Vector3(-0.5f, cbt.y + 0.5f, cbt.z - 0.5f),
                        new Vector3(-0.5f, cbt.y + 0.5f, cbt.z + 0.5f),
                        new Vector3(-0.5f, cbt.y - 0.5f, cbt.z + 0.5f),
                }));
                indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                faceColours.addAll(List.of(new Color[]{
                        BlockColours.blockColours[6*blocks[cbt.y][0][cbt.z].ordinal()+1],
                        BlockColours.blockColours[6*blocks[cbt.y][0][cbt.z].ordinal()+1]
                }));

                indexBase+=4;

                cd.addAABB(new AABB(new Vector3(basedX,cbt.y,basedZ+cbt.z),new Vector3(0.5f,0.5f,0.5f),true,"amogus"));
            }
        }

        //+z
        changed=changedBlocks.get(new ChangedBlockKey(chunkX, chunkZ+1));
        if(changed!=null){
            for(ChangedBlock cbt : changed) {
                if(cbt.z!=0)
                    continue;

                if(blocks[cbt.y][cbt.x][15]==BlockTypes.AIR)
                    continue;

                vertices.addAll(List.of(new Vector3[]{
                        new Vector3(cbt.x + 0.5f, cbt.y - 0.5f, 15.5f),
                        new Vector3(cbt.x - 0.5f, cbt.y - 0.5f, 15.5f),
                        new Vector3(cbt.x - 0.5f, cbt.y + 0.5f, 15.5f),
                        new Vector3(cbt.x + 0.5f, cbt.y + 0.5f, 15.5f),
                }));
                indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                faceColours.addAll(List.of(new Color[]{
                        BlockColours.blockColours[6*blocks[cbt.y][cbt.x][15].ordinal()+2],
                        BlockColours.blockColours[6*blocks[cbt.y][cbt.x][15].ordinal()+2]
                }));

                indexBase+=4;

                cd.addAABB(new AABB(new Vector3(basedX+cbt.x,cbt.y,basedZ+15),new Vector3(0.5f,0.5f,0.5f),true,"amogus"));
            }
        }

        //+x
        changed=changedBlocks.get(new ChangedBlockKey(chunkX+1, chunkZ));
        if(changed!=null){
            for(ChangedBlock cbt : changed) {
                if(cbt.x!=0)
                    continue;

                if(blocks[cbt.y][15][cbt.z]==BlockTypes.AIR)
                    continue;

                vertices.addAll(List.of(new Vector3[]{
                        new Vector3(15.5f, cbt.y - 0.5f, cbt.z + 0.5f),
                        new Vector3(15.5f, cbt.y + 0.5f, cbt.z + 0.5f),
                        new Vector3(15.5f, cbt.y + 0.5f, cbt.z - 0.5f),
                        new Vector3(15.5f, cbt.y - 0.5f, cbt.z - 0.5f),
                }));
                indices.addAll(List.of(new Integer[]{indexBase,indexBase+2,indexBase+1,indexBase+3,indexBase+2,indexBase}));
                faceColours.addAll(List.of(new Color[]{
                        BlockColours.blockColours[6*blocks[cbt.y][15][cbt.z].ordinal()+3],
                        BlockColours.blockColours[6*blocks[cbt.y][15][cbt.z].ordinal()+3]
                }));

                indexBase+=4;

                cd.addAABB(new AABB(new Vector3(basedX+15,cbt.y,basedZ+cbt.z),new Vector3(0.5f,0.5f,0.5f),true,"amogus"));
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
    }

    public void calculatePhysics(double deltaTime){
        cd.CalculatePhysics(deltaTime);
    }

    public RaycastHit isThereBlock(Vector3 point){
        int[] chunkPos= ChunkManager.getChunk(point);
        int[] blockPos=new int[3];

        blockPos[0]=Math.round(point.get(0))-chunkPos[0]*16;
        blockPos[1]=Math.round(point.get(1));
        blockPos[2]=Math.round(point.get(2))-chunkPos[1]*16;

        if(blockPos[1]<0||blockPos[1]>49||blockPos[0]<0||blockPos[0]>15||blockPos[2]<0||blockPos[2]>15)
            return null;

        if(blocks[blockPos[1]][blockPos[0]][blockPos[2]]==BlockTypes.AIR)
            return null;

        return new RaycastHit(
                point,
                chunkPos[0],
                chunkPos[1],
                blockPos[0],
                blockPos[1],
                blockPos[2],
                blocks[blockPos[1]][blockPos[0]][blockPos[2]]
        );
    }
}
