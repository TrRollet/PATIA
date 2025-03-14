package sokoban;

import java.io.*;
import java.util.*;

public class Agent {
    private static int width, height, numBoxes;
    private static char[][] map;
    private static int playerX, playerY;
    private static int[] boxesX, boxesY;
    
    // Pour générer le fichier problem.pddl
    private static void generateProblem() {
        try (PrintWriter writer = new PrintWriter("problem.pddl")) {
            // En-tête du fichier
            writer.println(";; Problem file for Sokoban");
            writer.println("(define (problem sokoban-instance)");
            writer.println("  (:domain sokoban)");
            writer.println();
            
            // Déclaration des objets
            writer.println("  (:objects");
            writer.println("    p - player");  // Le joueur
            // Les boîtes (b0, b1, etc.)
            for (int i = 0; i < numBoxes; i++) {
                writer.print("    b" + i);
            }
            writer.println(" - box");
            
            // Les positions (pos0-0, pos0-1, etc.)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    writer.print("    pos" + x + "-" + y);
                }
            }
            writer.println(" - position");
            writer.println("  )");
            writer.println();
            
            // État initial
            writer.println("  (:init");
                
            // Section des relations d'adjacence :
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] != '#') {
                        // Vérifier les 4 directions
                        if (y > 0 && map[y-1][x] != '#') {
                            writer.println("    (adjacent pos" + x + "-" + y + " pos" + x + "-" + (y-1) + ")");
                        }
                        if (y < height-1 && map[y+1][x] != '#') {
                            writer.println("    (adjacent pos" + x + "-" + y + " pos" + x + "-" + (y+1) + ")");
                        }
                        if (x > 0 && map[y][x-1] != '#') {
                            writer.println("    (adjacent pos" + x + "-" + y + " pos" + (x-1) + "-" + y + ")");
                        }
                        if (x < width-1 && map[y][x+1] != '#') {
                            writer.println("    (adjacent pos" + x + "-" + y + " pos" + (x+1) + "-" + y + ")");
                        }
                    }
                }
            }

            // Position initiale du joueur
            writer.println("    (at-player p pos" + playerX + "-" + playerY + ")");
                
            // Positions des boîtes
            for (int i = 0; i < numBoxes; i++) {
                writer.println("    (at-box b" + i + " pos" + boxesX[i] + "-" + boxesY[i] + ")");
            }
                
            // Cases libres (ni joueur, ni boîte, ni mur)
            boolean[][] occupied = new boolean[height][width];
            occupied[playerY][playerX] = true;
            for (int i = 0; i < numBoxes; i++) {
                occupied[boxesY[i]][boxesX[i]] = true;
            }
                
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] != '#' && !occupied[y][x]) {
                        writer.println("    (clear pos" + x + "-" + y + ")");
                    }
                }
            }
                
            // Positions cibles (marquées par *)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] == '*') {
                        writer.println("    (is-target pos" + x + "-" + y + ")");
                    }
                }
            }

            // Positions accessibles (toutes les positions qui ne sont pas des murs)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] != '#') {
                        writer.println("    (is-accessible pos" + x + "-" + y + ")");
                    }
                }
            }
                
            writer.println("  )");
                
            // But : avoir toutes les boîtes sur des cibles
            writer.println("  (:goal");
            writer.println("    (and");
            
            // Trouver toutes les positions cibles
            List<int[]> targets = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x] == '*') {
                        targets.add(new int[]{x, y});
                    }
                }
            }
            
            // Pour chaque boîte, définir un but vers une cible
            for (int i = 0; i < Math.min(numBoxes, targets.size()); i++) {
                int[] target = targets.get(i);
                writer.println("      (at-box b" + i + " pos" + target[0] + "-" + target[1] + ")");
            }
                
            writer.println("    )");
            writer.println("  )");
            writer.println(")");
                
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors de la génération du fichier problem.pddl: " + e.getMessage());
        }
    }
                    
    private static String executePlanifier() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "java", 
                "--add-opens", "java.base/java.lang=ALL-UNNAMED", 
                "-cp", "./pddl4j-4.0.0.jar", 
                "-server", "-Xms2048m", "-Xmx2048m", 
                "fr.uga.pddl4j.planners.statespace.FF", 
                "./domain.pddl", "./problem.pddl", 
                "-t", "40"
            );
            
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            p.waitFor();
            return output.toString();
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'exécution du planificateur: " + e.getMessage());
            return "";
        }
    }
    
    private static String extractPlan(String output) {
        StringBuilder actions = new StringBuilder();
        boolean inPlanSection = false;
        
        for (String line : output.split("\n")) {
            if (line.trim().equals("found plan as follows:")) {
                inPlanSection = true;
                continue;
            }
            
            if (inPlanSection && line.trim().startsWith("time spent:")) {
                break;
            }
            
            if (inPlanSection && line.matches("\\d+:\\s*\\(.*\\).*")) {
                try {
                    int openParen = line.indexOf("(");
                    int closeParen = line.indexOf(")");
                    
                    if (openParen == -1 || closeParen == -1) continue;
                    
                    String action = line.substring(openParen + 1, closeParen).trim();
                    String[] parts = action.split("\\s+");
                    
                    if (parts.length < 4) continue;
                    
                    if (parts[0].equals("move")) {
                        String[] fromPos = parts[2].substring(3).split("-");
                        String[] toPos = parts[3].substring(3).split("-");
                        
                        int fromX = Integer.parseInt(fromPos[0]);
                        int fromY = Integer.parseInt(fromPos[1]);
                        int toX = Integer.parseInt(toPos[0]);
                        int toY = Integer.parseInt(toPos[1]);
                        
                        if (toY < fromY) actions.append("U");
                        else if (toY > fromY) actions.append("D");
                        else if (toX > fromX) actions.append("R");
                        else if (toX < fromX) actions.append("L");
                    } else if (parts[0].equals("push")) {
                        // Pour une action push, on regarde la position de la boîte et sa destination
                        String[] boxPos = parts[3].substring(3).split("-");
                        String[] targetPos = parts[4].substring(3).split("-");
                        
                        int boxX = Integer.parseInt(boxPos[0]);
                        int boxY = Integer.parseInt(boxPos[1]);
                        int targetX = Integer.parseInt(targetPos[0]);
                        int targetY = Integer.parseInt(targetPos[1]);
                        
                        if (targetY < boxY) actions.append("U");
                        else if (targetY > boxY) actions.append("D");
                        else if (targetX > boxX) actions.append("R");
                        else if (targetX < boxX) actions.append("L");
                    }
                    
                    System.err.println("Action traitée: " + parts[0] + " -> " + 
                                    (actions.length() > 0 ? actions.charAt(actions.length()-1) : "?"));
                    
                } catch (Exception e) {
                    System.err.println("Erreur lors du parsing de la ligne: " + line);
                    e.printStackTrace(System.err);
                }
            }
        }
        
        return actions.toString();
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            // Lire les dimensions et le nombre de caisses
            String[] tokens = scanner.nextLine().split(" ");
            width = Integer.parseInt(tokens[0]);
            height = Integer.parseInt(tokens[1]);
            numBoxes = Integer.parseInt(tokens[2]);
            
            // Lire la carte
            map = new char[height][width];
            for (int y = 0; y < height; y++) {
                String line = scanner.nextLine();
                for (int x = 0; x < Math.min(width, line.length()); x++) {
                    map[y][x] = line.charAt(x);
                }
            }
            
            // Lire la position du joueur
            tokens = scanner.nextLine().split(" ");
            playerX = Integer.parseInt(tokens[0]);
            playerY = Integer.parseInt(tokens[1]);
            
            // Lire les positions des caisses
            boxesX = new int[numBoxes];
            boxesY = new int[numBoxes];
            for (int i = 0; i < numBoxes; i++) {
                tokens = scanner.nextLine().split(" ");
                boxesX[i] = Integer.parseInt(tokens[0]);
                boxesY[i] = Integer.parseInt(tokens[1]);
            }
            
            // Écrire les fichiers PDDL, exécuter le planificateur et convertir le plan
            File domainFile = new File("./domain.pddl");
            if (!domainFile.exists()) {
                System.err.println("Erreur: Le fichier domain.pddl est manquant!");
                System.out.println("U"); // Retourner une action par défaut
                scanner.close();
                return;
            }
            
            generateProblem();
            String rawOutput = executePlanifier();
            String actions = extractPlan(rawOutput);

            // Pour déboguer
            System.err.println("Plan brut:\n" + rawOutput);
            System.err.println("Actions extraites: " + actions);

            if (actions.isEmpty()) {
                System.out.println("U");
            } else {
                for (int i = 0; i < actions.length(); i++) {
                    System.out.println(actions.charAt(i));
                }
            }
            
            scanner.close();
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une action par défaut
            System.err.println("Erreur: " + e.getMessage());
            System.out.println("U");
        }
    }
}