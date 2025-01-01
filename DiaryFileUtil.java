package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiaryFileUtil {
	private static final String FILE_PATH = "diary_entries.csv";
	
	public static void saveEntries(List<DiaryEntry> entries) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))){
			for(DiaryEntry entry : entries) {
				writer.write(entry.toCSV());
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<DiaryEntry> loadEntries(){
		List<DiaryEntry> entries = new ArrayList<>();
		try(BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))){
			String line;
			while((line = reader.readLine()) != null) {
				entries.add(DiaryEntry.fromCSV(line));
			}
		}catch (FileNotFoundException e) {
			System.out.println("No saved entries found.");
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

}
