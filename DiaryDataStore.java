package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiaryDataStore {
	private static final String FILE_PATH = "diary_entries.csv";
	private static List<DiaryEntry> entries;
	
	static {
		entries = loadEntries();
	}
	
	public static void addEntry(DiaryEntry entry) {
		entries.add(entry);
		saveEntries();
	}
	
	public static void updateEntry(DiaryEntry oldEntry, DiaryEntry newEntry) {
		int index = entries.indexOf(oldEntry);
		if (index != -1) {
			entries.set(index, newEntry);
			saveEntries();
		}
	}
	
	public static void deleteEntry(DiaryEntry entry) {
		entries.remove(entry);
		saveEntries();
	}
	
	public static void removeEntry(DiaryEntry entry) {
		entries.remove(entry);
		saveEntries();
	}
	
	public static List<DiaryEntry> getEntries(){
		return new ArrayList<>(entries);
	}
	
	public static void saveEntries() {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))){
			for(DiaryEntry entry : entries) {
				writer.write(entry.toCSV());
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<DiaryEntry> loadEntries(){
		List<DiaryEntry> loadedEntries = new ArrayList<>();
		try(BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))){
			String line;
			while((line = reader.readLine()) != null) {
				loadedEntries.add(DiaryEntry.fromCSV(line));
			}
		}catch (FileNotFoundException e) {
			System.out.println("No saved entries found.");
		}catch (IOException e) {
			e.printStackTrace();
		}
		return loadedEntries;
	}
}
