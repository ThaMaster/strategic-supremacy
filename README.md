# Strategic Supremacy

## Introduction

This git repository represents a project in the _Advanced Distributed Systems_ course at _Umeå University_. The project
is to cover the expected learning outcomes of the course and thus needs to handle a certain degree of complex problems.

## Key Bindings

This section contains all the different key bindings that are present in the game. These could be implemented further to
allow players change these bindings. For now, the below table summarizes the bindings:

| Key                    | Function                                     |
|------------------------|----------------------------------------------|
| **1**                  | Selects the first of the three player units  |
| **2**                  | Selects the second of the three player units |                                  |
| **3**                  | Selects the third of the three player units  |   
| **ARROW_UP**           | Moves the camera upwards                     |
| **ARROW_DOWN**         | Moves the camera downwards                   |
| **ARROW_LEFT**         | Moves the camera to the left                 |
| **ARROW_RIGHT**        | Moves the camera to the right                |
| **RIGHT_MOUSE_BUTTON** | Selects a unit                               |
| **LEFT_MOUSE_BUTTON**  | Moves a unit                                 |

## System Components

This section contains the discussed system components and internal systems that will be implemented. Since
some of the components has just been mentioned many things can and will change.

### Different clients in the system

All players connected to the same game will have different roles and act as different clients. For this
system, there is to exists three different clients:

- **Follower:** Followers will redirect all messages that is supposed to be to the leader to their assigned sub-leader.
- **Sub-leader** This client will act as a regular follower, but will instead also forward the messages retrieved from
  their followers to their assigned leader or sub-leader.
- **Leader:** The leader will be at the top of the hierarchy and act as the source of truth, regularly send out **L1**
  updates to other clients to fix potential concurrency errors that could have occurred.


### Leader election

Some type of raft algorithm.

### Communication Priority

All messages that are sent throughout the system needs to be filtered in some way such that messages containing more 
important information takes priority over other messages. Therefore, each message will also include a **Priority**. 
There will exist three types of priority that can be assigned to a message:

- **High Priority:** These messages will contain information that is vital for the game to stay consistent. This is 
  things like a players score in the game, a unit has died or similar messages.
- **Medium Priority:** Medium priority messages contains information that should be prioritized over low priority. 
  should these be used remains to be seen.
- **Low Priority:** All other messages that is not of either high or medium are considered low priority.

Depending on the update event which the message represent, the priority of the message will dictate if the leader should
overwrite its current information and restart its timer for the **L1** timer, see image below for illustration.

![messagePriority](/docs/images/messagePriority.png)

_Illustration of how the different messages sent from a client affect the leaders timer._

### Communication Zones

Since the game is to handle a minimum of 100 players playing concurrently, the communication between the clients needs 
to be handled in a different way rather than each player being connected to all other players and constantly send 
updates on the game.

Therefore, the communication will be handled in different zones depending on where a players units are relative to 
other players. There is planned a total of three zones are the **Leader Zone**, the **Middle Zone** and the 
**Client Zone** which will be explained in each upcoming section.

#### Z1 - Leader Zone

All clients in the game will receive updates from the leader client regardless of
where the players units are located in a time interval of 50 ms (might change later). The leader will
always be the source of the truth and sends out its current state and the followers must update their state
if there is inconsistencies.

#### Z2 - Middle Zone

If a unit that belongs to player _X_ detects from the **Z3** update that another player _Y_
has a unit that has entered their **Z2** zone, _X_ and _Y_ will.

#### Z3 - Client Zone

If a unit detects from the **Z2** update that a unit has entered their **Z1** zone, which
makes the units visible on the screen between the players. Now every action a player makes is sent to
the other player. Since the leader is considered the source of truth every action is also sent to the leader.

### Bounding Boxes

## Milestones

This section covers the different _milestones_ that are to dictate the state of development. It is clear that the main goal
of this project is to have a completely playable game that can be scaled tremendously. However, in order to get to that
point the different _milestones_ divides the work in feasible, yet rewarding, elements.

### Milestone 1

After completing _Milestone 1_ the project will have achieved:

- Create the general project structure.
- Implement the **Entity** class.
- Create possible components that is needed, such as **Colliders**, **States**, **Animators**, etc.
- Showcase a pre-defined map in the **GUI**.
- Implement the functionality to control one **Unit**. 
  - This includes, moving, attacking, picking up objects, etc. This
    should be done in a similar way as other **RTS** games such as _Warcraft 3_.
- Implement _Chests_ that the unit can interact/pick up with to earn money.

### Milestone 2

After completing _Milestone 2_ the project will have achieved:

- Created the **Naming Service** that will handle the fetching of existing game lobbies. 
  - The naming service will also dictate whether a lobby is full or not, preventing players from joining full lobbies.
- Implement a _lobby system_, including the **GUI**, making it possible to create, edit and delete lobbies. 
- Create the starting window when opening the game, showcasing the game name and let players choose their _Username_
  before connecting to the **Naming Service**.

### Milestone 3

After completing _Milestone 3_ the project will have achieved:

- Make it possible for other players to join an existing lobby.
  - The players that joined a lobby should therefore be visible for all players already existing in the lobby.
- Create the **Spawn Algorithm** that decides where all the players will initially be placed on the map.
  - If there are around 100 players, they should be equally distributed around the map in some way.
- Make it possible to specify if a map should be automatically generated or created from a pre-defined map.
  - The generated map should place _obstacles_, _chests_, etc. randomly in a way that kinda makes sense.
- Make it possible to start the game and see that all players that was in the lobby now has spawned on their location 
  that was determined by the **Spawn Algorithm**.
  - _This means that the players should be static and no concurrent communication is happening yet._

### Milestone 4

After completing _Milestone 4_ the project will have achieved:

- Create the different layers of communication between the clients.
  - Implement the **Leader Zone (Z1)**.
  - Implement the **Middle Zone (Z2)**.
  - Implement the **Client 3 (Z3)**.

### Milestone 5

After completing _Milestone 5_ the project will have achieved:

- Implemented **NPCs** that is controlled by an evenly distribution of players.
  - How should this communication be handled, like units or their own algorithm?

## Assets

This section contains the assets that was not made but rather used from other creators which of course needs to be 
credited for their work.

### Flag sprites

The flag was made by _ankousse26_ which can be found [here](https://ankousse26.itch.io/).

### Chest sprites

The different sprites for the chest was made by _Francisco Téllez_ which can be found [here](https://franjatesa.itch.io/).

### The tile sprites

All the tiles used in this project was created by _SciGho_ which can be found [here](https://ninjikin.itch.io/).