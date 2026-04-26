/**
 * zmyslom tejto triedy je, ze si pamata nacitane strategie
 * nemusime teda znova nacitavat subor, ak uz je nacitany
 */

package org.example.automation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

            // skusanie nacitania strategie pre prehliadac
//            System.out.println("loading strategy...");
//            String fileName = "strategies/strategy_" + x + "x" + y;
//            fileName += isFirstPlayer ? "_p1.csv" : "_p2.csv";
//            HashMap<Long, Long> rawStrategy = loadStrategyCSVInternal(fileName);
//            System.out.println("strategy loaded!");
//
//            cache.put(key, new Strategy(rawStrategy, isFirstPlayer));
//            System.out.println("Z disku načítaná stratégia: " + fileName);

        } else {
            System.out.println("Stratégia načítaná z cache: " + key);
        }

        return cache.get(key);
    }

    // len na skusku - strategia pre prehliadac
    public static HashMap<Long, Long> loadStrategyCSVInternal(String assetPath) {
        HashMap<Long, Long> map = new HashMap<>();
        FileHandle file = Gdx.files.internal(assetPath);

        // reads the whole asset text (portable across desktop/html/android)
        String content = file.readString("UTF-8");
        for (String line : content.split("\n")) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            if (parts.length == 2) {
                map.put(Long.parseLong(parts[0].trim()), Long.parseLong(parts[1].trim()));
            }
        }
        return map;
    }

}
