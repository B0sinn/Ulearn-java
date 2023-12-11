package data.entity;

import java.util.ArrayList;

public class Student {

    private final String  id;
    private final String name;
    private final String surname;

    private final String email;
    private final String group;

    private final ArrayList<CourseTheme> courseThemes;


    public Student(String id, String name, String surname, String email, String group) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.group = group;
        this.courseThemes = new ArrayList<>();
    }

    @Override
    public String toString() {

        for (var courses : courseThemes)
        {
            for (var hw : courses.getHomeWorkList())
                System.out.println(hw);
            for (var up : courses.getExerciseList())
                System.out.println(up);
        }

        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", group='" + group + '\'' +
                ", courseThemes=" + courseThemes +
                '}';
    }

    public ArrayList<CourseTheme> getCourseThemes() {
        return courseThemes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getGroup() {
        return group;
    }
}
