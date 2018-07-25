/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.john.sudokusolver.SudokuPuzzleSolver.VALUE;
import static com.example.john.sudokusolver.SudokuPuzzleSolver.MAX_LENGTH;

public class GraphTest {
    static class PartitionGraph {
        private int[][] mDigitPairings = new int[MAX_LENGTH][MAX_LENGTH];
        private int[][] mPostDigitPairings;

        {
            resetNodes();
        }

        public void resetNodes() {
            for (int i = 0; i < MAX_LENGTH; i++) {
                for (int j = 0; j < MAX_LENGTH; j++) {
                    mDigitPairings[i][j] = 0;
                }
            }
        }

        /**
         *
         * @param cellDigits Each element is an integer representing the digits of a cell.
         *                   The corresponding bit for each digit is given by VALUE[digit]
         */
        public void addCellDigits(List<Integer> cellDigits) {
            boolean[] digitsArray = new boolean[MAX_LENGTH];

            for (Integer digits :
                    cellDigits) {

                // Parse the cell's digits
                for (int i = 0; i < MAX_LENGTH; i++) {
                    digitsArray[i] = (VALUE[i + 1] & digits) != 0;
                }
                updateNodes(digitsArray);
            }
        }

        // Increment digit pairs based on a complete graph made from digits. Every digit present
        // in digits pairs with every other digit present in digits. Consider pairing drawing an edge
        // and digits are the nodes
        private void updateNodes(boolean[] cellDigits) {
            int length = cellDigits.length;
            for (int i = 0; i < length; i++) {
                if (cellDigits[i]) {
                    mDigitPairings[i][i]++;

                    for (int j = i + 1; j < length; j++) {
                        if (cellDigits[j]) {
                            mDigitPairings[i][j]++;
                        }
                    }
                }
            }
        }

        /**
         *
         * @return A list of cell digits reconstructed by using the digit pairings graph
         */
        public List<Integer> reconstructCellDigits() {
            List<Integer> cellDigits = new ArrayList<>();
            int[][] digitPairings = copyDigitPairings();
            int[] connectedDigits = new int[MAX_LENGTH];
            int connectedDigitsSize;
            int digits;
            boolean[] connectedNodes = new boolean[MAX_LENGTH];
            boolean disconnectedNode, passesCompleteTest;

            for (int i = 0; i < MAX_LENGTH; i++) {
                connectedNodes[i] = false;
            }

            // For each digit in the pool of digits for all of the cells...
            for (int i = 0; i < MAX_LENGTH; i++) {
                if (digitPairings[i][i] > 0) {
                    disconnectedNode = true;

                    // For all of the connected digits for the digit corresponding to i...
                    do {
                        digits = VALUE[i + 1];
                        connectedDigitsSize = 0;

                        // For all of the connected digits forming an instance of a complete graph...
                        for (int j = i + 1; j < MAX_LENGTH; j++) {
                            if (digitPairings[i][j] > 0) {
                                // The digits in the cell must form a complete graph.
                                // Consequentially, each digit must be paired transitively
                                // We perform a test to make sure all transitive pairings are included
                                passesCompleteTest = true;
                                for (int k = 0; k < connectedDigitsSize; k++) {
                                    if (digitPairings[connectedDigits[k]][j] > 0) {
                                        continue;
                                    }
                                    passesCompleteTest = false;
                                    break;
                                }

                                if (passesCompleteTest) {
                                    for (int k = 0; k < connectedDigitsSize; k++) {
                                        digitPairings[connectedDigits[k]][j]--;
                                    }

                                    digits |= VALUE[j + 1];
                                    digitPairings[i][j]--;
                                    connectedDigits[connectedDigitsSize++] = j;
                                    connectedNodes[j] = true;
                                }
                            }
                        }

                        if (connectedDigitsSize > 0) {
                            cellDigits.add(digits);
                            disconnectedNode = false;
                        } else {
                            break;
                        }

                    } while (true);

                    if (disconnectedNode && !connectedNodes[i]) {
                        cellDigits.add(VALUE[i + 1]);
                    }
                }
            }
            return cellDigits;
        }

