package crypto;

import utils.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by tts on 3/16/17.
 */
public class AES extends BaseCryptoAlgorithm {
    private static final int BLOCK_SIZE = 16;  // 128 bits
    private static final int PROCESS_SIZE = 100 * BLOCK_SIZE; // Update every 100 block

    private class Task extends SwingWorker<Void, Void> {
        private JProgressBar progressBar;

        ActionType actionType;
        byte[] input;  // full input file byte array
        byte[] key;    // key in byte array
        String outPath;
        byte[] output; // output partially with the size of PROCESS_SIZE

        public Task(ActionType actionType, byte[] input, byte[] key, String outPath, JProgressBar progressBar) {
            this.actionType = actionType;
            this.input = input;
            this.key = key;
            this.outPath = outPath;
            this.output = new byte[PROCESS_SIZE];

            this.progressBar = progressBar;
            addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    this.progressBar.setValue((Integer) evt.getNewValue());
                }
            });
            this.progressBar.setVisible(true);
            this.progressBar.setStringPainted(true);
            this.progressBar.setValue(0);
        }

        public byte[] getOutput() {
            return this.output;
        }

        @Override
        protected Void doInBackground() throws Exception {
            float progress = 0;
            float progressDelta = (float) input.length / PROCESS_SIZE;
            setProgress((int) progress);

            int currentOffset = 0;
            while (progress < 100) {
                switch (actionType) {
                    case ENCRYPT:
                        if (currentOffset + PROCESS_SIZE > input.length) {
                            // last PROCESS_SIZE
                            output = encrypt(Arrays.copyOfRange(input, currentOffset, input.length),
                                    key, output);
                        } else {
                            output = encrypt(Arrays.copyOfRange(input, currentOffset, currentOffset + PROCESS_SIZE),
                                    key, output);
                            currentOffset += PROCESS_SIZE;
                        }
                        break;
                    case DECRYPT:
                        // TODO Hacky: there's an extra 16 byte block every PROCESS_SIZE
                        if (currentOffset + PROCESS_SIZE > input.length) {
                            // last PROCESS_SIZE
                            output = decrypt(Arrays.copyOfRange(input, currentOffset, input.length),
                                    key, output);
                        } else {
                            output = decrypt(Arrays.copyOfRange(input, currentOffset, currentOffset + PROCESS_SIZE + 16),
                                    key, output);
                            currentOffset += (PROCESS_SIZE + 16);
                        }
                        break;
                }
                FileUtils.appendBinary(output, outPath);

                progress += 100.0 / progressDelta;
                System.out.println(progress + " %");
                setProgress((int) Math.min(progress, 100));
            }

            return null;
        }

        @Override
        protected void done() {
            setProgress(100);
            Toolkit.getDefaultToolkit().beep();
            progressBar.setValue(100);
        }
    }

    @Override
    public void doAction(ActionType actionType, String inPath, String keyPath, String outPath, JProgressBar progressBar, Object... otherConfiguration) {
        try {
            byte[] input = FileUtils.readBinary(inPath);
            byte[] key = FileUtils.readBinary(keyPath);

            Task cryptoTask = new Task(actionType, input, key, outPath, progressBar);
            cryptoTask.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encrypt(byte[] in, byte[] key, byte[] out, Object... otherConfiguration) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            //TODO use doFinal(in, currentOffset, currentOffset + PROCESS_SIZE) - reduce array copy complexity
            return cipher.doFinal(in);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected byte[] decrypt(byte[] in, byte[] key, byte[] out, Object... otherConfiguration) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            return cipher.doFinal(in);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
