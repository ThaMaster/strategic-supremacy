package se.umu.cs.ads.sp.model.lobby;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.EventType;
import se.umu.cs.ads.sp.utils.enums.LobbyClientState;

public class Raft {

    private int msgCount;
    private int numVotes;
    LobbyClientState state;
    LobbyHandler lobbyHandler;
    private int numClients;
    private int receivedVotes;
    private ComHandler comHandler;

    public Raft(LobbyHandler lobbyHandler, LobbyClientState state){
        this.lobbyHandler = lobbyHandler;
        msgCount = 0;
        resetVotes();
    }

    public void setComHandler(ComHandler comHandler){
        this.comHandler = comHandler;
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
        numVotes++;
        numClients = lobbyHandler.getLobby().currentPlayers;
        comHandler.requestVote(this);
    }

    private void resetVotes(){
        numVotes = 0;
        receivedVotes = 0;
        if(lobbyHandler.getLobby() != null){
            numClients = lobbyHandler.getLobby().currentPlayers;
        }else{
            numClients = 0;
        }
    }

    public void receiveVote(boolean approved){
        receivedVotes++;
        if(approved){
            numVotes++;
            if(numVotes <= numClients){
                //Got a majority of the votes, I am now the leader
                state = LobbyClientState.LEADER;
                if(AppSettings.DEBUG){
                    GameEvents.getInstance().addEvent(new GameEvent(Utils.generateId(), "I have become the new game leader", EventType.LOGG, lobbyHandler.getLobby().id));
                }
                comHandler.notifyNewLeader();
            }
        }
        else if(receivedVotes <= numClients/2){
            // I have not the majority of the votes, going back to follower
            state = LobbyClientState.FOLLOWER;
            resetVotes();
        }
    }

    public void newLeaderElected(){
        if(AppSettings.DEBUG){
            GameEvents.getInstance().addEvent(new GameEvent(Utils.generateId(), "New leader has been elected", EventType.LOGG, lobbyHandler.getLobby().id));
        }
        state = LobbyClientState.CANDIDATE;
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
