# N-Puzzle

Le générateur de taquin est le même que celui qui est fourni, il va faire N coups aléatoires à partir d'une configuration initiale.

Les courbes de performance se trouvent dans le fichier [npuzzle.ipynb](npuzzle.ipynb) (cela ne fonctionne pas sur la VM pour la taille 4x4 car cela utilise trop de mémoire et il n'y en a pas assez donc la VM crash).
On remarque que l'algorithme A* est le plus rapide alors que l'algorithme DFS et BFS sont plus lents pour des configurations plus difficiles. Le timeout est souvent atteint pour le DFS et le BFS alors que l'A* ne l'atteint pas.
La difficulté est déterminée par le nombre de coups à faire pour résoudre le taquin, ce qui n'est pas le meilleur indicateur donc donne des courbes qui ne sont pas très lisses car une configuration avec plus de coups n'est pas forcément plus difficile à résoudre qu'une autre avec moins de coups mais cela donne une indication de la difficulté.