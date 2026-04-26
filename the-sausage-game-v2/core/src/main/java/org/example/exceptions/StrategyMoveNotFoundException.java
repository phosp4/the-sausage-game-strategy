package org.example.exceptions;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;

public class StrategyMoveNotFoundException extends RuntimeException {
    public StrategyMoveNotFoundException(GameBoard g) {
        super("Sausage for board was not found in the selected strategy. The board is: " + BitEncoder.sausageGridToLongBitboard(g.getGrid()));
    }
}
