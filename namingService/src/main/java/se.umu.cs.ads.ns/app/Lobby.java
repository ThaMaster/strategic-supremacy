package se.umu.cs.ads.ns.app;

import se.umu.cs.ads.ns.util.Util;

import java.util.ArrayList;

public class Lobby {

    public Long id;
    public String name;
    public ArrayList<User> users = new ArrayList<>();
    public User leader;

    public int maxPlayers;
    public int currentPlayers;
    public String selectedMap;
    public boolean started;

    public int messageCount = 0;

    public Lobby(Long id, String name, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
    }

    public Lobby(Long id, String name, User leader, int currentPlayers, int maxPlayers, String selectedMap) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.selectedMap = selectedMap;
    }

    public Lobby(User leader, String name, int maxPlayers) {
        this.leader = leader;
        this.id = Util.sha1Hash(leader.username + ":" + leader.id + ":" + Util.generateId(), 1024);
        this.name = name;
        this.addUser(leader);
        this.currentPlayers = 1;
        this.maxPlayers = maxPlayers;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        this.currentPlayers = users.size();
    }

    public void setMessageCount(int count) {
        this.messageCount = count;
    }

    public void addLeader(User user) {
        this.leader = user;
        addUser(user);
    }

    public void addUser(User user) {
        this.users.add(user);
        this.currentPlayers++;
    }

    public void removeUser(User user) {
        for (User lobbyUser : this.users) {
            if (lobbyUser.id == user.id) {
                this.users.remove(lobbyUser);
                this.currentPlayers--;
                break;
            }
        }
    }

    public boolean hasUser(User user) {
        for (User lobbyUser : this.users) {
            if (lobbyUser.id == user.id) {
                return true;
            }
        }
        return false;
    }
}
