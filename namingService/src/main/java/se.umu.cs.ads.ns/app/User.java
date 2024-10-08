package se.umu.cs.ads.ns.app;

import se.umu.cs.ads.ns.util.Util;

public class User {

    public String username;
    public String ip;
    public int port;
    public long id;

    public User(String username, String ip, int port) {
        this.id = Util.sha1Hash(username + ":" + ip + ":" + port, 1024);
        this.username = username;
        this.ip = ip;
        this.port = port;
    }
}
