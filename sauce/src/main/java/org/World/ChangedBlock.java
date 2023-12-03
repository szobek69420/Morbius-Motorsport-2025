package main.java.org.World;

public class ChangedBlock{
    //coordinates inside the chunk
    public final int x;
    public final int y;
    public final int z;

    public final BlockTypes block;

    public ChangedBlock(BlockTypes block, int x, int y, int z){
        this.block=block;
        this.x=x;
        this.y=y;
        this.z=z;
    }
}