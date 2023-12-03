package main.java.org.Physics;

import main.java.org.LinearAlgebruh.Vector3;

public class RaycastHit {
    public Vector3 point;
    public int chunkX;
    public int chunkZ;

    //coordinates inside a chunk
    public int x,y,z;

    public RaycastHit(Vector3 point, int chunkX, int chunkZ, int x, int y, int z){
        this.point=point;
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;
        this.x=x;
        this.y=y;
        this.z=z;
    }
}
