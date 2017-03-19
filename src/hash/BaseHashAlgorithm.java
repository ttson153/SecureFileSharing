package hash;

import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;

/**
 * Created by tts on 3/19/17.
 */
public abstract class BaseHashAlgorithm {
    MessageDigest md;

    public BaseHashAlgorithm() {
        for (Provider provider : Security.getProviders()) {
            System.out.println(provider.toString());
        }
    }

    public abstract void doAction(String inputPath, boolean saveToFile);
    public abstract void saveToFile(String inputPath);

    protected abstract byte[] digest(byte[] input);
}
