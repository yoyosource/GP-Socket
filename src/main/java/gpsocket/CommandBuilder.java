package gpsocket;

public class CommandBuilder {

    StringBuilder command = new StringBuilder();

    private CommandBuilder append(String s) {
        command.append(s);
        return this;
    }

    public static CommandBuilder listCommand() {
        return new CommandBuilder().append("list ");
    }

    public static CommandBuilder copyCommand() {
        return new CommandBuilder().append("copy ");
    }

    public static CommandBuilder moveCommand() {
        return new CommandBuilder().append("move ");
    }

    public CommandBuilder appendPath(String path) {
        if (command.toString().startsWith("list ") && command.length() == 5) {
            if (path.length() == 0) {
                command.append("/");
            }
            if (path.startsWith("/")) {
                command.append(path);
            } else {
                command.append("/").append(path);
            }
        }

        if (command.toString().startsWith("copy ")) {
            if (path.startsWith("@")) {
                appendDatabasePath(path);
            } else {
                appendAbsolutePath(path);
            }
        }
        if (command.toString().startsWith("move ")) {
            if (path.startsWith("@")) {
                appendDatabasePath(path);
            } else {
                appendAbsolutePath(path);
            }
        }
        return this;
    }

    public CommandBuilder appendDatabasePath(String databasePath) {
        if (!(command.toString().startsWith("copy ") || (command.toString().startsWith("move ")))) {
            return this;
        }
        if (!valid(true)) {
            return this;
        }

        if (!databasePath.startsWith("@")) {
            databasePath = "@" + databasePath;
        }
        command.append(databasePath).append(" ");
        return this;
    }

    public CommandBuilder appendAbsolutePath(String absolutePath) {
        if (!(command.toString().startsWith("copy ") || (command.toString().startsWith("move ")))) {
            return this;
        }
        if (!valid(true)) {
            return this;
        }

        command.append(absolutePath).append(" ");
        return this;
    }

    public boolean valid() {
        return valid(false) && command.toString().contains("@");
    }

    private boolean valid(boolean lazyValidation) {
        if (lazyValidation) {
            return command.length() - command.toString().replace(" ", "").length() < 3;
        }
        return command.length() - command.toString().replace(" ", "").length() == 2;
    }

}
