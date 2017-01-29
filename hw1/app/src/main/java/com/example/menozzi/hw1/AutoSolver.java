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

    @Nullable
    public static String solve(@NonNull Grid grid, MainActivity.CellColor targetState) {
        for (String sequence : ALL_COMBINATIONS) {
            if (trySequence(sequence, grid, targetState)) {
                return sequence;
            }
        }
        return null;
    }

    private static boolean trySequence(@NonNull String sequence, @NonNull Grid grid, MainActivity.CellColor targetState) {
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

    @NonNull
    private static String toPaddedBinaryString(int n) {
        return String.format(Locale.US, "%010d", Integer.parseInt(Integer.toBinaryString(n)));
    }

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
