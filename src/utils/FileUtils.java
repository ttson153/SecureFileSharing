package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

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
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream output = new FileOutputStream(path, true);
        output.write(toAppend);
        output.close();
    }

    public static boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    public static void listAllSubFiles(List<String> fileNames, Path root, String rootAbsPath) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(root);
        for (Path path : stream) {
            if (path.toFile().isDirectory()) {
                listAllSubFiles(fileNames, path, rootAbsPath);
            } else {
                String relativePath = path.toAbsolutePath().toString().replace(rootAbsPath, "");
                fileNames.add(relativePath);
            }
        }
    }
}