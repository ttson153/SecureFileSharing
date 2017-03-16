package crypto;

import utils.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tts on 3/16/17.
 */
public class AES extends BaseCryptoAlgorithm {
    private static final int BLOCK_SIZE = 16;  // 128 bits
    private static final int PROCESS_SIZE = 100 * BLOCK_SIZE; // Update every 100 block

    private class Task extends SwingWorker<Void, Void> {
        byte[] input;  // full input file byte array
        byte[] key;    // key in byte array
        byte[] output; // output partially with the size of PROCESS_SIZE

        public Task(byte[] input, byte[] key) {
            this.input = input;
            this.key = key;
            this.output = new byte[PROCESS_SIZE];
        }

        public byte[] getOutput() {
            return this.output;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int progress = 0;
            setProgress(progress);

            while (progress < 100) {
                output = encrypt(input, key, output);
            }

            return null;
        }

    }

    @Override
    public void doAction(ActionType actionType, String inPath, String keyPath, String outPath, Object... otherConfiguration) {
        try {
            byte[] input = FileUtils.readBinary(inPath);
            byte[] key = FileUtils.readBinary(keyPath);
            switch (actionType) {
                case ENCRYPT:
                    Task encryptTask = new Task(input, key);
                    break;
                case DECRYPT:
                    decrypt(inPath, keyPath, outPath, otherConfiguration);
                    break;
            }
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

            return cipher.doFinal(in);
//            FileUtils.writeBinary(encrypted, outPath);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        finally {
            return null;
        }
    }

    @Override
    protected void decrypt(String inPath, String keyPath, String outPath, Object... otherConfiguration) {
        try {
            byte[] input = FileUtils.readBinary(inPath);
            String key = FileUtils.readString(keyPath);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getBytes("UTF-8"));
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(input);
            FileUtils.writeBinary(encrypted, outPath);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
