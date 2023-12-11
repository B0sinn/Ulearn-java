package data.entity.vk;

import java.util.List;

public class Response {


    @Override
    public String toString() {
        return "Response{" +
                ", count=" + count +
                ", items=" + items +
                '}';
    }


    private int count;
    private List<User> items;

    public Response() {
    }

    public Response(int count, List<User> items) {
        this.count = count;
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
}
