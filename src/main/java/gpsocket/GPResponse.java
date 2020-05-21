package gpsocket;

public class GPResponse {

    public final RepsoneType type;
    public final String data;

    public GPResponse(RepsoneType type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return "GPResponse{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }

}
