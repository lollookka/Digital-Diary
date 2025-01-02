package application;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String email;
    private String password;
    private List<DiaryEntry> entries;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.entries = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<DiaryEntry> getEntries() {
        return entries;
    }

    public void addEntry(DiaryEntry entry) {
        entries.add(entry);
    }

    public void removeEntry(DiaryEntry entry) {
        entries.remove(entry);
    }
}
