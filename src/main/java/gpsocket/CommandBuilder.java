package gpsocket;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {

    private static String token = "";

    public static void setToken(String token) {
        CommandBuilder.token = token;
    }

    private String commandType;
    private List<String> args = new ArrayList<>();
    private int databasePaths = 0;

    private CommandBuilder setCommandType(String commandType) {
        this.commandType = commandType;
        return this;
    }

    public static CommandBuilder listCommand() {
        return new CommandBuilder().setCommandType("list");
    }

    public static CommandBuilder copyCommand() {
        return new CommandBuilder().setCommandType("copy");
    }

    public static CommandBuilder moveCommand() {
        return new CommandBuilder().setCommandType("move");
    }

    public CommandBuilder appendPath(String path) {
        if (commandType.equals("list")) {
            if (args.isEmpty()) {
                args.add(path);
            }
        }
        if (commandType.equals("copy") || commandType.equals("move")) {
            if (path.startsWith("@")) {
                appendDatabasePath(path);
            } else {
                appendAbsolutePath(path);
            }
        }
        return this;
    }

    public CommandBuilder appendDatabasePath(String databasePath) {
        if (!(commandType.equals("copy") || commandType.equals("move"))) {
            return this;
        }

        if (!databasePath.startsWith("@")) {
            databasePath = "@" + databasePath;
        }

        if (args.size() < 2) {
            args.add(databasePath);
            databasePaths++;
        }
        return this;
    }

    public CommandBuilder appendAbsolutePath(String absolutePath) {
        if (!(commandType.equals("copy") || commandType.equals("move"))) {
            return this;
        }

        if (args.size() < 2) {
            args.add(absolutePath);
        }
        return this;
    }

    String assembleCommand() {
        if (commandType.equals("copy") || commandType.equals("move")) {
            if (databasePaths == 0) {
                throw new IllegalArgumentException();
            }
            if (args.size() != 2) {
                throw new IllegalArgumentException();
            }
            return commandType + " " + token + " " + args.get(0) + " " + args.get(1);
        }
        if (commandType.equals("list")) {
            if (args.size() != 1) {
                throw new IllegalArgumentException();
            }
            return commandType + " " + token + " " + args.get(0);
        }

        throw new IllegalArgumentException();
    }

}
