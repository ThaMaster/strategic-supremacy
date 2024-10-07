package se.umu.cs.ads.ns.app;

public class User {

    public String username;
    public String ip;
    public int port;
    public long id;

    public User(long id, String username, String ip, int port) {
        this.id = id;
        this.username = username;
        this.ip = ip;
        this.port = port;
    }
}
