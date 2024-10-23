package se.umu.cs.ads.sp.model.lobby;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.UtilModel;
import se.umu.cs.ads.sp.util.enums.EventType;
import se.umu.cs.ads.sp.util.enums.LobbyClientState;

public class Raft {

    private int msgCount;
    private int numApprovedVotes;
    LobbyClientState state;
    LobbyHandler lobbyHandler;
    private int numClients;
    private int receivedVotes;
    private ComHandler comHandler;

    public Raft(LobbyHandler lobbyHandler, LobbyClientState state, ComHandler comhandler){
        this.lobbyHandler = lobbyHandler;
        msgCount = 0;
        this.state = state;
        resetVotes();
        this.comHandler = comhandler;
    }

    public void updateMsgCount(int msgCount){
        this.msgCount = msgCount;
    }

    public int incMsgCount(){
        msgCount++;
        return msgCount;
    }
    public int getMsgCount(){
        return msgCount;
    }
    public void initiateLeaderElection(){
        state = LobbyClientState.CANDIDATE;
        receivedVotes = 1;
        numApprovedVotes++;
        numClients = lobbyHandler.getLobby().currentPlayers;
        comHandler.requestVote(this);
    }

    private void resetVotes(){
        numApprovedVotes = 0;
        receivedVotes = 0;
        if(lobbyHandler.getLobby() != null){
            numClients = lobbyHandler.getLobby().currentPlayers;
        }else{
            numClients = 0;
        }
    }

    public void receiveVote(boolean approved){
        System.out.println("Received a vote -> " + approved);
        receivedVotes++;
        if(approved){
            numApprovedVotes++;
            if(numApprovedVotes > numClients/2){
                //Got a majority of the votes, I am now the leader
                state = LobbyClientState.LEADER;
                if(AppSettings.DEBUG){
                    GameEvents.getInstance().addEvent(new GameEvent(UtilModel.generateId(), "I have become the new game leader", EventType.LOGG, lobbyHandler.getLobby().id));
                }
                System.out.println("I am now the leader baby");
                comHandler.notifyNewLeader();
            }
        }
        else if(receivedVotes >= numClients/2 && numApprovedVotes <= numClients/2){
            // I have not the majority of the votes, going back to follower
            System.out.println("I did not win the vote, damn it");
            state = LobbyClientState.FOLLOWER;
            resetVotes();
        }
    }

    public boolean iAmLeader(){
        return state == LobbyClientState.LEADER;
    }

    public void newLeaderElected(){
        if(AppSettings.DEBUG){
            GameEvents.getInstance().addEvent(new GameEvent(UtilModel.generateId(), "New leader has been elected", EventType.LOGG, lobbyHandler.getLobby().id));
        }
        System.out.println("New leader elected!");
        state = LobbyClientState.FOLLOWER;
    }

    public boolean leaderElectionStarted(){
        return state == LobbyClientState.CANDIDATE;
    }

    public boolean approveNewLeader(int msgCount){
        if(state == LobbyClientState.LEADER){
            return false;
        }
        else {
            return this.msgCount <= msgCount;
        }
    }
}
