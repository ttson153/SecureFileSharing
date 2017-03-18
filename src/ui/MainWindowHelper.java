package ui;

import javax.swing.*;
import java.io.File;

/**
 * Created by tts on 3/16/17.
 */
public class MainWindowHelper {
//    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));

    //TODO DEBUG
    private static JFileChooser fileChooser = new JFileChooser("/home/tts/Projects/IntelliJIdea/SecureFileSharing/test");

    public static String openFileChooser(JComponent parent) {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }
}
