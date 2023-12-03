package main.java.org.World;

public class ChangedBlockKey {
    public final int chunkX;
    public final int chunkZ;

    public ChangedBlockKey(int chunkX, int chunkZ)
    {
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;
    }

    @Override
    public boolean equals(Object other){
        if(((ChangedBlockKey)other).chunkX==this.chunkX&&((ChangedBlockKey)other).chunkZ==this.chunkZ)
            return true;

        return false;
    }

    @Override
    public int hashCode(){
        return chunkX*10000+chunkZ;
    }
}
