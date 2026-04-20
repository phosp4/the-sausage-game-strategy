package org.example.strategy_minimax.archive;

public class GridBitMask {

    /**
     * Converts a 2D array (max 8x8) to a single long bitmask.
     * 1 represents an occupied field (non-null), 0 represents null.
     *
     * @param grid The 2D array of objects.
     * @return A long representing the occupancy state.
     */
    public static <T> long encode(T[][] grid) {
        if (grid == null) return 0L;

        long bitmask = 0L;

        // Iterate rows (max 8)
        for (int row = 0; row < grid.length && row < 8; row++) {
            if (grid[row] == null) continue;

            // Iterate columns (max 8)
            for (int col = 0; col < grid[row].length && col < 8; col++) {
                // If the object exists (is not null), set the bit
                if (grid[row][col] != null) {
                    // Calculate bit position: 0 to 63
                    int position = (row * 8) + col;

                    // Use bitwise OR to set the bit at 'position'
                    // We must use 1L (long) to prevent integer overflow at bit 31
                    bitmask |= (1L << position);
                }
            }
        }
        return bitmask;
    }

    /**
     * Converts a long bitmask back into a 2D boolean array representing occupancy.
     * True means occupied, False means empty.
     *
     * @param bitmask The long containing the grid state.
     * @return An 8x8 boolean array.
     */
    public static boolean[][] decode(long bitmask) {
        boolean[][] grid = new boolean[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int position = (row * 8) + col;

                // Check if the bit at 'position' is set to 1
                // We use bitwise AND. If result is non-zero, the bit was 1.
                if ((bitmask & (1L << position)) != 0) {
                    grid[row][col] = true;
                } else {
                    grid[row][col] = false;
                }
            }
        }
        return grid;
    }

    // Optional: A helper to visualize the binary output for debugging
    public static void printBinaryGrid(long bitmask) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int pos = (i * 8) + j;
                System.out.print(((bitmask & (1L << pos)) != 0 ? "1 " : "0 "));
            }
            System.out.println();
        }
    }
}
