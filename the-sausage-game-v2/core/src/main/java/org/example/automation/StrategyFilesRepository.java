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
import java.util.Set;

public class StrategyFilesRepository {

    private static final Map<String, Strategy> cache = new HashMap<>();

    public static final Strategy getStrategy(int x, int y, boolean isFirstPlayer) throws FileNotFoundException {

        // unikatny kluc
        String key = x + "x" + y + "_" + isFirstPlayer;

        if (!cache.containsKey(key)) {
            // Cesta k súboru (za predpokladu, že súbory máš v "assets/strategies/")
            String fileName = "strategies/strategy_" + x + "x" + y;
            fileName += isFirstPlayer ? "_p1" : "_p2";

            FileHandle fileHandle;

//            // CSV - stary sposob
//            String fileNameCsv = fileName + ".csv";
//            fileHandle = Gdx.files.internal(fileNameCsv);
//            if (!fileHandle.exists()) {
//                System.err.println("Súbor na webe/disku neexistuje: " + fileNameCsv);
//            }

            // potom BIN
            String fileNameBin = fileName + ".bin";
            fileHandle = Gdx.files.internal(fileNameBin);
            if (!fileHandle.exists()) {
                System.err.println("Súbor na webe/disku neexistuje: " + fileNameBin);
            }

            try {
                // Tu musíme upraviť aj FileHandlingUtil, aby prijímal FileHandle (viď bod 2)
                System.out.println("Načítavam stretégiu zo súboru...");
                Set<Long> rawMoves = FileHandlingUtil.loadStrategyBinaryFromFileHandle(fileHandle);
                Strategy strategy = new Strategy(x, y, rawMoves, isFirstPlayer, true); // todo mozno nejako tento fakt nacitat zo suboru
                cache.put(key, strategy);
                System.out.println("Načítaná stratégia: " + fileName);

            } catch (Exception e) {
                System.err.println("Chyba pri čítaní/parsovaní súboru: " + fileName);
//                e.printStackTrace();
            }

        } else {
            System.out.println("Stratégia načítaná z cache: " + key);
        }

        return cache.get(key);
    }
}
