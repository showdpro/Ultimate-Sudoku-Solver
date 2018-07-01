/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.john.sudokusolver.SudokuPuzzleSolver.VALUE;
import static com.example.john.sudokusolver.SudokuPuzzleSolver.MAX_LENGTH;

public class GraphTest {
    static class PartitionGraph {
        private int[][] mDigitPairings = new int[MAX_LENGTH][MAX_LENGTH];

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

        private int[][] copyDigitPairings() {
            int[][] cpy = new int[MAX_LENGTH][];

            for (int i = 0; i < MAX_LENGTH; i++) {
                cpy[i] = Arrays.copyOf(mDigitPairings[i], MAX_LENGTH);
            }
            return cpy;
        }

        public String printDigitPairings() {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < MAX_LENGTH; i++) {
                for (int j = 0; j < MAX_LENGTH; j++) {
                    if (i > j) {
                        builder.append("  ");
                    } else {
                        builder.append(mDigitPairings[i][j]);
                        builder.append(' ');
                    }
                }
                builder.append('\n');
            }
            return builder.toString();
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


        /*
        Output:

        0 0 0 0 0 0 0 0 0
          1 0 2 0 2 2 2 1
            1 0 0 2 2 0 0
              1 0 0 0 2 1
                0 0 0 0 0
                  1 4 0 0
                    1 0 0
                      1 2
                        1


        Reconstructed cell digit combinations as follows:

        0b110001010
        0b010001010
        0b001100010
        0b001100010
        0b001100100
        0b001100100
        0b110000000

         */

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

        /*
        Output:

        0 0 0 0 0 0 0 0 0
          0 0 0 0 0 0 0 0
            1 1 0 0 2 0 1
              1 0 0 2 0 1
                0 0 0 0 0
                  1 2 0 1
                    1 0 3
                      0 0
                        1


        Reconstructed cell digit combinations as follows:

        0b101001100
        0b001000100
        0b001001000
        0b101100000
        0b001100000
        0b101000000

         */
    }
}