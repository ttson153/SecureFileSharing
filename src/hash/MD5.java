package hash;

import utils.FileUtils;
import utils.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tts on 3/19/17.
 */
public class MD5 extends BaseHashAlgorithm {
    JPanel checkSumPanel;
    JFormattedTextField hashOutputText;

    public MD5() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setUIElements(JFormattedTextField hashOutputText, JPanel checkSumPanel) {
        this.hashOutputText = hashOutputText;
        this.checkSumPanel = checkSumPanel;
    }

    @Override
    public void doAction(String inputPath, boolean saveToFile) {
        try {
            byte[] input = FileUtils.readBinary(inputPath);
            byte[] output = digest(input);
            hashOutputText.setText(StringUtils.toHexString(output));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveToFile(String inputHashPath) {
        String inputParts[] = inputHashPath.split("\\.");
        String outputPath = inputParts[0] + "[MD5]." + inputParts[1];
        if (hashOutputText.getText().equals("")) {
            // perform hash if not already
            doAction(inputHashPath, false);
        }

        try {
            FileUtils.writeString(hashOutputText.getText(), outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checksum(String inputHashPath, String checksum, boolean isPath) {
        String checkSum = "";
        if (isPath) {
            // read checksum from file
            try {
                checkSum = FileUtils.readString(checksum);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            checkSum = checksum;
        }

        if (hashOutputText.getText().equals("")) {
            // perform hash if not already
            doAction(inputHashPath, false);
        }

        // Check duplicate with provided hash
        if (hashOutputText.getText().equals(checkSum)) {
            JOptionPane.showMessageDialog(checkSumPanel, "The file has the same checksum");
        } else {
            JOptionPane.showMessageDialog(checkSumPanel, "The file has the different checksum");
        }
    }

    @Override
    protected byte[] digest(byte[] input) {
        return md.digest(input);
    }
}
