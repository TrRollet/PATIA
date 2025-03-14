package fr.uga.pddl4j.yasp;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.util.BitVector;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

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

    public SATEncoding(Problem problem, int steps) {

        this.steps = steps;

        // Encoding of init
        // Each fact is a unit clause
        // Init state step is 1
        // We get the initial state from the planning problem
        // State is a bit vector where the ith bit at 1 corresponds to the ith fluent being true
        final int nb_fluents = problem.getFluents().size();
        //System.out.println(" fluents = " + nb_fluents );
        final BitVector init = problem.getInitialState().getPositiveFluents();
        
        // Encoder l'état initial
        for (int i = 0; i < nb_fluents; i++) {
            List<Integer> clause = new ArrayList<>();
            // Si le fluent est vrai dans l'état initial
            if (init.get(i)) {
                clause.add(pair(i + 1, 1)); // Fluent i+1 est vrai à l'étape 1
            } else {
                clause.add(-pair(i + 1, 1)); // Fluent i+1 est faux à l'étape 1
            }
            initList.add(clause);
        }
        
        // Encoder les préconditions et effets des actions
        List<Action> actions = problem.getActions();
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            int actionId = nb_fluents + i + 1; // ID unique pour chaque action
            
            // Préconditions : si l'action est exécutée, ses préconditions doivent être satisfaites
            BitVector precondPos = action.getPrecondition().getPositiveFluents();
            BitVector precondNeg = action.getPrecondition().getNegativeFluents();
            
            for (int j = 0; j < nb_fluents; j++) {
                if (precondPos.get(j)) {
                    // Si l'action est exécutée, sa précondition positive doit être vraie
                    List<Integer> clause = new ArrayList<>();
                    clause.add(-pair(actionId, 1)); // Non action à l'étape 1
                    clause.add(pair(j + 1, 1));     // Ou fluent j+1 est vrai à l'étape 1
                    actionPreconditionList.add(clause);
                }
                if (precondNeg.get(j)) {
                    // Si l'action est exécutée, sa précondition négative doit être vraie
                    List<Integer> clause = new ArrayList<>();
                    clause.add(-pair(actionId, 1)); // Non action à l'étape 1
                    clause.add(-pair(j + 1, 1));    // Ou fluent j+1 est faux à l'étape 1
                    actionPreconditionList.add(clause);
                }
            }
            
            /// Effets : si l'action est exécutée, ses effets doivent être appliqués
            if (!action.getConditionalEffects().isEmpty()) {
                BitVector effectPos = action.getConditionalEffects().get(0).getEffect().getPositiveFluents();
                BitVector effectNeg = action.getConditionalEffects().get(0).getEffect().getNegativeFluents();
                
                for (int j = 0; j < nb_fluents; j++) {
                    if (effectPos.get(j)) {
                        // Si l'action est exécutée, son effet positif doit être vrai
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-pair(actionId, 1)); // Non action à l'étape 1
                        clause.add(pair(j + 1, 2));     // Ou fluent j+1 est vrai à l'étape 2
                        actionEffectList.add(clause);
                        
                        // Ajouter à addList pour le frame axiom
                        if (!addList.containsKey(j + 1)) {
                            addList.put(j + 1, new ArrayList<>());
                        }
                        addList.get(j + 1).add(actionId);
                    }
                    if (effectNeg.get(j)) {
                        // Si l'action est exécutée, son effet négatif doit être vrai
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-pair(actionId, 1)); // Non action à l'étape 1
                        clause.add(-pair(j + 1, 2));    // Ou fluent j+1 est faux à l'étape 2
                        actionEffectList.add(clause);
                        
                        // Ajouter à delList pour le frame axiom
                        if (!delList.containsKey(j + 1)) {
                            delList.put(j + 1, new ArrayList<>());
                        }
                        delList.get(j + 1).add(actionId);
                    }
                }
            }
        }
        
        // Encoder le but
        BitVector goal = problem.getGoal().getPositiveFluents();
        for (int i = 0; i < nb_fluents; i++) {
            if (goal.get(i)) {
                goalList.add(pair(i + 1, steps)); // Le fluent i+1 doit être vrai à l'étape finale
            }
        }

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
        
        // Ajouter l'état initial (seulement pour from=1)
        if (from == 1) {
            this.currentDimacs.addAll(initList);
        }
        
        // Pour chaque étape
        for (int step = from; step <= to; step++) {
            
            // Encoder les préconditions des actions à cette étape
            for (List<Integer> clause : actionPreconditionList) {
                List<Integer> newClause = new ArrayList<>();
                for (Integer lit : clause) {
                    int[] unpaired = unpair(Math.abs(lit));
                    int bitnum = unpaired[0];
                    int oldStep = unpaired[1];
                    
                    // Ajuster l'étape
                    int adjustedLit = (lit > 0) ? 
                        pair(bitnum, step + (oldStep - 1)) : 
                        -pair(bitnum, step + (oldStep - 1));
                    
                    newClause.add(adjustedLit);
                }
                this.currentDimacs.add(newClause);
            }
            
            // Encoder les effets des actions à cette étape
            if (step < to) { // On n'encode pas les effets pour la dernière étape
                for (List<Integer> clause : actionEffectList) {
                    List<Integer> newClause = new ArrayList<>();
                    for (Integer lit : clause) {
                        int[] unpaired = unpair(Math.abs(lit));
                        int bitnum = unpaired[0];
                        int oldStep = unpaired[1];
                        
                        // Ajuster l'étape
                        int adjustedLit = (lit > 0) ? 
                            pair(bitnum, step + (oldStep - 1)) : 
                            -pair(bitnum, step + (oldStep - 1));
                        
                        newClause.add(adjustedLit);
                    }
                    this.currentDimacs.add(newClause);
                }
            }
            
            // Encoder les transitions d'état (frame axioms)
            if (step < to) {
                // Pour chaque fluent
                for (int fluent = 1; fluent <= addList.size() + delList.size(); fluent++) {
                    // Frame axiom positif : si un fluent est vrai à l'étape t+1, c'est qu'il était vrai à t ou qu'une action l'a rendu vrai
                    List<Integer> posFrame = new ArrayList<>();
                    posFrame.add(-pair(fluent, step + 1)); // Non fluent à t+1
                    posFrame.add(pair(fluent, step));      // Ou fluent à t
                    
                    // Ou une action qui ajoute ce fluent
                    if (addList.containsKey(fluent)) {
                        for (Integer action : addList.get(fluent)) {
                            posFrame.add(pair(action, step));
                        }
                    }
                    
                    this.currentDimacs.add(posFrame);
                    
                    // Frame axiom négatif : si un fluent est faux à l'étape t+1, c'est qu'il était faux à t ou qu'une action l'a rendu faux
                    List<Integer> negFrame = new ArrayList<>();
                    negFrame.add(pair(fluent, step + 1));  // Fluent à t+1
                    negFrame.add(-pair(fluent, step));     // Ou non fluent à t
                    
                    // Ou une action qui supprime ce fluent
                    if (delList.containsKey(fluent)) {
                        for (Integer action : delList.get(fluent)) {
                            negFrame.add(pair(action, step));
                        }
                    }
                    
                    this.currentDimacs.add(negFrame);
                }
            }
            
            // Encoder la disjonction des actions (au plus une action par étape)
            if (step <= to) {
                int nbFluents = addList.size() + delList.size();
                int nbActions = actionPreconditionList.size() / (2 * nbFluents); // Estimation du nombre d'actions
                
                for (int a1 = nbFluents + 1; a1 < nbFluents + nbActions; a1++) {
                    for (int a2 = a1 + 1; a2 <= nbFluents + nbActions; a2++) {
                        List<Integer> mutexClause = new ArrayList<>();
                        mutexClause.add(-pair(a1, step));
                        mutexClause.add(-pair(a2, step));
                        this.currentDimacs.add(mutexClause);
                    }
                }
            }
        }
        
        // Encoder le but
        this.currentGoal.clear();
        for (Integer lit : goalList) {
            int[] unpaired = unpair(Math.abs(lit));
            int bitnum = unpaired[0];
            
            // Ajuster l'étape pour le but
            int adjustedLit = (lit > 0) ? 
                pair(bitnum, to) : 
                -pair(bitnum, to);
            
            this.currentGoal.add(adjustedLit);
        }

        System.out.println("Encoding : successfully done (" + (this.currentDimacs.size()
                + this.currentGoal.size()) + " clauses, " + to + " steps)");
    }

}
