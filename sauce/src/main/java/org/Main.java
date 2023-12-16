package main.java.org;

import main.java.org.LinearAlgebruh.Vector3;
import main.java.org.MainFrame.MainFrame;
import main.java.org.World.ChangedBlockKey;
import main.java.org.World.Chunk;
import main.java.org.World.ChunkManager;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        MainFrame mf=new MainFrame("Morbius Motorsport 2025 GOTY Edition");
        mf.startGame();
        mf.dispatchEvent(new WindowEvent(mf, WindowEvent.WINDOW_CLOSING));
    }
}