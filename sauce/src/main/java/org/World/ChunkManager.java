package main.java.org.World;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Updateable.Player;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager {
    public static final int CHUNK_RENDER_DISTANCE=3;

    private List<Chunk> loadedChunks;

    public ChunkManager(){
        loadedChunks=new ArrayList<>();
    }

    public void loadChunk(int playerChunkX, int playerChunkZ, Player player){
        for(int i=0;i<=CHUNK_RENDER_DISTANCE;i++){
            for(int j=-i;j<=i;j++){
                for(int k=-i;k<=i;k++){
                    if(Math.abs(j)!=i&&Math.abs(k)!=i){
                        continue;
                    }

                    if(!isChunkLoaded(playerChunkX+j,playerChunkZ+k)&&Camera.main!=null){
                        Chunk chomk=new Chunk(playerChunkX+j,playerChunkZ+k, player);
                        loadedChunks.add(chomk);
                        Camera.main.addDrawable(chomk);
                        return;
                    }
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
