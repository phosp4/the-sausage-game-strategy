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
import java.util.HashMap;
import java.util.Map;

public class StrategyRepository {

    private static final Map<String, Strategy> cache = new HashMap<>();

    public static final Strategy getStrategy(int x, int y, boolean isFirstPlayer) throws FileNotFoundException {

        // unikatny kluc
        String key = x + "x" + y + "_" + isFirstPlayer;

        if (!cache.containsKey(key)) {
            // Cesta k súboru (za predpokladu, že súbory máš v "assets/strategies/")
            String fileName = "strategies/strategy_" + x + "x" + y;
            fileName += isFirstPlayer ? "_p1.csv" : "_p2.csv";

            // POUŽITIE LIBGDX API NA SÚBORY
            FileHandle fileHandle = Gdx.files.internal(fileName);

            if (!fileHandle.exists()) {
                throw new FileNotFoundException("Súbor na webe/disku neexistuje: " + fileName);
            }

            try {
                // Tu musíme upraviť aj FileHandlingUtil, aby prijímal FileHandle (viď bod 2)
                Map<Long, Long> rawMoves = FileHandlingUtil.loadStrategyCSV(fileHandle);
                cache.put(key, new Strategy(rawMoves, isFirstPlayer));
                System.out.println("Načítaná stratégia: " + fileName);
            } catch (Exception e) {
                System.err.println("Chyba pri čítaní/parsovaní súboru: " + fileName);
                e.printStackTrace();
            }

        } else {
            System.out.println("Stratégia načítaná z cache: " + key);
        }

        return cache.get(key);
    }
}
