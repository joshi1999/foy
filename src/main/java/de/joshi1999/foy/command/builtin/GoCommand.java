package de.joshi1999.foy.command.builtin;

import de.joshi1999.foy.command.CommandHandler;
import de.joshi1999.foy.window.ChatWindow;

public class GoCommand implements CommandHandler {
    private final ChatWindow window;

    public GoCommand(ChatWindow window) {
        this.window = window;
    }

    @Override
    public void handle(String cmd, String[] args) {
        if (args.length >= 1) {
            window.changeChannel(args[0]);
        } else {
            window.receiveMessage("James", "Du wechselst den Channel mit /go #<channel>");
        }
    }
}
