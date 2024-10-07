package se.umu.cs.ads.ns.app;

import se.umu.cs.ads.ns.util.Util;

import java.util.ArrayList;

public class Lobby {

    public Long id;
    public String name;
    public ArrayList<User> users = new ArrayList<>();
    public int maxPlayers;
    public User leader;
    public int currentPlayers;
    public Lobby(Long id,String name, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;

    }

    public void setUsers(ArrayList<User> users) {
        this.currentPlayers = users.size();
        this.users = users;
    }


    public Lobby(String name, int maxPlayers) {
        this.id = Util.generateId();
        this.name = name;
        this.maxPlayers = maxPlayers;
    }

    public void addLeader(User user){
        this.leader = user;
        this.users.add(user);
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
