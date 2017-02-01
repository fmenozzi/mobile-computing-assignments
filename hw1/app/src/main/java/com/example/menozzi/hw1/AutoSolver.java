package com.example.menozzi.hw1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class AutoSolver {
    private static final int PADDED_BINARY_STRING_LEN = 10;

    private static final String ALL_SWITCHES = "ABCDEFGHIJ";

    private static final String[] ALL_COMBINATIONS = generateCombinations();

    /**
     * Try all possible combinations of switch sequences on given grid state
     * to see whether any of them take the board to the given final target
     * state
     *
     * @param grid
     *          The grid to be solved
     * @param targetState
     *          CellColor of the target state
     *
     * @return String representing set of switches corresponding to minimum
     *         solution, or null if unsolvable
     */
    @Nullable
    public static String solve(@NonNull Grid grid, @NonNull CellColor targetState) {
        for (String sequence : ALL_COMBINATIONS) {
            if (trySequence(sequence, grid, targetState)) {
                return sequence;
            }
        }
        return null;
    }

    /**
     * Try the given sequence on the given grid to see whether it takes
     * the grid to the given target state
     *
     * @param sequence
     *          String representing sequence of switches to try
     * @param grid
     *          Grid to be solved
     * @param targetState
     *          CellColor of the target state
     *
     * @return Whether the sequence succeeded in taking the board to the given
     *         target state
     */
    private static boolean trySequence(@NonNull String sequence,
                                       @NonNull Grid grid,
                                       @NonNull CellColor targetState) {
        Grid temp = grid.copy();

        for (int i = 0; i < sequence.length(); i++) {
            String switchStr = Character.toString(sequence.charAt(i));
            for (Integer cellIdx : MainActivity.SWITCH_MAP.get(switchStr)) {
                int r = cellIdx / Grid.GRID_SIZE;
                int c = cellIdx % Grid.GRID_SIZE;

                temp.toggleCellColor(r, c);
            }
        }

        return temp.allCellColorsAre(targetState);
    }

    /**
     * Convert integer to its binary representation, padding the resulting
     * String to a fixed width of 10 (since there are only 2^10 = 1024
     * possible switch combinations to check).
     *
     * @param n
     *          Integer to be converted
     *
     * @return Resulting String of padded binary number
     */
    @NonNull
    private static String toPaddedBinaryString(int n) {
        return String.format(Locale.US, "%010d", Integer.parseInt(Integer.toBinaryString(n)));
    }

    /**
     * Convert padded binary String to the corresponding String representing
     * sequence of switches. Input essentially acts as a mask on the total
     * switch sequence "ABCDEFGHIJ".
     *
     * E.g. toSwitchString("1000101010") -> "AEGI"
     *
     * @param paddedBinaryString
     *          Padded binary String as returned by {@link #toPaddedBinaryString(int)}
     *
     * @return String representing resulting switch sequence
     */
    @NonNull
    private static String toSwitchString(@NonNull String paddedBinaryString) {
        StringBuilder sb = new StringBuilder(PADDED_BINARY_STRING_LEN);
        for (int i = 0; i < PADDED_BINARY_STRING_LEN; i++) {
            if (paddedBinaryString.charAt(i) == '1') {
                sb.append(ALL_SWITCHES.charAt(i));
            }
        }

        return sb.toString();
    }

    /**
     * Enumerate all possible switch sequences as follows:
     *
     *      1) Count from 0 to 1023
     *      2) Convert each integer into its corresponding
     *         binary number, padded to 10 bits
     *      3) Convert padded binary number and use it to
     *         mask the fixed String "ABCDEFGHIJ"
     *
     * Additionally, the sequences are sorted by length
     * so that the first matching sequence is necessarily
     * a minimal solution.
     *
     * Because this set of sequences is fixed, it can be
     * computed once and referenced multiple times.
     *
     * @return List of Strings representing all possible switch
     *         sequences
     */
    @NonNull
    private static String[] generateCombinations() {
        String[] combinations = new String[1024];
        for (int i = 0; i < combinations.length; i++) {
            combinations[i] = toSwitchString(toPaddedBinaryString(i));
        }

        Arrays.sort(combinations, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        });

        return combinations;
    }
}
