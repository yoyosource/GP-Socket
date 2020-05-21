package gpsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GPSocket {

    private static Map<String, Integer> portMap = new HashMap<>();

    static {
        portMap.put("versions", 8888);
        portMap.put("worlds", 8889);
        portMap.put("plugins", 8890);
        portMap.put("schematics", 8891);
        portMap.put("traces", 8892);
    }

    public static void main(String[] args) {

    }

    public static Map<String, Integer> getPortMap() {
        return portMap;
    }

    public static GPResponse databaseRequest(String portName, CommandBuilder command) {
        if ((command.command.toString().startsWith("copy ") || command.command.toString().startsWith("move ")) && !command.valid()) {
            return new GPResponse(RepsoneType.INVALID, "");
        }

        Integer port = portMap.get(portName);
        if (port == null) {
            return new GPResponse(RepsoneType.INVALID, "");
        }

        try {
            Socket socket = new Socket("127.0.0.1", port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            outputStream.write(command.command.toString().getBytes());
            outputStream.flush();

            long time = System.currentTimeMillis();
            while (inputStream.available() == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (System.currentTimeMillis() - time > 5000) {
                    return new GPResponse(RepsoneType.TIMEOUT, "");
                }
            }

            StringBuilder st = new StringBuilder();
            while (inputStream.available() > 0) {
                st.append((char)inputStream.read());
            }

            socket.close();
            outputStream.close();
            inputStream.close();

            RepsoneType type = RepsoneType.UNKNOWN;
            if (st.toString().equals("SUCCEEDED")) {
                type = RepsoneType.SUCCESS;
            } else if (st.toString().equals("FAILED")) {
                type = RepsoneType.FAILURE;
            } else {
                if (st.length() > 0) {
                    type = RepsoneType.DATA;
                }
            }
            return new GPResponse(type, st.toString());
        } catch (IOException e) {
            return new GPResponse(RepsoneType.EXCEPTION, "");
        }
    }

}
