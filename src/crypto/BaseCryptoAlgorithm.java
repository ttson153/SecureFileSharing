package crypto;

import javax.crypto.Cipher;

/**
 * Created by tts on 3/16/17.
 */
public abstract class BaseCryptoAlgorithm {
    Cipher cipher;

    public abstract void doAction(String inPath, String outPath, Object... otherConfiguration);
    protected abstract byte[] action(byte[] in, boolean isLastBlock);
}
