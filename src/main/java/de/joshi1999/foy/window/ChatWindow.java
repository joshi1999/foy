package de.joshi1999.foy.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChatWindow extends JFrame {
    JTextField inputField;
    JTextPane chat;
    JScrollPane chatScroll;
    DefaultListModel<String> users;
    JList<String> userList;
    JScrollPane userScroll;
    JComboBox<String> channelBox;
    JButton channelChangeButton;

    public ChatWindow() {
        setTitle("FOY - #welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(700, 500));
        pack();
        setLayout(null);
        setLocationRelativeTo(null);
        inputField = new JTextField();
        inputField.setSize(getContentPane().getPreferredSize().width, 25);
        inputField.setLocation(getContentPane().getX(), getContentPane().getHeight() - 25);
        inputField.setVisible(true);
        add(inputField);
        chat = new JTextPane();
        chat.setEditable(false);
        chat.setText("Hello :>");
        chatScroll = new JScrollPane(chat);
        add(chatScroll);
        chat.setVisible(true);
        chatScroll.setLocation(getContentPane().getX(), getContentPane().getY());
        chatScroll.setSize(getContentPane().getWidth() - 200, getContentPane().getHeight() - 25);
        chatScroll.setVisible(true);
        chat.getCaret().setVisible(false);
        chat.setFocusable(false);
        users = new DefaultListModel<>();
        userList = new JList<>(users);
        userList.setVisible(true);
        users.add(users.size(), "James");
        users.add(users.size(), "joshi1999");
        userScroll = new JScrollPane(userList);
        userList.setVisible(true);
        userScroll.setSize(200, getContentPane().getHeight() - 50);
        userScroll.setLocation(getContentPane().getWidth() - 200, getContentPane().getY());
        userScroll.setVisible(true);
        add(userScroll);
        channelBox = new JComboBox<>();
        channelBox.addItem("#welcome");
        channelBox.addItem("#manicdigger");
        channelBox.setSize(150, 25);
        channelBox.setLocation(getContentPane().getWidth() - 200, getContentPane().getHeight() - 50);
        channelBox.setVisible(true);
        add(channelBox);
        channelChangeButton = new JButton("GO");
        channelChangeButton.setMargin(new Insets(0, 0, 0, 0));
        channelChangeButton.addActionListener(e -> {
            changeChannel();
        });
        channelChangeButton.setSize(50, 25);
        channelChangeButton.setLocation(getContentPane().getWidth() - 50, getContentPane().getHeight() - 50);
        channelChangeButton.setVisible(true);
        add(channelChangeButton);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSizes();
            }
        });
        setVisible(true);
    }

    private void changeChannel() {

    }

    private void updateSizes() {
        inputField.setSize(getContentPane().getWidth(), 25);
        inputField.setLocation(getContentPane().getX(), getContentPane().getHeight() - 25);
        chatScroll.setLocation(getContentPane().getX(), getContentPane().getY());
        chatScroll.setSize(getContentPane().getWidth() - 200, getContentPane().getHeight() - 25);
        userScroll.setSize(200, getContentPane().getHeight() - 50);
        userScroll.setLocation(getContentPane().getWidth() - 200, getContentPane().getY());
        channelBox.setSize(150, 25);
        channelBox.setLocation(getContentPane().getWidth() - 200, getContentPane().getHeight() - 50);
        channelChangeButton.setSize(50, 25);
        channelChangeButton.setLocation(getContentPane().getWidth() - 50, getContentPane().getHeight() - 50);
    }
}
