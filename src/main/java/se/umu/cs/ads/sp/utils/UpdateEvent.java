package se.umu.cs.ads.sp.utils;

import se.umu.cs.ads.sp.utils.enums.EventType;

public class UpdateEvent {

    private long id;
    private String event;
    private EventType type;

    public UpdateEvent(long id, String event, EventType type) {
        this.type = type;
        this.id = id;
        this.event = event;
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
