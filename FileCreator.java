package application;

import java.io.File;
import java.io.IOException;

public class FileCreator {
	private static final String DIRECTORY = "data";
    private static final String FILE_NAME = "diary_entries.csv";

    public static void main(String[] args) {
        boolean result = createFileWithPath(DIRECTORY, FILE_NAME);
        if (result) {
            System.out.println("File successfully created.");
        } else {
            System.out.println("File already exists or could not be created.");
        }
    }

    /**
     * Creates a directory and file at the specified path.
     *
     * @param directoryPath the directory to create
     * @param fileName      the file to create inside the directory
     * @return true if the file was created, false otherwise
     */
    public static boolean createFileWithPath(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        // Create directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            // Create file if it doesn't exist
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                    System.out.println("File path: " + file.getAbsolutePath());
                    return true;
                }
            } else {
                System.out.println("File already exists at: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file.");
            e.printStackTrace();
        }
        return false;
    }
}
