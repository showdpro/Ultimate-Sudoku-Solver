package com.example.john.sudokusolver;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by john on 5/3/18.
 * Contains the logic to solve Sudoku puzzles
 */

class SudokuPuzzleSolver {

    abstract class SudokuIterator {

        final int[][] mPartition;
        /**
         * <p>Indicates the head of the partition as well as its size</p>
         * <p>Has the same dimensions of mPartition</p>
         * <p>Its index mapping is independent of the mapping of mPartition</p>
         */
        final int[][] mPartitionHeadFlags;
        int slice, element, i, j, partitionHead;

        SudokuIterator(int[][] partition) {
            mPartition = partition;
            mPartitionHeadFlags = new int[mPartition.length][mPartition[0].length];
        }

        public boolean nextSlice() {
            return ++slice < MAX_LENGTH;
        }

        public boolean nextElement() {
            if ( ++element < MAX_LENGTH) {
                computeCoordinate();
                return true;
            }
            return false;
        }

        /**
         * <p>Modifies element to be equal to partitionHead</p>
         * @return a return value of true implies a call to nextPartitionElement() should be made
         * to get the first element of the partition
         */
        boolean nextPartition() {
            element = partitionHead;

            int limit = MAX_LENGTH - 1; // PartitionHeads are on the closed domain [-1, MAX_LENGTH - 2]

            while (++element < limit) {

                partitionHead = element;
                if (isPartitionHead(element + 1)) { // element indexes the partitionHead; element + 1 indexes its flag
                    return true;
                }
            }
            return false;
        }

