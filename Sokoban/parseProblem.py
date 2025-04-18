import subprocess
import sys
import json
import os
from typing import List, Tuple, Optional

def generate_pddl_problem(width: int, height: int, 
                         player_pos: Tuple[int, int],
                         boxes_pos: List[Tuple[int, int]], 
                         goals_pos: List[Tuple[int, int]], 
                         walls: List[Tuple[int, int]]) -> str:
    """Génère le fichier problem.pddl"""
    problem = "(define (problem sokoban-instance)\n  (:domain sokoban)\n\n"
    
    # Trouver les cases accessibles (non murs)
    accessible_cells = set()
    for y in range(height):
        for x in range(width):
            if (x,y) not in walls:
                # Vérifier si la case est connectée au reste du niveau
                if any((x+dx,y+dy) not in walls 
                      for dx,dy in [(0,1),(0,-1),(1,0),(-1,0)]):
                    accessible_cells.add((x,y))

    # Objets (cases)
    cells = [f"c{x}-{y}" for x,y in sorted(accessible_cells)]
    problem += "  (:objects\n    " + " ".join(cells) + " - case)\n\n"
    
    # État initial
    problem += "  (:init\n"
    # Position joueur
    problem += f"    (player c{player_pos[0]}-{player_pos[1]})\n"
    # Position boîtes
    for box in boxes_pos:
        problem += f"    (box c{box[0]}-{box[1]})\n"
    
    # Cases libres
    occupied = set(boxes_pos)
    for (x,y) in accessible_cells:
        if (x,y) not in occupied:
            problem += f"    (clear c{x}-{y})\n"
    
    # Relations spatiales
    # Alignements horizontaux
    for (x,y) in accessible_cells:
        if (x+1,y) in accessible_cells:
            problem += f"    (aligned-left c{x}-{y} c{x+1}-{y})\n"
    
    # Alignements verticaux
    for (x,y) in accessible_cells:
        if (x,y+1) in accessible_cells:
            problem += f"    (aligned-up c{x}-{y} c{x}-{y+1})\n"
                
    problem += "  )\n\n"
    
    # But
    problem += "  (:goal\n    (and\n"
    for goal in goals_pos:
        problem += f"      (box c{goal[0]}-{goal[1]})\n"
    problem += "    )\n  )\n)"
    
    return problem

def run_planner() -> Optional[str]:
    """Exécute le planificateur et retourne le plan"""
    try:
        cmd = [
            "java", "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "-cp", "./pddl4j-4.0.0.jar",
            "-server", "-Xms2048m", "-Xmx2048m",
            "fr.uga.pddl4j.planners.statespace.HSP",
            "./domain.pddl", "./problem.pddl",
            "-t", "600"
        ]
        
        result = subprocess.run(cmd, capture_output=True, text=True)
        return parse_plan(result.stdout)
    except Exception as e:
        print(f"Erreur lors de l'exécution du planificateur: {e}", file=sys.stderr)
        return None

def parse_plan(output: str) -> str:
    """Parse la sortie du planificateur et retourne les actions"""
    actions = []
    in_plan = False
    for line in output.split('\n'):
        if line.strip() == "found plan as follows:":
            in_plan = True
            continue
        if line.strip().startswith("time spent:"):
            break
        if in_plan and line.strip():
            try:
                # Extraire l'action entre parenthèses
                action = line[line.index('(')+1:line.index(')')].strip()
                # Séparer le type d'action et les paramètres
                parts = action.split()
                if parts:
                    # Extraire la direction après le tiret
                    direction = parts[0].split('-')[1]
                    if direction == 'up': actions.append('U')
                    elif direction == 'down': actions.append('D')
                    elif direction == 'left': actions.append('L')
                    elif direction == 'right': actions.append('R')
            except:
                continue
    
    return ''.join(actions)

def parse_level(level_str: str) -> tuple[List[Tuple[int, int]], List[Tuple[int, int]], List[Tuple[int, int]], Tuple[int, int], int, int]:
    """Parse une chaîne représentant le niveau et retourne les positions"""
    lines = [line.rstrip() for line in level_str.split('\n')]
    
    # Trouver les limites réelles du niveau (entre les murs)
    min_x, max_x = float('inf'), 0
    min_y, max_y = float('inf'), 0
    
    # Premier passage pour trouver les limites avec les murs
    for y, line in enumerate(lines):
        for x, char in enumerate(line):
            if char == '#':
                min_x = min(min_x, x)
                max_x = max(max_x, x)
                min_y = min(min_y, y)
                max_y = max(max_y, y)
    
    width = max_x - min_x + 1
    height = max_y - min_y + 1
    
    walls = []
    goals = []
    boxes = []
    player = None
    
    # Deuxième passage pour parser le contenu dans les limites trouvées
    for y in range(min_y, max_y + 1):
        line = lines[y]
        for x in range(min_x, max_x + 1):
            if x >= len(line):
                continue
            char = line[x]
            # Ajuster les coordonnées relatives au début du niveau
            adj_x = x - min_x
            adj_y = y - min_y
            
            if char == '#':
                walls.append((adj_x, adj_y))
            elif char == '.':
                goals.append((adj_x, adj_y))
            elif char == '$':
                boxes.append((adj_x, adj_y))
            elif char == '*':
                goals.append((adj_x, adj_y))
                boxes.append((adj_x, adj_y))
            elif char == '+':
                goals.append((adj_x, adj_y))
                player = (adj_x, adj_y)
            elif char == '@':
                player = (adj_x, adj_y)
    
    return walls, goals, boxes, player, width, height

def main():
    # Vérifier les arguments
    if len(sys.argv) != 2:
        print("Usage: python parseProblem.py <numero_niveau>")
        print("Example: python parseProblem.py 1")
        sys.exit(1)
    
    try:
        niveau = int(sys.argv[1])
        fichier = f"config/test{niveau}.json"
        
        # Vérifier si le fichier existe
        if not os.path.exists(fichier):
            print(f"Erreur: Le niveau {niveau} n'existe pas ({fichier} non trouvé)")
            sys.exit(1)
            
        # Lire le fichier JSON
        with open(fichier, 'r') as f:
            level_data = json.load(f)
        
        # Parser le niveau
        level_str = level_data["testIn"]
        walls, goals, boxes, player, width, height = parse_level(level_str)
        
        # Générer le problème PDDL
        problem = generate_pddl_problem(width, height, player, boxes, goals, walls)
        
        # Écrire le problème dans un fichier
        with open("problem.pddl", 'w') as f:
            f.write(problem)
        
        # Exécuter le planificateur
        actions = run_planner()
        
        if actions:
            print(f"Plan trouvé pour le niveau {niveau}: {actions}")
            # Écrire le plan dans un fichier pour l'agent
            with open("plan.txt", 'w') as f:
                f.write(actions)
        else:
            print(f"Aucun plan trouvé pour le niveau {niveau}")
            
    except ValueError:
        print("Erreur: Le numéro de niveau doit être un nombre entier")
        sys.exit(1)

if __name__ == "__main__":
    main()
    