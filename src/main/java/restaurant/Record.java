package restaurant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Record {
    public static synchronized void recorded(String output) {
        try {
            String path = "src\\main\\resources\\out.txt";
            Files.write(Paths.get(path), (output + "\r\n").getBytes(), APPEND, CREATE);
        } catch (IOException e) {
            System.out.println("Not recorded: " + output);
        }
    }
}