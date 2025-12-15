package org.example.utils;

import org.example.strategy.StrategyLaunchers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriterUtil {

    public static void writeIntArrayToCSV(int[][] data) {
        try (FileWriter writer = new FileWriter(StrategyLaunchers.PATH)) {

            for (int i = 0; i < data.length; i++) {
                StringBuilder line = new StringBuilder();

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

            System.out.println("CSV file created: " + StrategyLaunchers.PATH);

        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }
}
