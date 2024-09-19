# Project Plan

## The Project Idea

The project involves creating a real-time strategy game for more than 100 players across three rounds on a map.
Players earn points by completing objectives like capturing flags or destroying enemy units. The player with
the highest score after three rounds wins. Each player begins with three units that is controlled simultaneously.
At the start of each round, players can purchase upgrades using gold earned from various activities. Rounds
are limited by a shared timer.

## System Goals and Learning Experience

The goals for the implemented game and the system that will handle the communication between the players
are summarized in the following list:

- Implement a functioning game which includes winning and loosing, three controllable units per player,
  upgrade system, end screen, timer to limit length of game, AI to control players/units, etc
- **Make communication between players possible and efficient:**
  - Support atleast 100 concurrent players.
  - Game module to provide connection points between clients, enabling games to be created, joined and deleted.
  - Sharing real time data between clients (timer, player actions, etc.).
  - Each game consists of a leader client, which holds the source of truth (state of the game).
  - Recognize that a player has lost the connection to the group, for example through heartbeats, and
    perform leader election if the player was the leader.
  - Clients multicasts the messages received from the leader to increase the fault-tolerance

By developing the project with the before mentioned system goals the following learning experiences is
to be expected:

- How to create a leader election system in a distributed environment.
- Handle more complex data in the communication.
- Learn how to handle communication consistent and efficient in real-time.

## Scope and ambition of the system

Our ambition for the game is to have a fun, smooth running game. Actions should appear to be in real-time,
causing no lags. There should be no cap for the number of games running concurrently, each game should
support a minimum of 100 players.

## Main dynamics of the distributed system

The game uses a peer-to-peer system with the creator of the game automatically designated as the leader. Since
the game is supposed to support at least a hundred players at once, the communication between them must be
efficient. Therefore, the communication will be handled in different zones depending on where a players units
are relative to other players. There is planned a total of three zones (see Figure 1a for illustration):

- **Z3 - Leader Zone:** All clients in the game will receive updates from the leader client regardless of
  where the players units are located in a time interval of 50 ms (might change later). The leader will
  always be the source of the truth and sends out its current state and the followers must update their state
  if there is inconsistencies.
- **Z2 - Middle Zone:** If a unit that belongs to player _X_ detects from the **Z3** update that another player _Y_
  has a unit that has entered their **Z2** zone, _X_ and _Y_ will start sending frequent updates to each other.
- **Z1 - Client Zone:** If a unit detects from the **Z2** update that a unit has entered their **Z1** zone, which
  makes the units visible on the screen between the players. Now every action a player makes is sent to
  the other player. Since the leader is considered the source of truth every action is also sent to the leader.

In Figure 1b player one and player two will update each other frequently about their game state since they
have units in their **Z2** zone and also receive updates from **Z3** from the game leader. Player three will only
receive updates from **Z1**.

![singleUnitLayers](/docs/images/projectplan/singleUnitLayers.png)

_(a) Single unit from a player_

![multipleUnitLayers](/docs/images/projectplan/multipleUnitLayers.png)

_(b) Units from multiple players_

_Figure 1: The communication layers around different amount of units_

If the leader disconnects, a new leader is elected using the Raft algorithm. Players send periodic signals
to the leader to confirm their connection status.

## How to test the system

We will evaluate performance by testing how the number of players affects latency, measuring response times
as the player count increases while performing identical operations. Functional testing will focus on the safety
of the system, ensuring the system handles scenarios like leader/player disconnection or invalid game states
without crashing the game. We will build a very simple dumb AI to populate the game to see where how
many players the game can support.

## Identified technology

The general logic will be developed in the very common _Java_ programming language where the _Swing_ package
will handle all the graphical aspects of the game. The communication between the clients during a game will
be handled using _gRPC_. The naming service, that will enable players to connect with each game, will be
developed using the _Spring Boot_ framework.

## Evaluation of time required

The project will have a pre-planning, development and testing phase. The planning and development of this
is estimated to take 114 hours. The work after the development such as testing the performance and fixing
bugs is estimated to 30 hours which results in a total time of 144 hours.

## Ideas for extensions

- Implement a level editor where players can create their own levels.
- Update the game module system to allow players to spectate ongoing games and watch a replay on a finished game.