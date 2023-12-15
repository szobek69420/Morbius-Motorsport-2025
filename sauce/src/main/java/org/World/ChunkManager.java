package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Settings;
import main.java.org.Updateable.Player;

import java.util.*;

public class ChunkManager {
    private int CHUNK_RENDER_DISTANCE;

    private List<Chunk> loadedChunks;
    private List<ChunkUpdate> pendingUpdates;


    private Map<ChangedBlockKey,List<ChangedBlock>> changedBlocks;

    private int chunkUpdates=0;
    public int getChunkUpdates(){return chunkUpdates;}
    public void setChunkUpdates(int chunkUpdates){this.chunkUpdates=chunkUpdates;}


    public ChunkManager(){
        loadedChunks=new ArrayList<>();
        changedBlocks=new HashMap<>();
        pendingUpdates=new ArrayList<ChunkUpdate>();

        CHUNK_RENDER_DISTANCE= Settings.renderDistance;
    }

    public void loadChunk(int playerChunkX, int playerChunkZ, Player player){
        for(int i=0;i<=CHUNK_RENDER_DISTANCE;i++){
            for(int j=-i;j<=i;j++){
                for(int k=-i;k<=i;k++){
                    if(Math.abs(j)!=i&&Math.abs(k)!=i){
                        continue;
                    }

                    if(!isChunkPending(playerChunkX+j,playerChunkZ+k)&&!isChunkLoaded(playerChunkX+j,playerChunkZ+k)&&Camera.main!=null){
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
            boolean shouldChunkBeAdded=true;
            for(ChunkUpdate cu :pendingUpdates){
                if(cu.type== ChunkUpdate.Type.RELOAD&&cu.chunkX==chomk.chunkX&&cu.chunkZ==chomk.chunkZ){
                    shouldChunkBeAdded=false;
                    break;
                }
            }

            if(shouldChunkBeAdded)
                pendingUpdates.add(0,new ChunkUpdate(chomk.chunkX,chomk.chunkZ, ChunkUpdate.Type.RELOAD,chomk));
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
                    //nem eleg meghivni a regenerate-et, mert ha render kozben irja felul, az szivas
                    //cu.chunk.regenerateChunk(changedBlocks);
                    Chunk reloaded=new Chunk(cu.chunkX,cu.chunkZ,player,changedBlocks);
                    Camera.main.removeDrawable(cu.chunk);

                    loadedChunks.set(loadedChunks.indexOf(cu.chunk),reloaded);
                    Camera.main.addDrawable(reloaded);
                }
            }

            chunkUpdates++;
        }
    }

    private boolean isChunkLoaded(int chunkX, int chunkZ){
        for(Chunk chomk : loadedChunks){
            if(chomk.chunkX==chunkX&&chomk.chunkZ==chunkZ)
                return true;
        }

        return false;
    }

    private boolean isChunkPending(int chunkX, int chunkZ){
        for(ChunkUpdate chomk: pendingUpdates){
            if(chomk.chunkX==chunkX&&chomk.chunkZ==chunkZ)
                return true;
        }

        return false;
    }

    public void unloadChunk(int playerChunkX, int playerChunkZ){
        for(Chunk chomk : loadedChunks){
            if(Math.abs(playerChunkX-chomk.chunkX)>CHUNK_RENDER_DISTANCE||Math.abs(playerChunkZ-chomk.chunkZ)>CHUNK_RENDER_DISTANCE){
                if(!isChunkPending(chomk.chunkX,chomk.chunkZ)&&isChunkLoaded(chomk.chunkX,chomk.chunkZ)){
                    pendingUpdates.add(new ChunkUpdate(chomk.chunkX,chomk.chunkZ, ChunkUpdate.Type.UNLOAD,chomk));
                    break;
                }
            }
        }
    }

    public void calculatePhysics(double deltaTime,Vector3 pos){

        int[] temp=getChunk(pos);
        int playerChunkX=temp[0];
        int playerChunkZ=temp[1];
        int playerPosInChunkX=(int)(0.5f+pos.get(0)-16.0f*playerChunkX);
        int playerPosInChunkZ=(int)(0.5f+pos.get(2)-16.0f*playerChunkZ);

        for(Chunk chomk : loadedChunks){
            if(chomk.chunkX==playerChunkX&&chomk.chunkZ==playerChunkZ){
                chomk.calculatePhysics(deltaTime);
            }
        }

        int neighbourX=playerChunkX;
        int neighbourZ=playerChunkZ;

        if(playerPosInChunkX==0)
            neighbourX--;
        if(playerPosInChunkX==15)
            neighbourX++;
        if(playerPosInChunkZ==0)
            neighbourZ--;
        if(playerPosInChunkZ==15)
            neighbourZ++;

        if(neighbourZ==playerChunkZ&&neighbourX==playerChunkX)
            return;

        for(Chunk chomk : loadedChunks){
            if(chomk.chunkX==neighbourX&&chomk.chunkZ==neighbourZ){
                chomk.calculatePhysics(deltaTime);
            }
        }
    }


    public RaycastHit gaycast(Vector3 pos, Vector3 direction, float range){
        Vector3.normalize(direction);
        direction=Vector3.multiplyWithScalar(0.01f,direction);//a pontossag 1/100 meter

        int range2=(int)(range*100);

        int[] chunkPos;
        RaycastHit rh;
        for(int i=0;i<range2;i++){
            pos=Vector3.sum(pos,direction);
            chunkPos=getChunk(pos);
            for(Chunk chomk:loadedChunks){
                if(chomk.chunkX==chunkPos[0]&&chomk.chunkZ==chunkPos[1]){
                    rh=chomk.isThereBlock(pos);
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

    public int getPendingCount(){
        return pendingUpdates.size();
    }

    public static int[] getChunk(Vector3 pos){
        int[] chunkPos=new int[2];

        if(pos.get(0)<0){
            chunkPos[0]=((int)(pos.get(0)-15.5f))/16;
        }
        else{
            chunkPos[0]=((int)(pos.get(0)+0.5f))/16;
        }

        if(pos.get(2)<0){
            chunkPos[1]=((int)(pos.get(2)-15.5f))/16;
        }
        else{
            chunkPos[1]=((int)(pos.get(2)+0.5f))/16;
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
