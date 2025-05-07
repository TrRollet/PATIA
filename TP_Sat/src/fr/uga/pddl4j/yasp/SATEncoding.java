package fr.uga.pddl4j.yasp;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.util.BitVector;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;

/**
 * This class implements a planning problem/domain encoding into DIMACS
 *
 * @author H. Fiorino
 * @version 0.1 - 30.03.2024
 */
public final class SATEncoding {
    /*
     * A SAT problem in dimacs format is a list of int list a.k.a clauses
     */
    private List<List<Integer>> initList = new ArrayList<List<Integer>>();

    /*
     * Goal
     */
    private List<Integer> goalList = new ArrayList<Integer>();

    /*
     * Actions
     */
    private List<List<Integer>> actionPreconditionList = new ArrayList<List<Integer>>();
    private List<List<Integer>> actionEffectList = new ArrayList<List<Integer>>();

    /*
     * State transistions
     */
    private HashMap<Integer, List<Integer>> addList = new HashMap<Integer, List<Integer>>();
    private HashMap<Integer, List<Integer>> delList = new HashMap<Integer, List<Integer>>();
    private List<List<Integer>> stateTransitionList = new ArrayList<List<Integer>>();

    /*
     * Action disjunctions
     */
    private List<List<Integer>> actionDisjunctionList = new ArrayList<List<Integer>>();

    /*
     * Current DIMACS encoding of the planning domain and problem for #steps steps
     * Contains the initial state, actions and action disjunction
     * Goal is no there!
     */
    public List<List<Integer>> currentDimacs = new ArrayList<List<Integer>>();

    /*
     * Current goal encoding
     */
    public List<Integer> currentGoal = new ArrayList<Integer>();

    /*
     * Current number of steps of the SAT encoding
     */
    private int steps;

    private Problem problem;

    public SATEncoding(Problem problem, int steps) {
        this.problem = problem;
        this.steps = steps;

        // Encoding of init
        // Each fact is a unit clause
        // Init state step is 1
        // We get the initial state from the planning problem
        // State is a bit vector where the ith bit at 1 corresponds to the ith fluent being true
        final int nb_fluents = problem.getFluents().size();
        //System.out.println(" fluents = " + nb_fluents );
        final BitVector init = problem.getInitialState().getPositiveFluents();
        
        // Initialisation
        for (int i = 0; i < nb_fluents; i++) {
            List<Integer> clause = new ArrayList<>();
            if (init.get(i)) {
                // Fluent vrai dans l'état initial
                clause.add(pair(i + 1, 1));
                initList.add(clause);
                // System.out.println("Ajout fluent positif " + (i+1) + " à l'état initial: " + 
                //     problem.toString(problem.getFluents().get(i)));
            } else {
                // Fluent faux dans l'état initial
                clause.add(-pair(i + 1, 1));
                initList.add(clause);
                // System.out.println("Ajout fluent négatif " + (i+1) + " à l'état initial: " + 
                //     problem.toString(problem.getFluents().get(i)));
            }
        }

        // Debug: afficher toutes les clauses de l'état initial
        // System.out.println("\nClauses de l'état initial:");
        // for (List<Integer> clause : initList) {
        //     System.out.println(toString(clause, problem));
        // }

        
        // Makes DIMACS encoding from 1 to steps
        encode(1, steps);
    }
    
    /*
     * SAT encoding for next step
     */
    public void next() {
        this.steps++;
        encode(this.steps, this.steps);
    }

