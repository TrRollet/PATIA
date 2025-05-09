
from npuzzle import (Solution,
					 State,
					 Move,
					 UP, 
					 DOWN, 
					 LEFT, 
					 RIGHT,
					 get_children,
					 is_goal,
					 is_solution,
					 load_puzzle,
					 to_string)
from node import Node
from typing import Literal, List, Set, Deque
import argparse
import math
import time
from collections import deque

BFS = 'bfs'
DFS = 'dfs'
ASTAR = 'astar'

Algorithm = Literal['bfs', 'dfs', 'astar']

def solve_bfs(open: List[Node]) -> Solution:
	'''Solve the puzzle using the BFS algorithm'''
	dimension = int(math.sqrt(len(open[0].get_state())))
	moves = [UP, DOWN, LEFT, RIGHT]
	
	queue: Deque[Node] = deque(open)
	visited: Set[tuple] = set()
	
	while queue:
		node = queue.popleft()
		state_tuple = tuple(node.get_state())
		
		if state_tuple in visited:
			continue
			
		visited.add(state_tuple)
		
		if is_goal(node.get_state()):
			return node.get_path()
			
		children = get_children(node.get_state(), moves, dimension)
		for child_state, move in children:
			if tuple(child_state) not in visited:
				child_node = Node(
					state=child_state,
					move=move,
					parent=node,
					cost=node.cost + 1
				)
				queue.append(child_node)
	return []

def solve_dfs(open: List[Node]) -> Solution:
	'''Solve the puzzle using the DFS algorithm'''
	max_depth = 1
	max_iterations = 50
	
	while max_depth <= max_iterations:
		dimension = int(math.sqrt(len(open[0].get_state())))
		moves = [UP, DOWN, LEFT, RIGHT]
		
		stack = open.copy()
		visited: Set[tuple] = set()
		
		while stack:
			node = stack.pop()
			state_tuple = tuple(node.get_state())
			
			if node.cost > max_depth:
				continue
				
			if state_tuple in visited:
				continue
				
			visited.add(state_tuple)
			
			if is_goal(node.get_state()):
				return node.get_path()

			children = get_children(node.get_state(), moves, dimension)
			for child_state, move in reversed(children):
				if tuple(child_state) not in visited:
					child_node = Node(
						state=child_state,
						move=move,
						parent=node,
						cost=node.cost + 1
					)
					stack.append(child_node)
		
		max_depth += 1
	
	return []	# Pas de solution

def heuristic(node: Node) -> int:
    """Calcule l'heuristique en utilisant la distance de Manhattan"""
    state = node.get_state()
    dimension = int(math.sqrt(len(state)))
    distance = 0
    
    for i in range(len(state)):
        if state[i] != 0:  # On ignore la case vide
            # Position actuelle
            current_row = i // dimension
            current_col = i % dimension
            
            # Position but
            target_row = state[i] // dimension
            target_col = state[i] % dimension
            
            # Calcul de la distance de Manhattan
            distance += abs(current_row - target_row) + abs(current_col - target_col)
    
    return distance

def solve_astar(open: List[Node], close: List[Node]) -> Solution:
    '''Solve the puzzle using the A* algorithm'''
    dimension = int(math.sqrt(len(open[0].get_state())))
    moves = [UP, DOWN, LEFT, RIGHT]
    
    open[0].heuristic = heuristic(open[0])
    
    # Ensemble des états visités pour éviter les doublons
    visited: Set[tuple] = set()
    
    while open:
        # Trouver le noeud avec le coût total (f = g + h) minimum
        current = min(open, key=lambda x: x.cost + x.heuristic)
        open.remove(current)
        
        state_tuple = tuple(current.get_state())
        
        if state_tuple in visited:
            continue
            
        visited.add(state_tuple)
        close.append(current)
        
        # Si on a atteint l'état but on renvoie le chemin
        if is_goal(current.get_state()):
            return current.get_path()

        # Génération des successeurs
        children = get_children(current.get_state(), moves, dimension)
        for child_state, move in children:
            if tuple(child_state) not in visited:
                child_node = Node(
                    state=child_state,
                    move=move,
                    parent=current,
                    cost=current.cost + 1
                )
                child_node.heuristic = heuristic(child_node)
                open.append(child_node)
    
    return []  # Pas de solution trouvée


def main():
	parser = argparse.ArgumentParser(description='Load an n-puzzle and solve it.')
	parser.add_argument('filename', type=str, help='File name of the puzzle')
	parser.add_argument('-a', '--algo', type=str, choices=['bfs', 'dfs', 'astar'], required=True, help='Algorithm to solve the puzzle')
	parser.add_argument('-v', '--verbose', action='store_true', help='Increase output verbosity')
	
	args = parser.parse_args()
	
	puzzle = load_puzzle(args.filename)
	
	if args.verbose:
		print('Puzzle:\n')
		print(to_string(puzzle))
	
	if not is_goal(puzzle):	 
		 
		root = Node(state = puzzle, move = None)
		open = [root]
		
		if args.algo == BFS:
			print('BFS\n')
			start_time = time.time()
			solution = solve_bfs(open)
			duration = time.time() - start_time
			if solution:
				print('Solution:', solution)
				print('Valid solution:', is_solution(puzzle, solution))
				print('Duration:', duration)
			else:
				print('No solution')
		elif args.algo == DFS:
			print('DFS\n')
			start_time = time.time()
			solution = solve_dfs(open)
			duration = time.time() - start_time
			if solution:
				print('Solution:', solution)
				print('Valid solution:', is_solution(puzzle, solution))
				print('Duration:', duration)
			else:
				print('No solution')
		elif args.algo == ASTAR:
			print('A*\n')
			start_time = time.time()
			solution = solve_astar([root], [])	# OPEN = [root], CLOSED = []
			duration = time.time() - start_time
			if solution:
				print('Solution:', solution)
				print('Valid solution:', is_solution(puzzle, solution))
				print('Duration:', duration)
			else:
				print('No solution')
	else:
		print('Puzzle is already solved')
	
if __name__ == '__main__':
	main()