        /**
         *
         * @return A list of cell digits reconstructed by using the digit pairings graph
         */
        public List<Integer> reconstructCellDigits2() {
            // A view of digitGroupings, indexed by digit
            List<List<Integer>>[] indexedDigitGroupings = new ArrayList[MAX_LENGTH];
            // The outermost list is by digit grouping; the innermost list
            // is the digit group obtained by paired digits.
            // When the algorithm finishes, the innermost list will be flattened to an integer,
            // with the bits signifying the digits
            List<List<Integer>> digitGroupings = new ArrayList<>();

            List<Integer> cellDigits = new ArrayList<>();
            int[][] digitPairings = copyDigitPairings();
            int[] connectedDigits = new int[MAX_LENGTH];
            int connectedDigitsSize;
            int digits;
            boolean[] connectedNodes = new boolean[MAX_LENGTH];
            boolean passesCompleteTest;

            // Initialize digit groupings
            {
                List<List<Integer>> list;
                List<Integer> digitz;

                for (int i = 0; i < MAX_LENGTH; i++) {
                    indexedDigitGroupings[i] = list = new ArrayList<>();

                    for (int j = 0; j < digitPairings[i][i]; j++) {
                        list.add(digitz = new ArrayList<>(2));
                        digitGroupings.add(digitz);
                        digitz.add(i);
                    }
                }
            }

//            for (int i = 0; i < MAX_LENGTH; i++) {
//                connectedNodes[i] = false;
//            }

            mergeDigits(indexedDigitGroupings, 5, 6, 0, 0);

            return cellDigits;
        }

        /**
         * Precondition:
         * indexDigitGroupings[digitA].get(indexA) must be disjoint from
         * indexDigitGroupings[digitB].get(indexB)
         */
        private void mergeDigits(List<List<Integer>>[] indexedDigitGroupings,
                                 int digitA,
                                 int digitB,
                                 int indexA,
                                 int indexB) {

            List<Integer> digitsA, digitsB;

            digitsA = indexedDigitGroupings[digitA].get(indexA);
            digitsB = indexedDigitGroupings[digitB].get(indexB);

            digitsA.addAll(digitsB);

            // We clear contents of digitsB so it is updated for all outside references
            digitsB.clear();
            indexedDigitGroupings[digitB].set(indexB, digitsA);
        }

        /**
         *
         * @return A list of cell digits reconstructed by using the digit pairings graph
         */
        public List<Integer> reconstructCellDigits3() {
            List<Integer> cellDigits = new ArrayList<>();
            mPostDigitPairings = copyDigitPairings();

            {
                int degree, degreeMax, degreePossibleIntersection, digit, digitValues;
                digit = 0;

                // For each row of the partition graph/matrix...
                for (int i = 0; i < MAX_LENGTH; i++) {
                    if (mPostDigitPairings[i][i] > 0) {
                        degreeMax = 0;

                        // Find the connecting digit of highest degree
                        for (int j = i + 1; j < MAX_LENGTH; j++) {
                            if (mPostDigitPairings[i][j] == 0)
                                continue;

                            degreePossibleIntersection = 0;

                            for (int k = i - 1; k > -1; k--) {
                                // Ok to read from the original, unmodified pairings
                                if (mDigitPairings[k][j] > 0)
                                    degreePossibleIntersection += mPostDigitPairings[k][i];
                            }

                            degree = minimum(mPostDigitPairings[i][j] - degreePossibleIntersection, mPostDigitPairings[j][j], mPostDigitPairings[i][i]);

                            if (degree > degreeMax) {
                                degreeMax = degree;
                                digit = j;
                            }
                        }
                        if (degreeMax > 0) {
                            mPostDigitPairings[i][i] -= degreeMax;
                            mPostDigitPairings[digit][digit] -= degreeMax;
                            mPostDigitPairings[i][digit] -= degreeMax;

                            // Update the solution
                            digitValues = VALUE[i + 1] | VALUE[digit + 1];
                            for (int j = 0; j < degreeMax; j++)
                                cellDigits.add(digitValues);
                        }
                    }
                }
            }
            return cellDigits;
        }

        private int minimum(int a, int b) {
            return a < b ? a : b;
        }

        private int minimum(int a, int b, int c) {
            return a < b ? (a < c ? a : c) : (b < c ? b : c);
        }

