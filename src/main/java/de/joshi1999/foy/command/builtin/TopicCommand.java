package de.joshi1999.foy.command.builtin;

import de.joshi1999.foy.command.CommandHandler;
import de.joshi1999.foy.window.ChatWindow;

public class TopicCommand implements CommandHandler {
    private final ChatWindow window;

    public TopicCommand(ChatWindow window) {
        this.window = window;
    }

    @Override
    public void handle(String cmd, String[] args) {
        window.showTopic(true);
    }
}
