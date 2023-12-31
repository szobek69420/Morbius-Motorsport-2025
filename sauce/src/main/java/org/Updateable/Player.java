package main.java.org.Updateable;

import main.java.org.InputManagement.InputManager;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Physics.AABB;
import main.java.org.Physics.CollisionDetection;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Rendering.Drawables.Drawable;
import main.java.org.Rendering.Drawables.HeldBlock;
import main.java.org.Screens.GameScreen;
import main.java.org.Settings;
import main.java.org.World.BlockTypes;


import java.awt.*;
import java.awt.image.BufferedImage;

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
    private boolean isMorbin;
    public boolean isMorbin(){return isMorbin;}
    private boolean canMorb;

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

    public static final int HOTBAR_SIZE=5;
    private BlockTypes[] hotbarSlots;
    private int selectedHotbarSlot, lastScrollCount;
    public int getSelectedHotbarSlot(){return selectedHotbarSlot;}
    public BlockTypes getHeldBlock(){return hotbarSlots[selectedHotbarSlot];}
    public BlockTypes getHotbarBlock(int index){return hotbarSlots[index];}
    public void setHotbarBlock(int index, BlockTypes block){this.hotbarSlots[index]=block;}
    private HeldBlock heldBlock;

    /**
     * Erzeugt eine neue Player-Instanz.
     * Die Collider und Schatten des Spielers werden auch hier erzeugt.
     */
    public Player(){
        aabb=new AABB(new Vector3(0,50,0),new Vector3(0.25f,0.9f, 0.25f), false,"Player");

        //BASED_FOV=Settings.getFov();
        //ZOOMED_FOV=Settings.getFov()/4;
        BASED_FOV= Settings.fieldOfView;
        ZOOMED_FOV= (float) Settings.fieldOfView /4;

        currentFov=BASED_FOV;

        hotbarSlots=new BlockTypes[]{BlockTypes.STONE,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR};
        selectedHotbarSlot=0;
        lastScrollCount=InputManager.SCROLL_COUNT;

        this.isSprinting=false;
        this.isMorbin=false;
        this.canMorb=true;
        this.canJump=false;

        heldBlock=new HeldBlock();
        heldBlock.setBlockType(hotbarSlots[selectedHotbarSlot]);
        heldBlock.setScale(new Vector3(0.1f,0.1f, 0.1f));
        heldBlock.setPosition(new Vector3(-0.3f,-0.3f,0.4f));
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

        if(!InputManager.M)
            canMorb=true;
        else if(canMorb){
            canMorb=false;
            isMorbin=!isMorbin;
        }

        if(lastScrollCount!=InputManager.SCROLL_COUNT){
            if(lastScrollCount>InputManager.SCROLL_COUNT)
                selectedHotbarSlot=selectedHotbarSlot==0?HOTBAR_SIZE-1:selectedHotbarSlot-1;
            else
                selectedHotbarSlot = selectedHotbarSlot + 1 == HOTBAR_SIZE ? 0 : selectedHotbarSlot + 1;
            lastScrollCount=InputManager.SCROLL_COUNT;
            heldBlock.setBlockType(hotbarSlots[selectedHotbarSlot]);
        }

        zoomControl((float) deltaTime);

        RotateCamera(deltaTime);
        Move(deltaTime);
        if(aabb.getPositionByReference().get(1)>1000||aabb.getPositionByReference().get(1)<-1000)
            aabb.getPositionByReference().set(1,60);
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

        if(isMorbin){
            Vector3 velocity=Vector3.multiplyWithScalar(2*forward, forwardVec);
            velocity=Vector3.sum(velocity,Vector3.multiplyWithScalar(2*left, Camera.main.getLeft()));
            velocity=Vector3.sum(velocity,Vector3.multiplyWithScalar(2*up, Vector3.up));

            aabb.setVelocity(Vector3.zero);
            aabb.setPosition(Vector3.sum(aabb.getPositionByReference(),Vector3.multiplyWithScalar((float) deltaTime,velocity)));
        }
        else{
            Vector3 acceleration=null;
            acceleration=Vector3.multiplyWithScalar(forward, forwardVec);
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
            else{
                velocity.set(1,velocity.get(1)-20.0f*(float)deltaTime);
                if(velocity.get(1)<-20.0f)
                    velocity.set(1,-20);
            }

            aabb.setVelocity(velocity);
            aabb.setPosition(Vector3.sum(aabb.getPositionByReference(),Vector3.multiplyWithScalar((float) deltaTime,velocity)));
        }
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

        up*=0.0005f*Settings.sensitivity;
        left*=0.0005f*Settings.sensitivity;

        //System.out.println(deltaTime+" "+up+" "+left);

        up+=Camera.main.getPitch();
        left+=Camera.main.getYaw();

        if(up<-89.9f)
            up=-89.9f;
        else if(up>89.9f)
            up=89.9f;

        if(left<-180)
            left+=360;
        if(left>180)
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
                        Math.abs(block.get(0)-aabb.getPositionByReference().get(0))>0.75f||
                        Math.abs(block.get(1)-aabb.getPositionByReference().get(1))>1.4f||
                        Math.abs(block.get(2)-aabb.getPositionByReference().get(2))>0.75f
        )
            return false;

        return true;
    }

    public void renderHeldBlock(BufferedImage image, float[][] depthBuffer, Camera cum){
        float pitch=cum.getPitch();
        float yaw=cum.getYaw();
        float fov=cum.getFOV();
        Vector3 pos=cum.getPosition();

        cum.setPitch(0);
        cum.setYaw(0);
        cum.setPosition(Vector3.zero);
        cum.setFOV(40);


        cum.renderUnregistered(heldBlock,image, depthBuffer);


        cum.setFOV(fov);
        cum.setPitch(pitch);
        cum.setYaw(yaw);
        cum.setPosition(pos);
        cum.renderUnregistered(null,null, null);//ezzel csak újraszámolja a kamera állását
    }
}
