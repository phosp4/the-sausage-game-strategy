package org.example.archive;

import org.example.entities.Player;
import org.example.entities.Sausage;

import java.util.List;
import java.util.Stack;

public class StrategyFinder {

    private Stack<Sausage> turns;
    private Player firstPlayer;
    private List<Sausage> firstPlayerStrategy; // zoznam klobasiek s vyhernou strategiou
    private Player winner;
    private Player playerWithStrategy;
//    private short vsetkyVoVetveVyherne; // zatial pre jedneho hraca, ale moze byt aj pre oboch
//    private short asponJednoVoVetveVyherne;

    // ako metoda generuj pri backtracku
//    public Map<Player, Integer> prehladajVetvu(Grid grid, Player player) {
//
//        if (grid.isFull()) {
//            Map<Player, Integer> bilanciaVetvy = new HashMap<>();
//            bilanciaVetvy.put(player, 1);
//            bilanciaVetvy.put(player.getNextPlayer(), 0);
//
//            return bilanciaVetvy;
//        }
//
//        Map<Player, Integer> bilanciaVetvy = new HashMap<>();
//        bilanciaVetvy.put(player, 0);
//        bilanciaVetvy.put(player.getNextPlayer(), 0);
//
//        for (tvar : kazdy-tvar-v-kazdej-rotacii) {
//            for (pozicia : vsetky-mozne-pozicie-v-gride) { // bez intersectioins
//                grid.addSausage(pozicia);
//                turns.add(pozicia); // technically not needed - included in grid
//
//                Map<Player, Integer> bilanciaPredoslejVetvy = prehladajVetvu(grid, player.getNextPlayer()); // alebo cez turn manager asi
//                bilanciaVetvy.put(player, bilanciaVetvy.get(player) + bilanciaPredoslejVetvy.get(player)); // asi takto?
//                Player nextPlayer = player.getNextPlayer();
//                bilanciaVetvy.put(nextPlayer, bilanciaVetvy.get(nextPlayer) + bilanciaPredoslejVetvy.get(nextPlayer));
//
//
//                turns.pop();
//                grid.removeSausage(pozicia);
//            }
//        }
//
//        if (firstPlayer.equals(player)) {
//            if (bilanciaVetvy.get(player) > 0) { // ak existuje
//                firstPlayerStrategy.add(klobasku-pre-ktoru-existuje...)
//            } else {
//                // ..?
//                // firstPlayerStrategy = null;
//            }
//        } else {
//            if (bilanciaVetvy.get(player.getNextPlayer() ) == 0) { // ak neexistuje
//                firstPlayerStrategy.add(hocijaku-klobasku-z-tej-vetvy);
//            }
//        }
//
//        // v tomto stadiu uz mame k dispozicii bilanciu celej sucastnej vetvy
//        // vieme teda zhodnotit, ci je tato veta vyherna pre daneho hraca, a pripadne to sekanie vetiev - NABUDUCE
//        // mozno nam teda netreba asponJednoVoVetveVyherne a vsetkyVoVetveVyherne nie?
//
//        //
//    }
}
