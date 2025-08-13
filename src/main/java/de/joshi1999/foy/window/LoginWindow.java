package de.joshi1999.foy.window;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private JButton loginButton;
    private JTextField hostField;
    private JTextField portField;
    private JLabel hostLabel;
    private JLabel portLabel;
    private JTextField usernameField;
    private JLabel usernameLabel;

    public LoginWindow() {
        setTitle("FOY - LOGIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setPreferredSize(new Dimension(350, 150));
        pack();
        setLocationRelativeTo(null);
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            login();
        });
        loginButton.setSize(300, 25);
        loginButton.setLocation(25,115);
        add(loginButton);
        loginButton.setVisible(true);
        hostField = new JTextField();
        hostField.setSize(250, 25);
        hostField.setLocation(25,30);
        add(hostField);
        hostField.setVisible(true);
        portField = new JTextField();
        portField.setSize(50, 25);
        portField.setLocation(275,30);
        portField.setVisible(true);
        add(portField);
        hostLabel = new JLabel("Host:");
        hostLabel.setSize(250, 25);
        hostLabel.setLocation(25,05);
        add(hostLabel);
        portLabel = new JLabel("Port:");
        portLabel.setSize(250, 25);
        portLabel.setLocation(275,05);
        add(portLabel);
        usernameField = new JTextField();
        usernameField.setSize(300, 25);
        usernameField.setLocation(25,80);
        usernameField.setVisible(true);
        add(usernameField);
        usernameLabel = new JLabel("Username:");
        usernameLabel.setSize(300, 25);
        usernameLabel.setLocation(25,55);
        usernameLabel.setVisible(true);
        add(usernameLabel);
        setVisible(true);
    }

    private void login() {

    }
}
