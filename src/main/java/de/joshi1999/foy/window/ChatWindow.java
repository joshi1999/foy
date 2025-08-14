package de.joshi1999.foy.window;

import com.google.common.collect.ImmutableList;
import de.joshi1999.foy.listener.ChatListener;
import org.pircbotx.ChannelListEntry;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ChatWindow extends JFrame {
    JTextField inputField;
    JEditorPane chat;
    JScrollPane chatScroll;
    DefaultListModel<String> users;
    JList<String> userList;
    JScrollPane userScroll;
    JComboBox<String> channelBox;
    JButton channelChangeButton;
    Color bgColor = new Color(237, 185, 86);

    String history = "";
    Set<String> usersOfChannel;

    private PircBotX bot;
    private ChatListener listener;
    private String channel = "#joshi1999";

    public ChatWindow(String username, String host, int port) {

        listener =  new ChatListener(this);

        usersOfChannel = new HashSet<>();

        Configuration config = new Configuration.Builder()
                .setName(username)
                .setRealName(username)
                .setLogin("FOY")
                .setAutoNickChange(true)
                .addAutoJoinChannel(channel)
                .addServer(host, port)
                .setSocketFactory(new UtilSSLSocketFactory().disableDiffieHellman())
                .addListener(listener)
                .buildConfiguration();

        bot = new PircBotX(config);

        setTitle("FOY - " + channel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(700, 500));
        pack();
        setLayout(null);
        setLocationRelativeTo(null);
        inputField = new JTextField();
        inputField.setSize(getContentPane().getPreferredSize().width, 25);
        inputField.setLocation(getContentPane().getX(), getContentPane().getHeight() - 25);
        inputField.addActionListener(e -> {
            sendMessage(inputField.getText());
            inputField.setText("");
        });
        inputField.setVisible(true);
        add(inputField);
        chat = new JEditorPane();
        chat.setEditable(false);
        chat.setContentType("text/html");
        chat.setText("<html><head><style>body { font-family: Arial; font-size: 12.5px; }</style></head><body>" + history + "</body></html>");
        chat.setBackground(bgColor);
        chat.setFont(new Font("Arial", Font.PLAIN, 12));
        chatScroll = new JScrollPane(chat);
        add(chatScroll);
        chatScroll.setAutoscrolls(true);
        chat.setVisible(true);
        chatScroll.setLocation(getContentPane().getX(), getContentPane().getY());
        chatScroll.setSize(getContentPane().getWidth() - 200, getContentPane().getHeight() - 25);
        chatScroll.setVisible(true);
        chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chat.getCaret().setVisible(false);
        chat.setFocusable(false);
        users = new DefaultListModel<>();
        users.add(users.size(), "James");
        userList = new JList<>(users);
        userList.setBackground(bgColor);
        userList.setVisible(true);
        userScroll = new JScrollPane(userList);
        userList.setVisible(true);
        userScroll.setSize(200, getContentPane().getHeight() - 50);
        userScroll.setLocation(getContentPane().getWidth() - 200, getContentPane().getY());
        userScroll.setVisible(true);
        add(userScroll);
        channelBox = new JComboBox<>();
        channelBox.setBackground(bgColor);
        channelBox.addItem(channel);
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

        Thread thread = new Thread(() -> {
            try {
                bot.startBot();
            } catch (IOException | IrcException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    private void changeChannel() {
        String selected = channelBox.getSelectedItem().toString();
        changeChannel(new String[]{selected});
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
        this.repaint();
    }

    public void retrieveChannels() {
        bot.sendIRC().listChannels();
    }

    public void setBot(PircBotX bot) {
        this.bot = bot;
    }

    public void receiveMessage(String user, String message) {
        history += "<b>" + user + ":</b> " + message + "<br>";
        chat.setText("<html><head><style>body { font-family: Arial; }</style></head><body>" + history + "</body></html>");
        chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
    }

    public void sendMessage(String message) {
        if (message.startsWith("/")) {
            // It's a command.
            executeCommand(message);
            return;
        }
        if (!message.isEmpty()) {
            bot.sendIRC().message(channel, message);
            history += "<b>" + bot.getUserBot().getNick() + ":</b> " + message + "<br>";
            chat.setText("<html><head><style>body { font-family: Arial; }</style></head><body>" + history + "</body></html>");
            chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
        }
    }

    public void executeCommand(String message) {
        String command = message.replaceFirst("/", "");
        String cmd = command.split(" ")[0];
        String[] args = command.replaceFirst(cmd + " ", "").split(" ");
        switch (cmd) {
            case "list": requestChannelList();
                return;
            case "go": changeChannel(args);
                return;
            default:
        }
    }

    private void changeChannel(String args[]) {
        if (args.length >= 1) {
            bot.sendIRC().joinChannel(args[0]);
            bot.sendRaw().rawLine("PART " + channel);
            channel = args[0];
            history = "";
            users.removeAllElements();
            chat.setText("<html><head><style>body { font-family: Arial; }</style></head><body>" + history + "</body></html>");
            setTitle("FOY - " + channel);
        }
    }

    private void requestChannelList() {
        bot.sendIRC().listChannels();
    }

    public void addUser(String user) {
        usersOfChannel.add(user);
        if (!users.contains(user)) {
            users.addElement(user);
        }
    }

    public void removeUser(String user) {
        usersOfChannel.remove(user);
        users.removeElement(user);
    }

    public void renewUsers() {
        users.clear();
        users.add(users.size(), "James");
        for (String s : usersOfChannel) {
            users.add(users.size(), s);
        }
    }

    public void renewChannels(ImmutableList<ChannelListEntry> list) {
        channelBox.removeAll();
        for (ChannelListEntry e : list) {
            channelBox.addItem(e.getName());
        }
        channelBox.setSelectedItem(channel);
        receiveMessage("James", "Jungchen, die Channels sind da :)");
    }
}
