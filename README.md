# Tetris AI
 Tetris AI bot using custom DQN (Deep Q-learning network)




## Test Run

```bash
javac -cp "./lib/*;." @tetris.srcs
java -cp "./lib/*;." edu.bu.tetris.Main
```

There are several command line options available to you. If you want to see the exhausive list, please add a `-h` or `--help` argument to the command line and see the help message! Most of these command line arguments are your way of configuring the way you train your model (such as learning rate, batch size, etc.).

## Training

The hyperparameters we ended up going with
```bash
java -cp "./lib/*;." edu.bu.tetris.Main -q src.pas.tetris.agents.TetrisQAgent -p 10000 -t 400 -v 200 -b 10000 -n 0.001 -g 0.99 -c 10000000 -u 5 -s | tee run.log 
```

## Visualize after training

change the iteration to the latest iteration model trained
```bash
java -cp "./lib/*;." edu.bu.tetris.Main -a src.pas.tetris.agents.TetrisQAgent -i ./params/qFunction<iteration>.model
```

## src script location
`src/pas/tetris/agents/TetrisQAgent.java`

# Model Architecture

Feed Forward Neural Network. 1 input layer, 1 hidden layer, 1 output layer

Input layer 37 nodes
- Height of each column
- Closest hole from top for each column
- Number of hole following it for each column
- Next 3 minos types
- Number of lines cleared for this move
- Total holes below the maximum height
- Tetris for this move
- T spin for this move

Hidden layer
- 64 nodes

Output layer
- 1 node representing the q value of this move

Reward function: -0.51 * height - 0.36 * holes - 0.18 * bumpiness + 7 * game.getScoreThisTurn();