package main.java.org.World;

public class Biomes {
    public static enum BiomeTypes{
        GRASSY,
        SANDY
    }

    public static final BlockTypes[] upperLayer=new BlockTypes[]{BlockTypes.GRASS,BlockTypes.SAND};
    public static final BlockTypes[] middleLayer=new BlockTypes[]{BlockTypes.DIRT,BlockTypes.SAND};
    public static final BlockTypes[] lowerLayer=new BlockTypes[]{BlockTypes.STONE,BlockTypes.STONE};

    public static final BlockTypes[] upperLayerMountainous=new BlockTypes[]{BlockTypes.SNOW,BlockTypes.STONE};
    public static final BlockTypes[] middleLayerMountainous=new BlockTypes[]{BlockTypes.DIRT,BlockTypes.STONE};
    public static final BlockTypes[] lowerLayerMountainous=new BlockTypes[]{BlockTypes.STONE,BlockTypes.STONE};
}
