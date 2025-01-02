package application;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DiaryDataStore {

    private static final String BASE_PATH = "data";  // Directory to store user-specific data
    private static final String RECYCLE_BIN_PATH = "recycle_bin.csv";
    private static Map<String, List<DiaryEntry>> userEntries = new HashMap<>(); // Map of user -> diary entries
    private static List<DeletedDiaryEntry> recycleBin = new ArrayList<>();

    static {
        loadAllEntries();  // Load all entries at startup
        loadRecycleBin();
    }

    // Load all diary entries for all users from CSV files
    private static void loadAllEntries() {
        File userDirectory = new File(BASE_PATH);
        if (!userDirectory.exists()) {
            userDirectory.mkdir();  // Create the directory if it doesn't exist
        }
        File[] userFiles = userDirectory.listFiles((dir, name) -> name.endsWith("_entries.csv"));
        if (userFiles != null) {
            for (File userFile : userFiles) {
                String username = userFile.getName().replace("_entries.csv", "");
                List<DiaryEntry> entries = loadEntriesForUser(username);
                userEntries.put(username, entries);
            }
        }
    }

    // Load diary entries for a specific user from their CSV file
    private static List<DiaryEntry> loadEntriesForUser(String username) {
        List<DiaryEntry> loadedEntries = new ArrayList<>();
        File file = new File(BASE_PATH + "/" + username + "_entries.csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    DiaryEntry entry = DiaryEntry.fromCSV(line);  // Assuming DiaryEntry.fromCSV converts CSV to DiaryEntry
                    loadedEntries.add(entry);
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid line: " + line); // Log invalid entries
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedEntries;
    }

    // Add a new diary entry for a specific user and save it to the CSV
    public static void addEntry(String username, DiaryEntry entry) {
        userEntries.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
        saveEntriesForUser(username);  // Save the updated list of entries for the user
    }

    // Update an existing diary entry for a specific user
    public static void updateEntry(String username, DiaryEntry oldEntry, DiaryEntry newEntry) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries != null) {
            int index = entries.indexOf(oldEntry);
            if (index != -1) {
                entries.set(index, newEntry);  // Replace the old entry with the new one
                saveEntriesForUser(username);  // Save the updated list of entries
            }
        }
    }

    // Delete a diary entry for a specific user
    public static void deleteEntry(String username, DiaryEntry entry) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries != null) {
            // Add the entry to the recycle bin and remove from entries list
            recycleBin.add(new DeletedDiaryEntry(entry, System.currentTimeMillis()));
            entries.remove(entry);
            saveEntriesForUser(username);
            saveRecycleBin();
        }
    }

    // Save the list of entries for a specific user to their CSV file
    private static void saveEntriesForUser(String username) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_PATH + "/" + username + "_entries.csv"))) {
                for (DiaryEntry entry : entries) {
                    writer.write(entry.toCSV());  // Assuming DiaryEntry.toCSV converts DiaryEntry to CSV string
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Load the recycle bin from file
    private static void loadRecycleBin() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECYCLE_BIN_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                recycleBin.add(DeletedDiaryEntry.fromCSV(line));  // Assuming DeletedDiaryEntry.fromCSV converts CSV to DeletedDiaryEntry
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save the recycle bin to file
    private static void saveRecycleBin() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECYCLE_BIN_PATH))) {
            for (DeletedDiaryEntry deletedEntry : recycleBin) {
                writer.write(deletedEntry.toCSV());  // Assuming DeletedDiaryEntry.toCSV converts DeletedDiaryEntry to CSV
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the diary entries for a specific user
    public static List<DiaryEntry> getEntries(String username) {
        return new ArrayList<>(userEntries.getOrDefault(username, new ArrayList<>())); 
    }

    // Get the entries from the recycle bin
    public static List<DiaryEntry> getRecycleBinEntries() {
        return recycleBin.stream()
                         .map(DeletedDiaryEntry::getEntry)
                         .collect(Collectors.toList());
    }
}
