package se.umu.cs.ads.sp.model.lobby;

import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.communication.ComHandler;
import se.umu.cs.ads.sp.performance.LatencyTest;
import se.umu.cs.ads.sp.performance.TestLogger;
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

    private Long testId;

    public Raft(LobbyHandler lobbyHandler, LobbyClientState state, ComHandler comhandler) {
        this.lobbyHandler = lobbyHandler;
        msgCount = 0;
        this.state = state;
        resetVotes();
        this.comHandler = comhandler;
        testId = UtilModel.generateId();
    }

    public void updateMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public int incMsgCount() {
        msgCount++;
        return msgCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void initiateLeaderElection() {
        LatencyTest lTest = new LatencyTest(testId);
        lTest.setNumClients(1);
        TestLogger.newEntry(TestLogger.RAFT, lTest);
        state = LobbyClientState.CANDIDATE;
        receivedVotes = 1;
        numApprovedVotes++;
        numClients = lobbyHandler.getLobby().currentPlayers;
        comHandler.requestVote();
    }

    private void resetVotes() {
        numApprovedVotes = 0;
        receivedVotes = 0;
        if (lobbyHandler.getLobby() != null) {
            numClients = lobbyHandler.getLobby().currentPlayers;
        } else {
            numClients = 0;
        }
    }

    public void receiveVote(boolean approved) {
        receivedVotes++;
        if (approved) {
            numApprovedVotes++;
            if (numApprovedVotes > numClients / 2 && state != LobbyClientState.LEADER) {
                TestLogger.setFinished(testId);
                //Got a majority of the votes, I am now the leader
                state = LobbyClientState.LEADER;
                if (AppSettings.DEBUG) {
                    GameEvents.getInstance().addEvent(new GameEvent(UtilModel.generateId(), "I have become the new game leader", EventType.LOGG, lobbyHandler.getLobby().id));
                }
                System.out.println();
                System.out.println("\t I AM THE LEADER NOW -> " + Thread.currentThread());
                System.out.println();
                comHandler.notifyNewLeader();
            }
        } else if (receivedVotes >= numClients / 2 && numApprovedVotes <= numClients / 2) {
            // I have not the majority of the votes, going back to follower
            System.out.println("\t I DID NOT WIN THE ELECTION, DAMN IT");
            state = LobbyClientState.FOLLOWER;
            resetVotes();
        }
    }

    public boolean iAmLeader() {
        return state == LobbyClientState.LEADER;
    }

    public void newLeaderElected() {
        if (AppSettings.DEBUG) {
            GameEvents.getInstance().addEvent(new GameEvent(UtilModel.generateId(), "New leader has been elected", EventType.LOGG, lobbyHandler.getLobby().id));
        }
        state = LobbyClientState.FOLLOWER;
    }

    public boolean leaderElectionStarted() {
        return state == LobbyClientState.CANDIDATE;
    }

    public boolean approveNewLeader(int msgCount) {
        if (state == LobbyClientState.LEADER) {
            return false;
        } else {
            return this.msgCount <= msgCount;
        }
    }
}
