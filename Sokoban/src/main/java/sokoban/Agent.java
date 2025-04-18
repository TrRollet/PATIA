package sokoban;

import java.util.*;
import java.io.*;

public class Agent {
    public static void main(String[] args) {
        Scanner gameScanner = new Scanner(System.in);
        String actions = "";
        
        try {
            File planFile = new File("plan.txt");
            Scanner fileScanner = new Scanner(planFile);
            actions = fileScanner.nextLine().trim();
            fileScanner.close();
            
            System.err.println("Plan lu depuis plan.txt : " + actions);
            
            if (actions.isEmpty()) {
                System.out.println("U");
            } else {
                for (char action : actions.toCharArray()) {
                    System.out.println(action);
                }
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : Fichier plan.txt non trouvé");
            System.out.println("U");
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            System.out.println("U");
        } finally {
            gameScanner.close();
        }
    }
}