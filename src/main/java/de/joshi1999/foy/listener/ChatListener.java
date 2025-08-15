package de.joshi1999.foy.listener;

import de.joshi1999.foy.window.ChatWindow;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ChatListener extends ListenerAdapter {

    ChatWindow window;

    public ChatListener(ChatWindow window) {
        this.window = window;
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        //window.receiveMessage(event.getUser().getNick(), event.getMessage());
    }

    @Override
    public void onMessage(MessageEvent event) {
        if (event.getUser() == null) return;
        window.receiveMessage(event.getUser().getNick(), event.getMessage());
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        if (event.getUser() == null) return;
        window.receivePrivateMessage(event.getUser().getNick(), event.getMessage());
    }

    @Override
    public void onJoin(JoinEvent e) {
        if (e.getUser() == null) return;
        window.addUser(e.getUser().getNick());
        window.renewUsers();
        window.userJoin(e.getUser().getNick());
        window.receiveMessage("James", "Wir begrüßen " + e.getUser().getNick() + " ganz herzlich. Herreinspaziert.");
    }

    @Override
    public void onQuit(QuitEvent e) {
        window.removeUser(e.getUser().getNick());
        window.userLeave(e.getUser().getNick());
    }

    @Override
    public void onUserList(UserListEvent e) {
        for (User user : e.getUsers()) {
            window.addUser(user.getNick());
        }
    }

    @Override
    public void onChannelInfo(ChannelInfoEvent e) {
        window.renewChannels(e.getList());
    }

    @Override
    public void onConnect(ConnectEvent e) {
        window.retrieveChannels();
    }
}
