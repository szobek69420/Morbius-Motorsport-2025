package main.java.org.World;

import java.awt.*;

public class BlockColours {
    public static final Color[] blockColours={
            //order:
            //-z
            //-x
            //+z
            //+x
            //+y
            //-y

            //AIR
            new Color(0,0,0),
            new Color(0,0,0),
            new Color(0,0,0),
            new Color(0,0,0),
            new Color(0,0,0),
            new Color(0,0,0),

            //STONE
            new Color(125,125,125),
            new Color(110,110,110),
            new Color(80,80,80),
            new Color(95,95,95),
            new Color(140,140,140),
            new Color(65,65,65),

            //DIRT
            new Color(140,74,36),
            new Color(125,65,32),
            new Color(95,48,24),
            new Color(110,56,28),
            new Color(155,83,40),
            new Color(80,40,24),

            //GRASS
            new Color(9,108,9),
            new Color(8,96,8),
            new Color(6,72,6),
            new Color(7,84,7),
            new Color(10,120,10),
            new Color(5,60,5),

            //LOG
            new Color(162,108,46),
            new Color(144,96,42),
            new Color(108,72,34),
            new Color(126,84,38),
            new Color(180,120,50),
            new Color(90,60,30),

            //LEAVES
            new Color(90,171,0),
            new Color(80,152,0),
            new Color(60,114,0),
            new Color(70,133,0),
            new Color(100,190,0),
            new Color(50,95,0)
    };
}
