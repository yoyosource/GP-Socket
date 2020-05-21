package gpsocket;

public class GPResponse {

    public final RepsoneType type;
    public final String data;

    public GPResponse(RepsoneType type, String data) {
        this.type = type;
        this.data = data;
    }

}