        private int[][] copyDigitPairings() {
            int[][] cpy = new int[MAX_LENGTH][];

            for (int i = 0; i < MAX_LENGTH; i++) {
                cpy[i] = Arrays.copyOf(mDigitPairings[i], MAX_LENGTH);
            }
            return cpy;
        }

        public String printDigitPairings() {
            return __printDigitPairings(mDigitPairings);
        }

        String printPostDigitPairings() {
            return __printDigitPairings(mPostDigitPairings);
        }

        private String __printDigitPairings(int[][] digitPairings) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < MAX_LENGTH; i++) {
                for (int j = 0; j < MAX_LENGTH; j++) {
                    if (i > j) {
                        builder.append("  ");
                    } else {
                        builder.append(digitPairings[i][j]);
                        builder.append(' ');
                    }
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public static class DigitGroupingComparator implements Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 == null) {
                    if (o2 == null)
                        return 0;
                    else
                        return -1;
                } else if (o2 == null)
                    return 1;
                else {
                    int a = o1;
                    int b = o2;

                    // Sort lexicographically with strings formed starting with the least bits
                    while (a > 0) {
                        switch (a % 2 - b % 2) {
                            case -1:
                                return -1;
                            case 1:
                                return 1;
                            default:
                                a = a >> 1;
                                b = b >> 1;
                        }
                    }
                    // b has higher order bits than a, or else they both have the same bits
                    return b > 0 ? -1 : 0;
                }
            }
        }
    }

    @Test
    public void testPartitionGraph() {

        {
            PartitionGraph partitionGraph = new PartitionGraph();
            partitionGraph.resetNodes();

            List<Integer> cellDigits = Arrays.asList(
                    0b001100100,
                    0b001100100,
                    0b110001010,
                    0b010001010,
                    0b001100010,
                    0b001100010,
                    0b110000000
            );
            partitionGraph.addCellDigits(cellDigits);
            System.out.println(partitionGraph.printDigitPairings());

            System.out.println();
            System.out.println("Reconstructed cell digit combinations as follows:");
            System.out.println();

            List<Integer> reconstructedCellDigits = partitionGraph.reconstructCellDigits();

            for (Integer digits : reconstructedCellDigits) {
                System.out.println(String.format("0b%9s", Integer.toString(digits, 2)).
                        replace(' ', '0'));
            }
        }

        {
            PartitionGraph partitionGraph = new PartitionGraph();
            partitionGraph.resetNodes();

            List<Integer> cellDigits = Arrays.asList(
                    0b001001100,
                    0b101000100,
                    0b101001000,
                    0b101100000,
                    0b001100000
            );
            partitionGraph.addCellDigits(cellDigits);
            System.out.println();
            System.out.println(partitionGraph.printDigitPairings());

            System.out.println();
            System.out.println("Reconstructed cell digit combinations as follows:");
            System.out.println();

            List<Integer> reconstructedCellDigits = partitionGraph.reconstructCellDigits();

            for (Integer digits : reconstructedCellDigits) {
                System.out.println(String.format("0b%9s", Integer.toString(digits, 2)).
                        replace(' ', '0'));
            }

            cellDigits = reconstructedCellDigits;
            partitionGraph.resetNodes();
            partitionGraph.addCellDigits(cellDigits);
            System.out.println();
            System.out.println(partitionGraph.printDigitPairings());

            System.out.println();
            System.out.println("Reconstructed cell digit combinations as follows:");
            System.out.println();

            reconstructedCellDigits = partitionGraph.reconstructCellDigits();

            for (Integer digits : reconstructedCellDigits) {
                System.out.println(String.format("0b%9s", Integer.toString(digits, 2)).
                        replace(' ', '0'));
            }
        }
    }

    /*

0 0 0 0 0 0 0 0 0
  4 0 2 0 2 2 2 1
    2 0 0 2 2 0 0
      2 0 0 0 2 1
        0 0 0 0 0
          4 4 0 0
            4 0 0
              3 2
                2


Reconstructed cell digit combinations as follows:

0b110001010
0b010001010
0b001100010
0b001100010
0b001100100
0b001100100
0b110000000

0 0 0 0 0 0 0 0 0
  0 0 0 0 0 0 0 0
    2 1 0 0 2 0 1
      2 0 0 2 0 1
        0 0 0 0 0
          2 2 0 1
            5 0 3
              0 0
                3


Reconstructed cell digit combinations as follows:

0b101001100
0b001000100
0b001001000
0b101100000
0b001100000
0b101000000

0 0 0 0 0 0 0 0 0
  0 0 0 0 0 0 0 0
    2 1 0 0 2 0 1
      2 0 0 2 0 1
        0 0 0 0 0
          2 2 0 1
            6 0 3
              0 0
                3


Reconstructed cell digit combinations as follows:

0b101001100
0b001000100
0b001001000
0b101100000
0b001100000
0b101000000
     */

    @Test
    public void testPartitionGraphReconstruct2() {
        PartitionGraph partitionGraph = new PartitionGraph();
        List<Integer> cellDigits = Arrays.asList(
                0b001001100,
                0b101000100,
                0b101001000,
                0b101100000,
                0b001100000
        );
        partitionGraph.addCellDigits(cellDigits);
        List<Integer> reconstruct = partitionGraph.reconstructCellDigits2();
    }

    @Test
    public void testPartitionGraphReconstruct3() {
        PartitionGraph partitionGraph = new PartitionGraph();
        List<Integer> cellDigits = Arrays.asList(
                /*
                0b001001100,
                0b101000100,
                0b101001000,
                0b101100000,
                0b001100000
                */
                0b001110001,
                0b001011001,
                0b010110000,
                0b110011000,
                0b100001001,
                0b101001001,
                0b001011001
        );

        System.out.println("Original digit groupings");
        System.out.println();

        PartitionGraph.DigitGroupingComparator digitComparator = new PartitionGraph.DigitGroupingComparator();
        Collections.sort(cellDigits, digitComparator);
        for (Integer digits :
                cellDigits) {
            System.out.println(String.format("0b%9s", Integer.toString(digits, 2)).
                    replace(' ', '0'));
        }

        partitionGraph.addCellDigits(cellDigits);

        System.out.println();
        System.out.println(partitionGraph.printDigitPairings());

        System.out.println();
        System.out.println("Reconstructed cell digit combinations as follows:");
        System.out.println();

        List<Integer> reconstructedCellDigits = partitionGraph.reconstructCellDigits3();
        Collections.sort(reconstructedCellDigits, digitComparator);

        for (Integer digits : reconstructedCellDigits) {
            System.out.println(String.format("0b%9s", Integer.toString(digits, 2)).
                    replace(' ', '0'));
        }

        System.out.println();
        System.out.println("Partially consumed digit pairing matrix:\n");
        System.out.println(partitionGraph.printPostDigitPairings());
    }

    /*
            0 0 0 0 0 0 0 0 0
              0 0 0 0 0 0 0 0
                2 1 0 0 2 0 1
                  2 0 0 2 0 1
                    0 0 0 0 0
                      2 2 0 1
                        5 0 3
                          0 0
                            3


            Reconstructed cell digit combinations as follows:

            0b001000100
            0b001000100
            0b001001000
            0b001100000
            0b001100000

            Partially consumed digit pairing matrix:

            0 0 0 0 0 0 0 0 0
              0 0 0 0 0 0 0 0
                0 1 0 0 0 0 1
                  1 0 0 1 0 1
                    0 0 0 0 0
                      0 0 0 1
                        0 0 3
                          0 0
                            3
    */

    /*

    Original digit groupings

    0b010110000
    0b110011000
    0b001110001
    0b100001001
    0b101001001
    0b001011001
    0b001011001

    5 0 0 4 3 1 4 0 2
      0 0 0 0 0 0 0 0
        0 0 0 0 0 0 0
          5 3 0 3 1 3
            5 2 3 2 1
              2 1 1 0
                4 0 1
                  2 1
                    3


    Reconstructed cell digit combinations as follows:

    0b000011000
    0b000001001
    0b000001001
    0b000001001
    0b000001001

    Partially consumed digit pairing matrix:

    1 0 0 0 3 1 4 0 2
      0 0 0 0 0 0 0 0
        0 0 0 0 0 0 0
          0 2 0 3 1 3
            4 2 3 2 1
              2 1 1 0
                4 0 1
                  2 1
                    3
     */
}