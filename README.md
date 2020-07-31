# Network Game Player
This is my implementation of the second group project for UC Berkeley CS61B Data Structures (Spring 2014). This project implements a computer player that plays the strategy board game "Network" against a human player or another computer player. The computer player can choose a move randomly, or by searching game trees. See ... for more details.

## Usage
 1. Clone the repositories
 `git clone https://github.com/kellyzhu11/network-game-player.git`
 2. Compile all the code from the root directory
`javac -g list/*.java
javac player/*.java`
3. Run Network with the following arguments from the root directory
`java Network {player1} {player2}`

A player can be one of "machine", "human", and "random". {player1} takes the first move.

For example, the below arguments starts a game between a human player (takes the first move) and a machine player:
`java Network human machine`



## Results
Sample game: A computer player plays against another computer player
