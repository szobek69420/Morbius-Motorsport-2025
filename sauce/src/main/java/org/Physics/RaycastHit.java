package main.java.org.Physics;

import main.java.org.LinearAlgebruh.Vector3;

public class RaycastHit {
    public final Vector3 point;
    public final int chunkX;
    public final int chunkZ;

    //coordinates inside a chunk
    public final int x,y,z;

    public RaycastHit(Vector3 point, int chunkX, int chunkZ, int x, int y, int z){
        this.point=point;
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;
        this.x=x;
        this.y=y;
        this.z=z;
    }
}
