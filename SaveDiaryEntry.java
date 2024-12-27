package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveDiaryEntry {
	
	private static final String CSV_FILE_PATH = "data/diary_entries.csv";

    
    public static void saveEntry(DiaryEntry entry) {
        List<DiaryEntry> entries = ReadDiaryEntry.readDiaryEntries(); 
        System.out.println("Before adding new entry: " + entries.size());
        entries.add(entry); 
        System.out.println("After adding new entry: " + entries.size());
        saveAllEntries(entries);
    }

    
    public static void saveAllEntries(List<DiaryEntry> entries) {
        ensureDirectoryExists(); 
        boolean isNewFile = !new File(CSV_FILE_PATH).exists();  

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            if (isNewFile) {
                
                writer.write("Title,Content,Date");
                writer.newLine();
            }

            for (DiaryEntry entry : entries) {
                writer.write(entry.getTitle() + "," + entry.getContent() + "," + entry.getDate());
                writer.newLine();
            }

            System.out.println("Entries saved to CSV file.");
        } catch (IOException e) {
            System.err.println("Error saving entries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureDirectoryExists() {
        File directory = new File("data");
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directory created: data");
            } else {
                System.err.println("Failed to create directory: data");
            }
        }
    }
}
