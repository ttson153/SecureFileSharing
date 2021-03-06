package ui;

import connection.Client;
import crypto.ActionType;
import ui.custom.HintText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by tts on 3/12/17.
 */
public class MainWindow extends JFrame{

    private static final String BROWSE_INPUT_COMMAND = "Browse Input";
    private static final String BROWSE_OUTPUT_COMMAND = "Browse Output";
    private static final String BROWSE_KEY_COMMAND = "Browse Key";
    private static final String BROWSE_HASH_INPUT_COMMAND = "Browse Hash Input";
    private static final String BROWSE_HASH_STRING_COMMAND = "Browse Hash String";
    private static final String START_CRYPTO_COMMAND = "Start Crypto";
    private static final String START_HASH_COMMAND = "Start Hash";
    private static final String SAVE_HASH_COMMAND = "Save Hash";
    private static final String START_CHECKSUM_COMMAND = "Start Checksum";
    private static final String INIT_CONNECTION = "Init connection";
    private static final String SEND_REQUEST = "Send Request";

    private static final String[] cryptoAlgorithms = {
            "-- Choose algorithms --",
            "AES",
            "DES"
    };

    private static final String[] hashAlgorithms = {
            "-- Choose algorithms --",
            "MD5",
            "SHA-1",
            "SHA-256"
    };

    private JPanel parentPanel;
    private JTabbedPane tabbedPane1;

    private JPanel cryptoPanel;
    private JRadioButton decryptRadioButton;
    private JRadioButton encryptRadioButton;
    private JComboBox crypto_method_combo_box;
    private JProgressBar fileProgressBar;
    private JFormattedTextField txt_input_path;
    private JFormattedTextField txt_key_path;
    private JButton btn_start;
    private JButton btn_browse_input;
    private JButton btn_browse_key;
    private JFormattedTextField txt_output_path;
    private JButton btn_browse_output;
    private JProgressBar overallProgressBar;
    private JTextArea txtFileInfo;

    private JPanel checkSumPanel;
    private JFormattedTextField txt_hash_output;
    private JFormattedTextField txt_input_hash;
    private JButton btn_browse_input_hash;
    private JFormattedTextField txt_checksum_path;
    private JButton btn_browse_checksum_path;
    private JFormattedTextField txt_checksum_string;
    private JButton btn_start_hash;
    private JButton btn_save_hash_to_file;
    private JButton btn_checksum;
    private JComboBox hash_method_combo_box;
    private JLabel txtStatus;
    private JButton btn_request;
    private JTextArea txtMessage;
    private JFormattedTextField txtDestIP;
    private JFormattedTextField txtDestPort;
    private JFormattedTextField txtLocalPort;
    private JButton btn_init;
    private JTextField txt_key;
    private JPanel keyExchangePanel;
    private Client MyConnection = null;
    private MainWindow window = this;
    private HintText hintTextInput, hintTextKey, hintTextOutput;


    public void set_status(String status){
        txtStatus.setText(status);
    }

    public void set_message(String status){
        txtMessage.setText(status);
    }

    public String get_key(){ return txt_key.getText(); }

