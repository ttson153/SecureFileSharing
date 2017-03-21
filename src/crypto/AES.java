package crypto;

import utils.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tts on 3/16/17.
 */
public class AES extends BaseCryptoAlgorithm {
    private static final int BLOCK_SIZE = 16;  // 128 bits
    private static final int PROCESS_SIZE = 100 * BLOCK_SIZE; // Update every 100 block
    SecretKeySpec secretKeySpec;
    IvParameterSpec ivParameterSpec;
    ActionType actionType;

    private JTextArea fileInfoArea;
    private JProgressBar fileProgressBar;
    private JProgressBar overallProgressBar;

    public AES(String keyPath, String ivPath, ActionType actionType) {
        try {
            byte[] key = FileUtils.readBinary(keyPath);
            byte[] iv  = FileUtils.readBinary(ivPath);
            secretKeySpec = new SecretKeySpec(key, "AES");
            ivParameterSpec = new IvParameterSpec(iv);
            this.actionType = actionType;

            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            switch (actionType) {
                case ENCRYPT:
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
                    break;
                case DECRYPT:
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Task extends SwingWorker<Void, String> {

        ActionType actionType;
        String inPath; // Root directory absolute path
        ArrayList<String> fileNames; // list of relative path to all directory or file to be processed
        String outPath;
        byte[] output; // output partially with the size of PROCESS_SIZE

        public Task(ActionType actionType, String inPath, String outPath) {
            this.actionType = actionType;
            this.fileNames = new ArrayList<>();
            if (FileUtils.isDirectory(inPath)) {
                // list all files if inPath is a directory
                try {
                    FileUtils.listAllSubFiles(fileNames, Paths.get(inPath), Paths.get(inPath).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.inPath = inPath;
            } else {
                // add only the file it inPath is a file
                String fileName = Paths.get(inPath).getFileName().toString();
                fileNames.add(fileName);
                this.inPath = inPath.replace(fileName, "");
            }
            this.outPath = outPath;

            fileProgressBar.setVisible(true);
            fileProgressBar.setStringPainted(true);
            fileProgressBar.setValue(0);
            overallProgressBar.setVisible(true);
            overallProgressBar.setStringPainted(true);
            overallProgressBar.setValue(0);

            // listener to update progressbars
            addPropertyChangeListener(evt -> {
                //TODO how to update overall progressbar
                if ("progress".equals(evt.getPropertyName())) {
                    fileProgressBar.setValue((Integer) evt.getNewValue());
                }
            });
        }

        public byte[] getOutput() {
            return this.output;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (String fileName : fileNames) {
                // Update UI
                publish(fileName);

                System.gc();
                byte[] input = FileUtils.readBinary(inPath + fileName);
                String indivPath = "";

                float progress = 0;
                setProgress((int) progress);
                float progressDelta = (float) input.length / PROCESS_SIZE;

                int processBlockLength = PROCESS_SIZE;
                switch (actionType) {
                    case ENCRYPT:
//                        processBlockLength = PROCESS_SIZE;
                        indivPath = outPath + fileName + ".encryted";
                        break;
                    case DECRYPT:
//                        processBlockLength = PROCESS_SIZE + 16;
                        indivPath = outPath + fileName + ".decryted";
                        break;
                }

                int numProcess = input.length / processBlockLength;
                int currentOffset = 0;
                while (numProcess >= 0) {
                    // TODO Hacky: there's an extra 16 byte block every PROCESS_SIZE
                    if (numProcess == 0) {
                        // last PROCESS_SIZE block
                        output = action(Arrays.copyOfRange(input, currentOffset, input.length), true
                        );
                    } else {
                        output = action(Arrays.copyOfRange(input, currentOffset, currentOffset + processBlockLength), false
                        );
                    }
                    FileUtils.appendBinary(output, indivPath);

                    currentOffset += processBlockLength;
                    numProcess--;
                    progress += 100.0 / progressDelta;
                    System.out.println(progress + " %");
                    setProgress((int) Math.min(progress, 100));
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String fileName : chunks) {
                fileInfoArea.append("Processing " + fileName + "\n");
            }
        }

        @Override
        protected void done() {
            setProgress(100);
            Toolkit.getDefaultToolkit().beep();
            // set all progressbar to 100%
            fileProgressBar.setValue(100);
            overallProgressBar.setValue(100);
        }
    }

    public void setUIElements(JTextArea fileInfoArea, JProgressBar fileProgressBar, JProgressBar overallProgressBar) {
        this.fileInfoArea = fileInfoArea;
        this.fileProgressBar = fileProgressBar;
        this.overallProgressBar = overallProgressBar;
    }

    @Override
    public void doAction(String inPath, String outPath, Object... otherConfiguration) {
        Task cryptoTask = new Task(actionType, inPath, outPath);
        cryptoTask.execute();
    }

    @Override
    protected byte[] action(byte[] in, boolean isLastBlock) {
        try {
            //TODO use doFinal(in, currentOffset, currentOffset + PROCESS_SIZE) - reduce array copy complexity
            if (isLastBlock) {
                return cipher.doFinal(in);
            } else {
                return cipher.update(in);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
