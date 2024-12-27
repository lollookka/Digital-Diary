package application;

import java.util.ArrayList;
import java.util.List;

public class RecycleBin {
	
	private static List<DiaryEntry> trashBin = new ArrayList<>();

   
    public static void addEntry(DiaryEntry entry) {
        trashBin.add(entry);
    }

   
    public static void removeEntry(DiaryEntry entry) {
        trashBin.remove(entry);
    }

    
    public static List<DiaryEntry> getTrashBin() {
        return trashBin;
    }

}
