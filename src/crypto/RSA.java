package crypto;

/**
 * Created by D.luffy on 3/22/2017.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RSA{

    //generate keys, save them to files:
    static{
        try {

            generateKeys();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static String callEncrypt(String path_file_encrypt)
    {
        try {

            encrypt(path_file_encrypt);

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "RSA_Encrypt";
    }
    public static String callDecrypt(String path_file_decrypt)
    {
        try {
            decrypt(path_file_decrypt);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "_RSA_Decrypt_";
    }




    public static void generateKeys() throws Exception {
        //String workingDir2 = System.getProperty("user.dir");
        //System.out.println("Current working directory2 : " + workingDir2);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        System.out.println("keys created");

        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
                RSAPrivateKeySpec.class);

        saveToFile("RSA_public.key", pub.getModulus(), pub.getPublicExponent());
        saveToFile("RSA_private.key", priv.getModulus(), priv.getPrivateExponent());

        System.out.println("keys saved");
    }

    public static void saveToFile(String fileName, BigInteger mod,
                                  BigInteger exp) throws IOException {
        ObjectOutputStream fileOut = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        try {
            fileOut.writeObject(mod);
            fileOut.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error");
        } finally {
            fileOut.close();
            System.out.println("Closed writing file.");
        }
    }

    // Return the saved key
    static Key readKeyFromFile(String keyFileName) throws IOException {
        InputStream in = new FileInputStream(keyFileName);

        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(
                in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            if (keyFileName.startsWith("RSA_public"))
                return fact.generatePublic(new RSAPublicKeySpec(m, e));
            else
                return fact.generatePrivate(new RSAPrivateKeySpec(m, e));
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
            System.out.println("Closed reading file.");
        }
    }

    // Use this PublicKey object to initialize a Cipher and encrypt some data
    public static void encrypt(String file_loc)
            throws Exception {
        byte[] data = new byte[32];
        int i;

        String file_des = file_loc + ".rsa_encrypt";
        System.out.println("start encyption");

        Key pubKey = readKeyFromFile("RSA_public.key");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        FileInputStream fileIn = new FileInputStream(file_loc);
        FileOutputStream fileOut = new FileOutputStream(file_des);
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);

        // Read in the data from the file and encrypt it
        while ((i = fileIn.read(data)) != -1) {
            cipherOut.write(data, 0, i);
        }

        // Close the encrypted file
        cipherOut.close();
        fileIn.close();

        System.out.println("encrypted file created");
    }

    // Use this PublicKey object to initialize a Cipher and decrypt some data
    public static void decrypt(String file_loc)
            throws Exception {
        byte[] data = new byte[32];
        int i;

        String file_des = file_loc + ".rsa_decrypt";
        System.out.println("start decyption");

        Key priKey = readKeyFromFile("RSA_private.key");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        FileInputStream fileIn = new FileInputStream(file_loc);
        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
        FileOutputStream fileOut = new FileOutputStream(file_des);

        // Write data to new file
        while ((i = cipherIn.read()) != -1) {
            fileOut.write(i);
        }

        // Close the file
        fileIn.close();
        cipherIn.close();
        fileOut.close();

        System.out.println("decrypted file created");

    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), UTF_8);
    }

    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return Base64.getEncoder().encodeToString(spec.getEncoded());
    }

    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }
}
