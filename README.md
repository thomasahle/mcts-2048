mcts-2048
=========

A Monte Carlo Tree Search AI for the game 2048.
Using bitboards and customizable strategies.

Compile:

    git clone git@github.com:thomasahle/mcts-2048.git
    cd mcts-2048/2048
    javac -cp src/ isrc/dk/ahle/thomas/mcts2048/Main.java
    
Run:

    java -cp src dk.ahle.thomas.mcts2048.Main

    ...

    7598.17444219067 / 493
    7610.59842519685 / 508
    9 11 12 9
    5 6 8 5
    1 3 4 3
    2 1 2 1
    
    UCTStrategy Win%: 1.0, Avg: 7598.0, StdVar: 0.0, Min: 4096.0, Max: 4096.0, ms/m: 115.93982627007108

In this casse the maximum tile achieved was 4096.
The sum of tiles on the board was 7598.
Note the miniature board uses the log-2 base to display tiles, so 12 means 2^12 = 4048.

The default configuration above usually achives a max tile of 4,048 or 8,096, with 16,192 being a rare guest.

Options
=======

The code supports multiple evaluation and rollout strategies.
By default t uses the following:

    test(new UCTStrategy(1000, true,
                    new SumMeasure(),
                    new GreedyStrategy(new EnsambleMeasure()
                            .addMeasure(-1, new SmoothMeasure())
                            .addMeasure(1, new FreesMeasure()))), 1);

Which means it does 1000 roll-outs before each move to find the most promising.
Each roll-out is done using a greedy strategy, which combines various herustics, by default optimsing for smoothness and free squares.
The eventual node is evaluated using the `SumMeasure`, which means taking the sum of all tiles.
Alternatively one could use the maximum tile or the score achived.

Another quite good strategy is the CyclicStrategy, which simply repeats a certain sequence of moves:

    test(new CyclicStrategy(Board.DOWN, Board.LEFT, Board.DOWN, Board.RIGHT), 1000);
        
It is also possible to use the CyclicStrategy for rollouts ini UCT:

    test(new UCTStrategy(1000, true,
            new SumMeasure(),
            new CyclicStrategy(Board.DOWN, Board.LEFT, Board.DOWN, Board.RIGHT), 1);

Inspiration
===========

The idea of using bitboards for faster move generation is inspired by https://github.com/nneonneo/2048-ai which uses min-max search.

For some state of the art papers, using n-tuple networks, see https://link.springer.com/chapter/10.1007/978-3-319-50935-8_8 and https://arxiv.org/pdf/1604.05085.pdf
