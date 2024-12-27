package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadDiaryEntry {

	public static List<DiaryEntry> readDiaryEntries() {
        List<DiaryEntry> entries = new ArrayList<>();
        File file = new File("data/diary_entries.csv");

        
        if (!file.exists()) {
            try {
               
                file.createNewFile();
                System.out.println("CSV file created: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
                e.printStackTrace();
                return entries; 
            }
        }

        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidCSVLine(line)) { 
                    try {
                        DiaryEntry entry = DiaryEntry.fromCSV(line);
                        entries.add(entry);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Skipping invalid entry: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid CSV format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private static boolean isValidCSVLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        return values.length >= 3; 
    }
}
