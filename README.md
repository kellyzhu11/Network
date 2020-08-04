
# Network Game Player
This is my implementation of the second group project for UC Berkeley CS61B Data Structures (Spring 2014). This project implements a computer player that plays the strategy board game "Network" against a human player or another computer player. The computer player can choose a move randomly, or by searching game trees. See [readme.pdf](https://github.com/kellyzhu11/network-game-player/blob/master/readme.pdf) for more details.

## Usage
 1. Clone the repositories
 
 ```bash
 $ git clone https://github.com/kellyzhu11/Network-Game-Player.git
 ```
 
 2. Compile all the code from the root directory
 
```bash
$ javac -g list/*.java player/*.java
```

3. Run Network with the following arguments from the root directory

```bash
$ java Network {player1} {player2}
```

A player can be one of "machine", "human", and "random". {player1} takes the first move.

For example, the below arguments starts a game between a human player (takes the first move) and a machine player:

```bash
$ java Network human machine
```

## Results
Sample game: A computer player plays against another computer player

<p align="center">
  <img src="https://raw.githubusercontent.com/kellyzhu11/network-game-player/master/sample_game.gif" />
</p>
