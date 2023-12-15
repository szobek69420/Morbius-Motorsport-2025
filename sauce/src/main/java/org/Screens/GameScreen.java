package main.java.org.Screens;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import main.java.org.InputManagement.InputManager;
import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.Main;
import main.java.org.MainFrame.MainFrame;
import main.java.org.Physics.RaycastHit;
import main.java.org.Rendering.Camera.Camera;
import main.java.org.Rendering.Drawables.SelectionCube;
import main.java.org.Settings;
import main.java.org.UI.MyChangeListener;
import main.java.org.UI.RoundedBorder;
import main.java.org.Updateable.Player;
import main.java.org.World.*;
import main.java.org.Rendering.Drawables.Cube;

public class GameScreen extends JPanel {

    private MainFrame mainFrame;

    private Foreground fg;
    private Background bg;

    private ChunkManager chunkManager;

    private Player player;
    public Player getPlayer(){return player;}

    private SelectionCube selectionKuba;

    private static int lastFrameCount=0;
    public static int getFrameCount(){return lastFrameCount;}
    private static int lastPendingChunks=0;
    public static int getPendingCount(){return lastPendingChunks;}
    private static int lastChunkUpdates=0;
    public static int getLastChunkUpdates(){return lastChunkUpdates;}

    //states
    private boolean paused=false;
    private boolean inventoryOpen=false; private boolean canChangeInventoryState=true;

    private double heldBlockChanged=69.420f;//ha nulla, az azt jelenti, hogy most változott, ha 10, az azt jelenti, hogy 10 másodperce változott
    public double getHeldBlockChanged(){return heldBlockChanged;}
    private int currentlyHeldBlock;

    public GameScreen(MainFrame mf){
        super();

        this.setLayout(null);

        this.mainFrame=mf;

        fg=new Foreground(this);
        bg=new Background(this);

        fg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);
        bg.setBounds(0,0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);

        this.add(fg);
        this.add(bg);

        this.chunkManager=new ChunkManager();
        this.player=new Player();
        this.currentlyHeldBlock=player.getSelectedHotbarSlot();

        //---------------------------
        Camera cum=new Camera(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);
        cum.setYaw(30);
        cum.setPitch(-20);
        cum.setFOV(50);
        cum.setPosition(new Vector3(-10,20,-10));
        Camera.main=cum;

