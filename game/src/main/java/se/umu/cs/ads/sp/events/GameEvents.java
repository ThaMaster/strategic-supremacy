package se.umu.cs.ads.sp.events;

import java.util.ArrayList;

public class GameEvents {

    private static GameEvents instance;
    private ArrayList<GameEvent> events;
    private ArrayList<GameEvent> history;

    private GameEvents() {
        events = new ArrayList<>();
        history = new ArrayList<>();
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

    public ArrayList<GameEvent> getHistory(){
        return history;
    }

    public synchronized void clearHistory(){
        history.clear();
    }

    public synchronized void moveToHistory(GameEvent event){
        history.add(event);
    }

}