    public String toString(final List<Integer> clause, final Problem problem) {
        final int nb_fluents = problem.getFluents().size();
        List<Integer> dejavu = new ArrayList<Integer>();
        String t = "[";
        String u = "";
        int tmp = 1;
        int [] couple;
        int bitnum;
        int step;
        for (Integer x : clause) {
            if (x > 0) {
                couple = unpair(x);
                bitnum = couple[0];
                step = couple[1];
            } else {
                couple = unpair(- x);
                bitnum = - couple[0];
                step = couple[1];
            }
            t = t + "(" + bitnum + ", " + step + ")";
            t = (tmp == clause.size()) ? t + "]\n" : t + " + ";
            tmp++;
            final int b = Math.abs(bitnum);
            if (!dejavu.contains(b)) {
                dejavu.add(b);
                u = u + b + " >> ";
                if (nb_fluents >= b) {
                    Fluent fluent = problem.getFluents().get(b - 1);
                    u = u + problem.toString(fluent)  + "\n";
                } else {
                    u = u + problem.toShortString(problem.getActions().get(b - nb_fluents - 1)) + "\n";
                }
            }
        }
        return t + u;
    }

    public Plan extractPlan(final List<Integer> solution, final Problem problem) {
        Plan plan = new SequentialPlan();
        HashMap<Integer, Action> sequence = new HashMap<Integer, Action>();
        final int nb_fluents = problem.getFluents().size();
        int[] couple;
        int bitnum;
        int step;
        for (Integer x : solution) {
            if (x > 0) {
                couple = unpair(x);
                bitnum = couple[0];
            } else {
                couple = unpair(-x);
                bitnum = -couple[0];
            }
            step = couple[1];
            // This is a positive (asserted) action
            if (bitnum > nb_fluents) {
                final Action action = problem.getActions().get(bitnum - nb_fluents - 1);
                sequence.put(step, action);
            }
        }
        for (int s = sequence.keySet().size(); s > 0 ; s--) {
            plan.add(0, sequence.get(s));
        }
        return plan;
    }
    
    // Cantor paring function generates unique numbers
    private static int pair(int num, int step) {
        return (int) (0.5 * (num + step) * (num + step + 1) + step);
    }

    private static int[] unpair(int z) {
        /*
        Cantor unpair function is the reverse of the pairing function. It takes a single input
        and returns the two corespoding values.
        */
        int t = (int) (Math.floor((Math.sqrt(8 * z + 1) - 1) / 2));
        int bitnum = t * (t + 3) / 2 - z;
        int step = z - t * (t + 1) / 2;
        return new int[]{bitnum, step}; //Returning an array containing the two numbers
    }

    private void encode(int from, int to) {
        this.currentDimacs.clear();
        this.currentGoal.clear();
        this.actionPreconditionList.clear();
        this.actionEffectList.clear();
        this.stateTransitionList.clear();
        this.addList.clear();
        this.delList.clear();
        this.actionDisjunctionList.clear();
        this.goalList.clear();
        
        // Encodage des actions
        // System.out.println("\nEncodage des actions:");
        
        final int nb_fluents = this.problem.getFluents().size();
        final List<Action> actions = this.problem.getActions();
        
        for (int step = from; step <= to; step++) {
            for (int i = 0; i < actions.size(); i++) {
                Action action = actions.get(i);
                int actionVar = pair(nb_fluents + i + 1, step);
                
                // System.out.println("\nAction (" + this.problem.toShortString(action) + ") à l'étape " + step + ":");
                
                // 1. Préconditions : ai → precond(ai) ≡ ¬ai ∨ precond(ai)
                BitVector precPos = action.getPrecondition().getPositiveFluents();
                for (int j = 0; j < nb_fluents; j++) {
                    if (precPos.get(j)) {
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-actionVar);           // ¬ai
                        clause.add(pair(j + 1, step));   // ∨ precond
                        actionPreconditionList.add(clause);
                        // System.out.println("  Précondition positive: " + 
                        //     this.problem.toString(this.problem.getFluents().get(j)));
                    }
                }

                BitVector precNeg = action.getPrecondition().getNegativeFluents();
                for (int j = 0; j < nb_fluents; j++) {
                    if (precNeg.get(j)) {
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-actionVar);           // ¬ai
                        clause.add(-pair(j + 1, step));  // ∨ ¬precond
                        actionPreconditionList.add(clause);
                        // System.out.println("  Précondition négative: " + 
                        //     this.problem.toString(this.problem.getFluents().get(j)));
                    }
                }

                // 2. Effets positifs : ai → effect+(ai) ≡ ¬ai ∨ effect+(ai)
                BitVector addEffects = action.getUnconditionalEffect().getPositiveFluents();
                for (int j = 0; j < nb_fluents; j++) {
                    if (addEffects.get(j)) {
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-actionVar);               // ¬ai
                        clause.add(pair(j + 1, step + 1));   // ∨ effect+ à l'étape suivante
                        actionEffectList.add(clause);
                        // System.out.println("  Effet positif: " + 
                        //     this.problem.toString(this.problem.getFluents().get(j)));
                    }
                }

