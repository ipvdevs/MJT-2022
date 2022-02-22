package bg.sofia.uni.fmi.mjt.boardgames.utils;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BoardGameInitializer {
    private static final String[] boardGames = {
            "id;details.maxplayers;details.minage;details.minplayers;details.name;details.playingtime;attributes.boardgamecategory;attributes.boardgamemechanic;details.description",
            "1;8;8;2;Monopoly;120;Economic,Political,Negotiation;Area Control / Area Influence,Auction/Bidding,Dice Rolling;Monopoly is the classic fast-dealing property trading board game.",
            "2;4;12;3;Catan;90;Bluffing,Civilization,Fantasy,Negotiation,Political;Area Movement,Trading;Catan, previously known as The Settlers of Catan or simply Settlers, is a multiplayer board game designed by Klaus Teuber.",
            "3;2;10;5;Carcassonne;100;Abstract Strategy,Ancient,Civilization,Territory Building;Area Control / Area Influence,Hand Management,Set Collection,Tile Placement;Carcassonne is a tile-based German-style board game for two to five players, designed by Klaus-Jürgen Wrede and published in 2000 by Hans im Glück in German and by Rio Grande Games and Z-Man Games in English.",
            "4;12;16;4;Stay Away;60;Horror,Card Game,Negotiation;Hand Management;A group of archeologists has mysteriously disappeared during an expedition to the risen island of R'lyeh. You are a member of a rescue team sent to aid them.",
            "5;4;9;4;Belote;20;Card Game;Partnerships,Trick-taking;Belote, originally belotte, trick-and-meld card game derived from klaberjass about 1920 and now the most popular card game in France."
    };

    public static StringReader initBoardGamesStream() {
        return new StringReader(
                Arrays.stream(boardGames).collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
