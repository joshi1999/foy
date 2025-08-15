package de.joshi1999.foy.events;

import de.joshi1999.foy.window.ChatWindow;

import java.util.Arrays;

public class CommandDispatcher {
    private ChatWindow window;

    public CommandDispatcher(ChatWindow window) {
        this.window = window;
    }

    public void dispatch(String line) {
        String command = line.replaceFirst("/", "");
        String cmd = command.split(" ")[0];
        String[] args = command.replaceFirst(cmd + " ", "").split(" ");
        switch (cmd) {
            case "list": listCommand();
                return;
            case "go": goCommand(args);
                return;
            case "msg": sendPrivateMessage(args);
            default:
        }
    }

    private void listCommand() {
        window.requestChannelList();
    }

    private void goCommand(String[] args) {
        window.changeChannel(args);
    }

    private void sendPrivateMessage(String[] args) {
        if (args.length < 2) {
            window.receivePrivateMessage("James", "Private Nachrichten schreibst du mit '/msg NickName <Text>'");
            return;
        }
        String[] messageArray = Arrays.copyOfRange(args, 1, args.length);
        String message = String.join(" ", messageArray);
        window.sendPrivateMessage(args[0], message);
    }
}
