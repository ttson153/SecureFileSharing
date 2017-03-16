package ui;

import javax.swing.*;
import java.io.File;

/**
 * Created by tts on 3/16/17.
 */
public class MainWindowHelper {
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));

    public static String openFileChooser(JComponent parent) {
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }
}