# Ultimate-Sudoku-Solver
### Sudoku Puzzle Game and Solver

## Solver
Solves Sudoku Puzzles using a search to make all possible deductions. When the state becomes such that no more logical deductions are possible, the solver is finished.
The solver is stable; it "solves" puzzles in the following states:
- Not completed
- Completed
- Inconsistent

### Not completed
No formal definition apart from neither _Completed_ nor _Inconsistent_. Informally, if the puzzle has enough information, and it is not inconsistent, it will be solved

### Completed
The puzzle has already been completed, in which case the solver makes the quick determination 'already finished'. If the puzzle is not inconsistent, the solver will report _Success_

### Inconsistent
The puzzle was incorrectly entered or a false deduction was made external to the solver. The solver will report _Failed_
