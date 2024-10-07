package se.umu.cs.ads.ns.app;

import java.util.ArrayList;

public class Lobby {

    public Long id;
    public String name;
    public ArrayList<User> users;
    public int maxPlayers;
    public User leader;
    public int currentPlayers;
    public Lobby(Long id, String name, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = 0;
    }

    public void addLeader(User user){
        this.leader = user;
        this.currentPlayers++;
    }

    public void addUser(User user){
        this.users.add(user);
        this.currentPlayers++;
    }

    public void removeUser(User user) {
        this.users.remove(user);
        this.currentPlayers--;
    }


}