    public void set_key(String key){ txt_key.setText(key); }


    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            String inputPath, keyPath, outputPath;
            String inputHashPath, inputHashStringPath, hashAlgorithm, checksumPath, checksumString;
            switch (command) {
                case BROWSE_INPUT_COMMAND:
                    inputPath = MainWindowHelper.openFileChooser(cryptoPanel, JFileChooser.FILES_AND_DIRECTORIES);
                    if (inputPath != null) {
                        txt_input_path.setForeground(Color.BLACK);
                        txt_output_path.setForeground(Color.BLACK);
                        txt_input_path.setText(inputPath);
                        // set default output path
                        txt_output_path.setText(inputPath.substring(0, inputPath.lastIndexOf(File.separator) + 1));
                        pack();
                    }
                    break;
                case BROWSE_KEY_COMMAND:
                    keyPath = MainWindowHelper.openFileChooser(cryptoPanel, JFileChooser.FILES_ONLY);
                    if (keyPath != null) {
                        txt_key_path.setForeground(Color.BLACK);
                        txt_key_path.setText(keyPath);
                    }
                    break;
                case BROWSE_OUTPUT_COMMAND:
                    outputPath = MainWindowHelper.openFileChooser(cryptoPanel, JFileChooser.DIRECTORIES_ONLY);
                    if (outputPath != null) {
                        hintTextOutput.setGhostColor(Color.BLACK);
                        txt_output_path.setForeground(Color.BLACK);
                        txt_output_path.setText(outputPath + File.separator);
                    }
                    break;

                case BROWSE_HASH_INPUT_COMMAND:
                    inputHashPath = MainWindowHelper.openFileChooser(checkSumPanel, JFileChooser.FILES_ONLY);
                    if (inputHashPath != null) {
                        txt_input_hash.setText(inputHashPath);
                        // reset hash output
                        txt_hash_output.setText("");
                    }
                    break;

                case BROWSE_HASH_STRING_COMMAND:
                    inputHashStringPath = MainWindowHelper.openFileChooser(checkSumPanel, JFileChooser.FILES_ONLY);
                    if (inputHashStringPath != null) {
                        txt_checksum_path.setText(inputHashStringPath);
                    }
                    break;

                case START_CRYPTO_COMMAND:
                    // gather required information
                    inputPath = txt_input_path.getText();
                    keyPath = txt_key_path.getText();
                    outputPath = txt_output_path.getText();
                    String cryptoAlgorithm = String.valueOf(crypto_method_combo_box.getSelectedItem());
                    ActionType actionType = ActionType.ENCRYPT;
                    if (decryptRadioButton.isSelected()) {
                        actionType = ActionType.DECRYPT;
                    }

                    if (!cryptoAlgorithm.equals(cryptoAlgorithms[0])) {
                        MainWindowController.performCrypto(cryptoAlgorithm, actionType,
                                txtFileInfo, fileProgressBar, overallProgressBar,
                                inputPath, keyPath, outputPath);
                    } else {
                        JOptionPane.showMessageDialog(cryptoPanel, "Please choose an algorithm");
                    }
                    break;

                case START_HASH_COMMAND:
                    // gather required information
                    inputHashPath = txt_input_hash.getText();
                    hashAlgorithm = String.valueOf(hash_method_combo_box.getSelectedItem());

                    if (!hashAlgorithm.equals(hashAlgorithms[0])) {
                        MainWindowController.performHash(hashAlgorithm,
                                txt_hash_output,
                                inputHashPath);
                    } else {
                        JOptionPane.showMessageDialog(checkSumPanel, "Please choose an algorithm");
                    }
                    break;

                case SAVE_HASH_COMMAND:
                    // gather required information
                    inputHashPath = txt_input_hash.getText();
                    hashAlgorithm = String.valueOf(hash_method_combo_box.getSelectedItem());

                    if (!hashAlgorithm.equals(hashAlgorithms[0])) {
                        MainWindowController.performSaveHash(hashAlgorithm,
                                txt_hash_output,
                                inputHashPath);
                    } else {
                        JOptionPane.showMessageDialog(checkSumPanel, "Please choose an algorithm");
                    }
                    break;

                case START_CHECKSUM_COMMAND:
                    inputHashPath = txt_input_hash.getText();
                    checksumPath = txt_checksum_path.getText();
                    checksumString = txt_checksum_string.getText();
                    hashAlgorithm = String.valueOf(hash_method_combo_box.getSelectedItem());

                    if (!hashAlgorithm.equals(hashAlgorithms[0])) {
                        // inform user just select either path or string
                        if (checksumPath.equals("") && checksumString.equals("")) {
                            JOptionPane.showMessageDialog(checkSumPanel, "Please choose a path or a string");
                        } else if (!checksumPath.equals("") && !checksumString.equals("")) {
                            JOptionPane.showMessageDialog(checkSumPanel, "Please choose either path or string");
                        } else if (!checksumPath.equals("")) {
                            MainWindowController.performChecksum(hashAlgorithm,
                                    txt_hash_output, checkSumPanel,
                                    inputHashPath, checksumPath, true);
                        } else if (!checksumString.equals("")) {
                            MainWindowController.performChecksum(hashAlgorithm,
                                    txt_hash_output, checkSumPanel,
                                    inputHashPath, checksumString, false);
                        }
                    } else {
                        JOptionPane.showMessageDialog(checkSumPanel, "Please choose an algorithm");
                    }

                    break;

                case INIT_CONNECTION:
                    MyConnection = new Client(window, Integer.parseInt(txtLocalPort.getText()));
                    break;
                case SEND_REQUEST:
                    Integer destination_port = Integer.parseInt(txtDestPort.getText());
                    String destination_ip = txtDestIP.getText();
                    MyConnection.request_connect(destination_port, destination_ip);
                    break;
            }
        }
    }

    private void initComponents() {
        ButtonListener buttonListener = new ButtonListener();

        // SET LISTENER
        // crypto tab
        btn_browse_input.setActionCommand(BROWSE_INPUT_COMMAND);
        btn_browse_input.addActionListener(buttonListener);

        btn_browse_key.setActionCommand(BROWSE_KEY_COMMAND);
        btn_browse_key.addActionListener(buttonListener);

        btn_browse_output.setActionCommand(BROWSE_OUTPUT_COMMAND);
        btn_browse_output.addActionListener(buttonListener);

        btn_start.setActionCommand(START_CRYPTO_COMMAND);
        btn_start.addActionListener(buttonListener);

        // checksum tab
        btn_browse_input_hash.setActionCommand(BROWSE_HASH_INPUT_COMMAND);
        btn_browse_input_hash.addActionListener(buttonListener);

        btn_browse_checksum_path.setActionCommand(BROWSE_HASH_STRING_COMMAND);
        btn_browse_checksum_path.addActionListener(buttonListener);

        btn_start_hash.setActionCommand(START_HASH_COMMAND);
        btn_start_hash.addActionListener(buttonListener);

        btn_save_hash_to_file.setActionCommand(SAVE_HASH_COMMAND);
        btn_save_hash_to_file.addActionListener(buttonListener);

        btn_checksum.setActionCommand(START_CHECKSUM_COMMAND);
        btn_checksum.addActionListener(buttonListener);

        btn_init.setActionCommand(INIT_CONNECTION);
        btn_init.addActionListener(buttonListener);

        btn_request.setActionCommand(SEND_REQUEST);
        btn_request.addActionListener(buttonListener);

        // POPULATE COMBOBOX
        crypto_method_combo_box.setModel(new DefaultComboBoxModel(cryptoAlgorithms));
        hash_method_combo_box.setModel(new DefaultComboBoxModel(hashAlgorithms));

        hintTextInput = new HintText(txt_input_path, "File or Folder to be processed");
        hintTextKey = new HintText(txt_key_path, "Path to key file");
        hintTextOutput = new HintText(txt_output_path, "Destination Folder");
    }

    public MainWindow() {
        super("Secure File Sharing");
        setContentPane(parentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // register listener
        initComponents();

        pack();
        setVisible(true);
    }
}
