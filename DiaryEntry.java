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
	
	public static DiaryEntry fromCSV(String csvLine) {
		String[] fields = csvLine.split(",", -1);
		return new DiaryEntry(unescapeCSV(fields[0]), unescapeCSV(fields[1]), unescapeCSV(fields[2]), unescapeCSV(fields[3]));
	}
	
	private static String escapeCSV(String field) {
		return field.replace(",", "\\,").replace("\n", "\\n");
	}
	
	private static String unescapeCSV(String field) {
		return field.replace(",", "\\,").replace("\n", "\\n");
	}
	

}