                // 3. Effets négatifs : ai → ¬effect-(ai) ≡ ¬ai ∨ ¬effect-(ai)
                BitVector delEffects = action.getUnconditionalEffect().getNegativeFluents();
                for (int j = 0; j < nb_fluents; j++) {
                    if (delEffects.get(j)) {
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-actionVar);               // ¬ai
                        clause.add(-pair(j + 1, step + 1));  // ∨ ¬effect- à l'étape suivante
                        actionEffectList.add(clause);
                        // System.out.println("  Effet négatif: " + 
                        //     this.problem.toString(this.problem.getFluents().get(j)));
                    }
                }
            }
        }

        // Debug: afficher toutes les clauses des actions
        // System.out.println("\nClauses des préconditions:");
        // for (List<Integer> clause : actionPreconditionList) {
        //     System.out.println(toString(clause, this.problem));
        // }

        // System.out.println("\nClauses des effets:");
        // for (List<Integer> clause : actionEffectList) {
        //     System.out.println(toString(clause, this.problem));
        // }


        // Encodage des transitions d'états
        // System.out.println("\nEncodage des transitions d'états:");

        // Construction des listes add et del pour chaque fluent
        // System.out.println("\nConstruction des listes add/del:");
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            // System.out.println("\nAction: " + this.problem.toShortString(action));
            
            BitVector addEffects = action.getUnconditionalEffect().getPositiveFluents();
            BitVector delEffects = action.getUnconditionalEffect().getNegativeFluents();
            
            // Pour chaque fluent qui est un effet positif de l'action
            for (int j = 0; j < nb_fluents; j++) {
                if (addEffects.get(j)) {
                    if (!addList.containsKey(j)) {
                        addList.put(j, new ArrayList<>());
                    }
                    addList.get(j).add(nb_fluents + i + 1);
                    // System.out.println("  Ajout à addList[" + j + "]: " + 
                    //     this.problem.toString(this.problem.getFluents().get(j)) +
                    //     " produit par " + this.problem.toShortString(action));
                }
            }
            
            // Pour chaque fluent qui est un effet négatif de l'action
            for (int j = 0; j < nb_fluents; j++) {
                if (delEffects.get(j)) {
                    if (!delList.containsKey(j)) {
                        delList.put(j, new ArrayList<>());
                    }
                    delList.get(j).add(nb_fluents + i + 1);
                    // System.out.println("  Ajout à delList[" + j + "]: " + 
                    //     this.problem.toString(this.problem.getFluents().get(j)) +
                    //     " supprimé par " + this.problem.toShortString(action));
                }
            }
        }

        // System.out.println("\nClauses de transition d'état:");
        for (int step = from; step < to; step++) {
            // System.out.println("\nÉtape " + step + ":");
            for (int f = 0; f < nb_fluents; f++) {
                // 1. ¬fi ∧ fi+1 → ∨ ai devient fi ∨ ¬fi+1 ∨ (∨ ai)
                if (addList.containsKey(f)) {
                    List<Integer> clause = new ArrayList<>();
                    clause.add(pair(f + 1, step));      
                    clause.add(-pair(f + 1, step + 1)); 
                    for (Integer actionNum : addList.get(f)) {
                        clause.add(pair(actionNum, step));
                    }
                    stateTransitionList.add(clause);
                    // System.out.println("  Transition positive pour " + 
                    //     this.problem.toString(this.problem.getFluents().get(f)) + 
                    //     " : " + toString(clause, this.problem));
                }

                // 2. fi ∧ ¬fi+1 → ∨ ai devient ¬fi ∨ fi+1 ∨ (∨ ai)
                if (delList.containsKey(f)) {
                    List<Integer> clause = new ArrayList<>();
                    clause.add(-pair(f + 1, step));     
                    clause.add(pair(f + 1, step + 1));  
                    for (Integer actionNum : delList.get(f)) {
                        clause.add(pair(actionNum, step));
                    }
                    stateTransitionList.add(clause);
                    // System.out.println("  Transition négative pour " + 
                    //     this.problem.toString(this.problem.getFluents().get(f)) + 
                    //     " : " + toString(clause, this.problem));
                }
            }
        }

        // Debug: afficher les clauses de transition
        // System.out.println("\nClauses de transition:");
        // for (List<Integer> clause : stateTransitionList) {
        //     System.out.println(toString(clause, this.problem));
        // }

        // // Encodage de la disjonction des actions (exclusion mutuelle)
        // System.out.println("\nEncodage des disjonctions d'actions:");
        actionDisjunctionList.clear();

        for (int step = from; step <= to; step++) {
            // Pour chaque paire d'actions (ai, aj)
            for (int i = 0; i < actions.size(); i++) {
                for (int j = i + 1; j < actions.size(); j++) {
                    List<Integer> clause = new ArrayList<>();
                    
                    // Encode ¬ai ∨ ¬aj
                    int actionVar1 = pair(nb_fluents + i + 1, step);
                    int actionVar2 = pair(nb_fluents + j + 1, step);
                    
                    clause.add(-actionVar1);  // ¬ai
                    clause.add(-actionVar2);  // ¬aj
                    
                    actionDisjunctionList.add(clause);
                    
                    // System.out.println("Disjonction entre " + 
                    //     this.problem.toShortString(actions.get(i)) + " et " +
                    //     this.problem.toShortString(actions.get(j)) + 
                    //     " à l'étape " + step);
                }
            }
        }

        // Debug: afficher les clauses de disjonction
        // System.out.println("\nClauses de disjonction:");
        // for (List<Integer> clause : actionDisjunctionList) {
        //     System.out.println(toString(clause, this.problem));
        // }

        // Encodage du but
        final BitVector goalPos = this.problem.getGoal().getPositiveFluents(); // g+
        final BitVector goalNeg = this.problem.getGoal().getNegativeFluents(); // g-
        goalList.clear();

        // System.out.println("\nEncodage du but:");

        // 1. Fluents positifs du but (g+)
        for (int i = 0; i < this.problem.getFluents().size(); i++) {
            if (goalPos.get(i)) {
                goalList.add(pair(i + 1, to));
                // System.out.println("Ajout fluent but positif " + (i+1) + ": " + 
                //     this.problem.toString(this.problem.getFluents().get(i)));
            }
        }

        // 2. Fluents négatifs explicites du but (g-)
        for (int i = 0; i < this.problem.getFluents().size(); i++) {
            if (goalNeg.get(i)) {
                goalList.add(-pair(i + 1, to));
                // System.out.println("Ajout fluent but négatif explicite " + (i+1) + ": " + 
                //     this.problem.toString(this.problem.getFluents().get(i)));
            }
        }

        currentDimacs.addAll(initList);
        currentDimacs.addAll(actionPreconditionList);
        currentDimacs.addAll(actionEffectList);
        currentDimacs.addAll(stateTransitionList);
        currentDimacs.addAll(actionDisjunctionList);
        currentGoal.addAll(goalList);

        // Debug: afficher toutes les clauses
        // System.out.println("\nClauses de l'encodage:");
        // for (List<Integer> clause : currentDimacs) {
        //     System.out.println(toString(clause, this.problem));
        // }

        // Debug: afficher le but
        // System.out.println("\nBut à l'étape " + to + ":");
        // System.out.println(toString(currentGoal, this.problem));
        
        System.out.println("Encoding : successfully done (" + (this.currentDimacs.size()
                + this.currentGoal.size()) + " clauses, " + to + " steps)");
    }
}
