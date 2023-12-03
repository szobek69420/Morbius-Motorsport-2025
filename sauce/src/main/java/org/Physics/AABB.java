package main.java.org.Physics;

import main.java.org.LinearAlgebruh.Vector3;

import java.util.Vector;

/**
 * Die einzige Art von Collider
 */
public class AABB {
    /**
     * Von welcher Richtung der Stoß passierte
     */
    public static enum CollisionType{
        NONE,TOP,BOTTOM,SIDE
    }

    /**
     * Die Position des Colliders
     */
    private Vector3 position;
    /**
     * Die Größe des Colliders
     */
    private Vector3 scale;

    /**
     * Die Geschwindigkeit des Colliders
     */
    private Vector3 velocity;
    /**
     * Der Name des Colliders
     */
    private String name;

    /**
     * Von welcher Richtung die letzte Kollision passierte
     */
    private CollisionType lastCollisionType;
    /**
     * Wann ist es passiert
     */
    private long lastCollision;
    /**
     * Was war der Name des anderen Colliders
     */
    private String lastCollisionName;

    /**
     * Darf dieser Collider sich bewegen?
     */
    public final boolean isKinematic;

    /**
     * Erzeugt eine neue Collider-Instanz
     * @param position die Position des Colliders
     * @param scale die Größe des Colliders
     * @param isKinematic TRUE, falls der Collider sich nicht bewegen kann
     * @param name der Name des Colliders, falls es null ist, wird der Name zum "default" gestellt
     */
    public AABB(Vector3 position, Vector3 scale, boolean isKinematic, String name){
        this.position=position;

        this.scale=scale;

        this.velocity=new Vector3(0,0,0);

        if(name==null)
            this.name="default";
        else
            this.name=name;

        this.lastCollision=0;
        this.lastCollisionType=CollisionType.NONE;
        this.lastCollisionName="";

        this.isKinematic=isKinematic;
    }

    /**
     * Bewegt den Körper mit seiner Geschwindigkeit
     * @param deltaTime die Zeit, die seit dem letzten Frame verging
     */
    public void move(double deltaTime){
        position=Vector3.sum(position,Vector3.multiplyWithScalar((float)deltaTime,velocity));
    }

    /**
     * Löscht die Kollisioninformationen der letzten Kollision
     */
    public void clearHistory(){
        this.lastCollision=0;
        this.lastCollisionType=CollisionType.NONE;
        this.lastCollisionName="";
    }

    public boolean isInsideTheCollider(Vector3 point){
        boolean isInside=true;
        for(int i=0;i<3;i++){
            if(Math.abs(point.get(i)-position.get(i))>scale.get(i)){
                isInside=false;
                break;
            }
        }

        return isInside;
    }

    /**
     * Eine statische Funktion, die Kollisionen zwischen kinematische und nicht kinematische Körper gelöst
     * @param nonKinematic ein nicht kinematischer Körper
     * @param kinematic ein kinematischer Körper
     * @return TRUE, falls die Körper sich gestoßen sind
     */
    public static boolean resolveCollision(AABB nonKinematic, AABB kinematic){
        Vector3 deltaPos=Vector3.difference(nonKinematic.position,kinematic.position);
        Vector3 minDistance=new Vector3(nonKinematic.scale.get(0)+kinematic.scale.get(0),nonKinematic.scale.get(1)+kinematic.scale.get(1),nonKinematic.scale.get(2)+kinematic.scale.get(2));

        float[] delta=new float[3];
        boolean[] isIntersecting=new boolean[]{false,false, false};

        for(int i=0;i<3;i++){
            if(deltaPos.get(i)>0){
                delta[i]=minDistance.get(i)-deltaPos.get(i);
                if(delta[i]>0)
                    isIntersecting[i]=true;
            }
            else{
                delta[i]=-minDistance.get(i)-deltaPos.get(i);
                if(delta[i]<0)
                    isIntersecting[i]=true;
            }
        }

        if(isIntersecting[0]&&isIntersecting[1]&&isIntersecting[2]) {

            float min = Math.abs(delta[0]);
            int index = 0;
            if (min > Math.abs(delta[1])) {
                min = Math.abs(delta[1]);
                index = 1;
            }
            if (min > Math.abs(delta[2])) {
                min = Math.abs(delta[2]);
                index = 2;
            }

            nonKinematic.lastCollision = System.nanoTime();
            nonKinematic.lastCollisionName = kinematic.getName();
            kinematic.lastCollision = System.nanoTime();
            kinematic.lastCollisionName = nonKinematic.getName();

            switch (index) {
                case 1:
                    if (nonKinematic.getVelocityByReference().get(1) > 0) {
                        nonKinematic.lastCollisionType = CollisionType.TOP;
                        kinematic.lastCollisionType = CollisionType.BOTTOM;
                    } else {
                        nonKinematic.lastCollisionType = CollisionType.BOTTOM;
                        kinematic.lastCollisionType = CollisionType.TOP;
                    }
                    break;

                default:
                    nonKinematic.lastCollisionType = CollisionType.SIDE;
                    kinematic.lastCollisionType = CollisionType.SIDE;
                    break;
            }

            nonKinematic.position.set(index, nonKinematic.position.get(index) + delta[index]);
            if (delta[index] > 0 && nonKinematic.velocity.get(index) < 0)
                nonKinematic.velocity.set(index, 0);
            else if (delta[index] < 0 && nonKinematic.velocity.get(index) > 0)
                nonKinematic.velocity.set(index, 0);



            //System.out.println("collision");
            return true;
        }
        else
            return false;
    }

    /**
     * Gibt die Position des Objektes zurück. Der Wert der Position wird nicht in eine andere Vector3-Instanz kopiert.
     * @return die Position des Objektes
     */
    public Vector3 getPositionByReference(){
        return this.position;
    }
    /**
     * Stellt die Position des Objektes
     * @param pos die neue Position
     */
    public void setPosition(Vector3 pos){
        this.position=pos;
    }
    /**
     * Stellt die Größe des Objektes
     * @param scale die neue Größe
     */
    public void setScale(Vector3 scale){
        this.scale=scale;
    }
    /**
     * Gibt der Name des Objektes zurück
     * @return der Name des Objektes
     */
    public String getName(){return name;}
    /**
     * Stellt den Name des Objektes ein
     * @param name der neue Name
     */
    public void setName(String name){
        this.name=name;
    }

    /**
     * Gibt die Geschwindigkeit des Objektes zurück. Der Wert der Geschwindigkeit wird nicht in eine andere Vector3-Instanz kopiert.
     * @return die Geschwindigkeit des Objektes
     */
    public Vector3 getVelocityByReference(){
        return velocity;
    }
    /**
     * Stellt die Geschwindigkeit des Objektes
     * @param velocity die neue Geschwindigkeit
     */
    public void setVelocity(Vector3 velocity){
        this.velocity=velocity;
    }

    /**
     * Gibt die Richtung des letzten Anstoßes zurück
     * @return die Richtung des letzten Anstoßes
     */
    public CollisionType getLastCollisionType(){
        return lastCollisionType;
    }

    /**
     * Gibt den Zeitpunkt des letzten Anstoßes zurück
     * @return der Zeitpunkt des letzten Anstoßes
     */
    public long getLastCollision(){
        return lastCollision;
    }

    /**
     * Gibt den Name des anderen Körpers, der in dem letzten Anstoß teilnahm zurück
     * @return der Name des anderen Körpers, der in dem letzten Anstoß teilnahm
     */
    public String getLastCollisionName(){return lastCollisionName;}
}
