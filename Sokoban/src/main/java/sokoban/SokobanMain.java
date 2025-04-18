package sokoban;

import com.codingame.gameengine.runner.SoloGameRunner;

public class SokobanMain {
    public static void main(String[] args) {
        SoloGameRunner gameRunner = new SoloGameRunner();
        gameRunner.setAgent(Agent.class);
        // liste des fichiers de test à exécuter
        gameRunner.setTestCase("test8.json");

        gameRunner.start();
    }
}
