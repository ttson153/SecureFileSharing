package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by tts on 3/16/17.
 */
public class FileUtils {
    //TODO Unified flow: We are currently read all the input and gradually append to output

    public static byte[] readBinary(String path) throws IOException {
        Path p = FileSystems.getDefault().getPath(path);
        return Files.readAllBytes(p);
    }

    public static String readString(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    public static void writeBinary(byte[] toWrite, String path) throws IOException {
        Path p = Paths.get(path);
        Files.write(p, toWrite);
    }

    public static void appendBinary(byte[] toAppend, String path) throws IOException {
        FileOutputStream output = new FileOutputStream(path, true);
        output.write(toAppend);
        output.close();
    }
}