package ui;

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

    private static final String[] comboBoxAlgorithm = {
            "AES",
            "DES"
    };

    private JPanel parentPanel;
    private JRadioButton decryptRadioButton;
    private JRadioButton encryptRadioButton;
    private JComboBox algorithm_combo_box;
    private JProgressBar progressBar_status;
    private JFormattedTextField txt_input_path;
    private JFormattedTextField txt_key_path;
    private JButton btn_start;
    private JButton btn_browse_input;
    private JButton btn_browse_key;
    private JFormattedTextField txt_output_path;
    private JButton btn_browse_output;

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case BROWSE_INPUT_ACTION_COMMAND:
                    String inputPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (inputPath != null) {
                        txt_input_path.setText(inputPath);
                    }
                    break;
                case BROWSE_KEY_ACTION_COMMAND:
                    String keyPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (keyPath != null) {
                        txt_key_path.setText(keyPath);
                    }
                    break;
                case BROWSE_OUTPUT_ACTION_COMMAND:
                    String outputPath = MainWindowHelper.openFileChooser(parentPanel);
                    if (outputPath != null) {
                        txt_key_path.setText(outputPath);
                    }
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

        // populate combobox
        algorithm_combo_box.setModel(new DefaultComboBoxModel(comboBoxAlgorithm));
    }

    public MainWindow() {
        super("Secure File Sharing");
        setContentPane(parentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        // register listener
        initComponents();

        setVisible(true);
    }
}
