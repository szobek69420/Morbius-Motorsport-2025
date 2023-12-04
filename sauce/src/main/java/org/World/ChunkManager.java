package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Updateable.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManager {
    public static final int CHUNK_RENDER_DISTANCE=2;

    private List<Chunk> loadedChunks;

    private Map<ChangedBlockKey,List<ChangedBlock>> changedBlocks;


    public ChunkManager(){
        loadedChunks=new ArrayList<>();
        changedBlocks=new HashMap<>();
    }

    public void loadChunk(int playerChunkX, int playerChunkZ, Player player){
        for(int i=0;i<=CHUNK_RENDER_DISTANCE;i++){
            for(int j=-i;j<=i;j++){
                for(int k=-i;k<=i;k++){
                    if(Math.abs(j)!=i&&Math.abs(k)!=i){
                        continue;
                    }

                    if(!isChunkLoaded(playerChunkX+j,playerChunkZ+k)&&Camera.main!=null){
                        if(!changedBlocks.containsKey(new ChangedBlockKey(playerChunkX+j,playerChunkZ+k))){

                            ChangedBlockKey cbt=new ChangedBlockKey(playerChunkX+j,playerChunkZ+k);
                            changedBlocks.put(cbt, new ArrayList<>());
                        }

                        Chunk chomk=new Chunk(playerChunkX+j,playerChunkZ+k, player, changedBlocks);
                        loadedChunks.add(chomk);
                        Camera.main.addDrawable(chomk);

                        System.out.println("chunk "+(playerChunkX+j)+" "+(playerChunkZ+k+" loaded"));
                        return;
                    }
                }
            }
        }
    }

    public void changeBlock(int chunkX, int chunkZ, int x, int y, int z, BlockTypes block){
        ChangedBlockKey cbt=new ChangedBlockKey(chunkX, chunkZ);
        if(!changedBlocks.containsKey(cbt)){
            changedBlocks.put(cbt, new ArrayList<>());
        }
        changedBlocks.get(cbt).add(new ChangedBlock(block, x, y, z));
    }

    public void reloadChunk(int chunkX, int chunkZ,Player player){
        boolean containsChunk=false;

        for(Chunk chomk :loadedChunks){
            if(chomk.chunkX==chunkX&&chomk.chunkZ==chunkZ){
                Camera.main.removeDrawable(chomk);
                loadedChunks.remove(chomk);
                containsChunk=true;
                break;
            }
        }

        if(containsChunk){
            Chunk chomk=new Chunk(chunkX,chunkZ,player,changedBlocks);
            Camera.main.addDrawable(chomk);
            loadedChunks.add(chomk);
        }
    }

    private boolean isChunkLoaded(int chunkX, int chunkZ){
        for(Chunk chomk : loadedChunks){
            if(chomk.chunkX==chunkX&&chomk.chunkZ==chunkZ)
                return true;
        }

        return false;
    }

    public void unloadChunk(int playerChunkX, int playerChunkZ){
        for(Chunk chomk : loadedChunks){
            if(Math.abs(playerChunkX-chomk.chunkX)>CHUNK_RENDER_DISTANCE||Math.abs(playerChunkZ-chomk.chunkZ)>CHUNK_RENDER_DISTANCE){
                Camera.main.removeDrawable(chomk);
                loadedChunks.remove(chomk);
                break;
            }
        }
    }

    public void calculatePhysics(double deltaTime,int playerChunkX, int playerChunkZ){
        for(Chunk chomk:loadedChunks){
            if(Math.abs(chomk.chunkX-playerChunkX)<2&&Math.abs(chomk.chunkZ-playerChunkZ)<2)
                chomk.calculatePhysics(deltaTime);
        }
    }


    public RaycastHit gaycast(Vector3 pos, Vector3 direction, float range){
        Vector3.normalize(direction);
        direction=Vector3.multiplyWithScalar(0.02f,direction);//a pontossag 1/50 meter

        int range2=(int)(range*50);

        int[] chunkPos;
        RaycastHit rh;
        for(int i=0;i<range2;i++){
            pos=Vector3.sum(pos,direction);
            chunkPos=getChunk(pos);
            for(Chunk chomk:loadedChunks){
                if(chomk.chunkX==chunkPos[0]&&chomk.chunkZ==chunkPos[1]){
                    rh=chomk.cd.isThereCollider(pos);
                    if(rh!=null)
                        return rh;
                }
            }
        }

        return null;
    }

    public static int[] getChunk(Vector3 pos){
        int[] chunkPos=new int[2];

        if(pos.get(0)<0){
            chunkPos[0]=((int)(pos.get(0)-17.0f))/16;
        }
        else{
            chunkPos[0]=((int)pos.get(0))/16;
        }

        if(pos.get(2)<0){
            chunkPos[1]=((int)(pos.get(2)-17.0f))/16;
        }
        else{
            chunkPos[1]=((int)pos.get(2))/16;
        }

        return chunkPos;
    }
}
