package se.umu.cs.ads.sp.events;

import se.umu.cs.ads.sp.utils.enums.EventType;

public class GameEvent {
    private long id;
    private String event;
    private EventType type;
    private long creator;

    public GameEvent(long id, String event, EventType type, long creator) {
        this.type = type;
        this.id = id;
        this.event = event;
        this.creator = creator;
    }

    public long getEventAuthor(){
        return creator;
    }
    public EventType getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
