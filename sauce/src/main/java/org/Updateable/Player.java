package main.java.org.Updateable;

import main.java.org.InputManagement.InputManager;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.AABB;
import main.java.org.Physics.CollisionDetection;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Screens.GameScreen;


import java.awt.*;

/**
 * Es ist die Entität, die von dem Spieler steuert werden wird
 */
public class Player implements Updateable{

    /**
     * Der Collider des Spielers
     */
    private AABB aabb;


    /**
     * Die maximale horizontale Geschwindigkeit, die von dem Spieler durch Spazieren erreicht werden kann
     */
    private static final float MAX_VELOCITY=4;
    /**
     * @hidden
     */
    private static final float MAX_VELOCITY_SQUARED=16;
    /**
     * @hidden
     */
    private boolean canJump=false;
    /**
     * @hidden
     */
    private boolean isSprinting=false;

    /**
     * @hidden
     */
    private final float ZOOM_SPEED=300.0f;
    /**
     * @hidden
     */
    private final float ZOOMED_FOV;
    /**
     * @hidden
     */
    private final float BASED_FOV;
    /**
     * Das aktuelle Sichtfeld der Kamera
     */
    private float currentFov;

    /**
     * Erzeugt eine neue Player-Instanz.
     * Die Collider und Schatten des Spielers werden auch hier erzeugt.
     */
    public Player(){
        aabb=new AABB(new Vector3(0,50,0),new Vector3(0.25f,0.9f, 0.25f), false,"Player");

        //BASED_FOV=Settings.getFov();
        //ZOOMED_FOV=Settings.getFov()/4;
        BASED_FOV=50;
        ZOOMED_FOV=10;

        currentFov=BASED_FOV;
    }

    /**
     * Registriert den Collider des Spielerinstanzes zu einem Physiksystem
     * @param cd Das Physiksystem, zu dem der Collider hinzufügt wird
     */
    public void addToPhysics(CollisionDetection cd){
        cd.addAABB(aabb);
    }



    /**
     * Überschreibt die update Funktion des Updateable Interfaces
     * Überprüft, ob der Spieler mit einem anderen Collider gestoßen ist. Falls ja:
     * -falls der andere Collider der Name "Sus" hat, dann wird der Spieler sterben
     * -falls der andere Collider der Name "Finish" hat und er ist unter dem Spieler, dann wird das Spiel enden
     * Sucht für Benutzereingaben
     * @param deltaTime die Zeit, die nach dem letzten Frame verging
     */
    @Override
    public void update(double deltaTime){
        if((System.nanoTime()-aabb.getLastCollision())*0.000000001<deltaTime&&aabb.getLastCollisionType()== AABB.CollisionType.BOTTOM)
            canJump=true;
        if(aabb.getVelocityByReference().get(1)<-40.0f*deltaTime)
            canJump=false;


        if(InputManager.CONTROL&&InputManager.W&&canJump)
            isSprinting=true;
        if(!InputManager.W)
            isSprinting=false;

        zoomControl((float) deltaTime);

        RotateCamera(deltaTime);
        Move(deltaTime);
        Camera.main.setPosition(Vector3.sum(aabb.getPositionByReference(),new Vector3(0,0.8f,0)));
    }

