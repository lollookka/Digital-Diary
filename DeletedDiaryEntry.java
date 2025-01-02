package application;

public class DeletedDiaryEntry {
    private DiaryEntry entry;
    private long deletionTime;

    public DeletedDiaryEntry(DiaryEntry entry, long deletionTime) {
        this.entry = entry;
        this.deletionTime = deletionTime;
    }

    public DiaryEntry getEntry() {
        return entry;
    }

    public long getDeletionTime() {
        return deletionTime;
    }

    public String toCSV() {
        return entry.toCSV() + "," + deletionTime;
    }

    public static DeletedDiaryEntry fromCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        
        // Extract the DiaryEntry data (everything except the last field, which is the deletion time)
        String diaryEntryCSV = String.join(",", java.util.Arrays.copyOfRange(fields, 0, fields.length - 1));
        
        // Parse the DiaryEntry from the CSV string
        DiaryEntry entry = DiaryEntry.fromCSV(diaryEntryCSV);
        
        // Parse the deletion time (last field)
        long deletionTime = Long.parseLong(fields[fields.length - 1]);
        
        return new DeletedDiaryEntry(entry, deletionTime);
    }
}
