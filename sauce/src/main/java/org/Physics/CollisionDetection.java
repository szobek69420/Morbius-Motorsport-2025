package main.java.org.Physics;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.World.ChunkManager;

import java.util.ArrayList;

/**
 * Das Physiksystem
 */
public class CollisionDetection {
    /**
     * Liste von solchen Collidern, die nicht bewegt werden können
     */
    private ArrayList<AABB> nonKinematic;
    /**
     * Liste von solchen Collidern, die bewegt werden können
     */
    private ArrayList<AABB> kinematic;

    /**
     * Erzeugt eine neue Physiksysteminstanz
     */
    public CollisionDetection(){
        nonKinematic=new ArrayList<>();
        kinematic=new ArrayList<>();
    }

    /**
     * Registrieren einen Collider zum Physiksystem.
     * Falls der Collider kinematisch ist, dann wird zur kinematic-Liste hinzufügt, andererweise zur nonKinematic-Liste
     * @param aabb der zu registrierende Collider
     */
    public void addAABB(AABB aabb){
        if(aabb.isKinematic)
            kinematic.add(aabb);
        else
            nonKinematic.add(aabb);
    }

    /**
     * Lösch einen Collider von dem Physiksystem
     * @param aabb der zu löschene Collider
     */
    public void removeAABB(AABB aabb){
        if(aabb.isKinematic)
            kinematic.remove(aabb);
        else
            nonKinematic.remove(aabb);
    }

    /**
     * Simuliert die Bewegung der Körper.
     * Überprüft, dass es keinen solchen Collider gibt, die mit einem anderen Collider überlappen.
     * @param deltaTime die Zeit, die seit dem letzten Frame verging.
     */
    public void CalculatePhysics(double deltaTime){
        for(AABB aabb:nonKinematic){
            for (AABB aabb2:kinematic){
                AABB.resolveCollision(aabb,aabb2);
            }
        }
    }

    public RaycastHit isThereCollider(Vector3 point){
        for(AABB aabb:kinematic){
            if(aabb.isInsideTheCollider(point)){
                int[] chunkPos= ChunkManager.getChunk(point);
                return new RaycastHit(
                        point,
                        chunkPos[0],
                        chunkPos[1],
                        Math.round(aabb.getPositionByReference().get(0))-chunkPos[0]*16,
                        Math.round(aabb.getPositionByReference().get(1)),
                        Math.round(aabb.getPositionByReference().get(2))-chunkPos[1]*16
                );
            }
        }

        return null;
    }
}
