package org.example.strategy;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.entities.GameBoard;

@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class MemoCall {
    GameBoard gb;
    Boolean isMax;
}
