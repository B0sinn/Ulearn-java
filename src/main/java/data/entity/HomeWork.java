package data.entity;

public class HomeWork {

    private final String name;
    private final Integer coursePoints;

    public HomeWork(String name,Integer coursePoints) {
        this.name=name;
        this.coursePoints = coursePoints;
    }

    @Override
    public String toString() {
        return "HomeWork{" +
                "name='" + name + '\'' +
                ", score=" + coursePoints +
                '}';
    }

    public String getName() {
        return name;
    }

    public Integer getCoursePoints() {
        return coursePoints;
    }
}
