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
            new Color(230,155,18),
            new Color(210,140,16),
            new Color(170,110,12),
            new Color(190,125,14),
            new Color(250,170,20),
            new Color(150,95,10),
    };
}
