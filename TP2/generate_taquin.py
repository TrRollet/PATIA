import argparse
import math
import glob

def get_next_problem_number():
    # Trouve tous les fichiers pXXX.pddl
    pattern = "taquin/p[0-9][0-9][0-9].pddl"
    existing_files = glob.glob(pattern)
    
    if not existing_files:
        return "001"
        
    # Extrait les numéros
    numbers = []
    for f in existing_files:
        filename = f.split('/')[-1]
        num = int(filename[1:4])
        numbers.append(num)
    
    next_num = max(numbers) + 1
    return f"{next_num:03d}"

def read_config(filename):
    """Lit la configuration depuis un fichier"""
    with open(filename, 'r') as f:
        numbers = [int(x) for x in f.read().split()]
    return numbers

def generate_taquin_pddl(numbers, output_file):
    # Vérifie si c'est une grille carrée
    size = int(math.sqrt(len(numbers)))
    if size * size != len(numbers):
        raise ValueError("Le nombre de valeurs doit être un carré parfait")

    with open(output_file, 'w') as f:
        f.write(f"""(define (problem taquin-{size}x{size})
  (:domain taquin)
  
  (:objects
    t1 t2""")
        
        # Création des objets tuiles (toutes sauf la case vide)
        for i in range(3, size*size):
            f.write(f" t{i}")
        
        f.write(" - tile\n    p1")
        # Création des positions
        for i in range(2, size*size + 1):
            f.write(f" p{i}")
        
        f.write(" - position\n  )\n\n  (:init\n")
        
        # Définition des adjacences
        f.write("    ; Adjacences horizontales\n")
        for row in range(size):
            for col in range(size-1):
                pos = row * size + col + 1
                f.write(f"    (adjacent p{pos} p{pos+1})\n")
                f.write(f"    (adjacent p{pos+1} p{pos})\n")
        
        f.write("\n    ; Adjacences verticales\n")
        for row in range(size-1):
            for col in range(size):
                pos = row * size + col + 1
                f.write(f"    (adjacent p{pos} p{pos+size})\n")
                f.write(f"    (adjacent p{pos+size} p{pos})\n")
        
        f.write("\n    ; Positions initiales\n")
        # Configuration initiale
        for i, num in enumerate(numbers, 1):
            if num == 0:
                f.write(f"    (blank p{i})\n")
            else:
                f.write(f"    (at t{num} p{i})\n")
        
        # État but
        f.write("""  )

  (:goal
    (and
""")
        
        f.write("      (blank p1)\n")  # Case vide en première position
        for i in range(1, size*size):
            f.write(f"      (at t{i} p{i+1})\n")
        f.write("    )\n  )\n)")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Générateur de problèmes Taquin')
    parser.add_argument('input_file', help='Fichier contenant la configuration initiale')
    
    args = parser.parse_args()
    
    try:
        numbers = read_config(args.input_file)
        next_num = get_next_problem_number()
        output_file = f"taquin/p{next_num}.pddl"
        print(f"Génération du fichier {output_file}")
        generate_taquin_pddl(numbers, output_file)
        print(f"Problème PDDL généré dans {output_file}")
    except Exception as e:
        print(f"Erreur : {e}")