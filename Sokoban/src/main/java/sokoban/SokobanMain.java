package sokoban;

import com.codingame.gameengine.runner.SoloGameRunner;

public class SokobanMain {
    public static void main(String[] args) {
        SoloGameRunner gameRunner = new SoloGameRunner();
        gameRunner.setAgent(Agent.class);
        // liste des fichiers de test à exécuter

        int testNumber = -1;
        if (args.length > 0) {
            try {
                testNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Il faut entrer un numéro de test valide: " + args[0]);
                System.exit(1);
            }
        }
        gameRunner.setTestCase("test" + testNumber + ".json");

        gameRunner.start();
    }
}
