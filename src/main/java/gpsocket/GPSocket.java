package gpsocket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GPSocket {

    private static Map<String, Integer> portMap = new HashMap<>();

    static {
        portMap.put("MANAGER", 8800);

        getHosts();
        getToken();
    }

    private static void getHosts() {
        GPResponse response = databaseRequest("MANAGER", LazyCommand.getHostsInstance());
        if (response.type == RepsoneType.DATA && !response.data.isEmpty()) {
            String[] strings = response.data.split("\n");
            portMap.clear();
            portMap.put("MANAGER", 8800);
            for (String s: strings) {
                portMap.put(s.split(" -> ")[0], Integer.parseInt(s.split(" -> ")[1]));
            }
        }
        System.out.println("MANAGER: " + response);
    }

    private static void getToken() {
        GPResponse response = databaseRequest("MANAGER", LazyCommand.getTokenInstance());
        if (response.type == RepsoneType.DATA && !response.data.isEmpty()) {
            File tokenFile = new File(response.data);
            if (tokenFile.exists()) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(tokenFile)))) {
                    CommandBuilder.setToken(bufferedReader.readLine());
                } catch (IOException e) {

                }
            }
        }
        System.out.println("MANAGER: " + response);
    }

    public static void main(String[] args) {

    }

    public static Map<String, Integer> getPortMap() {
        return portMap;
    }

    public static GPResponse databaseRequest(String host, String portName, CommandBuilder command) {
        try {
            command.assembleCommand();
        } catch (Exception e) {
            return new GPResponse(RepsoneType.INVALID, "");
        }

        Integer port = portMap.get(portName);
        if (port == null) {
            return new GPResponse(RepsoneType.INVALID, "");
        }

        try {
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            outputStream.write(command.assembleCommand().getBytes());
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
            e.printStackTrace();
            return new GPResponse(RepsoneType.EXCEPTION, "");
        }
    }

    public static GPResponse databaseRequest(String portName, CommandBuilder command) {
        try {
            command.assembleCommand();
        } catch (Exception e) {
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

            outputStream.write(command.assembleCommand().getBytes());
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
            e.printStackTrace();
            return new GPResponse(RepsoneType.EXCEPTION, "");
        }
    }

}
