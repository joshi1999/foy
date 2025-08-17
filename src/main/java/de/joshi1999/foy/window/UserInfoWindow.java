package de.joshi1999.foy.window;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class UserInfoWindow extends JFrame {
    private JLabel userInfoLabel;

    public UserInfoWindow(String username) {
        setTitle("User Info - " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(null);

        userInfoLabel = new JLabel("DSGVO. Keine Auskunft.");

        userInfoLabel.setBounds(20, 20, 250, 120);
        add(userInfoLabel);

        setVisible(true);
    }

}
