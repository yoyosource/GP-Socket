package gpsocket;

public class LazyCommand extends CommandBuilder {

    private String command;

    LazyCommand(String command) {
        this.command = command;
    }

    @Override
    String assembleCommand() {
        return command;
    }

    public static LazyCommand getHostsInstance() {
        return new LazyCommand("hosts");
    }

    public static LazyCommand getTokenInstance() {
        return new LazyCommand("token");
    }

}
