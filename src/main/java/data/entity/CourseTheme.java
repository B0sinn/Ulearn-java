package data.entity;

import java.util.ArrayList;
import java.util.List;

public class CourseTheme {

    private final String name;

    private final List<HomeWork> homeWorkList;
    private final List<Exercise> exerciseList;

    public CourseTheme(String name) {
        this.name=name;
        this.homeWorkList = new ArrayList<>();
        this.exerciseList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "CourseTheme{" +
                "name='" + name + '\'' +
                ", homeWorkList=" + homeWorkList +
                ", exerciseList=" + exerciseList +
                '}';
    }

    public String getName() {
        return name;
    }

    public List<HomeWork> getHomeWorkList() {
        return homeWorkList;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }
}
