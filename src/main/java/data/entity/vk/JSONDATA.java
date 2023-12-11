package data.entity.vk;

public class JSONDATA {
    private Response response;

    public JSONDATA(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "JSONDATA{" +
                "response=" + response +
                '}';
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