    /**
     * Überprüft, ob es neue Benutzereingaben gibt und stellt die Geschwindigkeit des Spielers den entsprechend
     * @param deltaTime die Zeit, die nach dem letzten Frame verging
     */
    private void Move(double deltaTime){
        float forward=0.0f;
        float left=0.0f;
        float up=0.0f;

        if(InputManager.W)
            forward++;
        if(InputManager.S)
            forward--;

        if(InputManager.A)
            left++;
        if(InputManager.D)
            left--;

        if(InputManager.SPACE)
            up++;
        if(InputManager.L_SHIT)
            up--;


        forward*=10;
        left*=10;
        up*=10;

        Vector3 forwardVec=Camera.main.getForward().copy();
        forwardVec.set(1,0);
        Vector3.normalize(forwardVec);

        Vector3 acceleration=Vector3.multiplyWithScalar(forward, forwardVec);
        acceleration=Vector3.sum(acceleration,Vector3.multiplyWithScalar(left, Camera.main.getLeft()));
        acceleration=Vector3.difference(acceleration,aabb.getVelocityByReference());
        acceleration.set(1,0);

        float accelMag=Vector3.magnitude(acceleration);

        if(accelMag>20.0f*deltaTime){
            Vector3.normalize(acceleration);
            acceleration=Vector3.multiplyWithScalar(20*(float)deltaTime,acceleration);
        }


        Vector3 velocity=Vector3.sum(aabb.getVelocityByReference(),acceleration);

        float maxVel=MAX_VELOCITY;
        float maxVelSqr=MAX_VELOCITY_SQUARED;
        if(isSprinting){
            maxVelSqr*=9;
            maxVel*=3;
        }

        float magnitude=velocity.get(0)*velocity.get(0)+velocity.get(2)*velocity.get(2);
        if(magnitude>maxVelSqr){

            magnitude=maxVel-(float)Math.sqrt(magnitude);
            if(magnitude<-30.0f*(float)deltaTime)
                magnitude=-30.0f*(float)deltaTime;

            velocity=Vector3.sum(velocity,Vector3.multiplyWithScalar(magnitude,new Vector3(velocity.get(0),0,velocity.get(2))));
        }

        if(up>1&&canJump){
            canJump=false;
            velocity.set(1,10);
        }
        else
            velocity.set(1,velocity.get(1)-20.0f*(float)deltaTime);

        aabb.setVelocity(velocity);
        aabb.setPosition(Vector3.sum(aabb.getPositionByReference(),Vector3.multiplyWithScalar((float) deltaTime,velocity)));
        //System.out.println(pos);
    }

    /**
     * Überprüft, ob eine Mausbewegung passierte und stellt die Kameraeinrichtung dementsprechend
     * @param deltaTime die Zeit, die nach dem letzten Frame verging
     */
    private void RotateCamera(double deltaTime){
        //float left=RenderThread.mainCamera.getYaw();
        //float up=RenderThread.mainCamera.getPitch();
        float up=-InputManager.deltaMouseY;
        float left=-InputManager.deltaMouseX;

        up*=0.05f;
        left*=0.05f;

        //System.out.println(deltaTime+" "+up+" "+left);

        up+=Camera.main.getPitch();
        left+=Camera.main.getYaw();

        if(up<-89.9f)
            up=-89.9f;
        else if(up>89.9f)
            up=89.9f;

        if(left<-360)
            left+=360;
        if(left>360)
            left-=360;

        Camera.main.setPitch(up);
        Camera.main.setYaw(left);
    }

    /**
     * Verändert das Sichtfeld des Spielers entsprechend den Benutzereingaben
     * @param deltaTime die Zeit, die nach dem letzten Frame verging
     */
    private void zoomControl(float deltaTime){
        if(InputManager.C){
            currentFov-=deltaTime*ZOOM_SPEED;
            if(currentFov<ZOOMED_FOV)
                currentFov=ZOOMED_FOV;
        }
        else{
            currentFov+=deltaTime*ZOOM_SPEED;
            if(currentFov>BASED_FOV)
                currentFov=BASED_FOV;
        }

        Camera.main.setFOV(currentFov);
    }

    /**
     * Falls das Spiel zum Start zurückgestellt werden soll, wird das von dieser Funktion erledigt
     * respawn wird vor dem Start und nach Tasten des Respawnbuttones des Endbildschirmes gerufen
     */
    public void respawn(){
        aabb.setVelocity(new Vector3(0,0,0));
        aabb.setPosition(new Vector3(0,0,0));

        Camera.main.setPitch(0);
        Camera.main.setYaw(0);
    }

    public boolean isBlockInPlayer(Vector3 block){
        if(
                        Math.abs(block.get(0)-aabb.getPositionByReference().get(0))>0.75f&&
                        Math.abs(block.get(0)-aabb.getPositionByReference().get(0))>1.4f&&
                        Math.abs(block.get(0)-aabb.getPositionByReference().get(0))>0.75f
        )
            return false;

        return true;
    }
}
