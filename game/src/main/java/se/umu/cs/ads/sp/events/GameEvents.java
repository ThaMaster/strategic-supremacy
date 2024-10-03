package se.umu.cs.ads.sp.Events;

import java.util.ArrayList;

public class GameEvents {

    private static GameEvents instance;
    private ArrayList<GameEvent> events;

    private GameEvents() {
        events = new ArrayList<>();
    }

    public void addEvent(GameEvent event) {
        this.events.add(event);
    }

    public ArrayList<GameEvent> getEvents() {
        return events;
    }

    public void clearEvents() {
        this.events.clear();
    }

    public static synchronized GameEvents getInstance(){
        if(instance == null){
            instance = new GameEvents();
        }
        return instance;
    }
}
