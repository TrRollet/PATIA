import random
import glob
import argparse

def generate_hardest_config(n, pegs):
    """Génère la configuration la plus difficile : tous les disques sur t1"""
    config = {peg: [] for peg in pegs}
    discs = [f'd{i+1}' for i in range(n)]
    # Met tous les disques sur la première tige dans l'ordre (plus grand en bas)
    config['t1'] = list(reversed(discs))
    return config

def generate_random_config(n, pegs):
    """Génère une configuration aléatoire valide"""
    config = {peg: [] for peg in pegs}
    available_discs = [f'd{i+1}' for i in range(n)]
    
    current_peg = random.choice(pegs)
    while available_discs:
        disc = max(available_discs, key=lambda x: int(x[1:]))
        config[current_peg].append(disc)
        available_discs.remove(disc)
        current_peg = random.choice(pegs)
    return config

def generate_hanoi_pddl(n, output_file, mode='random'):
    # Configuration initiale selon le mode
    pegs = ['t1', 't2', 't3']
    discs = [f'd{i+1}' for i in range(n)]  # d1, d2, ..., dn
    
    if mode == 'hard':
        config = generate_hardest_config(n, pegs)
    else:
        config = generate_random_config(n, pegs)

    # Génération du fichier PDDL
    with open(output_file, 'w') as f:
        # En-tête
        f.write(f"""(define (problem hanoi-{n})
  (:domain hanoi)
  
  (:objects 
    {' '.join(discs)} - disc    ; {n} disques
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    ; Relations de taille
""")
        
        # Génération des relations smaller
        for i in range(n):
            for j in range(i+1, n):
                f.write(f"    (smaller d{i+1} d{j+1}) ; d{i+1} < d{j+1}\n")
        
        f.write("\n    ; États clear initiaux\n")
        
        # Définition des états clear
        for peg in pegs:
            if not config[peg]:  # si la tige est vide
                f.write(f"    (clear {peg})\n")
            else:
                f.write(f"    (clear {config[peg][-1]})\n")  # dernier disque sur la tige
        
        f.write("\n    ; Positions initiales des disques\n")
        
        # Configuration des disques
        for peg in pegs:
            for i in range(len(config[peg])):
                if i == 0:  # premier disque sur la tige
                    f.write(f"    (on {config[peg][i]} {peg})\n")
                else:  # disque sur un autre disque
                    f.write(f"    (on {config[peg][i]} {config[peg][i-1]})\n")
        
        f.write("\n    (handempty) ; main vide\n")
        
        # Objectif : tout sur la dernière tige
        f.write("""  )

  (:goal 
    (and
""")
        # Configuration finale (tous les disques sur t3)
        for i in range(n-1):
            f.write(f"      (on d{i+1} d{i+2})\n")
        f.write(f"      (on d{n} t3)\n")
        
        f.write("    )\n  )\n)")

def get_next_problem_number():
    # Trouve tous les fichiers pXXX.pddl
    pattern = "hanoi/p[0-9][0-9][0-9].pddl"
    existing_files = glob.glob(pattern)
    
    if not existing_files:
        return "001"
        
    # Extrait les numéros en prenant seulement le nom du fichier
    numbers = []
    for f in existing_files:
        filename = f.split('/')[-1]
        num = int(filename[1:4])
        numbers.append(num)
    
    next_num = max(numbers) + 1
    return f"{next_num:03d}"

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Générateur de problèmes Tours de Hanoï')
    parser.add_argument('n', type=int, help='Nombre de disques')
    parser.add_argument('--mode', choices=['random', 'hard'], default='random',
                      help='Mode de génération : random (aléatoire) ou hard (le plus difficile)')
    
    args = parser.parse_args()
    
    next_num = get_next_problem_number()
    output_file = f"hanoi/p{next_num}.pddl"
    print(f"Génération du fichier {output_file}")
    print(f"Mode : {args.mode}")
    print(f"Nombre de disques : {args.n}")
    
    generate_hanoi_pddl(args.n, output_file, args.mode)