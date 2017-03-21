package ui;

import crypto.AES;
import crypto.ActionType;
import hash.ImplHashAlgorithm;

import javax.swing.*;

/**
 * Created by tts on 3/16/17.
 */
public class MainWindowController {

    /**
     * Bridge between UI and application logic
     *
     * @param algorithm     The crypto algorithm to use
     * @param actionType    The action to perform (ENCRYPT or DECRYPT)
     * @param configuration Pass in the configuration in the specific other.
     *                      If algorithm is AES: inputPath, keyPath, outputPath
     */
    public static void performCrypto(String algorithm, ActionType actionType,
                                     JTextArea fileInfoArea, JProgressBar fileProgressBar, JProgressBar overallProgressBar,
                                     Object... configuration) {
        switch (algorithm) {
            case "AES":
                // parse configuration passed in
                String inputPath = configuration[0].toString();
                String keyPath = configuration[1].toString();
                String outputPath = configuration[2].toString();

                AES aesInstance = new AES(keyPath, keyPath, actionType);
                aesInstance.setUIElements(fileInfoArea, fileProgressBar, overallProgressBar);
                aesInstance.doAction(inputPath, outputPath);
                break;
        }
    }

    public static void performHash(String algorithm,
                                   JFormattedTextField hashOutputText,
                                   Object... configuration) {
        // parse configuration passed in
        String inputHashPath = configuration[0].toString();
        ImplHashAlgorithm hashAlgorithmInstance = new ImplHashAlgorithm(algorithm);
        hashAlgorithmInstance.setUIElements(hashOutputText, null);
        hashAlgorithmInstance.doAction(inputHashPath, false);
    }

    public static void performSaveHash(String algorithm,
                                       JFormattedTextField hashOutputText,
                                       Object... configuration) {
        // parse configuration passed in
        String inputHashPath = configuration[0].toString();
        ImplHashAlgorithm hashAlgorithmInstance = new ImplHashAlgorithm(algorithm);
        hashAlgorithmInstance.setUIElements(hashOutputText, null);
        hashAlgorithmInstance.saveToFile(inputHashPath, algorithm);
    }

    public static void performChecksum(String algorithm,
                                       JFormattedTextField hashOutputText,
                                       JPanel checkSumPanel,
                                       String inputHashPath, String checksum, boolean isPath) {
        ImplHashAlgorithm hashAlgorithmInstance = new ImplHashAlgorithm(algorithm);
        hashAlgorithmInstance.setUIElements(hashOutputText, checkSumPanel);
        hashAlgorithmInstance.checksum(inputHashPath, checksum, isPath);
    }
}
