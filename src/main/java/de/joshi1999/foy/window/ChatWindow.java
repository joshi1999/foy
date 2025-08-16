package de.joshi1999.foy.window;

import com.google.common.collect.ImmutableList;
import de.joshi1999.foy.command.CommandDispatcher;
import de.joshi1999.foy.command.builtin.GoCommand;
import de.joshi1999.foy.command.builtin.ListCommand;
import de.joshi1999.foy.command.builtin.MsgCommand;
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
    // Swing Components
    JTextField inputField;
    JEditorPane chat;
    JScrollPane chatScroll;
    DefaultListModel<String> users;
    JList<String> userList;
    JScrollPane userScroll;
    JComboBox<String> channelBox;
    JButton channelChangeButton;
    Color bgColor = new Color(237, 185, 86);

    // Swing helpers
    String history = "";
    Set<String> usersOfChannel;

    private PircBotX bot;

    private ChatListener listener;
    private CommandDispatcher commandDispatcher;

    private String channel = "#joshi1999";

    public ChatWindow(String username, String host, int port) {
        listener = new ChatListener(this);
        commandDispatcher = new CommandDispatcher(this);
        registerCommands();

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
        postHistoryToScreen();
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
        users = new DefaultListModel<>();
        users.add(users.size(), "James");
        userList = new JList<>(users);
        userList.setBackground(bgColor);
        userList.setVisible(true);
        userList.setFont(new Font("Arial", Font.PLAIN, 16));
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

    private void registerCommands() {
        commandDispatcher.registerCommand("list", new ListCommand(this));
        commandDispatcher.registerCommand("go", new GoCommand(this));
        commandDispatcher.registerCommand("msg", new MsgCommand(this));
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    private void changeChannel() {
        SwingUtilities.invokeLater(() -> {
            String selected = channelBox.getSelectedItem().toString();
            changeChannel(selected);
        });
    }

    private void updateSizes() {
        SwingUtilities.invokeLater(() -> {
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
        });
    }

    public void retrieveChannels() {
        bot.sendIRC().listChannels();
    }

    public void postHistoryToScreen() {
        SwingUtilities.invokeLater(() -> {
            chat.setText("<html><head><style>body { font-family: Arial; font-size: 12px; background-image: url('https://knuddels-wiki.de/images/8/82/Background_D%C3%BCsseldorf.png'); background-repeat: no-repeat; background.attachment: fixed; background-size: cover; background-position: center center; background-attachment: fixed; }</style></head><body>" + history + "</body></html>");
        });
    }

    private void clearChat() {
        SwingUtilities.invokeLater(() -> {
            history = "";
            postHistoryToScreen();
        });
    }

    private void postMessageToScreen(String string) {
        SwingUtilities.invokeLater(() -> {
            history += string;
            history += "<br></br>";
            postHistoryToScreen();
            int x;
            chat.selectAll();
            x = chat.getSelectionEnd();
            chat.select(x, x);
        });
    }

    public void receiveMessage(String user, String message) {
        SwingUtilities.invokeLater(() -> {
            String receivedMessage = "<b>" + user + ":</b></font> " + message;
            postMessageToScreen(receivedMessage);
        });
    }

    public void receivePrivateMessage(String user, String message) {
        SwingUtilities.invokeLater(() -> {
            String receivedMessage = "<font color=\"red\"><b>" + user + " (privat):</b> " + message;
            postMessageToScreen(receivedMessage);
        });
    }

    public void sendMessage(String message) {
        if (message.startsWith("/")) {
            // It's a command.
            commandDispatcher.dispatch(message);
            return;
        }
        if (!message.isEmpty()) {
            bot.sendIRC().message(channel, message);
            SwingUtilities.invokeLater(() -> {
                String sendingMessage = "<b>" + bot.getUserBot().getNick() + ":</b> " + message;
                postMessageToScreen(sendingMessage);
            });
        }
    }

    public void sendPrivateMessage(String user, String message) {
        if (!message.isEmpty()) {
            bot.sendIRC().message(user, message);
            SwingUtilities.invokeLater(() -> {
                String sendingMessage = "<font color=\"red\"><b>" + bot.getUserBot().getNick() + " (privat an " + user + "):</b></font> " + message;
                postMessageToScreen(sendingMessage);
            });
        }
    }

    public void changeChannel(String newChannel) {
        SwingUtilities.invokeLater(() -> {
            clearChat();
            users.removeAllElements();
            bot.sendIRC().joinChannel(newChannel);
            bot.sendRaw().rawLine("PART " + channel);
            channel = newChannel;
            setTitle("FOY - " + channel);
        });
    }

    public void requestChannelList() {
        bot.sendIRC().listChannels();
    }

    public void addUser(String user) {
        SwingUtilities.invokeLater(() -> {
            usersOfChannel.add(user);
            if (!users.contains(user)) {
                users.addElement(user);
            }
        });
    }

    public void removeUser(String user) {
        SwingUtilities.invokeLater(() -> {
            usersOfChannel.remove(user);
            users.removeElement(user);
        });
    }

    public void renewUsers() {
        SwingUtilities.invokeLater(() -> {
            users.clear();
            users.add(users.size(), "James");
            for (String s : usersOfChannel) {
                users.add(users.size(), s);
            }
        });
    }

    public void renewChannels(ImmutableList<ChannelListEntry> list) {
        SwingUtilities.invokeLater(() -> {
            channelBox.removeAll();
            for (ChannelListEntry e : list) {
                channelBox.addItem(e.getName());
            }
            channelBox.setSelectedItem(channel);
            receiveMessage("James", "Jungchen, die Channels sind da :)");
        });
    }

    public void userJoin(String user) {
        SwingUtilities.invokeLater(() -> {
            String message = "<font color=\"blue\">*** <b>" + user + "</b> eilt herbei.</font>";
            postMessageToScreen(message);
        });
    }

    public void userLeave(String user) {
        SwingUtilities.invokeLater(() -> {
            String message = "<font color=\"blue\">*** " + user + " hat uns verlassen.</font>";
            postMessageToScreen(message);
        });
    }
}
