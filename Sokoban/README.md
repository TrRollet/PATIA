# Sokoban
Pour lancer le Sokoban il faut utiliser le script [sokoban.sh](sokoban.sh) avec les arguments suivants :
```bash
./sokoban.sh <numéro_niveau> [timeout]
```

Les niveaux se trouvent dans le dossier [config/](config/). Le timeout est en secondes et est optionnel. Si le timeout n'est pas spécifié, alors la limite est de 600 secondes.
Puis il faut ouvrir son navigateur et aller à l'adresse suivante : http://localhost:8888 (fonctionne en SSH depuis VSCode)

# Domaine
Le domaine se trouve dans le fichier [domain.pddl](domain.pddl).

Objets :
- `case` : case vide

Prédicats :
- `player` : position du joueur
- `box` : position d'une boîte
- `clear` : position d'une case vide
- `aligned-left` : Vérifie si 2 cases sont alignées à gauche (`aligned-right` n'est pas nécessaire car c'est équivalent)
- `aligned-up` : Vérifie si 2 cases sont alignées en haut (`aligned-down` n'est pas nécessaire car c'est équivalent)

Actions :
- `move-left` : déplace le joueur à gauche
- `move-right` : déplace le joueur à droite
- `move-up` : déplace le joueur en haut
- `move-down` : déplace le joueur en bas
- `push-left` : pousse une boîte à gauche
- `push-right` : pousse une boîte à droite
- `push-up` : pousse une boîte en haut
- `push-down` : pousse une boîte en bas

Quand une boîte est poussée, elle doit être poussée sur une case vide et il faut que le joueur, la boîte et la case vide soient alignés dans la même direction (même chose quand le joueur se déplace).

# Problème
Le problème généré sera enregistré dans le fichier `problem.pddl` (écrasé à chaque fois) et contiendra la représentation du niveau. Le but est qu'il y ait une boîte sur chaque case cible.

# Fonctionnement
Le fichier python [parseProblem.py](parseProblem.py) permet de parser le fichier de configuration du niveau et de générer le fichier PDDL correspondant et essaye de le résoudre. Si un plan est trouvé alors il est écrit dans le fichier `plan.txt` (écrasé à chaque fois), sous la forme `RUDUDL...` avec `R` pour droite, `U` pour haut, `D` pour bas et `L` pour gauche.

