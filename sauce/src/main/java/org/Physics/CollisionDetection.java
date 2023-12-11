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

    public void clear(boolean onlyKinematic){
        kinematic.clear();

        if(!onlyKinematic){
            nonKinematic.clear();
        }
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
}
