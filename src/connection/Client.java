package connection;

/**
 * Created by D.luffy on 3/21/2017.
 */

import crypto.RSA;
import crypto.SymmetricCrypto;
import ui.MainWindow;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;

public class Client {
    private Integer local_port;
    private Socket sock_message = null;
    private Socket sock_file = null;
    private BufferedReader input;
    private String FileName;
    private Boolean isServer;
    private Server server;
    private Thread thread_server;
    private MainWindow window;
    private KeyPair mykey = null;
    private SecretKey exchangeKey = null;

    public Client(MainWindow window, Integer local_port){
        this.window = window;
        this.local_port = local_port;
        this.isServer = true;
        this.exchangeKey = SymmetricCrypto.randomKey();
        window.set_key(SymmetricCrypto.saveSecretKey(exchangeKey));
        server = new Server(window, local_port);
        try {
            mykey = RSA.generateKeyPair();
        }

        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        thread_server = new Thread(server);
        thread_server.start();
    }

    public Socket get_socket_file(){
        if (isServer)
            return server.get_socket_file();
        else
            return sock_file;
    }

    public Socket get_socket_message(){
        if (isServer)
            return server.get_socket_message();
        else
            return sock_message;
    }

    public void request_connect(Integer destination_port, String destination_ip){
        try
        {
            System.out.println("Connecting to " + destination_ip + " on port " + destination_port);
            sock_file = new Socket(destination_ip, destination_port);
            sock_message = new Socket(destination_ip, destination_port);
            window.set_status("Just connected to " + sock_file.getRemoteSocketAddress());
            OutputStream outToServer = sock_message.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            InputStream inFromServer = sock_message.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
//            String publicK = new String(Base64.getDecoder().decode(mykey.getPublic().getEncoded()));
            String publicK =  RSA.savePublicKey(mykey.getPublic());


//            out.writeUTF("Hello from "+ sock_message.getLocalSocketAddress());
            out.writeUTF(publicK);
            String message = in.readUTF();
            String decrypted = RSA.decrypt(message, mykey.getPrivate());
//            window.set_message(message);
            window.set_message("Key from other: "+ decrypted);
            System.out.println("Server says " + message);

            isServer = false;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
