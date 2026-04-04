package org.example.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Strategy implements Serializable {

    private Map<GameState,Sausage> table = new HashMap<>();

}