        this.selectionKuba=new SelectionCube(new Color(255,255,255));
        this.selectionKuba.setScale(new Vector3(0.5f, 0.5f, 0.5f));
        Camera.main.addDrawable(selectionKuba);
    }

    private int frameCount=0;
    private long lastFrame=0;

    private double lastBlockBreak=0;
    private double lastBlockPlace=0;

    public void frame(double deltaTime){
        if(paused)
            return;


        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        if(Camera.main!=null)
            Camera.main.setRenderPosition(Camera.main.getPosition());
        this.repaint();

        if((System.nanoTime()-lastFrame)*0.000000001>1){
            lastFrameCount=frameCount;

            lastChunkUpdates=this.chunkManager.getChunkUpdates();
            this.chunkManager.setChunkUpdates(0);

            lastFrame=System.nanoTime();
            frameCount=0;
        }
        frameCount++;
        lastPendingChunks=this.chunkManager.getPendingCount();
    }

    public void physicsFrame(double deltaTime, boolean chunkLoad){
        if(paused)
            return;

        if(InputManager.ESCAPE&&!inventoryOpen){
            pause(false);
            mainFrame.setVisible(true);
        }

        int[] chunkPos= ChunkManager.getChunk(Camera.main.getPosition());

        if(chunkLoad){
            this.chunkManager.unloadChunk(chunkPos[0],chunkPos[1]);
            this.chunkManager.loadChunk(chunkPos[0],chunkPos[1], player);
            this.chunkManager.updateChunks(player);
            Vector3 tempVector69=selectionKuba.getPositionByReference().copy();
            selectionKuba.setPosition(Camera.main.getPosition());
            Camera.main.sortDrawables();
            selectionKuba.setPosition(tempVector69);
            return;
        }

        if(!inventoryOpen){
            InputManager.fetchMousePosition();
            player.update(deltaTime);
        }

        this.chunkManager.calculatePhysics(deltaTime,Camera.main.getPosition());

        if(!InputManager.MOUSE_LEFT)
            lastBlockBreak=69;
        if(!InputManager.MOUSE_RIGHT)
            lastBlockPlace=69;

        if(!InputManager.E)
            canChangeInventoryState=true;
        if(InputManager.E&&!inventoryOpen&&canChangeInventoryState){
            canChangeInventoryState=false;
            inventory();
        }

        if(player.getSelectedHotbarSlot()!=currentlyHeldBlock){
            currentlyHeldBlock=player.getSelectedHotbarSlot();
            heldBlockChanged=0.0f;
        }
        heldBlockChanged+=deltaTime;

        lastBlockBreak+=deltaTime;
        lastBlockPlace+=deltaTime;
        RaycastHit rh=this.chunkManager.gaycast(Camera.main.getPosition(),Camera.main.getForward(),5);
        if(rh!=null){
            Vector3 selectionPosition=new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z);
            selectionKuba.setPosition(selectionPosition);
            int kubaColorInt= BlockColours.blockColours[6*rh.blockType.ordinal()+4].brighter().getRGB();
            kubaColorInt&=0x00FFFFFF;
            kubaColorInt|=0x80000000;
            selectionKuba.setColor(new Color(kubaColorInt,true));

            if(InputManager.MOUSE_LEFT&&lastBlockBreak>0.2&&rh.blockType!=BlockTypes.BEDROCK){
                lastBlockBreak=0;
                chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, BlockTypes.AIR);
                Chunk chomk;

                if(rh.x==0){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX-1,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.x==15){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX+1,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.z==0){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ-1);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
                if(rh.z==15){
                    chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ+1);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }

                chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ);
                if(chomk!=null)
                    chunkManager.reloadChunk(chomk);
            }
            else if(InputManager.MOUSE_RIGHT&&lastBlockPlace>0.2){
                lastBlockPlace=0;
                Vector3 tempDeltaPos=new Vector3(
                        rh.point.get(0)-selectionPosition.get(0),
                        rh.point.get(1)-selectionPosition.get(1),
                        rh.point.get(2)-selectionPosition.get(2)
                );

                float tempMax=Math.abs(tempDeltaPos.get(0));
                int tempMaxIndex=0;
                if(tempMax<Math.abs(tempDeltaPos.get(1))){
                    tempMax=Math.abs(tempDeltaPos.get(1));
                    tempMaxIndex=1;
                }
                if(tempMax<Math.abs(tempDeltaPos.get(2))){
                    tempMax=Math.abs(tempDeltaPos.get(2));
                    tempMaxIndex=2;
                }

                switch(tempMaxIndex){
                    case 0:
                        if(tempDeltaPos.get(0)<0) {
                            rh.x--;
                            if (rh.x < 0) {
                                rh.x = 15;
                                rh.chunkX--;
                            }
                        }
                        else{
                            rh.x++;
                            if (rh.x >15) {
                                rh.x = 0;
                                rh.chunkX++;
                            }
                        }
                        break;

                    case 1:
                        if(tempDeltaPos.get(1)<0) {
                            rh.y--;
                            if (rh.y < 0) {
                                rh.y = 0;
                            }
                        }
                        else{
                            rh.y++;
                            if (rh.y >49) {
                                rh.y = 49;
                            }
                        }
                        break;

                    case 2:
                        if(tempDeltaPos.get(2)<0) {
                            rh.z--;
                            if (rh.z < 0) {
                                rh.z = 15;
                                rh.chunkZ--;
                            }
                        }
                        else{
                            rh.z++;
                            if (rh.z >15) {
                                rh.z = 0;
                                rh.chunkZ++;
                            }
                        }
                        break;
                }

                if(!player.isBlockInPlayer(new Vector3(rh.chunkX*16+rh.x,rh.y,rh.chunkZ*16+rh.z))){
                        chunkManager.changeBlock(rh.chunkX,rh.chunkZ,rh.x,rh.y,rh.z, player.getHeldBlock());
                    Chunk chomk=this.chunkManager.getChunkAtPos(rh.chunkX,rh.chunkZ);
                    if(chomk!=null)
                        chunkManager.reloadChunk(chomk);
                }
            }
        }
        else{
            selectionKuba.setPosition(new Vector3(0,-10000,0));
        }
    }

    public void pause(boolean fromFocusListener){
        if(!paused&&inventoryOpen&&fromFocusListener)
            return;

        if(!paused){
            if(inventoryOpen)//bezárja az inventoryt, hogy ne legyen ütközés
                inventory();

            InputManager.showCursor(mainFrame);
            bg.setVisible(false);
            fg.pause(mainFrame);
            paused=true;
        }
    }

    public void unpause(){
        if(paused){
            InputManager.hideCursor(mainFrame);
            fg.unPause();
            bg.setVisible(true);
            InputManager.fetchMousePosition();//visszarakja a kurzort a helyére
            InputManager.fetchMousePosition();//törli az előző visszarakás adatait
            mainFrame.requestFocus();
            paused=false;
        }
    }

    public void inventory(){
        if(inventoryOpen){
            inventoryOpen=false;
            fg.closeInventory();
            InputManager.fetchMousePosition();//visszarakja a kurzort a helyére
            InputManager.fetchMousePosition();//törli az előző visszarakás adatait
            InputManager.hideCursor(mainFrame);
            mainFrame.setVisible(true);
            mainFrame.requestFocus();
        }
        else{
            inventoryOpen=true;
            fg.openInventory();
            InputManager.fetchMousePosition();//visszarakja a kurzort a helyére
            InputManager.fetchMousePosition();//törli az előző visszarakás adatait
            InputManager.showCursor(mainFrame);
            mainFrame.setVisible(true);
            mainFrame.requestFocus();
        }
    }


    private static class Foreground extends JPanel{

        private GameScreen gameScreen;

        public Foreground(GameScreen gameScreen){
            this.setBackground(new Color(0,0,0,0));
            this.setLayout(new GridLayout(1,1));
            this.setDoubleBuffered(true);

            this.gameScreen=gameScreen;

            this.add(new IngameCanvas(gameScreen));
        }

        public void pause(MainFrame mainFrame){
            this.removeAll();
            this.add(new PauseMenu(mainFrame,gameScreen));
            this.setVisible(true);
        }

        public void unPause(){
            this.removeAll();
            this.add(new IngameCanvas(gameScreen));
            this.setVisible(true);
        }

        public void openInventory(){
            this.removeAll();
            this.add(new Inventory(gameScreen));
            this.setVisible(true);
        }

        public void closeInventory(){
            this.removeAll();
            this.add(new IngameCanvas(gameScreen));
            this.setVisible(true);
        }

        private static class IngameCanvas extends JPanel{
            private GameScreen gameScreen;

            private Font font;
            private FontMetrics fontMetrics;

            private BufferedImage image;
            private Graphics imageGraphics;
            private Graphics2D imageGraphics2D;

            public IngameCanvas(GameScreen gameScreen){
                super();

                this.setBackground(new Color(0,0,0,0));
                this.setLayout(null);

                this.image=new BufferedImage(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT,BufferedImage.TYPE_INT_ARGB);
                this.imageGraphics=image.getGraphics();
                this.imageGraphics2D=image.createGraphics();
                imageGraphics2D.setBackground(new Color(0,0,0,0));

                this.font=new Font("Arial",Font.PLAIN,10);
                imageGraphics.setFont(font);
                this.fontMetrics= imageGraphics.getFontMetrics();


                this.gameScreen=gameScreen;
            }

            @Override
            public void paint(Graphics g){
                //clear
                imageGraphics2D.clearRect(0,0,MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);

                //text
                imageGraphics.setColor(Color.white);

                //left side
                Vector3 pos;
                float pitch=0,yaw=0;
                if(Camera.main!=null){
                    pos=Camera.main.getPosition();
                    pos.set(1,pos.get(1)-0.8f);
                    pitch=Camera.main.getPitch();
                    yaw=Camera.main.getYaw();
                }
                else
                    pos=Vector3.zero;

                String posString="Pos: "+(int)pos.get(0) +", "+(int)pos.get(1) +", "+(int)pos.get(2);
                imageGraphics.drawString(posString,5,10);
                String lookString="Rot: "+((int)(yaw*100))/100.0f +", "+((int)(pitch*100))/100.0f;
                imageGraphics.drawString(lookString,5,20);

                //right side
                String updateString="Updates: "+GameScreen.getLastChunkUpdates();
                imageGraphics.drawString(updateString,MainFrame.FRAME_BUFFER_WIDTH-fontMetrics.stringWidth(updateString)-10,10);
                String pendingString="Pending: "+GameScreen.getPendingCount();
                imageGraphics.drawString(pendingString,MainFrame.FRAME_BUFFER_WIDTH-fontMetrics.stringWidth(pendingString)-10,20);

                //held block
                int slotWidth=40;

                double temp=gameScreen.getHeldBlockChanged();
                if(temp<2.0&&gameScreen.getPlayer().getHeldBlock()!=BlockTypes.AIR){
                    String blockNameString= BlockNames.blockNames[gameScreen.getPlayer().getHeldBlock().ordinal()];

                    int tempColorInt=0xFFFFFFFF;
                    if(temp>1.0){
                        tempColorInt&=0x00FFFFFF;
                        tempColorInt|=((int)((2.0-temp)*255))<<24;
                    }
                    imageGraphics.setColor(new Color(tempColorInt,true));
                    imageGraphics.drawString(
                            blockNameString,
                            MainFrame.FRAME_BUFFER_WIDTH/2-fontMetrics.stringWidth(blockNameString)/2,
                            MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-25
                            );
                }

                //hotbar
                paintHotbar(image.getGraphics(),slotWidth);

                //last step
                g.drawImage(image,0,0,MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT,null);
            }

            private void paintHotbar(Graphics imageGraphics, int slotWidth){
                int startX=MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2;
                imageGraphics.setColor(new Color(0,255,255));
                for(int i=0;i<Player.HOTBAR_SIZE;i++){
                    imageGraphics.fillRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-19,slotWidth,slotWidth);
                    startX+=slotWidth;
                }
                imageGraphics.setColor(Color.white);
                imageGraphics.fillRect(
                        MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2+gameScreen.getPlayer().getSelectedHotbarSlot()*slotWidth-1,
                        MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-20,
                        slotWidth+2,
                        slotWidth+2);

                startX=MainFrame.FRAME_BUFFER_WIDTH/2-Player.HOTBAR_SIZE*slotWidth/2+4;
                for(int i=0;i<Player.HOTBAR_SIZE;i++){
                    if(this.gameScreen.getPlayer().getHotbarBlock(i)==BlockTypes.AIR){
                        imageGraphics2D.clearRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-15,slotWidth-8,slotWidth-8);
                        imageGraphics.setColor(new Color(0,0,0,50));
                        imageGraphics.fillRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-15,slotWidth-8,slotWidth-8);
                    }
                    else{
                        imageGraphics.setColor(BlockColours.blockColours[6*gameScreen.getPlayer().getHotbarBlock(i).ordinal()]);
                        imageGraphics.fillRect(startX,MainFrame.FRAME_BUFFER_HEIGHT-slotWidth-15,slotWidth-8,slotWidth-8);
                    }
                    startX+=slotWidth;
                }
            }
        }

        private static class Inventory extends JPanel{
            public static final int INVENTORY_WIDTH=5;
            public static final int INVENTORY_HEIGHT=3;
            public static final int SLOT_WIDTH=80;
            public static final BlockTypes[] INVENTORY_CONTENT=new BlockTypes[]{
                    BlockTypes.BEDROCK,BlockTypes.DIRT,BlockTypes.STONE,BlockTypes.GRASS,BlockTypes.YELLOW,
                    BlockTypes.GEH,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR,
                    BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR,BlockTypes.AIR
            };

            private GameScreen gameScreen;

            private BufferedImage buffer;
            private Graphics bufferGraphics;

            private Font font;

            private BlockTypes blockSelected;
            private int hotbarSlotSelected;
            private HotbarButton hotbarButtonSelected;


            public Inventory(GameScreen gameScreen){
                super();

                this.setBackground(new Color(0,0,0,0));
                this.setLayout(null);


                this.font=new Font("Arial",Font.BOLD,20);

                this.gameScreen=gameScreen;

                this.buffer=new BufferedImage(MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT,BufferedImage.TYPE_INT_ARGB);
                bufferGraphics=buffer.getGraphics();
                ((Graphics2D)bufferGraphics).setBackground(new Color(0,0,0,0));

                this.blockSelected=BlockTypes.AIR;
                this.hotbarSlotSelected=-1;
                this.hotbarButtonSelected=null;

                fillScreen();
                this.setVisible(true);
            }

            @Override
            public void paint(Graphics g){
                super.paint(g);

                ((Graphics2D)bufferGraphics).clearRect(0,0,MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT);

                int startX=MainFrame.SCREEN_WIDTH/2-INVENTORY_WIDTH*SLOT_WIDTH/2-10;
                int startY=MainFrame.SCREEN_HEIGHT/2-INVENTORY_HEIGHT*SLOT_WIDTH/2-50;

                bufferGraphics.setColor(Color.white);
                bufferGraphics.setFont(this.font);
                bufferGraphics.drawString("List of blocks:",startX+17,startY+35);

                //Point p=InputManager.getMousePosition();
                //bufferGraphics.fillRect(p.x-19,p.y-40,20,20);
                if(blockSelected!=BlockTypes.AIR){
                    Point p=InputManager.getMousePosition();
                    bufferGraphics.setColor(BlockColours.blockColours[6*blockSelected.ordinal()]);
                    bufferGraphics.fillRect(p.x-44,p.y-65,SLOT_WIDTH-10,SLOT_WIDTH-10);
                }

                g.drawImage(buffer,0,0,MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT,this);
            }

            private void fillScreen(){
                int startX=MainFrame.SCREEN_WIDTH/2-INVENTORY_WIDTH*SLOT_WIDTH/2+5;
                int startY=MainFrame.SCREEN_HEIGHT/2-INVENTORY_HEIGHT*SLOT_WIDTH/2+5;

                //inventory
                for(int i=0;i<INVENTORY_HEIGHT;i++){
                    for(int j=0;j<INVENTORY_WIDTH;j++){

                        final BlockTypes tempType=INVENTORY_CONTENT[i*INVENTORY_WIDTH+j];

                        if(tempType==BlockTypes.AIR)
                            continue;

                        InventorySlotButton button=new InventorySlotButton(tempType);
                        button.setBounds(startX,startY,SLOT_WIDTH-10,SLOT_WIDTH-10);

                        button.addChangeListener((change)->{
                            ButtonModel butt=((JButton)change.getSource()).getModel();
                            if(butt.isPressed()){
                                blockSelected=tempType;
                            }
                            else{
                                if(hotbarSlotSelected!=-1){
                                    gameScreen.getPlayer().setHotbarBlock(hotbarSlotSelected,blockSelected);
                                    hotbarButtonSelected.setBackground(BlockColours.blockColours[6*blockSelected.ordinal()]);
                                }
                                blockSelected=BlockTypes.AIR;
                            }
                        });

                        this.add(button);
                        button.setVisible(true);

                        startX+=SLOT_WIDTH;
                    }
                    startX=MainFrame.SCREEN_WIDTH/2-INVENTORY_WIDTH*SLOT_WIDTH/2+5;
                    startY+=SLOT_WIDTH;
                }

                //hotbar
                startX=MainFrame.SCREEN_WIDTH/2-INVENTORY_WIDTH*SLOT_WIDTH/2;
                for(int i=0;i<Player.HOTBAR_SIZE;i++){
                    final int index=i;
                    final BlockTypes tempType=gameScreen.getPlayer().getHotbarBlock(index);
                    HotbarButton button=new HotbarButton(tempType);
                    button.setBounds(startX,MainFrame.SCREEN_HEIGHT-SLOT_WIDTH-38,SLOT_WIDTH,SLOT_WIDTH);

                    button.addMouseListener(new MouseListener() {
                        public void mouseClicked(MouseEvent e) {}
                        public void mousePressed(MouseEvent e) {}
                        public void mouseReleased(MouseEvent e) {}
                        public void mouseEntered(MouseEvent e) {hotbarSlotSelected=index; hotbarButtonSelected=button;}
                        public void mouseExited(MouseEvent e) {
                            if(hotbarSlotSelected==index){//ha felülírná egy másik hotbarslot, akkor azt ne törölje
                                hotbarButtonSelected=null;
                                hotbarSlotSelected=-1;
                            }
                        }
                    });

                    this.add(button);
                    button.setVisible(true);

                    startX+=SLOT_WIDTH;
                }

                //close button
                JButton closeButton=new JButton("X");
                closeButton.setBackground(Color.red);
                closeButton.setForeground(Color.white);
                closeButton.setBorder(BorderFactory.createLineBorder(Color.white,2));
                closeButton.setBounds(
                        MainFrame.SCREEN_WIDTH/2+INVENTORY_WIDTH*SLOT_WIDTH/2-35,
                        MainFrame.SCREEN_HEIGHT/2-INVENTORY_HEIGHT*SLOT_WIDTH/2-37,
                        30,
                        25
                        );
                closeButton.addActionListener((e)->{gameScreen.inventory();});
                this.add(closeButton);

                //background
                JPanel background=new JPanel(){
                    @Override
                    public void paintComponent(Graphics g){
                        g.setColor(new Color(0,0,0,150));
                        g.fillRoundRect(0,0,this.getWidth()-1,this.getHeight(),20,20);
                    }
                };
                background.setBounds(
                        MainFrame.SCREEN_WIDTH/2-INVENTORY_WIDTH*SLOT_WIDTH/2-10,
                        MainFrame.SCREEN_HEIGHT/2-INVENTORY_HEIGHT*SLOT_WIDTH/2-50,
                        INVENTORY_WIDTH*SLOT_WIDTH+20,
                        INVENTORY_HEIGHT*SLOT_WIDTH+60
                        );
                this.add(background);
            }

            private static class InventorySlotButton extends JButton{

                public InventorySlotButton(BlockTypes blockType){

                    this.setBackground(BlockColours.blockColours[6*blockType.ordinal()]);

                    this.setForeground(Color.white);

                    this.setBorder(BorderFactory.createLineBorder(new Color(0,255,255),5,false));


                    this.setVisible(true);
                }
            }

            private static class HotbarButton extends JButton{

                public HotbarButton(BlockTypes blockType){

                    if(blockType==BlockTypes.AIR)
                        this.setBackground(new Color(0,0,0,50));
                    else
                        this.setBackground(BlockColours.blockColours[6*blockType.ordinal()]);

                    this.setForeground(Color.white);

                    this.setBorder(BorderFactory.createLineBorder(new Color(0,255,255),8,false));


                    this.setVisible(true);
                }
            }
        }

        private static class PauseMenu extends JPanel{
            public PauseMenu(MainFrame mf, GameScreen gameScreen){
                super();

                this.setBackground(new Color(0,0,0,100));

                this.setLayout(null);

                Font titleFont=new Font("Arial",Font.BOLD,50);
                Font normalFont=new Font("Arial",Font.BOLD,20);
                int centerX= MainFrame.SCREEN_WIDTH/2;
                int centerY=MainFrame.SCREEN_HEIGHT/2;


                JLabel title=new JLabel("paused",SwingConstants.CENTER);
                title.setBackground(new Color(0,0,0,0));
                title.setForeground(new Color(255,209,0));
                title.setFont(titleFont);
                title.setBounds(centerX-400,centerY-100,800,50);
                this.add(title);

                JButton startButton = new JButton("proceed");
                startButton.setBackground(Color.black);
                startButton.setForeground(Color.white);
                startButton.setFont(normalFont);
                startButton.setContentAreaFilled(false);
                startButton.setBorder(new RoundedBorder(30));
                startButton.setBounds(centerX-100,centerY-5,200,50);
                startButton.addActionListener((a)->{gameScreen.unpause();});
                startButton.addChangeListener(new MyChangeListener(Color.white,new Color(0,255,255),new Color(0,150,150)));
                this.add(startButton);

                JButton settingsButton = new JButton("not proceed");
                settingsButton.setBackground(Color.black);
                settingsButton.setForeground(Color.white);
                settingsButton.setFont(normalFont);
                settingsButton.setContentAreaFilled(false);
                settingsButton.setBorder(new RoundedBorder(30));
                settingsButton.setBounds(centerX-100,centerY+55,200,50);
                settingsButton.addActionListener((a)->{mf.setCurrentState(MainFrame.STATE.MAIN_MENU);});
                settingsButton.addChangeListener(new MyChangeListener(Color.white,new Color(0,255,255),new Color(0,150,150)));
                this.add(settingsButton);
            }

            @Override
            public void paint(Graphics g){
                int centerX= MainFrame.SCREEN_WIDTH/2;
                int centerY=MainFrame.SCREEN_HEIGHT/2;

                Color orgColor=g.getColor();
                g.setColor(Color.black);
                g.fillRoundRect(centerX-200,centerY-150,400,300,40,40);
                g.setColor(Color.white);
                g.drawRoundRect(centerX-200,centerY-150,400,300,40,40);
                g.setColor(orgColor);

                super.paint(g);
            }
        }
    }

    private static class Background extends JPanel{

        private BufferedImage lastImage;
        private Graphics lastImageGraphics;

        private BufferedImage image;
        private Graphics imageGraphics;
        private float[][] depthBuffer;

        private GameScreen gameScreen;


        public Background(GameScreen gameScreen){
            this.setBackground(Camera.CLEAR_COLOR);

            this.gameScreen=gameScreen;

            image=new BufferedImage(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT,BufferedImage.TYPE_INT_ARGB);
            imageGraphics=image.getGraphics();
            ((Graphics2D)imageGraphics).setBackground(new Color(Camera.CLEAR_COLOR_INT&0x00FFFFFF,true));

            lastImage=new BufferedImage(MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT,BufferedImage.TYPE_INT_ARGB);
            lastImageGraphics=lastImage.getGraphics();
            ((Graphics2D)lastImageGraphics).setBackground(new Color(Camera.CLEAR_COLOR_INT&0x00FFFFFF,true));


            depthBuffer=new float[MainFrame.FRAME_BUFFER_WIDTH][MainFrame.FRAME_BUFFER_HEIGHT];
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=Camera.RENDER_DISTANCE;
            }
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            clearBuffers();

            Camera.main.render(image,depthBuffer);
            gameScreen.player.renderHeldBlock(image,depthBuffer,Camera.main);

            for(int i=MainFrame.FRAME_BUFFER_WIDTH/2-2;i<=MainFrame.FRAME_BUFFER_WIDTH/2;i++){
                for(int j=MainFrame.FRAME_BUFFER_HEIGHT/2-2;j<=MainFrame.FRAME_BUFFER_HEIGHT/2;j++){
                    int temp=image.getRGB(i,j);
                    temp^=0xFFFFFFFF;
                    temp|=0xFF000000;
                    image.setRGB(i,j,temp);
                }
            }

            //draw screen
            if(Settings.interpolateFrames)
                g.drawImage(lastImage, 0, 0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, null);
            g.drawImage(image, 0, 0, MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, null);

            swapBuffers();
        }

        private void clearBuffers(){
            clearDepthBuffer(Camera.RENDER_DISTANCE);
            ((Graphics2D)imageGraphics).clearRect(0,0,MainFrame.FRAME_BUFFER_WIDTH,MainFrame.FRAME_BUFFER_HEIGHT);
        }

        private void swapBuffers(){
            BufferedImage temp=lastImage;
            lastImage=image;
            image=temp;

            Graphics temp2=lastImageGraphics;
            lastImageGraphics=imageGraphics;
            imageGraphics=temp2;
        }

        private void clearDepthBuffer(float clearValue){
            for(int i=0;i<MainFrame.FRAME_BUFFER_WIDTH;i++){
                for(int j=0;j<MainFrame.FRAME_BUFFER_HEIGHT;j++)
                    depthBuffer[i][j]=clearValue;
            }
        }
    }

}
