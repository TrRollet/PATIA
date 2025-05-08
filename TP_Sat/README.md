# Implémentation
L'encodage du problème PDDL en SAT est fait de la même façon que celle qui est montrée dans les slides du cours.

# Lancement du SATPlanner
```bash
./yetanothersatplanner.sh
```

# Comparaison avec le domaine des tours de Hanoi du TP2
- Sortie avec le SATPlanner:
```bash
Encoding : successfully done (15745 clauses, 15 steps)
Solution trouvée avec 15 étapes.

Modèle trouvé :
4 11 13 19 23 24 26 29 41 43 47 53 58 61 62 64 67 70 80 87 89 94 99 102 103 114 115 116 117 118 125 130 133 140 142 149 151 154 173 176 185 186 193 194 196 202 204 214 215 217 220 223 225 232 236 237 239 242 245 255 259 260 262 268 283 286 289 309 311 312 314 326 333 335 352 353 357 362 380 381 385 390 409 410 414 436 439 440 466 467 468 470 471 498 499 500 502 503 529 531 533 535 536 562 563 565 569 570 596 597 598 600 604 605 631 633 634 636 641 667 670 671 673 678 704 708 709 711 716 742 747 748 750 755 781 787 788 789 790 795 828 829 830 831 835 862 870 871 872 873 874 913 914 915 916 917 957 958 959 960 961 1002 1003 1004 1005 1048 1049 1050 1095 1096 1143 1385 1391 1496 1774 1901 2280 2421 2853 2931 2934 3017 3090 3093 

00: ( pick-up d1 t2) [0]
01: (   stack d1 d4) [0]
02: ( unstack d2 d3) [0]
03: (put-down d2 t2) [0]
04: ( unstack d1 d4) [0]
05: (   stack d1 d2) [0]
06: ( pick-up d3 t1) [0]
07: (   stack d3 d4) [0]
08: ( unstack d1 d2) [0]
09: (put-down d1 t1) [0]
10: ( pick-up d2 t2) [0]
11: (   stack d2 d3) [0]
12: ( pick-up d1 t1) [0]
13: (   stack d1 d2) [0]
```

- Sortie avec PDDL4J:
```bash
problem instantiation done successfully (38 actions, 38 fluents)

* Starting ASTAR search with FAST_FORWARD heuristic 
* ASTAR search succeeded

found plan as follows:

00: ( pick-up d1 t2) [0]
01: (   stack d1 d4) [0]
02: ( unstack d2 d3) [0]
03: (put-down d2 t2) [0]
04: ( unstack d1 d4) [0]
05: (   stack d1 d2) [0]
06: ( pick-up d3 t1) [0]
07: (   stack d3 d4) [0]
08: ( unstack d1 d2) [0]
09: (put-down d1 t1) [0]
10: ( unstack d2 d4) [0]
11: (   stack d2 d3) [0]
12: ( pick-up d1 t1) [0]
13: (   stack d1 d2) [0]
```

On peut voir que le plan trouvé est le même, les deux solutions ont 14 étapes. Cependant, le SATPlanner met plus de temps à trouver une solution, notamment pour les plus gros problèmes.