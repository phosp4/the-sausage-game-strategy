package org.example.strategy;

import lombok.Builder;
import lombok.NonNull;
import org.example.entities.GameBoard;

@Builder
public class MinimaxRunConfig {

    @NonNull private Integer width;
    @NonNull private Integer height;
    @NonNull private Boolean useTT;
    @NonNull private MinimaxType minimaxType;

    private boolean storeStrategy;
    private boolean abPruning;

    // ako ulozit strategy

    // ++ pridavne
    // benchmarks
    // task - single, table
    // max nodes investigated = 1_000_000_000

//    public Minimax run() {
//        GameBoard gameBoard = new GameBoard(width, height);
//        Minimax minimaxRunner = new Minimax();
//
//    }
}

enum MinimaxType {
    OBJECT_ORIENTED, BITBOARD
}

enum Task {
    SINGLE, TABLE
}
