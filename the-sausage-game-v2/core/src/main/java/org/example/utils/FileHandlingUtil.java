package org.example.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileHandlingUtil {

    public static final String PATH_PREFIX = "minimax_";
    public static final String MINIMAX_RESULTS_FOLDER = "minimax_results_final";
    public static final String GROUND_TRUTH = "minimax_results/truth/truth.csv";
    public static final String STRATEGY_PATH = "minimax_strategies";

    public static String writeArrayToCSV(long[][] data, String fileName) {

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE); // e.g. 2026-01-12
        Path dailyDir = Paths.get("minimax_results", today);

        // Create folder if it doesn't exist
        if (!Files.exists(dailyDir)) {
            try {
                Files.createDirectories(dailyDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Path filePath = dailyDir.resolve(fileName);
        String path = filePath.toString();

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(","); // Header row initial empty cell

            for (int i = 0; i < data[0].length; i++) {
                writer.write(String.valueOf(i + 1));
                if (i < data[0].length - 1) {
                    writer.write(","); // Add comma between header values
                }
            }
            writer.write("\n"); // New line after header row

            for (int i = 0; i < data.length; i++) {
                StringBuilder line = new StringBuilder();
                line.append(i + 1).append(","); // Row header

                long[] row = data[i];

                for (int j = 0; j < row.length; j++) {
//                    if (row[j] == 1) {
//                        line.append(row[j]);
//                    } else {
//                        line.append(" "); // namiesto nul, resp. -1, nech to vidno
//                    }
                    line.append(row[j]);

                    if (j < row.length - 1) {
                        line.append(","); // Add comma between values
                    }
                }

                writer.write(line.toString());
                writer.write("\n"); // New line after each row
            }

            System.out.println("CSV file created: " + path);

        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
        return path;
    }

    public static boolean CompareCSVsOnesMinusOnes(String file1, String file2) throws IOException {

        String line1, line2;
        int row = 0;
        boolean identical = true;

        try (BufferedReader br1 = new BufferedReader(new FileReader(file1));
             BufferedReader br2 = new BufferedReader(new FileReader(file2))) {

            while ((line1 = br1.readLine()) != null | (line2 = br2.readLine()) != null) {
                String[] cells1 = line1 != null ? line1.split(",") : new String[0];
                String[] cells2 = line2 != null ? line2.split(",") : new String[0];

                int maxCols = Math.max(cells1.length, cells2.length);
                for (int col = 0; col < maxCols; col++) {
                    String cell1 = col < cells1.length ? cells1[col] : "";
                    String cell2 = col < cells2.length ? cells2[col] : "";

                    if (!cell1.equals(cell2)) {
                        // iba ak su vypocitane nas zaujima rozdiel
                        if ((cell1.equals("-1") || cell1.equals("1")) &&
                            (cell2.equals("-1") || cell2.equals("1"))) {

                        identical = false;
                        System.out.println("Difference from groud truth at row " + row + ", column " + col +
                            ": '" + cell1 + "' != '" + cell2 + "'");
                        }
                    }
                }
                row++;
            }

            if (identical) {
                System.out.println("Files are identical in 1s and -1s (to the extent of the known values).");
            }
        }
        return identical;
    }

    public static boolean isSymmetricCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the entire CSV into a 2D array
            String line;
            int rowCount = 0;
            int colCount = -1;

            // First, count rows and columns
            while ((line = br.readLine()) != null) {
                String[] cells = line.split(",");
                if (colCount == -1) colCount = cells.length;
                else if (colCount != cells.length) {
                    System.out.println("CSV is not rectangular.");
                    return false; // uneven rows
                }
                rowCount++;
            }

            // Reset reader to read values
            br.close();
            BufferedReader br2 = new BufferedReader(new FileReader(filePath));

            int[][] matrix = new int[rowCount][colCount];
            int r = 0;
            while ((line = br2.readLine()) != null) {
                String[] cells = line.split(",");
                for (int c = 0; c < colCount; c++) {
                    try {
                        matrix[r][c] = Integer.parseInt(cells[c].trim());
                    } catch (NumberFormatException e) {
                        matrix[r][c] = 0; // ignore non-numeric cells
                    }
                }
                r++;
            }
            br2.close();

            boolean result = true;

            // Check symmetry
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    if (matrix[i][j] != matrix[j][i]) {
                        int val1 = matrix[i][j];
                        int val2 = matrix[j][i];
                        if ((val1 == 1 || val1 == -1) && (val2 == 1 || val2 == -1)) {
                            System.out.println("CSV is not symmetric at (" + i + ", " + j + "): " +
                                matrix[i][j] + " != " + matrix[j][i]);
                            result = false;
                        }
                    }
                }
            }

            if (result)
                System.out.println("CSV is symmetric.");
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveStrategyBinary(Map<Long,Long> strategy, String filePath) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))) {

            // na zaciatku zapiseme pocet poloziek na citanie
            dos.writeInt(strategy.size());

            for (Map.Entry<Long, Long> entry : strategy.entrySet()) {
                dos.writeLong(entry.getKey());
                dos.writeLong(entry.getValue());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Long, Long> loadStrategyBinary(String filePath) throws IOException {
        HashMap<Long, Long> strategy = new HashMap<>();

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {

            // prvy int je pocet prvkov
            int size = dis.readInt();

            for (int i = 0; i < size; i++) {
                long key = dis.readLong();
                long val = dis.readLong();
                strategy.put(key, val);
            }
        }

        return strategy;
    }

    public static void saveStrategyCSV(Map<Long, Long> strategy, String filepath) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filepath)))) {
            for (Map.Entry<Long, Long> entry : strategy.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * pred tym som pouzival toto, ale cez teavm to nefungovalo
     */
    public static HashMap<Long, Long> loadStrategyCSV(String filepath) throws IOException {

        HashMap<Long, Long> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    map.put(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }
            }
        }
        return map;
    }

    /**
     * od chatu - funguje aj cez teavm
     */
    public static Map<Long, Long> loadStrategyCSV(FileHandle file) throws IOException {
        Map<Long, Long> moves = new HashMap<>();

        // LibGDX FileHandle poskytuje priamo BufferedReader
        try (BufferedReader reader = file.reader(256)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Preskoč prázdne riadky
                if (line.trim().isEmpty()) continue;

                // Tu je tvoja logika parsovania CSV...
                // Príklad (predpokladá formát: kľúč,hodnota):
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    long key = Long.parseLong(parts[0].trim());
                    long value = Long.parseLong(parts[1].trim());
                    moves.put(key, value);
                }
            }
        }

        return moves;
    }


//    public static HashMap<Long, Long> loadStrategyCSV(File file) throws IOException {
//        return loadStrategyCSV(file.getPath());
//    }
}
