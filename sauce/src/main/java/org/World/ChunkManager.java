package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Updateable.Player;

import java.util.*;

public class ChunkManager {
    public static final int CHUNK_RENDER_DISTANCE=1;

    private List<Chunk> loadedChunks;
    private List<ChunkUpdate> pendingUpdates;


    private Map<ChangedBlockKey,List<ChangedBlock>> changedBlocks;


    public ChunkManager(){
        loadedChunks=new ArrayList<>();
        changedBlocks=new HashMap<>();
        pendingUpdates=new ArrayList<ChunkUpdate>();
    }

    public void loadChunk(int playerChunkX, int playerChunkZ, Player player){
        for(int i=0;i<=CHUNK_RENDER_DISTANCE;i++){
            for(int j=-i;j<=i;j++){
                for(int k=-i;k<=i;k++){
                    if(Math.abs(j)!=i&&Math.abs(k)!=i){
                        continue;
                    }

                    if(!isChunkLoaded(playerChunkX+j,playerChunkZ+k)&&Camera.main!=null){
                        pendingUpdates.add(new ChunkUpdate(playerChunkX+j,playerChunkZ+k, ChunkUpdate.Type.LOAD,null));
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

    public void reloadChunk(Chunk chomk){
        if(isChunkLoaded(chomk.chunkX,chomk.chunkZ)){
            pendingUpdates.add(0,new ChunkUpdate(-69,-69, ChunkUpdate.Type.RELOAD,chomk));
        }
    }

    public void updateChunks(Player player){
        if(!pendingUpdates.isEmpty()){
            ChunkUpdate cu=pendingUpdates.get(0);
            pendingUpdates.remove(0);
            switch(cu.type){
                case LOAD -> {
                    //load if it was not
                    if(!changedBlocks.containsKey(new ChangedBlockKey(cu.chunkX,cu.chunkZ))){
                        ChangedBlockKey cbt=new ChangedBlockKey(cu.chunkX,cu.chunkZ);
                        changedBlocks.put(cbt, new ArrayList<>());
                    }

                    Chunk chomk=new Chunk(cu.chunkX,cu.chunkZ, player, changedBlocks);
                    loadedChunks.add(chomk);
                    Camera.main.addDrawable(chomk);
                }
                case UNLOAD -> {
                    Camera.main.removeDrawable(cu.chunk);
                    loadedChunks.remove(cu.chunk);
                }
                case RELOAD -> {
                    Camera.main.removeDrawable(cu.chunk);
                    cu.chunk.regenerateChunk(changedBlocks);
                    Camera.main.addDrawable(cu.chunk);
                }
            }
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
                pendingUpdates.add(new ChunkUpdate(-69,-69, ChunkUpdate.Type.UNLOAD,chomk));
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

    public Chunk getChunkAtPos(int chunkX, int chunkZ){
        for(Chunk chomk : loadedChunks){
            if(chomk.chunkX==chunkX&&chomk.chunkZ==chunkZ)
                return chomk;
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

    public static class ChunkUpdate{

        public static enum Type{
            LOAD,
            UNLOAD,
            RELOAD
        };

        public final int chunkX;
        public final int chunkZ;
        public final Chunk chunk;
        public final Type type;

        public ChunkUpdate(int chunkX, int chunkZ,Type type,Chunk chunk){
            this.chunkX=chunkX;
            this.chunkZ=chunkZ;
            this.type=type;
            this.chunk=chunk;
        }
    }
}
