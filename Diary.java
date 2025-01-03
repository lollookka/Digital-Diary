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
        BorderPane layout = new BorderPane();

        VBox entryList = new VBox();
        Button newEntryButton = new Button("New Entry");
        Button editEntryButton = new Button("Edit Entry");
        Button searchButton = new Button("Search Entries");
        Button motivationalButton = new Button("Get Motivational Quote");

        newEntryButton.setOnAction(e -> showEntryCreationScreen());
        editEntryButton.setOnAction(e -> showEditEntryScreen());
        searchButton.setOnAction(e -> showSearchScreen());
        motivationalButton.setOnAction(e -> showMotivationalQuote());

        layout.setCenter(entryList);
        HBox actions = new HBox(newEntryButton, editEntryButton, searchButton, motivationalButton);
        layout.setBottom(actions);

        Scene scene = new Scene(layout, 600, 400);
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

        layout.getChildren().addAll(selectEntryLabel, entryComboBox, editButton);

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
        // Clear previous content from mainLayout if it already exists
        if (mainLayout != null) {
            mainLayout.getChildren().clear();
        }

        // Create a new GridPane for the search layout
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

        searchButton.setOnAction(e -> handleSearchEntries(searchField.getText(), resultsArea));
        clearButton.setOnAction(e -> resultsArea.clear());
        backButton.setOnAction(e -> showDiaryScreen());

        HBox controls = new HBox(10, searchButton, clearButton, backButton);

        // Add controls and results area to grid
        grid.add(searchLabel, 0, 0);
        grid.add(searchField, 1, 0);
        grid.add(controls, 2, 0, 2, 1);
        grid.add(resultsArea, 0, 1, 4, 1);

        // Add the grid to the main layout (VBox)
        if (mainLayout == null) {
            mainLayout = new VBox();
        }
        mainLayout.setSpacing(10);
        mainLayout.getChildren().add(grid);

        // Create a new scene using mainLayout as the root
        Scene scene = new Scene(mainLayout, 600, 400);
        
        // Set the scene for the primary stage
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
                    String imagePath = details.length > 5 ? details[5] : "no-image";

                    // Append text-based entry details to the TextArea
                    resultsArea.appendText("Date: " + date + "\n");
                    resultsArea.appendText("Title: " + title + "\n");
                    resultsArea.appendText("Mood: " + mood + "\n");
                    resultsArea.appendText("Content: " + content + "\n");
                    resultsArea.appendText("Image: " + (imagePath.equals("no-image") ? "No image" : imagePath) + "\n\n");

                    // Create a VBox for the image and TextArea content
                    VBox entryBox = new VBox();

                    // Add the TextArea content
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
                            imageView.setFitHeight(100);  // Set size of the image
                            imageView.setFitWidth(100);

                            // Add ImageView to the VBox
                            entryBox.getChildren().add(imageView);
                        } else {
                            resultsArea.appendText("Image file not found.\n");
                        }
                    }

                    // Add the VBox (TextArea and ImageView) to your main layout container (VBox)
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
} 