from typing import List, Literal, Set, Tuple
import random
from collections import deque

Algorithm = Literal['bfs', 'dfs', 'astar']
Move = Literal['up', 'down', 'left', 'right']

Solution = List[Move]
State = List[int]

'''
N-Puzzle problem
'''   
class NPuzzle:    
    UP = 'up'
    DOWN = 'down'
    LEFT = 'left'
    RIGHT = 'right'

    BFS = 'bfs'
    DFS = 'dfs'
    ASTAR = 'astar'
    
    goal_state : State = []
    initial_state : State = []
    
    def __init__(self, dimension_or_state : int | State):
        if isinstance(dimension_or_state, int):
            self.dimension = dimension_or_state
            self.initial_state = []
        else:
            self.initial_state = dimension_or_state
            self.dimension = int(len(dimension_or_state) ** 0.5)
        
    def generate(self, dimension : int) -> State:
        size = dimension * dimension
        state : State = list(range(size))
        
        for _ in range(1000):
            moves : List[Move] = self.get_possible_moves(state)

            move = random.choice(moves)
            newState = self.move(state, move)

            if newState: # l'état peut être None
                state = newState

        return state
    
    def create_goal(self) -> State:
        '''Create the goal state of the puzzle'''
        size = self.dimension * self.dimension
        return list(range(1, size)) + [0]
    
    def solve_bfs(self, puzzle : State) -> Solution:
        '''Solve the puzzle using the BFS algorithm'''
        # File pour BFS et dictionnaire pour garder trace du parent
        queue: deque[Tuple[State, Solution]] = deque([(puzzle, [])])
        # Ensemble des états visités (sous forme de string)
        visited: Set[str] = {str(puzzle)}
        
        while queue:
            current_state, moves = queue.popleft()
            
            # Si on a atteint l'état but
            if current_state == self.create_goal():
                return moves
                
            # Explorer tous les voisins
            possible_moves = self.get_possible_moves(current_state)
            for move in possible_moves:
                new_state = self.move(current_state, move)
                
                if new_state and str(new_state) not in visited:
                    visited.add(str(new_state))
                    # Ajouter le nouvel état et les mouvements pour y arriver
                    queue.append((new_state, moves + [move]))
                    
        return []  # Pas de solution trouvée
    
    def solve_dfs(self, puzzle : State) -> Solution:
        '''Solve the puzzle using the DFS algorithm'''
        return []
    
    def solve_astar(self, puzzle : State) -> Solution:
        '''Solve the puzzle using the A* algorithm'''
        return []
    
    def heuristic(self, puzzle : State) -> int:
        '''Calculate the heuristic value of the puzzle'''
        return 0
    
    def solve(self, puzzle : State, algorithm : Algorithm) -> Solution:
        '''Solve the puzzle using the algorithm'''
        solution : Solution = []
        
        if algorithm == self.BFS:
            solution = self.solve_bfs(puzzle)
        elif algorithm == self.DFS:
            solution = self.solve_dfs(puzzle)
        elif algorithm == self.ASTAR:
            solution = self.solve_astar(puzzle)
        
        return solution
    
    def is_solvable(self, puzzle : State) -> bool:
        '''Check if the puzzle is solvable'''
        return False
    
    def is_goal(self, puzzle : State) -> bool:
        '''Check if the puzzle is the goal state'''
        return False
    
    def get_neighbors(self, puzzle : State) -> List[State]:
        '''Get the neighbors of the puzzle'''
        neighbors : List[State] = []
        moves = self.get_possible_moves(puzzle)

        for move in moves:
            newState = self.move(puzzle, move)
            if newState:
                neighbors.append(newState)

        return neighbors

    def get_possible_moves(self, puzzle: State) -> List[Move]:
        '''Get the possible moves for the blank tile in the puzzle'''
        moves : List[Move] = []
        blankIndex = puzzle.index(0)
        size = self.dimension * self.dimension

        if blankIndex >= self.dimension:
            moves.append(self.DOWN)
        if blankIndex < size - self.dimension:
            moves.append(self.UP)
        if blankIndex % self.dimension != 0:
            moves.append(self.RIGHT)
        if blankIndex % self.dimension != self.dimension - 1:
            moves.append(self.LEFT)

        return moves
    
    def move(self, puzzle : State, direction : Move) -> State | None:
        '''Move the blank tile in the puzzle'''
        
        new_state = None

        current_state = puzzle.copy()
        blank_index = current_state.index(0)
        
        if direction == self.UP:
            bottomBlock = current_state[blank_index + self.dimension]
            current_state[blank_index] = bottomBlock
            current_state[blank_index + self.dimension] = 0
        elif direction == self.DOWN:
            rightBlock = current_state[blank_index - self.dimension]
            current_state[blank_index] = rightBlock
            current_state[blank_index - self.dimension] = 0
        elif direction == self.LEFT:
            rightBlock = current_state[blank_index + 1]
            current_state[blank_index] = rightBlock
            current_state[blank_index + 1] = 0
        elif direction == self.RIGHT:
            leftBlock = current_state[blank_index - 1]
            current_state[blank_index] = leftBlock
            current_state[blank_index - 1] = 0
        
        if current_state != puzzle:
            new_state = current_state

        return new_state
    
    def save_puzzle(self, puzzle : State, filename : str) -> None:
        '''Save the puzzle to a file'''
        pass
    
    def load_puzzle(self, filename : str) -> State:
        '''Load the puzzle from a file'''
        return []
    
    def print_puzzle(self, state: State) -> None:
        """Affiche le puzzle avec une grille stylisée"""
        if not state:
            print("État vide")
            return

        max_width = len(str(self.dimension * self.dimension - 1))
        h_line = '+' + ('-' * (max_width + 2) + '+') * self.dimension
        
        for i in range(self.dimension):
            print(h_line)
            for j in range(self.dimension):
                idx = i * self.dimension + j

                val = ' ' * max_width if state[idx] == 0 else str(state[idx]).rjust(max_width)
                print(f'| {val} ', end='')
            print('|')
        print(h_line)