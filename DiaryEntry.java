package application;

public class DiaryEntry {
	private String date;
	private String title;
	private String content;
	private String imagePath;
	
	public DiaryEntry(String date, String title, String content, String imagePath) {
		this.date = date;
		this.title = title;
		this.content = content;
		this.imagePath = imagePath;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	public String toCSV() {
		return String.join(",", escapeCSV(date), escapeCSV(title), escapeCSV(content), escapeCSV(imagePath));
	}
	
	public static DiaryEntry fromCSV(String csvLine) throws IllegalArgumentException {
	    String[] fields = csvLine.split(",");
	    if (fields.length < 4) {
	        throw new IllegalArgumentException("CSV format is invalid, expected at least 4 fields. Found: " + fields.length);
	    }

	    String title = fields[0];
	    String date = fields[1];
	    String content = fields[2];
	    String imagePath = fields[3];

	    return new DiaryEntry(title, date, content, imagePath);
	}

	
	private static String escapeCSV(String field) {
		return field.replace(",", "\\,").replace("\n", "\\n");
	}
	
	private static String unescapeCSV(String field) {
		return field.replace(",", "\\,").replace("\n", "\\n");
	}
	

}