package main.java.org.Rendering.Drawables;

import main.java.org.LinearAlgebruh.Vector3;

import java.awt.*;

/**
 * Ein Würfel, der ans Bildschirm gezeichnet werden kann.
 * Vererbt von Drawable.
 */
public class Cube extends Drawable{

    /**
     * Erzeugt eine neue Würfelinstanz
     * @param color Die Basisfarbe des Würfels
     */
    public Cube(Color color){
        vertices=new Vector3[8];
        vertices[0]=new Vector3(1,-1,-1);
        vertices[1]=new Vector3(-1, -1, -1);
        vertices[2]=new Vector3(-1, -1,1);
        vertices[3]=new Vector3(1, -1, 1);
        vertices[4]=new Vector3(1,1,-1);
        vertices[5]=new Vector3(-1, 1, -1);
        vertices[6]=new Vector3(-1, 1 ,1);
        vertices[7]=new Vector3(1, 1, 1);

        indices=new int[]{
                0,1,5,
                0,5,4,
                1,2,6,
                1,6,5,
                2,3,7,
                2,7,6,
                3,0,4,
                3,4,7,
                0,2,1,
                0,3,2,
                4,5,6,
                4,6,7
        };

        int r=color.getRed();
        int g=color.getGreen();
        int b=color.getBlue();

        faceColors=new Color[]{
                new Color(7*r/8,7*g/8,7*b/8),
                new Color(7*r/8,7*g/8,7*b/8),
                new Color(6*r/8,6*g/8,6*b/8),
                new Color(6*r/8,6*g/8,6*b/8),
                new Color(4*r/8,4*g/8,4*b/8),
                new Color(4*r/8,4*g/8,4*b/8),
                new Color(5*r/8,5*g/8,5*b/8),
                new Color(5*r/8,5*g/8,5*b/8),
                new Color(3*r/8,3*g/8,3*b/8),
                new Color(3*r/8,3*g/8,3*b/8),
                new Color(r,g,b),
                new Color(r,g,b)
        };

        scale=new Vector3(1,1,1);
        pos=new Vector3(0,0,0);

        calculateModelMatrix();

        this.setName("amogus");
    }

}
