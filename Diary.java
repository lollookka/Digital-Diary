package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Diary extends Application {

    private static final String USER_DATA_FILE = "users.csv";
    private static final String ENTRIES_FILE = "entries.csv";

    private Stage primaryStage;
    private String currentUser;
    private VBox mainLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	cleanUpRecycleBin();
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    private void showLoginScreen() {
        GridPane grid = new GridPane();

        Label usernameLabel = new Label("Username/Email:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> handleRegister(usernameField.getText(), passwordField.getText()));

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 0, 2);
        grid.add(registerButton, 1, 2);

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setTitle("Digital Diary - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if ((userDetails[0].equals(username) || userDetails[1].equals(username)) && userDetails[2].equals(password)) {
                    currentUser = username;
                    showDiaryScreen();  // Show diary screen on successful login
                    return;
                }
            }
            showAlert("Login Failed", "Invalid username or password.");
        } catch (IOException e) {
            showAlert("Error", "Unable to read user data.");
        }
    }


    private void handleRegister(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.write(username + "," + username + "@example.com," + password + "\n");
            showAlert("Registration Successful", "User registered successfully.");
        } catch (IOException e) {
            showAlert("Error", "Unable to save user data.");
        }
    }

    private void showDiaryScreen() {
        BorderPane layout = new BorderPane();

        VBox entryList = new VBox(10);
        Button newEntryButton = new Button("New Entry");
        Button editEntryButton = new Button("Edit Entry");
        Button searchButton = new Button("Search Entries");
        Button motivationalButton = new Button("Get Motivational Quote");
        Button logOutButton = new Button("Log Out");
        Button allEntriesButton = new Button("View All Entries");
        Button recycleBinButton = new Button("Recycle Bin");

        newEntryButton.setOnAction(e -> showEntryCreationScreen());
        editEntryButton.setOnAction(e -> showEditEntryScreen());
        searchButton.setOnAction(e -> showSearchScreen());
        motivationalButton.setOnAction(e -> showMotivationalQuote());
        logOutButton.setOnAction(e -> showLoginScreen());  // Log out and show the login screen
        allEntriesButton.setOnAction(e -> viewAll());
        recycleBinButton.setOnAction(e -> showRecycleBinScreen());

        layout.setCenter(entryList);
        HBox actions = new HBox(10, newEntryButton, editEntryButton, searchButton, motivationalButton, logOutButton, allEntriesButton, recycleBinButton);
        actions.setAlignment(Pos.CENTER);
        layout.setBottom(actions);

        Scene diaryScene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Digital Diary - Welcome " + currentUser);
        primaryStage.setScene(diaryScene);
    }
    
      private void viewAll() {
    	    // Create a new layout to display all entries
    	    ScrollPane scrollPane = new ScrollPane();
    	    VBox entriesBox = new VBox();
    	    scrollPane.setContent(entriesBox);

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        List<String> entries = reader.lines()
    	            .filter(line -> line.contains(currentUser))  // Ensure entries are for the current user
    	            .collect(Collectors.toList());

    	        if (entries.isEmpty()) {
    	            Label noEntriesLabel = new Label("No entries found.");
    	            entriesBox.getChildren().add(noEntriesLabel);
    	        } else {
    	            for (String entry : entries) {
    	                String[] details = entry.split(",");

    	                // Extract details for each entry
    	                String date = details[1];
    	                String title = details[2];
    	                String content = details[3];
    	                String mood = details[4];
    	                String imagePath = details.length > 5 ? details[5] : "no-image";

    	                // Create TextArea for entry details
    	                TextArea entryTextArea = new TextArea();
    	                entryTextArea.setText("Date: " + date + "\n" +
    	                                      "Title: " + title + "\n" +
    	                                      "Mood: " + mood + "\n" +
    	                                      "Content: " + content + "\n" +
    	                                      "Image: " + (imagePath.equals("no-image") ? "No image" : imagePath));
    	                entryTextArea.setEditable(false);
    	                entryTextArea.setWrapText(true);
    	                entriesBox.getChildren().add(entryTextArea);

    	                // Add image if available
    	                if (!imagePath.equals("no-image")) {
    	                    String imagePathWithDir = "images/" + imagePath;
    	                    File imageFile = new File(imagePathWithDir);
    	                    if (imageFile.exists()) {
    	                        Image image = new Image(imageFile.toURI().toString());
    	                        ImageView imageView = new ImageView(image);
    	                        imageView.setFitHeight(100);
    	                        imageView.setFitWidth(100);
    	                        entriesBox.getChildren().add(imageView);
    	                    } else {
    	                        entriesBox.getChildren().add(new Label("Image file not found."));
    	                    }
    	                }
    	            }
    	        }

    	    } catch (IOException e) {
    	        entriesBox.getChildren().add(new Label("Error reading entries: " + e.getMessage()));
    	    }

    	    // Create a back button to return to the diary screen
    	    Button backButton = new Button("Back");
    	    Button ascend = new Button("Ascending Date Order");
    	    Button descend = new Button("Descending Date Order");
    	    backButton.setOnAction(e -> showDiaryScreen());  // Return to diary screen
    	    ascend.setOnAction(e -> ascendingDate());
    	    descend.setOnAction(e -> descendingDate());

    	    VBox container = new VBox(scrollPane, backButton,ascend,descend);  // Add scroll pane and back button in a VBox

    	    Scene scene = new Scene(container, 600, 400);
    	    primaryStage.setTitle("All Entries");
    	    primaryStage.setScene(scene);
    	} 
      
      private void ascendingDate() {
    	    // Create a new layout to display all entries
    	    ScrollPane scrollPane = new ScrollPane();
    	    VBox entriesBox = new VBox();
    	    scrollPane.setContent(entriesBox);

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        List<String> entries = reader.lines()
    	            .filter(line -> line.contains(currentUser))  // Ensure entries are for the current user
    	            .collect(Collectors.toList());

    	        if (entries.isEmpty()) {
    	            Label noEntriesLabel = new Label("No entries found.");
    	            entriesBox.getChildren().add(noEntriesLabel);
    	        } else {
    	            // Sort the entries by date in ascending order
    	            entries.sort((entry1, entry2) -> {
    	                String date1 = entry1.split(",")[1];
    	                String date2 = entry2.split(",")[1];
    	                return date1.compareTo(date2);
    	            });

    	            // Display sorted entries
    	            for (String entry : entries) {
    	                String[] details = entry.split(",");

    	                // Extract details for each entry
    	                String date = details[1];
    	                String title = details[2];
    	                String content = details[3];
    	                String mood = details[4];
    	                String imagePath = details.length > 5 ? details[5] : "no-image";

    	                // Create TextArea for entry details
    	                TextArea entryTextArea = new TextArea();
    	                entryTextArea.setText("Date: " + date + "\n" +
    	                                      "Title: " + title + "\n" +
    	                                      "Mood: " + mood + "\n" +
    	                                      "Content: " + content + "\n" +
    	                                      "Image: " + (imagePath.equals("no-image") ? "No image" : imagePath));
    	                entryTextArea.setEditable(false);
    	                entryTextArea.setWrapText(true);
    	                entriesBox.getChildren().add(entryTextArea);

    	                // Add image if available
    	                if (!imagePath.equals("no-image")) {
    	                    String imagePathWithDir = "images/" + imagePath;
    	                    File imageFile = new File(imagePathWithDir);
    	                    if (imageFile.exists()) {
    	                        Image image = new Image(imageFile.toURI().toString());
    	                        ImageView imageView = new ImageView(image);
    	                        imageView.setFitHeight(100);
    	                        imageView.setFitWidth(100);
    	                        entriesBox.getChildren().add(imageView);
    	                    } else {
    	                        entriesBox.getChildren().add(new Label("Image file not found."));
    	                    }
    	                }
    	            }
    	        }

    	    } catch (IOException e) {
    	        entriesBox.getChildren().add(new Label("Error reading entries: " + e.getMessage()));
    	    }

    	    // Create a back button to return to the diary screen
    	    Button backButton = new Button("Back");
    	    backButton.setOnAction(e -> showDiaryScreen());  // Return to diary screen

    	    VBox container = new VBox(scrollPane, backButton);  // Add scroll pane and back button in a VBox

    	    Scene scene = new Scene(container, 600, 400);
    	    primaryStage.setTitle("All Entries - Ascending Date Order");
    	    primaryStage.setScene(scene);
    	}

    	private void descendingDate() {
    	    // Create a new layout to display all entries
    	    ScrollPane scrollPane = new ScrollPane();
    	    VBox entriesBox = new VBox();
    	    scrollPane.setContent(entriesBox);

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        List<String> entries = reader.lines()
    	            .filter(line -> line.contains(currentUser))  // Ensure entries are for the current user
    	            .collect(Collectors.toList());

    	        if (entries.isEmpty()) {
    	            Label noEntriesLabel = new Label("No entries found.");
    	            entriesBox.getChildren().add(noEntriesLabel);
    	        } else {
    	            // Sort the entries by date in descending order
    	            entries.sort((entry1, entry2) -> {
    	                String date1 = entry1.split(",")[1];
    	                String date2 = entry2.split(",")[1];
    	                return date2.compareTo(date1);  // Reverse order for descending
    	            });

    	            // Display sorted entries
    	            for (String entry : entries) {
    	                String[] details = entry.split(",");

    	                // Extract details for each entry
    	                String date = details[1];
    	                String title = details[2];
    	                String content = details[3];
    	                String mood = details[4];
    	                String imagePath = details.length > 5 ? details[5] : "no-image";

    	                // Create TextArea for entry details
    	                TextArea entryTextArea = new TextArea();
    	                entryTextArea.setText("Date: " + date + "\n" +
    	                                      "Title: " + title + "\n" +
    	                                      "Mood: " + mood + "\n" +
    	                                      "Content: " + content + "\n" +
    	                                      "Image: " + (imagePath.equals("no-image") ? "No image" : imagePath));
    	                entryTextArea.setEditable(false);
    	                entryTextArea.setWrapText(true);
    	                entriesBox.getChildren().add(entryTextArea);

    	                // Add image if available
    	                if (!imagePath.equals("no-image")) {
    	                    String imagePathWithDir = "images/" + imagePath;
    	                    File imageFile = new File(imagePathWithDir);
    	                    if (imageFile.exists()) {
    	                        Image image = new Image(imageFile.toURI().toString());
    	                        ImageView imageView = new ImageView(image);
    	                        imageView.setFitHeight(100);
    	                        imageView.setFitWidth(100);
    	                        entriesBox.getChildren().add(imageView);
    	                    } else {
    	                        entriesBox.getChildren().add(new Label("Image file not found."));
    	                    }
    	                }
    	            }
    	        }

    	    } catch (IOException e) {
    	        entriesBox.getChildren().add(new Label("Error reading entries: " + e.getMessage()));
    	    }

    	    // Create a back button to return to the diary screen
    	    Button backButton = new Button("Back");
    	    backButton.setOnAction(e -> showDiaryScreen());  // Return to diary screen

    	    VBox container = new VBox(scrollPane, backButton);  // Add scroll pane and back button in a VBox

    	    Scene scene = new Scene(container, 600, 400);
    	    primaryStage.setTitle("All Entries - Descending Date Order");
    	    primaryStage.setScene(scene);
    	}

      
    	private void showEntryCreationScreen() {
    	    GridPane grid = new GridPane();
    	    grid.setVgap(10);
    	    grid.setHgap(10);

    	    Label dateLabel = new Label("Date:");
    	    DatePicker datePicker = new DatePicker(LocalDate.now());
    	    Label titleLabel = new Label("Title:");
    	    TextField titleField = new TextField();
    	    Label contentLabel = new Label("Content:");
    	    TextArea contentArea = new TextArea();
    	    Label moodLabel = new Label("Mood:");
    	    ComboBox<String> moodComboBox = new ComboBox<>();
    	    moodComboBox.getItems().addAll("Happy", "Neutral", "Sad");
    	    moodComboBox.getSelectionModel().selectFirst();

    	    Button uploadImageButton = new Button("Upload Image");
    	    Button saveButton = new Button("Save Entry");

    	    // Handle image upload
    	    FileChooser fileChooser = new FileChooser();
    	    Label imageLabel = new Label("No image selected");
    	    ImageView imageView = new ImageView();
    	    imageView.setFitHeight(100);
    	    imageView.setFitWidth(100);

    	    uploadImageButton.setOnAction(e -> {
    	        File selectedFile = fileChooser.showOpenDialog(primaryStage);
    	        if (selectedFile != null) {
    	            Image image = new Image(selectedFile.toURI().toString());
    	            imageView.setImage(image);
    	            imageLabel.setText(selectedFile.getName());
    	        }
    	    });

    	    // Save the entry
    	    saveButton.setOnAction(e -> {
    	        String title = titleField.getText().trim();
    	        String content = contentArea.getText().trim();
    	        
    	        if (title.isEmpty() || content.isEmpty()) {
    	            showAlert("Error", "Title and content cannot be empty.");
    	        } else {
    	            handleSaveEntry(datePicker.getValue(), title, content, moodComboBox.getValue(), imageLabel.getText(), imageView);
    	        }
    	    });

    	    grid.add(dateLabel, 0, 0);
    	    grid.add(datePicker, 1, 0);
    	    grid.add(titleLabel, 0, 1);
    	    grid.add(titleField, 1, 1);
    	    grid.add(contentLabel, 0, 2);
    	    grid.add(contentArea, 1, 2);
    	    grid.add(moodLabel, 0, 3);
    	    grid.add(moodComboBox, 1, 3);
    	    grid.add(uploadImageButton, 0, 4);
    	    grid.add(imageLabel, 1, 4);
    	    grid.add(imageView, 1, 5);
    	    grid.add(saveButton, 1, 6);

    	    Scene scene = new Scene(grid, 400, 400);
    	    primaryStage.setTitle("New Diary Entry");
    	    primaryStage.setScene(scene);
    	}

    	private void handleSaveEntry(LocalDate date, String title, String content, String mood, String imageName, ImageView imageView) {
    	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE, true))) {
    	        // Ensure imageName is not null or empty
    	        String imagePath = imageName.isEmpty() ? "no-image" : imageName; 

    	        // Save the entry with imagePath
    	        writer.write(currentUser + "," + date + "," + title + "," + content + "," + mood + "," + imagePath + "\n");

    	        showAlert("Entry Saved", "Diary entry saved successfully.");
    	        showDiaryScreen();
    	    } catch (IOException e) {
    	        showAlert("Error", "Unable to save diary entry.");
    	    }
    	}


    	private void showEditEntryScreen() {
    	    VBox layout = new VBox(10);

    	    Label selectEntryLabel = new Label("Select an entry to edit:");
    	    ComboBox<String> entryComboBox = new ComboBox<>();

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        reader.lines()
    	            .filter(line -> line.startsWith(currentUser + ","))
    	            .forEach(entryComboBox.getItems()::add);
    	    } catch (IOException e) {
    	        showAlert("Error", "Unable to load entries for editing.");
    	    }

    	    Button editButton = new Button("Edit Selected Entry");
    	    editButton.setOnAction(e -> {
    	        String selectedEntry = entryComboBox.getValue();
    	        if (selectedEntry != null) {
    	            showEditDetailsScreen(selectedEntry);
    	        } else {
    	            showAlert("No Entry Selected", "Please select an entry to edit.");
    	        }
    	    });

    	    Button deleteButton = new Button("Delete Selected Entry");
    	    deleteButton.setOnAction(e -> {
    	        String selectedEntry = entryComboBox.getValue();
    	        if (selectedEntry != null) {
    	            handleDeleteEntry(selectedEntry);
    	        } else {
    	            showAlert("No Entry Selected", "Please select an entry to delete.");
    	        }
    	    });

    	    Button backButton = new Button("Back to Diary");
    	    backButton.setOnAction(e -> showDiaryScreen());

    	    layout.getChildren().addAll(selectEntryLabel, entryComboBox, editButton, deleteButton, backButton);

    	    Scene scene = new Scene(layout, 400, 300);
    	    primaryStage.setTitle("Edit Diary Entry");
    	    primaryStage.setScene(scene);
    	}

    	private void showEditDetailsScreen(String entry) {
    	    String[] entryDetails = entry.split(",");
    	    
    	    // Use a helper method to generate the form
    	    GridPane grid = createEntryForm(entryDetails);

    	    Button saveButton = new Button("Save Changes");
    	    saveButton.setOnAction(e -> handleEditEntry(entry, 
    	        LocalDate.parse(entryDetails[1]), 
    	        entryDetails[2], 
    	        entryDetails[3], 
    	        entryDetails[4], 
    	        entryDetails[5]
    	    ));

    	    grid.add(saveButton, 1, 6);

    	    Scene scene = new Scene(grid, 400, 400);
    	    primaryStage.setTitle("Edit Entry Details");
    	    primaryStage.setScene(scene);
    	}

    	private GridPane createEntryForm(String[] entryDetails) {
    	    GridPane grid = new GridPane();
    	    grid.setVgap(10);
    	    grid.setHgap(10);

    	    Label dateLabel = new Label("Date:");
    	    DatePicker datePicker = new DatePicker(LocalDate.parse(entryDetails[1]));
    	    Label titleLabel = new Label("Title:");
    	    TextField titleField = new TextField(entryDetails[2]);
    	    Label contentLabel = new Label("Content:");
    	    TextArea contentArea = new TextArea(entryDetails[3]);
    	    Label moodLabel = new Label("Mood:");
    	    ComboBox<String> moodComboBox = new ComboBox<>();
    	    moodComboBox.getItems().addAll("Happy", "Neutral", "Sad");
    	    moodComboBox.setValue(entryDetails[4]);

    	    // Image handling
    	    Label imageLabel = new Label("Image:");
    	    ImageView imageView = new ImageView();
    	    imageView.setFitHeight(100);
    	    imageView.setFitWidth(100);

    	    if (entryDetails.length > 5 && !entryDetails[5].isEmpty() && !entryDetails[5].equals("no-image")) {
    	        String imagePath = "images/" + entryDetails[5];
    	        File imageFile = new File(imagePath);

    	        if (imageFile.exists()) {
    	            Image image = new Image(imageFile.toURI().toString());
    	            imageView.setImage(image);
    	        } else {
    	            imageLabel.setText("Image not found: " + entryDetails[5]);
    	            imageView.setImage(new Image("file:images/placeholder.png"));
    	        }
    	    }

    	    grid.add(dateLabel, 0, 0);
    	    grid.add(datePicker, 1, 0);
    	    grid.add(titleLabel, 0, 1);
    	    grid.add(titleField, 1, 1);
    	    grid.add(contentLabel, 0, 2);
    	    grid.add(contentArea, 1, 2);
    	    grid.add(moodLabel, 0, 3);
    	    grid.add(moodComboBox, 1, 3);
    	    grid.add(imageLabel, 0, 4);
    	    grid.add(imageView, 1, 4);

    	    return grid;
    	}

    	private void handleEditEntry(String originalEntry, LocalDate date, String title, String content, String mood, String imageName) {
    	    try {
    	        List<String> allEntries = new ArrayList<>();
    	        boolean entryUpdated = false;

    	        try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	            allEntries = reader.lines().collect(Collectors.toList());
    	        }

    	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE))) {
    	            for (String entry : allEntries) {
    	                if (entry.equals(originalEntry)) {
    	                    String updatedEntry = String.join(",", currentUser, date.toString(), title, content, mood, imageName);
    	                    writer.write(updatedEntry + "\n");
    	                    entryUpdated = true;
    	                } else {
    	                    writer.write(entry + "\n");
    	                }
    	            }
    	        }

    	        if (entryUpdated) {
    	            showAlert("Entry Updated", "The diary entry has been updated successfully.");
    	            showDiaryScreen();
    	        } else {
    	            showAlert("Error", "Entry not found for editing.");
    	        }

    	    } catch (IOException e) {
    	        showAlert("Error", "Unable to update entry.");
    	    }
    	}


    	private void showSearchScreen() {
    	    // Create a new VBox for search layout, separate from mainLayout
    	    VBox searchLayout = new VBox(10);
    	    searchLayout.setPadding(new Insets(10));

    	    // Add search-related controls
    	    Label searchLabel = new Label("Keyword:");
    	    TextField searchField = new TextField();
    	    Button searchButton = new Button("Search");
    	    Button clearButton = new Button("Clear");
    	    Button backButton = new Button("Back to Home");

    	    TextArea resultsArea = new TextArea();
    	    resultsArea.setEditable(false);

    	    searchButton.setOnAction(e -> handleSearchEntries(searchField.getText(), resultsArea));
    	    clearButton.setOnAction(e -> resultsArea.clear());
    	    backButton.setOnAction(e -> showDiaryScreen());

    	    HBox controls = new HBox(10, searchButton, clearButton, backButton);

    	    // Add controls and results area to searchLayout
    	    searchLayout.getChildren().addAll(searchLabel, searchField, controls, resultsArea);

    	    // Create a new scene using searchLayout as the root
    	    Scene scene = new Scene(searchLayout, 600, 400);
    	    primaryStage.setTitle("Search Entries");
    	    primaryStage.setScene(scene);
    	}

    	private void handleSearchEntries(String keyword, TextArea resultsArea) {
    	    if (keyword == null || keyword.trim().isEmpty()) {
    	        // Display an error message if the keyword is empty
    	        resultsArea.clear();  // Clear previous results
    	        resultsArea.appendText("Please enter a keyword to search.\n");
    	        return;
    	    }

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        List<String> results = reader.lines()
    	            .filter(line -> line.contains(currentUser) && line.toLowerCase().contains(keyword.toLowerCase()))
    	            .collect(Collectors.toList());

    	        resultsArea.clear();  // Clear previous results

    	        if (results.isEmpty()) {
    	            resultsArea.appendText("No entries found for the keyword: " + keyword + "\n");
    	        } else {
    	            // Create a VBox to display the results
    	            VBox resultsLayout = new VBox(10);
    	            resultsLayout.setPadding(new Insets(10));

    	            // Create a back button to return to the search screen
    	            Button backButton = new Button("Back to Search");
    	            backButton.setOnAction(e -> showSearchScreen());  // Navigate back to the search screen

    	            // Add the back button to the top of the results layout
    	            resultsLayout.getChildren().add(backButton);

    	            for (String result : results) {
    	                String[] details = result.split(",");

    	                // Extract entry details
    	                String date = details[1];
    	                String title = details[2];
    	                String content = details[3];
    	                String mood = details[4];
    	                String imagePath = details.length > 5 ? details[5] : "no-image";

    	                // Create a VBox for each entry
    	                VBox entryBox = new VBox(5);
    	                entryBox.setPadding(new Insets(5));

    	                TextArea entryTextArea = new TextArea();
    	                entryTextArea.setText("Date: " + date + "\n" +
    	                                      "Title: " + title + "\n" +
    	                                      "Mood: " + mood + "\n" +
    	                                      "Content: " + content + "\n" +
    	                                      "Image: " + (imagePath.equals("no-image") ? "No image" : imagePath));
    	                entryTextArea.setEditable(false);  // Make it non-editable
    	                entryTextArea.setWrapText(true);
    	                entryBox.getChildren().add(entryTextArea);

    	                // Add the image if available
    	                if (!imagePath.equals("no-image")) {
    	                    String imagePathWithDir = "images/" + imagePath;
    	                    File imageFile = new File(imagePathWithDir);

    	                    if (imageFile.exists()) {
    	                        Image image = new Image(imageFile.toURI().toString());
    	                        ImageView imageView = new ImageView(image);
    	                        imageView.setFitHeight(100);
    	                        imageView.setFitWidth(100);

    	                        // Add ImageView to the VBox
    	                        entryBox.getChildren().add(imageView);
    	                    } else {
    	                        entryBox.getChildren().add(new Label("Image file not found."));
    	                    }
    	                }

    	                // Add entryBox to resultsLayout
    	                resultsLayout.getChildren().add(entryBox);
    	            }

    	            // Display results layout in a new scene
    	            Scene resultsScene = new Scene(resultsLayout, 600, 400);
    	            primaryStage.setTitle("Search Results");
    	            primaryStage.setScene(resultsScene);
    	        }

    	    } catch (IOException e) {
    	        resultsArea.clear();
    	        resultsArea.appendText("Error reading entries: " + e.getMessage() + "\n");
    	    }
    	}



    	private void showMotivationalQuote() {
    	    // Define quotes based on mood
    	    Map<String, List<String>> quotes = new HashMap<>();
    	    quotes.put("Happy", Arrays.asList("Keep shining, you're amazing!", "Spread your joy to the world!"));
    	    quotes.put("Neutral", Arrays.asList("Every day is a chance to grow.", "Stay steady and keep moving forward."));
    	    quotes.put("Sad", Arrays.asList("It's okay to not be okay.", "Keep going, brighter days are ahead."));

    	    String mood = "Neutral"; // Default mood if no mood is selected.

    	    try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
    	        // Retrieve the last entry to get the user's mood
    	        Optional<String> lastEntry = reader.lines()
    	            .filter(line -> line.contains(currentUser))
    	            .reduce((first, second) -> second);

    	        if (lastEntry.isPresent()) {
    	            String[] details = lastEntry.get().split(",");
    	            mood = details[4];
    	        }
    	    } catch (IOException e) {
    	        showAlert("Error", "Unable to retrieve last mood.");
    	    }

    	    // Get the appropriate quotes for the mood
    	    List<String> moodQuotes = quotes.getOrDefault(mood, quotes.get("Neutral"));
    	    String randomQuote = moodQuotes.get(new Random().nextInt(moodQuotes.size()));

    	    // Create a new VBox layout to display the motivational quote
    	    VBox layout = new VBox(10);
    	    layout.setPadding(new Insets(10));

    	    // Add the motivational quote label
    	    Label quoteLabel = new Label(randomQuote);
    	    quoteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    	    // Add a "Back" button to return to the previous screen (e.g., Diary screen)
    	    Button backButton = new Button("Back to Diary");
    	    backButton.setOnAction(e -> showDiaryScreen());  // This will return to the Diary screen or appropriate screen

    	    // Add the quote and back button to the layout
    	    layout.getChildren().addAll(quoteLabel, backButton);

    	    // Create a new scene for the Motivational Quote screen
    	    Scene scene = new Scene(layout, 400, 200);
    	    primaryStage.setTitle("Motivational Quote");
    	    primaryStage.setScene(scene);
    	}


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void ensureRecycleBinExists() {
        File recycleBinDir = new File("recycleBin");
        if (!recycleBinDir.exists()) {
            recycleBinDir.mkdir();  // Create the recycle bin directory if it doesn't exist
        }
    }

    private void handleDeleteEntry(String entryToDelete) {
        try {
            List<String> allEntries = new ArrayList<>();
            boolean entryFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
                allEntries = reader.lines().collect(Collectors.toList());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE))) {
                for (String entry : allEntries) {
                    if (entry.trim().equals(entryToDelete.trim())) {
                        // Move the entry to the recycle bin
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        File recycleBinFile = new File("recycleBin/deleted_" + timestamp + ".csv");

                        try (BufferedWriter recycleWriter = new BufferedWriter(new FileWriter(recycleBinFile))) {
                            recycleWriter.write(entry + "\n");
                        }

                        entryFound = true;
                    } else {
                        writer.write(entry + "\n");
                    }
                }
            }

            if (entryFound) {
                showAlert("Entry Deleted", "The diary entry has been deleted and moved to the recycle bin.");
                showDiaryScreen();
            } else {
                showAlert("Entry Not Found", "The entry you tried to delete was not found.");
            }

        } catch (IOException e) {
            showAlert("Error", "Unable to delete entry.");
        }
    }

    private void showRecycleBinScreen() {
        File recycleBinDir = new File("recycleBin");
        File[] files = recycleBinDir.listFiles();

        VBox layout = new VBox(10);
        ListView<String> recycleBinListView = new ListView<>();
        Button restoreButton = new Button("Restore Entry");
        Button permanentlyDeleteButton = new Button("Permanently Delete Entry");

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String preview = getFilePreview(file);
                    String fileInfo = fileName + " - " + preview;
                    recycleBinListView.getItems().add(fileInfo);
                }
            }
        }

        restoreButton.setOnAction(e -> {
            String selectedFile = recycleBinListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                String fileName = selectedFile.split(" ")[0]; // Extract file name
                restoreEntryFromRecycleBin(fileName);
            } else {
                showAlert("No Entry Selected", "Please select an entry to restore.");
            }
        });

        permanentlyDeleteButton.setOnAction(e -> {
            String selectedFile = recycleBinListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                String fileName = selectedFile.split(" ")[0];
                permanentlyDeleteEntry(fileName);
            } else {
                showAlert("No Entry Selected", "Please select an entry to permanently delete.");
            }
        });

        Button backToFrontPageButton = new Button("Back to Front Page");
        backToFrontPageButton.setOnAction(e -> showDiaryScreen());

        layout.getChildren().addAll(recycleBinListView, restoreButton, permanentlyDeleteButton, backToFrontPageButton);

        // Create a new scene for the Recycle Bin screen
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Recycle Bin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getFilePreview(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                return line.substring(0, Math.min(line.length(), 30)) + "..."; // Preview first 30 characters
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No preview available";
    }

    private void restoreEntryFromRecycleBin(String fileName) {
        File recycleBinFile = new File("recycleBin/" + fileName);

        if (recycleBinFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(recycleBinFile))) {
                String entry = reader.readLine();

                // Add back to the main entries file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE, true))) {
                    writer.write(entry + "\n");
                }

                // Delete the entry from the recycle bin after restoring
                recycleBinFile.delete();
                showAlert("Entry Restored", "The entry has been restored to your diary.");
                showDiaryScreen();  // Optionally go back to the diary screen after restoring
            } catch (IOException e) {
                showAlert("Error", "Unable to restore the entry.");
            }
        } else {
            showAlert("Error", "The selected entry does not exist.");
        }
    }

    private void permanentlyDeleteEntry(String fileName) {
        File recycleBinFile = new File("recycleBin/" + fileName);

        if (recycleBinFile.exists()) {
            // Check the age of the file and delete if it's older than 30 days
            long currentTime = System.currentTimeMillis();
            long fileTime = Long.parseLong(fileName.split("_")[1].split("\\.")[0]);

            // If the file is older than 30 days, delete permanently
            if (currentTime - fileTime > 30L * 24 * 60 * 60 * 1000) {
                recycleBinFile.delete();
                showAlert("Entry Permanently Deleted", "The entry has been permanently deleted.");
            } else {
                showAlert("Entry Not Yet Expired", "This entry has not yet expired. Please try again later.");
            }
        } else {
            showAlert("Error", "The selected entry does not exist.");
        }
    }

    private void cleanUpRecycleBin() {
        File recycleBinDir = new File("recycleBin");
        File[] files = recycleBinDir.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    long fileTime = Long.parseLong(fileName.split("_")[1].split("\\.")[0]);

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - fileTime > 30L * 24 * 60 * 60 * 1000) {
                        file.delete();
                    }
                } catch (Exception e) {
                    // Handle parsing errors if filename format is unexpected
                }
            }
        }
    }
}
