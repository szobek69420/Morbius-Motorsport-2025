package main.java.org.Resizable;

import java.util.ArrayList;
import java.util.List;

public class ResizableManager {
    private List<Resizable> resizables;

    public ResizableManager(){
        resizables=new ArrayList<>();
    }

    public void addResizable(Resizable resizable){
        if(!resizables.contains(resizable))
            resizables.add(resizable);
    }

    public void removeResizable(Resizable resizable){
        resizables.remove(resizable);
    }

    public void clear(){
        resizables.clear();
    }
}
