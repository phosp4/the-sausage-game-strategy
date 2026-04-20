/**
 * zmyslom tejto triedy je, ze si pamata nacitane strategie
 * nemusime teda znova nacitavat subor, ak uz je nacitany
 */

package org.example.automation;

import org.example.entities.Strategy;
import org.example.utils.FileHandlingUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StrategyRepository {

    private static final Map<String, Strategy> cache = new HashMap<>();

    public static final Strategy getStrategy(int x, int y, boolean isFirstPlayer) throws FileNotFoundException {

        // unikatny kluc - napriklad takyto
        String key = x + "x" + y + "_" + isFirstPlayer;

        if (!cache.containsKey(key)) {
            String fileName = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + x + "x" + y;
            fileName += isFirstPlayer ? "_p1.csv" : "_p2.csv";

            try {
                Map<Long, Long> rawMoves = FileHandlingUtil.loadStrategyCSV(fileName);
                cache.put(key, new Strategy(rawMoves, isFirstPlayer));
                System.out.println("Z disku načítaná stratégia: " + fileName);
            } catch (IOException e) {
                throw new FileNotFoundException(fileName);
            }
        } else {
            System.out.println("Stratégia načítaná z cache: " + key);
        }

        return cache.get(key);
    }
}
