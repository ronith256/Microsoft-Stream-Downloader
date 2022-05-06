import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;

public class GUI {
    static JFrame frame;
    JButton downloadButton;
    static JTextField urlBox, emailField;
    static JPasswordField passwordField;
    JPanel panel;
    static JLabel label, statusLabel;
    public static JProgressBar jp;
    static String fileName = "Output";
    GUI(){
            JFrame.setDefaultLookAndFeelDecorated(true);
            frame = new JFrame("Stream Downloader");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(380,420);
            panel = new JPanel(null);

            JLabel email = new JLabel("E-Mail");
            JLabel password = new JLabel("Password");
            emailField = new JTextField("Enter E-Mail Here");
            passwordField = new JPasswordField();
            emailField.setBounds(80,90,200,20);
            passwordField.setBounds(80,130,200,20);
            email.setBounds(35, 90,50,20);
            password.setBounds(20,130,50,20);
            panel.add(password);
            panel.add(emailField);
            panel.add(passwordField);
            label = new JLabel("Microsoft Stream Downloader");
            label.setBounds(20, 20,400,50);
            label.setFont(new Font("Comic Sans", Font.BOLD, 22));
            urlBox = new JTextField("Enter URL here");
            urlBox.setBounds(80,180,200,20);
            downloadButton =  new JButton("Download");
            downloadButton.setBounds(140, 215,90,20);

            // Connecting GUI to back end
            jp = new JProgressBar(0,100);
            jp.setBounds(105,300,160, 30);
            jp.setValue(0);
            jp.setStringPainted(true);
            panel.add(jp);
            jp.setVisible(false);
            statusLabel = new JLabel();
            statusLabel.setFont(new Font("",Font.BOLD,15));
            statusLabel.setBounds(138,250,110,20);

            downloadButton.addActionListener(e -> new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadButton.setEnabled(false);
                    fileName = JOptionPane.showInputDialog(frame, "", "Enter File Name", JOptionPane.INFORMATION_MESSAGE);
                    if(!fileName.contains(".mp4")){
                        fileName = fileName+".mp4";
                    }
                    new StreamDL(urlBox.getText(), emailField.getText(), String.valueOf(passwordField.getPassword()));
                    downloadButton.setEnabled(true);
                    jp.setValue(0);
                }
            }).start());

            panel.add(statusLabel);
            panel.add(email);
            panel.add(label);
            panel.add(urlBox);
            panel.add(downloadButton);
            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            new Config().config();
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        new GUI();
    }
}
