package main.java.org.Updateable;

import java.util.ArrayList;

/**
 * Die Behalterklasse für Updateable-Instanzen
 */
public class UpdateableManager {
    /**
     * Der Behalter von Updateablen des Managers
     */
    private ArrayList<Updateable> updateables;

    /**
     * Erzeugt eine neue UpdateableManager-Instanz
     */
    public UpdateableManager(){
        updateables=new ArrayList<>();
    }

    /**
     *
     * @param deltaTime Die Zeit, die seit dem letzten Frame verging
     */
    public void update(double deltaTime){
        for (Updateable u: updateables){
            u.update(deltaTime);
        }
    }

    /**
     * Registriert eine Updateable-Instanz
     * @param u Der zu registrierende Updateable
     */
    public void addUpdateable(Updateable u){
        updateables.add(u);
    }

    /**
     * Löscht die Updateable-Instanz von der Liste
     * @param u Der zu löschene Updateable
     */
    public void removeUpdateable(Updateable u){
        updateables.remove(u);
    }
}
