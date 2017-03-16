package ui;

import crypto.AES;
import crypto.ActionType;

/**
 * Created by tts on 3/16/17.
 */
public class MainWindowController {

    /** Bridge between UI and application logic
     * @param algorithm The crypto algorithm to use
     * @param actionType The action to perform (ENCRYPT or DECRYPT)
     * @param configuration Pass in the configuration in the specific other.
     *                      If algorithm is AES: inputPath, keyPath, outputPath
     */
    public static void performAction(String algorithm, ActionType actionType, Object... configuration) {
        switch (algorithm) {
            case "AES":
                // parse configuration passed in
                AES aesInstance = new AES();
                String inputPath = configuration[0].toString();
                String keyPath = configuration[1].toString();
                String outputPath = configuration[2].toString();

                aesInstance.doAction(actionType, inputPath, keyPath, outputPath);
                break;
        }
    }
}
