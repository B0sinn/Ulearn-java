package data.entity;

public class Exercise {

    private final String name;

    private final Integer coursePoints;


    public Exercise(String name, Integer coursePoints) {
        this.name = name;
        this.coursePoints = coursePoints;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ",score=" + coursePoints +
                '}';
    }

    public String getName() {
        return name;
    }

    public Integer getCoursePoints() {
        return coursePoints;
    }
}
