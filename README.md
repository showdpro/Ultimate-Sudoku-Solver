# Ultimate-Sudoku-Solver
### Sudoku Puzzle Game and Solver

New to Sudoku. Find out!

https://en.wikipedia.org/wiki/Sudoku

https://www.youtube.com/watch?v=XI6_VhPFjCQ

## Game
New puzzle

![New Puzzle](images/s_Screenshot_startPuzzle.png)

### Solve and Confirm Solution
Click on the _star_ button

![Solved Puzzle](images/s_Screenshot_startPuzzleSolved.png)

### Made a mistake?

Click on the _star_ button

![Failed Puzzle](images/s_Screenshot_BadPuzzleFail.png)

### Start over
Click from the action bar 'Reset Puzzle'

### Make a new Puzzle
Fork this Repo to include your own generator
- Click 'New Puzzle'
- Click on the _check mark_ button when finsihed setting up
- Play or solve, as usual

![Create New Puzzle](images/s_Screenshot_EnterDigits.png)

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

## Algorithm

### Divide and Conquer

Search for a smaller _partition_ within a parent _partition_. Two smaller _partitions_ are consequentially formed.

_Slice_, Defined:
> An arrangement of cells in the form of the following: 3x3 grid, 1 x 9 row, 9 x 1 column, with the condition each cell contains a digit distinct from digits therein the other cells

_Partition_, Defined:
>A partition is a subset of cells within a _slice_ that contain digit possibilities that number in size equal to the number of cells

_Deduction_, Defined:
>The event of forming or discovering two partitions whose union is a partition already contained in the puzzle 

The algorithm is finished when all partitions are of size 1, a cell with a single digit, or all deductions have been exhausted

### Code Snippets _Java_

    public SudokuPuzzleSolution solve(int[][] puzzle) {
            initializePuzzle(puzzle);
            makeInitialDeductions();
            boolean isSolved;
    
            try {
                makeGeneralDeductions();
                isSolved = isSolved();
            } catch (Exception e) {
                Log.e("Solver", e.getMessage());
                // The solver had an internal error
                isSolved = false;
            }
            return new SudokuPuzzleSolution(isSolved, copyPartiallySolvedPuzzle());
        }
        
1) Place all cells into partitions

        makeInitialDeductions();
        
2) Make all deductions

    Failure by Exception implies an internal error with the solver
         
        try {
            makeGeneralDeductions();
            
3) Return the condition of whether the puzzle was solved successfully

    Check all partitions are of size 1 -each cell contains a single digit

         try {
             //..
             isSolved = isSolved();     
         }
         
#### The crux

    void makeGeneralDeductions() throws Exception {
        SudokuIterator[] its = { gridIterator, rowIterator, columnIterator };

        // Used to keep track of newly formed partitions
        LinkedList<Integer> list = new LinkedList<>();

        boolean madeADeduction;

        do {
            madeADeduction = false;

            for (SudokuIterator it : its) {
                it.resetSlice();

                while (it.nextSlice()) {
                    madeADeduction |= deducePartitions(it, queuePartitions(it, list));
                }
            }
        } while (madeADeduction);
    }         
    
For each slice, for each direction: row, column, grid; _deduce_ partitions
from a queue of partitions extracted from the slice, where each of the queued partitions must be 
greater than one in size
- The trailing condition, aforementioned, ensures the process terminates