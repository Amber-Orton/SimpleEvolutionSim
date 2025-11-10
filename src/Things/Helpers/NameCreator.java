package Things.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NameCreator {
    private static BufferedReader reader;
    private static String filePath;

    public static void open() throws IOException {
        if (reader != null) {
            reader.close();
        }
        filePath = "assets/names.txt";
        reader = new BufferedReader(new FileReader(filePath));
    }

    public static String nextLine() throws IOException {
        if (reader == null) {
            throw new IllegalStateException("File not opened. Call open() first.");
        }

        String line = reader.readLine();

        // If we reached EOF, reset to the beginning
        if (line == null) {
            reader.close();
            reader = new BufferedReader(new FileReader(filePath));
            line = reader.readLine();
        }

        return line;
    }

    public static void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}
