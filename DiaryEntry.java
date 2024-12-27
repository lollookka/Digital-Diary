package application;

import java.io.Serializable;


public class DiaryEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	private String date;
	private String title;
	private String content;
	private String imagePath;
	
	public DiaryEntry(String date, String title, String content, String imagePath) {
		if (date == null || date.isEmpty()) {
		    throw new IllegalArgumentException("Date cannot be null or empty");
		}
		if (title == null || title.isEmpty()) {
			throw new IllegalArgumentException("Title cannot be null or empty");
		}
	    if (content == null) {
		    throw new IllegalArgumentException("Content cannot be null or empty");
		}
		this.date = date;
		this.title = title;
		this.content = content;
		this.imagePath = imagePath;
	}
	
	public DiaryEntry(String date, String title, String imagePath) {
		this(date, title, "No content provided", imagePath);
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
	
	public void setDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        this.date = date;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public void setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath; 
    }
	
    public String toCSV() {
        return String.format("%s,%s,%s,%s", date, title, content.replace(",", " "), imagePath != null ? imagePath : "");
    }
    
    public static DiaryEntry fromCSV(String csvLine) {
        System.out.println("Parsing CSV: " + csvLine);
       
        String[] values = csvLine.split(",", -1); 

        if (values.length < 3) {
            throw new IllegalArgumentException("Invalid CSV format: " + csvLine);
        }

        String date = values[0].trim();          
        String title = values[1].trim();        
        String content = values[2].trim();      
        String imagePath = values.length > 3 ? values[3].trim() : null; 

        System.out.println("Date: " + date);
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        System.out.println("ImagePath: " + (imagePath != null ? imagePath : "null"));

        return new DiaryEntry(date, title, content, imagePath);
    }

    @Override
    public String toString() {
        return String.format("Date: %s%nTitle: %s%nContent: %s%nImage: %s", date, title, content, imagePath);
    }
}
