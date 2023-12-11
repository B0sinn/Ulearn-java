package data.entity.vk;


public class User {
    private int id;
    private String first_name;
    private String last_name;
    private int sex;
    private Country country;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", sex=" + sex +
                ", country=" + country +
                '}';
    }

    public User(int id, String first_name, String last_name, int sex, Country country) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.sex = sex;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
