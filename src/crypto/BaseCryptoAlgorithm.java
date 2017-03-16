package crypto;

import javax.crypto.Cipher;

/**
 * Created by tts on 3/16/17.
 */
public abstract class BaseCryptoAlgorithm {
    Cipher cipher;

    protected abstract void encrypt(String inPath, String keyPath, String outPath, Object... otherConfiguration);
    protected abstract void decrypt(String inPath, String keyPath, String outPath, Object... otherConfiguration);
}
