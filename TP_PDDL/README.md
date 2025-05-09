# Hanoi
Le domaine se trouve dans le fichier [hanoi/domain.pddl](hanoi/domain.pddl) et les problèmes dans le dossier [hanoi/](hanoi/).

Pour générer un problème, il faut exécuter le script [hanoi/generate_hanoi.py](hanoi/generate_hanoi.py) avec les arguments suivants :
```bash
python generate_hanoi.py <nombre_disques> --mode <random|hard>
```
Le mode `random` génère un problème aléatoire, tandis que le mode `hard` génère le problème le plus difficile (quand tous les disques sont au même endroit). Le problème est enregistré dans le dossier `hanoi/` avec le nom `pXXX.pddl`, où `XXX` est le numéro du problème. Le nombre de disques doit être supérieur à 0.

Puis il faut utiliser le script [pddl4j.sh](pddl4j.sh) pour résoudre le problème.

# Taquin
Le domaine se trouve dans le fichier [taquin/domain.pddl](taquin/domain.pddl) et les problèmes dans le dossier [taquin/](taquin/).

Pour générer un problème à partir d'un fichier du même format que le premier TP, il faut exécuter le script [taquin/generate_taquin.py](taquin/generate_taquin.py) avec les arguments suivants :
```bash
python generate_taquin.py <nom_fichier>
```

Le problème est enregistré dans le dossier `taquin/` avec le nom `pXXX.pddl`, où `XXX` est le numéro du problème.

Puis il faut utiliser le script [pddl4j.sh](pddl4j.sh) pour résoudre le problème.