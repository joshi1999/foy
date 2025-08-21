package de.joshi1999.foy.window;

import org.pircbotx.hooks.events.WhoisEvent;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;

public class UserInfoWindow extends JFrame {
    private JEditorPane userInfo;

    public UserInfoWindow(String username, ChatWindow parentWindow, Color backgroundColor) {
        setTitle("User Info - " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(500, 600));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(backgroundColor);

        userInfo = new JEditorPane();
        userInfo.setContentType("text/html");
        userInfo.setBackground(backgroundColor);
        userInfo.setEditable(false);

        userInfo.setText("<html><body>" +
                "<b>Loading user info for " + username + "...</b>" +
                "</body></html>");

        userInfo.setBounds(20, 20, 460, 560);
        add(userInfo);

        setVisible(true);

        parentWindow.getBot().sendIRC().whois(username);
    }

    public void completeUserInfo(WhoisEvent event) {
        String info = "<html><body>" + "<b>Username:</b> " + event.getNick() + "<br>" +
                "<b>Real Name:</b> " + event.getRealname() + "<br>" +
                "<b>Hostname:</b> " + event.getHostname() + "<br>" +
                "<b>Server:</b> " + event.getServer() + "<br>" +
                "<b>Channels:</b> " + String.join(", ", event.getChannels()) + "<br>" +
                "</body></html>";

        userInfo.setText(info);
        userInfo.setBounds(20, 20, 450, 120);
        userInfo.revalidate();
        userInfo.repaint();
    }
}
