package org.example.utils;

import org.example.strategy.StrategyMinimaxLaunchers;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvWriterUtil {

    public static void writeIntArrayToCSV(int[][] data) {

        String fileName = StrategyMinimaxLaunchers.PATH_PREFIX + data.length + "x" + data[0].length + ".csv";

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

                int[] row = data[i];

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
    }
}
