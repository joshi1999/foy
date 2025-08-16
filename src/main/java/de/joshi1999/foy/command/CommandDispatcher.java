package de.joshi1999.foy.command;

import de.joshi1999.foy.window.ChatWindow;

import java.util.HashMap;

public class CommandDispatcher {
    private final ChatWindow window;
    private final HashMap<String, CommandHandler> handlers;

    public CommandDispatcher(ChatWindow window) {
        this.window = window;
        handlers = new HashMap<>();
    }

    public void registerCommand(String command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public void dispatch(String line) {
        String command = line.replaceFirst("/", "");
        String cmd = command.split(" ")[0];
        String[] args = command.replaceFirst(cmd + " ", "").split(" ");

        CommandHandler handler = handlers.get(cmd);
        if (handler == null) {
            window.receivePrivateMessage("James", "Den Command kenn ich leider nicht.");
            return;
        }
        handler.handle(cmd, args);
    }
}
