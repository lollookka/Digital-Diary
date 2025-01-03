package application;

import javafx.application.Application;
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
                    showDiaryScreen();
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
        mainLayout = new VBox();  // Initialize the VBox

        VBox entryList = new VBox();
        Button newEntryButton = new Button("New Entry");
        Button editEntryButton = new Button("Edit Entry");
        Button searchButton = new Button("Search Entries");
        Button motivationalButton = new Button("Get Motivational Quote");
        Button recycleBinButton = new Button("Recycle Bin");

        // Set actions for each button
        newEntryButton.setOnAction(e -> showEntryCreationScreen());
        editEntryButton.setOnAction(e -> showEditEntryScreen());
        searchButton.setOnAction(e -> showSearchScreen());
        motivationalButton.setOnAction(e -> showMotivationalQuote());

        // Action for the Recycle Bin button
        recycleBinButton.setOnAction(e -> showRecycleBinScreen()); // Navigate to Recycle Bin screen

        // Add buttons to the layout
        mainLayout.getChildren().add(entryList);

        // Add the buttons to a horizontal box (HBox)
        HBox actions = new HBox(10, newEntryButton, editEntryButton, searchButton, motivationalButton, recycleBinButton);
        mainLayout.getChildren().add(actions); // Set the actions at the bottom of the screen

        // Set the scene
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setTitle("Digital Diary - Welcome " + currentUser);
        primaryStage.setScene(scene);
    }


    private void showEntryCreationScreen() {
        GridPane grid = new GridPane();

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
        FileChooser fileChooser = new FileChooser();
        Label imageLabel = new Label("No image selected");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        // Set the placeholder image initially
        Image placeholderImage = new Image("file:resources/images/placeholder.png");
        imageView.setImage(placeholderImage);

        uploadImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    // Ensure the `images` directory exists
                    File imagesDir = new File("images");
                    if (!imagesDir.exists()) {
                        imagesDir.mkdir(); // Create the directory if it doesn't exist
                    }

                    // Copy the selected file to the `images` directory
                    File destFile = new File(imagesDir, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Update the UI with the selected image
                    Image image = new Image(destFile.toURI().toString());
                    imageView.setImage(image);
                    imageLabel.setText(selectedFile.getName()); // Update label with the file name
                } catch (IOException ex) {
                    showAlert("Error", "Unable to save image.");
                }
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> handleSaveEntry(datePicker.getValue(), titleField.getText(), contentArea.getText(), moodComboBox.getValue(), imageLabel.getText(), imageView));

        // Add back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showDiaryScreen());  // Navigate to front page or previous screen

        // Add controls to grid
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
        grid.add(backButton, 0, 7);  // Add back button below the save button

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
        VBox layout = new VBox();

        Label selectEntryLabel = new Label("Select an entry to edit:");
        ComboBox<String> entryComboBox = new ComboBox<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
            reader.lines()
                .filter(line -> line.contains(currentUser))
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

        // Add the Delete Button
        Button deleteButton = new Button("Delete Selected Entry");
        deleteButton.setOnAction(e -> {
            String selectedEntry = entryComboBox.getValue();
            if (selectedEntry != null) {
                handleDeleteEntry(selectedEntry);  // Call delete handler
            } else {
                showAlert("No Entry Selected", "Please select an entry to delete.");
            }
        });

        // Add the Back Button to go back to the diary screen
        Button backButton = new Button("Back to Diary");
        backButton.setOnAction(e -> showDiaryScreen()); // Navigate back to the diary screen

        layout.getChildren().addAll(selectEntryLabel, entryComboBox, editButton, deleteButton, backButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Edit Diary Entry");
        primaryStage.setScene(scene);
    }


    private void showEditDetailsScreen(String entry) {
        GridPane grid = new GridPane();
        String[] entryDetails = entry.split(",");

        // Extract entry details
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

        // Load the image if specified
        if (entryDetails.length > 5 && !entryDetails[5].isEmpty() && !entryDetails[5].equals("no-image")) {
            String imagePath = "images/" + entryDetails[5];
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            } else {
                imageLabel.setText("Image not found: " + entryDetails[5]);
                imageView.setImage(new Image("file:images/placeholder.png")); // Fallback to placeholder
            }
        } else {
            imageLabel.setText("No image available.");
            imageView.setImage(new Image("file:images/placeholder.png"));
        }

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> handleEditEntry(entry, datePicker.getValue(), titleField.getText(), contentArea.getText(), moodComboBox.getValue(), entryDetails[5]));

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
        grid.add(saveButton, 1, 5);

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setTitle("Edit Entry Details");
        primaryStage.setScene(scene);
    }



    private void handleEditEntry(String originalEntry, LocalDate date, String title, String content, String mood, String imageName) {
        try {
            List<String> allEntries = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
                allEntries = reader.lines().collect(Collectors.toList());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE))) {
                for (String entry : allEntries) {
                    if (entry.equals(originalEntry)) {
                        String updatedEntry = String.join(",", currentUser, date.toString(), title, content, mood, imageName);
                        writer.write(updatedEntry + "\n");
                    } else {
                        writer.write(entry + "\n");
                    }
                }
            }

            showAlert("Entry Updated", "The diary entry has been updated successfully.");
            showDiaryScreen();
        } catch (IOException e) {
            showAlert("Error", "Unable to update entry.");
        }
    }


    private void showSearchScreen() {
        // Create a new VBox for the search screen
        VBox searchLayout = new VBox();

        // Clear previous content if necessary (but we're using a new VBox here)
        // Add a new GridPane for the search layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add search-related controls
        Label searchLabel = new Label("Keyword:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        Button clearButton = new Button("Clear");
        Button backButton = new Button("Back to Home");

        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);

        // Handle search, clear and back actions
        searchButton.setOnAction(e -> handleSearchEntries(searchField.getText(), resultsArea));
        clearButton.setOnAction(e -> resultsArea.clear());
        backButton.setOnAction(e -> showDiaryScreen()); // Navigate back to the Diary screen

        HBox controls = new HBox(10, searchButton, clearButton, backButton);

        // Add controls and results area to grid
        grid.add(searchLabel, 0, 0);
        grid.add(searchField, 1, 0);
        grid.add(controls, 2, 0, 2, 1);
        grid.add(resultsArea, 0, 1, 4, 1);

        // Create a "Go Back" button to go back after search results
        Button goBackButton = new Button("Go Back");
        goBackButton.setOnAction(e -> showDiaryScreen()); // Action to go back to the Diary screen

        // Add the Go Back button below the results
        VBox searchResultsLayout = new VBox(10, grid, goBackButton);
        searchLayout.getChildren().add(searchResultsLayout);

        // Create a new scene and set it to the primaryStage
        Scene scene = new Scene(searchLayout, 600, 400);  // Using searchLayout instead of mainLayout
        primaryStage.setTitle("Search Entries");
        primaryStage.setScene(scene);
    }

    private void handleSearchEntries(String keyword, TextArea resultsArea) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Display an error message if keyword is empty
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
                // Clear previous results from mainLayout before adding new ones
                mainLayout.getChildren().clear();

                for (String result : results) {
                    String[] details = result.split(",");

                    // Extract the details of the entry
                    String date = details[1];
                    String title = details[2];
                    String content = details[3];
                    String mood = details[4];
                    String imagePath = details.length > 5 ? details[5] : "no-image"; // Get the image path

                    // Append text-based entry details to the TextArea
                    resultsArea.appendText("Date: " + date + "\n");
                    resultsArea.appendText("Title: " + title + "\n");
                    resultsArea.appendText("Mood: " + mood + "\n");
                    resultsArea.appendText("Content: " + content + "\n");

                    // Create a VBox for the image and TextArea content
                    VBox entryBox = new VBox();

                    // Add the TextArea content
                    TextArea entryTextArea = new TextArea();
                    entryTextArea.setText("Date: " + date + "\n" +
                                          "Title: " + title + "\n" +
                                          "Mood: " + mood + "\n" +
                                          "Content: " + content + "\n");
                    entryTextArea.setEditable(false);  // Make it non-editable
                    entryTextArea.setWrapText(true);
                    entryBox.getChildren().add(entryTextArea);

                    // Image handling - Add the image if available
                    if (!imagePath.equals("no-image")) {
                        // Ensure the image is loaded from the "images/" folder
                        String imagePathWithDir = "images/" + imagePath;  // Image path relative to "images/" folder
                        File imageFile = new File(imagePathWithDir);

                        if (imageFile.exists()) {
                            // Create an Image object from the file path
                            Image image = new Image(imageFile.toURI().toString());  // Convert the file to an image URL
                            ImageView imageView = new ImageView(image);
                            imageView.setFitHeight(100);  // Set the image size
                            imageView.setFitWidth(100);

                            // Add the ImageView to the VBox to display it
                            entryBox.getChildren().add(imageView);
                        } else {
                            resultsArea.appendText("Image file not found: " + imagePathWithDir + "\n");
                        }
                    } else {
                        resultsArea.appendText("Image: No image\n");
                    }

                    // Add the VBox (TextArea and ImageView) to the main layout container (VBox)
                    mainLayout.getChildren().add(entryBox);  // Add each entry VBox
                }
            }
        } catch (IOException e) {
            resultsArea.clear();
            resultsArea.appendText("Error reading entries: " + e.getMessage() + "\n");
        }
    }



    private void showMotivationalQuote() {
        Map<String, List<String>> quotes = new HashMap<>();
        quotes.put("Happy", Arrays.asList("Keep shining, you're amazing!", "Spread your joy to the world!"));
        quotes.put("Neutral", Arrays.asList("Every day is a chance to grow.", "Stay steady and keep moving forward."));
        quotes.put("Sad", Arrays.asList("It's okay to not be okay.", "Keep going, brighter days are ahead."));

        String mood = "Neutral"; // Default if no mood is selected.

        try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
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

        List<String> moodQuotes = quotes.getOrDefault(mood, quotes.get("Neutral"));
        String randomQuote = moodQuotes.get(new Random().nextInt(moodQuotes.size()));

        showAlert("Motivational Quote", randomQuote);
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

            // Read all entries from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(ENTRIES_FILE))) {
                allEntries = reader.lines().collect(Collectors.toList());
            }

            // Check if the entry is in the list and write updated entries back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENTRIES_FILE))) {
                for (String entry : allEntries) {
                    // Trim both the file entry and the entry to delete for better comparison
                    if (entry.trim().equals(entryToDelete.trim())) {
                        // Move the entry to the recycle bin with a timestamp
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        File recycleBinFile = new File("recycleBin/deleted_" + timestamp + ".csv");

                        // Ensure the recycle bin directory exists
                        File recycleBinDir = new File("recycleBin");
                        if (!recycleBinDir.exists()) {
                            recycleBinDir.mkdir();
                        }

                        // Write the entry to the recycle bin file
                        try (BufferedWriter recycleWriter = new BufferedWriter(new FileWriter(recycleBinFile))) {
                            recycleWriter.write(entry + "\n");
                        }

                        entryFound = true; // Mark that the entry was found and deleted
                    } else {
                        // Write the remaining entries back to the main file
                        writer.write(entry + "\n");
                    }
                }
            }

            // Show appropriate message based on whether the entry was found and deleted
            if (entryFound) {
                showAlert("Entry Deleted", "The diary entry has been deleted and moved to the recycle bin.");
                showDiaryScreen(); // Go to the front page after deleting
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

        VBox layout = new VBox();
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
                long currentTime = System.currentTimeMillis();
                long fileTime = Long.parseLong(file.getName().split("_")[1].split("\\.")[0]);

                // Delete files older than 30 days
                if (currentTime - fileTime > 30L * 24 * 60 * 60 * 1000) {
                    file.delete();
                }
            }
        }
    }
} 