package main.java.org.Physics;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.World.ChunkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        final int length=kinematic.size();
        List<ColliderSortHelper> array=new ArrayList<>();

        for(AABB aabb:nonKinematic){
            Vector3 currentNonKinematicPos=aabb.getPositionByReference();
            for(int i=0;i<length;i++){
                array.add(new ColliderSortHelper(i,Vector3.sqrMagnitude(Vector3.difference(currentNonKinematicPos,kinematic.get(i).getPositionByReference()))));
            }
            Collections.sort(array);

            for (ColliderSortHelper csh : array){
                AABB.resolveCollision(aabb,kinematic.get(csh.index));
            }
        }
    }

    private static class ColliderSortHelper implements Comparable<ColliderSortHelper>{
        public int index;
        public float distance;

        public ColliderSortHelper(int index, float distance){
            this.index=index;
            this.distance=distance;
        }

        @Override
        public int compareTo(ColliderSortHelper o) {
            if(this.distance<o.distance)
                return -69;
            return 69;
        }
    }
}
