package se.umu.cs.ads.sp.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEvents {

    private static GameEvents instance;
    private List<GameEvent> events;
    private List<GameEvent> history;

    private GameEvents() {
        events = Collections.synchronizedList(new ArrayList<GameEvent>());
        history = Collections.synchronizedList(new ArrayList<GameEvent>());
    }

    public void addEvent(GameEvent event) {
        this.events.add(event);
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void clearEvents() {
        this.events.clear();
    }

    public static synchronized GameEvents getInstance(){
        if(instance == null){
            instance = new GameEvents();//new GameEvents();
        }
        return instance;
    }

    public List<GameEvent> getHistory(){
        return history;
    }

    public synchronized void clearHistory(){
        history.clear();
    }

    public synchronized void moveToHistory(GameEvent event){
        history.add(event);
    }

}
