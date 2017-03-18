package ui;

import crypto.ActionType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tts on 3/12/17.
 */
public class MainWindow extends JFrame{

    private static final String BROWSE_INPUT_ACTION_COMMAND = "Browse Input";
    private static final String BROWSE_OUTPUT_ACTION_COMMAND = "Browse Output";
    private static final String BROWSE_KEY_ACTION_COMMAND = "Browse Key";
    private static final String START_ACTION_COMMAND = "Start Action";

    private static final String[] comboBoxAlgorithm = {
            "-- Choose algorithms --",
            "AES",
            "DES"
    };

    private JPanel parentPanel;
    private JRadioButton decryptRadioButton;
    private JRadioButton encryptRadioButton;
    private JComboBox algorithmComboBox;
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

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            String inputPath, keyPath, outputPath;
            switch (command) {
                case BROWSE_INPUT_ACTION_COMMAND:
                    inputPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (inputPath != null) {
                        txt_input_path.setText(inputPath);

                        // set default output path
                        if (decryptRadioButton.isSelected()) {
                            txt_output_path.setText(inputPath + ".decrypted");
                        } else {
                            txt_output_path.setText(inputPath + ".encrypted");
                        }
                        pack();
                    }
                    break;
                case BROWSE_KEY_ACTION_COMMAND:
                    keyPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (keyPath != null) {
                        txt_key_path.setText(keyPath);
                    }
                    break;
                case BROWSE_OUTPUT_ACTION_COMMAND:
                    outputPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (outputPath != null) {
                        txt_output_path.setText(outputPath);
                    }
                    break;
                case START_ACTION_COMMAND:
                    // gather required information
                    inputPath = txt_input_path.getText();
                    keyPath = txt_key_path.getText();
                    outputPath = txt_output_path.getText();
                    String algorithm = String.valueOf(algorithmComboBox.getSelectedItem());
                    ActionType actionType = ActionType.ENCRYPT;
                    if (decryptRadioButton.isSelected()) {
                        actionType = ActionType.DECRYPT;
                    }

                    MainWindowController.performAction(algorithm, actionType, fileProgressBar, inputPath, keyPath, outputPath);
                    break;
            }
        }
    }

    private void initComponents() {
        // set listener
        btn_browse_input.setActionCommand(BROWSE_INPUT_ACTION_COMMAND);
        btn_browse_input.addActionListener(new ButtonListener());

        btn_browse_key.setActionCommand(BROWSE_KEY_ACTION_COMMAND);
        btn_browse_key.addActionListener(new ButtonListener());

        btn_browse_output.setActionCommand(BROWSE_OUTPUT_ACTION_COMMAND);
        btn_browse_output.addActionListener(new ButtonListener());

        btn_start.setActionCommand(START_ACTION_COMMAND);
        btn_start.addActionListener(new ButtonListener());

        // populate combobox
        algorithmComboBox.setModel(new DefaultComboBoxModel(comboBoxAlgorithm));
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
