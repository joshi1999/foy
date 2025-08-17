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
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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
    BackgroundPanel backgroundPanel;
    //Color bgColor = new Color(170, 201, 127);
    Color bgColor = new Color(237, 185, 86);
    String backgroundURL = "https://knuddels-wiki.de/images/8/82/Background_D%C3%BCsseldorf.png"; // Background image URL

    // Swing helpers
    String history = "";
    Set<String> usersOfChannel;

    private HashMap<String, Theme> themes;

    private PircBotX bot;

    private ChatListener listener;
    private CommandDispatcher commandDispatcher;

    private String channel = "#FOYClient";

    public ChatWindow(String username, String host, int port) {
        themes = new HashMap<>();
        themes.put("#greensurvivors", new Theme(new Color(170, 201, 127), "https://greensurvivors.de/wp-content/uploads/2018/08/Logo_1000.png"));
        themes.put("#default", new Theme(new Color(237, 185, 86), "https://knuddels-wiki.de/images/8/82/Background_D%C3%BCsseldorf.png"));

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
        //chat.setBackground(bgColor);
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
        chatScroll.setOpaque(true);
        chat.setOpaque(true);
        chat.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        chatScroll.setBackground(new Color(0, 0, 0, 0));
        backgroundPanel = new BackgroundPanel(bgColor, backgroundURL);
        backgroundPanel.setBackground(bgColor);
        backgroundPanel.setSize(getContentPane().getWidth() - 200, getContentPane().getHeight() - 25);
        backgroundPanel.setLocation(getContentPane().getX(), getContentPane().getY());
        backgroundPanel.setVisible(true);
        add(backgroundPanel);
        users = new DefaultListModel<>();
        users.add(users.size(), "James");
        userList = new JList<>(users);
        userList.setBackground(bgColor);
        userList.setVisible(true);
        userList.setFocusable(true);
        userList.setInheritsPopupMenu(true);
        userList.setFont(new Font("Arial", Font.PLAIN, 16));
        userScroll = new JScrollPane(userList);
        userList.setVisible(true);
        userScroll.setSize(200, getContentPane().getHeight() - 50);
        userScroll.setLocation(getContentPane().getWidth() - 200, getContentPane().getY());
        userScroll.setVisible(true);
        // MouseListener jetzt auf userList statt userScroll setzen
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupIfRightClick(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupIfRightClick(e);
            }

            private void showPopupIfRightClick(MouseEvent e) {
                if (e.isPopupTrigger() && userList.locationToIndex(e.getPoint()) != -1) {
                    userList.setSelectedIndex(userList.locationToIndex(e.getPoint()));
                    UserPopup popup = new UserPopup(userList);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
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
        chat.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateSizes();
            }
        });

        Thread thread = new Thread(() -> {
            try {
                bot.startBot();
            } catch (IOException | IrcException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        applyTheme(channel);
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
            if (channelBox.getSelectedItem() == null) {
                return;
            }
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
            backgroundPanel.setSize(getContentPane().getWidth() - 200, getContentPane().getHeight() - 25);
            backgroundPanel.setLocation(getContentPane().getX(), getContentPane().getY());
            userScroll.setSize(200, getContentPane().getHeight() - 50);
            userScroll.setLocation(getContentPane().getWidth() - 200, getContentPane().getY());
            channelBox.setSize(150, 25);
            channelBox.setLocation(getContentPane().getWidth() - 200, getContentPane().getHeight() - 50);
            channelChangeButton.setSize(50, 25);
            channelChangeButton.setLocation(getContentPane().getWidth() - 50, getContentPane().getHeight() - 50);
            backgroundPanel.revalidate();
            backgroundPanel.repaint();
            chatScroll.revalidate();
            userScroll.revalidate();
            chatScroll.repaint();
            userScroll.repaint();
        });
    }

    public void postHistoryToScreen() {
        SwingUtilities.invokeLater(() -> {
            chat.setText("<html><head><style>body { font-family: Arial; font-size: 12px; }</style></head><body>" + history + "</body></html>");
            updateSizes();
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
        message = message.trim();
        if (message.startsWith("/")) {
            // It's a command.
            commandDispatcher.dispatch(message);
            return;
        }
        String finalMessage = message;
        if (!message.isEmpty()) {
            bot.sendIRC().message(channel, message);
            SwingUtilities.invokeLater(() -> {
                String sendingMessage = "<b>" + bot.getUserBot().getNick() + ":</b> " + finalMessage;
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
            if (newChannel == null || newChannel.isEmpty()) {
                return;
            }
            if (newChannel.equals(channel)) {
                return;
            }
            clearChat();
            users.clear();
            userList.removeAll();
            usersOfChannel.clear();
            bot.sendIRC().joinChannel(newChannel);
            bot.sendRaw().rawLine("PART " + channel);
            channel = newChannel;
            setTitle("FOY - " + channel);
            channelBox.setSelectedItem(channel);
            if (!channelBox.getSelectedItem().equals(channel)) {
                channelBox.addItem(channel);
                channelBox.setSelectedItem(channel);
            }
            applyTheme(channel);
        });
    }

    public void applyTheme(String channel) {
        Theme theme = themes.get(channel);
        if (theme == null) {
            // If no theme is defined for the channel, use the default theme
            theme = themes.get("#default");
        }
        bgColor = theme.getBackgroundColor();
        backgroundURL = theme.getBackgroundURL();
        backgroundPanel.setBgColor(theme.getBackgroundColor());
        backgroundPanel.setBackgroundURL(theme.getBackgroundURL());
        userList.setBackground(theme.getBackgroundColor());
        channelBox.setBackground(theme.getBackgroundColor());
        updateSizes();

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
            users.addAll(usersOfChannel);
        });
    }

    public void renewChannels(ImmutableList<ChannelListEntry> list) {
        SwingUtilities.invokeLater(() -> {
            channelBox.removeAll();
            // Sort channels
            List<ChannelListEntry> sortedList = new ArrayList<>();
            list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
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

class UserPopup extends JPopupMenu implements ActionListener {
    String user;
    final JMenuItem info;
    final JMenuItem privateMessaging;

    UserPopup(JList<String> users) {
        this.user = users.getSelectedValue();
        info = new JMenuItem("Info");
        info.addActionListener(this);
        privateMessaging = new JMenuItem("PM");
        privateMessaging.addActionListener(this);
        add(info);
        add(privateMessaging);
        // Probably more, but it will do the trick for now...
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == info) {
            info();
        } else if (e.getSource() == privateMessaging) {
            privateMessage();
        }
    }

    private void info() {
        UserInfoWindow window = new UserInfoWindow(user);
    }

    private void privateMessage() {

    }
}

class BackgroundPanel extends JPanel {
    private Color bgColor;
    private String backgroundURL;
    private BufferedImage img;

    public BackgroundPanel(Color bgColor, String backgroundURL) {
        this.bgColor = bgColor;
        this.backgroundURL = backgroundURL;
        setOpaque(false);
    }

    public void setBackgroundURL(String backgroundURL) {
        this.backgroundURL = backgroundURL;
        try {
            URL url = new URL(backgroundURL);
            img = ImageIO.read(url);
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(bgColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (img == null) {
            return; // If the image could not be loaded, do not draw it
        }

        // Image shall be not stretched but centered. Also it should max 50% of the width and height of the panel
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int maxImgWidth = panelWidth / 2;
        int maxImgHeight = panelHeight / 2;
        double widthScale = (double) maxImgWidth / imgWidth;
        double heightScale = (double) maxImgHeight / imgHeight;
        double scale = Math.min(1.0, Math.min(widthScale, heightScale));
        int drawWidth = (int) (imgWidth * scale);
        int drawHeight = (int) (imgHeight * scale);
        int x = (panelWidth - drawWidth) / 2;
        int y = (panelHeight - drawHeight) / 2;
        g.drawImage(img, x, y, drawWidth, drawHeight, null);
    }
}