        boolean nextPartitionElement() {

            int currentValue = getPartitionValue();

            while (nextElement()) {
                if (getPartitionValueByIndex(partitionHead, element) == currentValue) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Use this when writing over the partition
         * @param oldValue the value that was in the partition before starting the write. Useful is
         *         {@link #getPartitionValue()}
         * @return
         */
        boolean nextPartitionElement(int oldValue) {
            while (nextElement()) {
                if (getPartitionValueByIndex(partitionHead, element) == oldValue) {
                    return true;
                }
            }
            return false;
        }

        /**
         *
         * @param partitionHead
         * @param index When index == partitionHead, then the value of the partition is guaranteed.
         *              Moreover, if index points to any element of the partition, it will return
         *              the same value, as in the former case.
         * @return a value signifying the partition; incidentally, it is the value of the bitwise 'or'
         * of all of its elements.
         */
        int getPartitionValueByIndex(int partitionHead, int index) {
            int tmp = element;
            element = index;

            if (index == partitionHead) {
                nextElement(); // index at partitionHead guarantees a next element
            } else {
                computeCoordinate();
            }
            element = tmp;
            return mPartition[i][j];
        }

        /**
         *
         * @return the value of the partition at its head
         */
        int getPartitionValue() {
            return getPartitionValueByIndex(partitionHead, partitionHead);
        }

        /**
         * update i, j based on slice and element
         */
        abstract void computeCoordinate();

        void resetSlice() {
            slice = -1;
        }

        void resetElement() {
            element = -1;
        }

        void resetPartition() {
            resetPartitionHeadFlags();
            element = partitionHead = -2;
        }

        /**
         * <p>Precondition: nextSlice() == true</p>
         * <p>modifies: element</p>
         */
        void resetPartitionHeadFlags() {

            for (int k = 0; k < MAX_LENGTH; k++) {
                mPartitionHeadFlags[slice][k] = 1;
            }

            for (int k = 0, count, value; k < MAX_LENGTH; k++) {

                if (!isPartitionHead(k))
                    continue;

                element = k;
                computeCoordinate();
                value = mPartition[i][j];
                count = 1;
                while (nextElement()) {

                    if (isPartitionHead(element) && value == mPartition[i][j]) {
                        mPartitionHeadFlags[slice][element] = 0;
                        count++;
                    }
                }

                // Post updates
                mPartitionHeadFlags[slice][k] = count;
            }
        }

        private boolean isPartitionHead(int index) {
            return mPartitionHeadFlags[slice][index]!=0;
        }

        int getPartitionSize() {
            return mPartitionHeadFlags[slice][partitionHead + 1];
        }

        /**
         * <p>Modifies element to be equal to partitionHead</p>
         * @param value the value to match of the element
         * @return true iff the partition head was set to the index preceding the first element
         * with matching value
         */
        boolean resetPartition(int value) {
            resetPartition();

            int limit = MAX_LENGTH - 1; // PartitionHeads are on the closed domain [-1, MAX_LENGTH - 2]

            while (++element < limit) {

                partitionHead = element;
                if (getPartitionValueByIndex(partitionHead, element) == value) {
                    return true;
                }
            }
            return false;
        }
    }

    private class GridIterator extends SudokuIterator {
        GridIterator(int[][] partition) {
            super(partition);
        }

        @Override
        void computeCoordinate() {
            i = 3 * (slice / 3) + element / 3;
            j = 3 * (slice % 3) + element % 3;
        }
    }

    private class RowIterator extends SudokuIterator {
        RowIterator(int[][] partition) {
            super(partition);
        }
        @Override
        void computeCoordinate() {
            i = slice;
            j = element;
        }
    }

    private class ColumnIterator extends SudokuIterator {
        ColumnIterator(int[][] partition) {
            super(partition);
        }
        @Override
        void computeCoordinate() {
            i = element;
            j = slice;
        }
    }

    /**
     * Helper class that stores the search selections. A global instance of this will suffice
     * for all of the recursive steps of the Sudoku puzzle solving algorithm.
     */
    private class SearchHelper {
        /**
         * value > 0  -> element of partition found by augmentation
         * value == 0 -> element of partition found by alternative partition
         */
        private int[] selections = new int[MAX_LENGTH];
        private int size = 0;
        private int topmostIndex; // Not to be used for iteration
        private int partitionValue;
        private int partitionSize;
        private boolean isSuccess;
        SudokuIterator iterator;
        /**
         * The index immediately preceding the index of the first element of the partition
         */
        private int mNullIndex;

        void reset() {
            for (int i = 0; i < selections.length; i++) {
                selections[i] = 0;
            }
            size = 0;
            isSuccess = false;
        }

        void setNullIndex(int index) {
            mNullIndex = index;
        }

        void setPartitionValue(int val) {
            partitionValue = val;
            partitionSize = getLength(val);
        }

        int getPartitionValue() {
            return partitionValue;
        }

        int getPartitionSize() {
            return partitionSize;
        }

        /**
         * Use to help update state after a successful search
         */
        int getSizeAug() {
            return size;
        }

        /**
         * Use to help update state after a successful search
         */
        int getSizeAlt() {
            return partitionSize - size;
        }

        /**
         * Use to help update state after a successful search
         */
        int getValAug() {
            return selections[topmostIndex];
        }

        /**
         * Use to help update state after a successful search
         */
        int getValAlt() {
            return partitionValue & ~getValAug();
        }

        /**
         * Use to help update state after a successful search
         */
        boolean isElementTypeAugment(int index) {
            return selections[index] > 0;
        }

        boolean isSearchSuccess() {
            return isSuccess;
        }

        /**
         * @param cellValue is the value of the box/cell that corresponds to nextIndex,
         *                  given a SudokuIterator
         *
         * @param nextIndex Indicates the next cell to choose in forming the augmented partition
         *
         * @param prevIndex  Indicates the previously selected box/cell or -1 if no previous index.
         *                   The value of the partition by augmentation = selections[prevIndex]
         */
        boolean performAdd(int cellValue, int prevIndex, int nextIndex) {
            if (prevIndex == mNullIndex) {
                selections[nextIndex] = cellValue;

            } else if ((cellValue & selections[prevIndex]) != 0) { // Non-empty intersection
                selections[nextIndex] = cellValue | selections[prevIndex]; // Take a union

            } else {
                return false;
            }
            size++;
            topmostIndex = nextIndex;
            return true;
        }

        /**
         * Undoes an 'add'
         * <p>Precondition: {@link #performAdd(int, int, int) performAdd(int, int, nextIndex)}
         * == true</p>
         * @param nextIndex The index passed as nextIndex to performAdd
         */
        void performDelete(int nextIndex) {
            selections[nextIndex] = 0;
            size--;
        }

        /**
         * <p>Precondition: {@link #performAdd(int, int, int)} == true</p>
         * @return The length of the augmented value equals the length of the parent partition.
         * This is equivalent to a negative search outcome
         */
        boolean isSaturated() {
            return getLength(selections[topmostIndex]) == partitionSize;
        }

        /**
         * <p>Precondition: {@link #performAdd(int, int, int)} == true &&
         * {@link #isSaturated()} == false</p>
         * @return true iff a positive search outcome
         */
        boolean isSelfContainedPartitionOfSmallerSize() {
            return isSuccess = getLength(selections[topmostIndex]) == size;
        }
    }

    static final int[] VALUE = {0, 1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8};
    static final int ALL_DIGITS = 0b111111111;
    static final int MAX_LENGTH = 9;
    public static final int GRID    = 0;
    public static final int ROW     = 1;
    public static final int COLUMN  = 2;

    private int[][] mPuzzle;
    private int[][] partiallySolvedPuzzle;
    private GridIterator gridIterator;
    private RowIterator rowIterator;
    private ColumnIterator columnIterator;
    private SearchHelper searchHelper;

    public SudokuPuzzleSolver() {
        partiallySolvedPuzzle = new int[MAX_LENGTH][MAX_LENGTH];
    }

    public void printPartialSolution() {
        printPartialSolution("0.000", "-");
    }

    public void printPartialSolution(String decimalPattern, String negativePrefix) {
        float[][] printPuzzle = fetchPrintablePuzzle();

        DecimalFormat decimalFormat = new DecimalFormat(decimalPattern);
        decimalFormat.setPositivePrefix(" ");
        decimalFormat.setNegativePrefix(negativePrefix);

        for (float[] slice : printPuzzle) {
            for (float v : slice) {
                System.out.printf("%s ", decimalFormat.format(v));
            }
            System.out.println();
        }
    }

    public float[][] fetchPrintablePuzzle() {
        float[][] A = new float[MAX_LENGTH][MAX_LENGTH];

        for (int i = 0; i < MAX_LENGTH; i++) {
            for (int j = 0; j < MAX_LENGTH; j++) {
                A[i][j] = mPuzzle[i][j] != 0 ? -mPuzzle[i][j]
                        : getDigit(partiallySolvedPuzzle[i][j]);
            }
        }
        return A;
    }

    void initializePuzzle(int[][] puzzle) {

        int[][] partitionsRow = new int[MAX_LENGTH][];
        int[][] partitionsColumn = new int[MAX_LENGTH][];
        int[][] partitionsGrid = new int[MAX_LENGTH][];
        mPuzzle = puzzle;

        for (int i = 0; i < MAX_LENGTH; i++) {
            for (int j = 0; j < MAX_LENGTH; j++) {
                partiallySolvedPuzzle[i][j] =  this.mPuzzle[i][j] == 0 ? ALL_DIGITS :
                        VALUE[mPuzzle[i][j]];
            }
            partitionsRow[i] = Arrays.copyOf(partiallySolvedPuzzle[i], MAX_LENGTH);
            partitionsColumn[i] = Arrays.copyOf(partiallySolvedPuzzle[i], MAX_LENGTH);
            partitionsGrid[i] = Arrays.copyOf(partiallySolvedPuzzle[i], MAX_LENGTH);
        }

        gridIterator = new GridIterator(partitionsGrid);
        rowIterator = new RowIterator(partitionsRow);
        columnIterator = new ColumnIterator(partitionsColumn);

        searchHelper = new SearchHelper();
    }

    /**
     * <p>Invariant: The partial Solutions are stored in an array return by
     * {@link #getPartiallySolvedPuzzle()}. Let X be equal to {@link #getDigit(int) getDigit}
     * (y), where y is any element in the array. Then X has no fractional part implies X
     * is either a solved digit or an original digit in the puzzle</p>
     * @param puzzle A MAX_LENGTH by MAX_LENGTH array containing the digits 1-9 of the puzzle
     * @return true iff the puzzle is completely solved
     * @see #MAX_LENGTH
     */
    public boolean solve(int[][] puzzle) {
        initializePuzzle(puzzle);
        makeInitialDeductions();
        try {
            makeGeneralDeductions();
            return isSolved();
        } catch (Exception e) {
            Log.e("Solver", e.getMessage());
            // The solver had an internal error
            return false;
        }
    }

    public int[][] getPartiallySolvedPuzzle() {
        return partiallySolvedPuzzle;
    }

    /**
     *
     * @param type one of {@link #GRID}, {@link #ROW}, {@link #COLUMN}
     * @return The iterator that corresponds to type
     */
    SudokuIterator getIterator(int type) {
        return type == GRID ? gridIterator :
                type == ROW ? rowIterator :
                        columnIterator;
    }

    public float getDigit(int value) {
        return (float) (Math.log(value) / Math.log(2)) + 1;
    }

    /**
     *
     * @param value
     * @return The number of 1 bits in value
     */
    private int getLength(int value) {
        int len = 0;
        int size = VALUE.length;

        for (int i = 1; i < size; i++) {
            if ((value & VALUE[i]) != 0)
                len++;
        }
        return len;
    }

    /**
     * <p>Makes the length of the value of the partition equal to the number of cells of the partition.
     * Applies the value of the partition to the individual cell to make deductions to limit its
     * possible digits; ie. the possibilities for each cell are reduced</p>
     * <p>Does NOT reduce partition into smaller partitions. This comes later in the next step</p>
     * @see #getLength(int)
     * @see SudokuIterator
     */
    @SuppressLint("DefaultLocale")
    void makeInitialDeductions() {
        SudokuIterator[] its = { gridIterator, rowIterator, columnIterator };

        int newValue, i, j;
        int invertibleDigits;
        int partitionDigits;

        for (SudokuIterator it : its) {

            it.resetSlice();

            while (it.nextSlice()) {
                invertibleDigits = 0;

                // For each partition of the slice, accumulate its digits, if the partition is self-contained
                it.resetPartition();

                while (it.nextPartition()) {
                    partitionDigits = it.getPartitionValue();

                    if (getLength(partitionDigits) == it.getPartitionSize()) {
                        invertibleDigits |= partitionDigits;
                    }
                }

                // Invert the digits to use as a bitmask for the digits of the partitions that need to be masked.
                // These are the complementary set to the partitions in the first step
                invertibleDigits = ~invertibleDigits;

                // Update digits, or value, for each partition and its set of corresponding cells in the 9x9 puzzle grid
                it.resetPartition();

                while (it.nextPartition()) {
                    partitionDigits = it.getPartitionValue();

                    // Apply the bitmask to reduce the possible digits for the partition
                    newValue = partitionDigits & invertibleDigits;

                    if (getLength(partitionDigits) > it.getPartitionSize()) {
                        while (it.nextPartitionElement(partitionDigits)) {
                            i = it.i;
                            j = it.j;
                            it.mPartition[i][j] = newValue;

                            if (mPuzzle[i][j] != 0)
                                throw new RuntimeException(String.format(
                                        "Attempting to modify original digits @ (row=%d, column=%d", i, j));

                            // Update the puzzle
                            partiallySolvedPuzzle[i][j] &= newValue;
                        }
                    }
                }
            }
        }
    }

    /**
     * @throws Exception Internal error
     */
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

    boolean isSolved() {
        for (int i = 0; i < partiallySolvedPuzzle.length; i++) {
            for (int j = 0; j < partiallySolvedPuzzle[i].length; j++) {
                if (getLength(partiallySolvedPuzzle[i][j]) != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>Precondition: it.nextSlice() == true</p>
     * @param list an allocated list. The list will be populated with the value property of each
     *             partition for which the partition's size > 1
     * @return reference to list for convenience
     */
    private LinkedList<Integer> queuePartitions(SudokuIterator it, @NonNull LinkedList<Integer> list) {
        list.clear();
        it.resetPartition();

        while (it.nextPartition()) {
            if (it.getPartitionSize() > 1) {
                list.add(it.getPartitionValue());
            }
        }
        return list;
    }

    /**
     *
     * @param list an allocated list, containing all of the partition values of some slice,
     *             with the restriction getLength(v) > 1 for each value v in the list
     * @return whether a deduction was made
     */
    private boolean deducePartitions(SudokuIterator it, LinkedList<Integer> list)
            throws Exception {

        boolean deduction = false;

        while (!list.isEmpty()) {
            deduction |= shrinkPartitions(it, list);
        }
        return deduction;
    }

    /**
     *
     * Perform a search to determine how to split a single partition into two partitions
     * @param list may grow or shrink if the search succeeds. If it does not succeed, list
     *              will necessarily shrink
     */
    private boolean shrinkPartitions(SudokuIterator it, LinkedList<Integer> list) throws Exception {
        if (list.isEmpty()) {
            throw new IllegalStateException("Unable to perform deductions on partitions" +
                    "for empty list");
        }

        searchHelper.reset();
        searchHelper.iterator = it;
        searchHelper.setPartitionValue(list.poll()); // Remove the head from the queue. The removed item represents the partition that is to be worked on

        if (searchHelper.getPartitionSize() == 1) {
            throw new IllegalStateException("Operable partitions should be of size > 1, invariably");
        }

        it.resetPartition(searchHelper.getPartitionValue());
        searchHelper.setNullIndex(it.element);

        // Perform the search
        // The selection of cells that form a smaller partition will be maintained by the state of
        // searchHelper. The unselected cells form the alternative partition.
        search(it.element);

        if (searchHelper.isSearchSuccess()) {

            // New partition values
            int pv = searchHelper.getValAlt();
            int pvA = searchHelper.getValAug();

            // Number of cells of the partition formed by augmentation must be > 1
            if (searchHelper.getSizeAug() > 1) {
                list.addFirst(pvA);
            }
            // Number of cells of the alternative partition must be > 1
            if (searchHelper.getSizeAlt() > 1) {
                list.addFirst(pv);
            }

            int i, j;
            int parent_pVal = searchHelper.getPartitionValue();
            it.resetPartition(parent_pVal);

            while (it.nextPartitionElement(parent_pVal)) {
                i = it.i;
                j = it.j;
                it.mPartition[i][j] = searchHelper.isElementTypeAugment(it.element) ? pvA : pv;

                // Make deductions in the puzzle
                if (!searchHelper.isElementTypeAugment(it.element)) {
                    partiallySolvedPuzzle[i][j] &= pv;
                }
            }

            it.resetPartitionHeadFlags(); // Newly formed partitions require an update of the partition heads

            return true;
        }
        return false;
    }

    /**
     * <p>There are preconditions such that searchHelper is ready to begin the search. At least,
     * searchHelper.reset() and searchHelper.it != null</p>
     * <p>Uses SearchHelper to encapsulate steps, parameters, and return values</p>
     * @param startingIndex Where to begin the search. The indices are referred to as 'elements'
     *                      in Sudoku Iterator
     */
    private void search(int startingIndex) {
        SudokuIterator it = searchHelper.iterator;
        it.element = startingIndex; // Set the iterator to its correct starting place

        int currentIndex;

        while (it.nextPartitionElement()) {
            if (!searchHelper.performAdd(partiallySolvedPuzzle[it.i][it.j],
                    startingIndex,
                    it.element)) {
                continue;
            }

            currentIndex = it.element;

            if (searchHelper.isSaturated()) { // Fail
                searchHelper.performDelete(currentIndex);
                continue;

            } else if (searchHelper.isSelfContainedPartitionOfSmallerSize()) { // Success
                break;
            }
            search(currentIndex);

            if (searchHelper.isSearchSuccess()) {
                break;

            } else { // Fail
                searchHelper.performDelete(it.element = currentIndex); // Post update
            }
        }
    }
}