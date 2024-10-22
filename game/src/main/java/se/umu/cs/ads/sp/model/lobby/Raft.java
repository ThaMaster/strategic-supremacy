package se.umu.cs.ads.sp.model.lobby;

import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.utils.enums.LobbyClientState;

public class Raft {

    private int msgCount;
    private int numVotes;
    LobbyClientState state;
    LobbyHandler lobbyHandler;
    private int numClients;
    private int receivedVotes;
    public Raft(LobbyHandler lobbyHandler, LobbyClientState state){
        this.lobbyHandler = lobbyHandler;
        msgCount = 0;
        resetVotes();
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
    public void initiateLeaderElection(ComHandler comhandler){
        state = LobbyClientState.CANDIDATE;
        receivedVotes = 0;
    }

    private void resetVotes(){
        numVotes = 0;
        receivedVotes = 0;
        numClients = lobbyHandler.getLobby().currentPlayers;
    }

    public void receiveVote(boolean approved){
        receivedVotes++;
        if(approved){
            numVotes++;
            if(numVotes <= numClients){
                //Got majority of the votes, i am now the leader
                state = LobbyClientState.LEADER;
                //Send out i am now the new leader
            }
        }
        else if(receivedVotes <= numClients/2){
            // I have not the majority of the votes, going back to follower
            state = LobbyClientState.FOLLOWER;
            resetVotes();
        }
    }

    public boolean approveNewLeader(int msgCount){
        //if(state == LobbyClientState.LEADER){
            // Maybe return false?
        //}
        if(state == LobbyClientState.CANDIDATE){
            return this.msgCount < msgCount;
        }
        else{
            //I am a follower or a leader
            return this.msgCount <= msgCount;
        }
    }
}
