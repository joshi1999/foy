package de.joshi1999.foy.command.builtin;

import de.joshi1999.foy.command.CommandHandler;
import de.joshi1999.foy.window.ChatWindow;

import java.util.Arrays;

public class MsgCommand implements CommandHandler {
    private final ChatWindow window;

    public MsgCommand(ChatWindow window) {
        this.window = window;
    }

    @Override
    public void handle(String cmd, String[] args) {
        if (args.length < 2) {
            window.receivePrivateMessage("James", "Private Nachrichten schreibst du mit '/msg NickName <Text>'");
            return;
        }
        String[] messageArray = Arrays.copyOfRange(args, 1, args.length);
        String message = String.join(" ", messageArray);
        window.sendPrivateMessage(args[0], message);
    }
}
