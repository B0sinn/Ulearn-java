package data;

import com.google.gson.Gson;
import data.entity.CourseTheme;
import data.entity.Exercise;
import data.entity.HomeWork;
import data.entity.Student;
import data.entity.vk.JSONDATA;
import data.entity.vk.User;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Database {

    private static Connection connection; // отвечает за подключение БД

    public static void saveDB(List<Student> students) throws Exception {
        Class.forName("org.sqlite.JDBC");
        var conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        var statement = conn.createStatement();//создание запросов
        conn.setAutoCommit(false);// отключил, т.к. с комитами очень долго сохраняется
        connection = conn;

        createTables(statement);//создаем таблицы

        //сохраняет в бд структуру курса, темы, дз и упражнения для того, чтобы в будущем
        //в таблицах student2exercise и studen2homework ссылаться на id
        saveEntities(students.get(0).getCourseThemes());

        // сохраняем для каждого студента его статистику за курс
        saveStudents(students);

        saveGroupMembers(); //сохраняем из json данный о студентах(участниках группы)
        conn.commit();//сохраняем изменения в БД
        conn.rollback();
        statement.close();// закрываем запросы
        conn.close(); // закрываем соединение с бд
    }

    private static void saveGroupMembers() throws Exception {
        var file = new File("groupMembers.json");
        var sc = new Scanner(file);
        Gson gson = new Gson();
        String jsonInString = sc.nextLine();
        JSONDATA jsondata = gson.fromJson(jsonInString, JSONDATA.class);

        for (var user : jsondata.getResponse().getItems()) {
            System.out.println(user);
            insertGroupMembers(user);
        }
    }
    // считывает JSON-данные из файла, преобразует их в объект Java с помощью библиотеки Gson,
    // а затем обрабатывает каждый элемент в JSON-данных


    private static void saveEntities(List<CourseTheme> themes) throws Exception {

        for (var theme : themes) {
            insertThemes(connection, theme);
            insertDataHomeWork(theme, theme.getHomeWorkList());
            insertDataExercise(theme, theme.getExerciseList());
        }
    } //для каждой темы сохраняются  ДЗ и УПР
    private static void insertGroupMembers(User user) throws Exception {
        final var SQL = "INSERT INTO 'members' ('id','first_name','last_name','sex','country') VALUES (?,?,?,?,?);";
        var statement = connection.prepareStatement(SQL);

        statement.setLong(1, user.getId());
        statement.setString(2, user.getFirst_name());
        statement.setString(3, user.getLast_name());
        statement.setString(4, user.getSex() == 2 ? "Male" : "Female");
        statement.setString(5, user.getCountry() == null ? "None" : user.getCountry().getTitle());
        statement.executeUpdate();
        statement.close();
    }// Вставляем данные в таблицу
    private static void createTables(Statement statement) throws SQLException {
        statement.execute("drop table IF EXISTS 'students';");
        statement.execute(
                "CREATE TABLE if not exists 'students' (" +
                        "'id' varchar PRIMARY KEY, " +
                        "'name' varchar, " +
                        "'surname' varchar, " +
                        "'email' varchar, " +
                        "'group' varchar);");
        statement.execute("drop table IF EXISTS 'members';");
        statement.execute(
                "CREATE TABLE if not exists 'members' (" +
                        "'id' INTEGER PRIMARY KEY, " +
                        "'first_name' varchar, " +
                        "'last_name' varchar, " +
                        "'sex' varchar, " +
                        "'country' varchar);");

        statement.execute("drop table IF EXISTS 'themes';");
        statement.execute(
                "CREATE TABLE if not exists 'themes' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'name' varchar);");

        statement.execute("drop table IF EXISTS 'homework';");
        statement.execute(
                "CREATE TABLE if not exists 'homework' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'theme_id' INTEGER, " +
                        "'name' varchar);");

        statement.execute("drop table IF EXISTS 'exercise';");
        statement.execute(
                "CREATE TABLE if not exists 'exercise' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'theme_id' INTEGER, " +
                        "'name' varchar);");

        statement.execute("drop table IF EXISTS 'student2homework';");
        statement.execute(
                "CREATE TABLE if not exists 'student2homework' (" +
                        "'student_id' varchar, " +
                        "'homework_id' INTEGER, " +
                        "'coursePoints' INTEGER);");

        statement.execute("drop table IF EXISTS 'student2exercise';");
        statement.execute(
                "CREATE TABLE if not exists 'student2exercise' (" +
                        "'student_id' varchar, " +
                        "'exercise_id' INTEGER, " +
                        "'coursePoints' INTEGER);");
    }

    private static void insertDataHomeWork(CourseTheme theme, List<HomeWork> homeWork) throws Exception {
        final var SQL = "INSERT INTO 'homework' ('name','theme_id') VALUES (?,?);";
        var ps = connection.prepareStatement(SQL);

        for (var hw : homeWork) {
            ps.clearParameters();
            ps.setString(1, hw.getName());
            ps.setLong(2, getThemeId(connection, theme)); // по названию ищем в БД id темы
            ps.addBatch();
        }
        ps.clearParameters();
        ps.executeBatch();
        ps.close();
    }

    private static void insertDataExercise(CourseTheme theme, List<Exercise> exercise) throws Exception {
        final var SQL = "INSERT INTO 'exercise' ('name','theme_id') VALUES (?,?);";
        var ps = connection.prepareStatement(SQL);

        for (var ex : exercise) {
            ps.clearParameters();
            ps.setString(1, ex.getName());
            ps.setLong(2, getThemeId(connection, theme));
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }



    private static void insertThemes(Connection connection, CourseTheme theme) throws Exception {
        final var SQL = "INSERT INTO 'themes' ('name') VALUES (?);";
        var statement = connection.prepareStatement(SQL);

        statement.setString(1, theme.getName());
        statement.executeUpdate();
        statement.close();
    }

    public static void saveStudents(List<Student> students) throws Exception {
        final var SQL = "INSERT INTO 'students' ('id','name','surname','email','group') VALUES (?,?,?,?,?);";
        var ps = connection.prepareStatement(SQL);

        for (var student : students) {
            ps.clearParameters();
            ps.setString(1, student.getId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getSurname());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getGroup());
            ps.addBatch();

        }
        ps.clearParameters();
        ps.executeBatch();
        ps.close();


        insertStudentHomework(connection, students);
        insertStudentExercise(connection, students);
    }

    private static void insertStudentHomework(Connection connection, List<Student> students) throws Exception {
        final var SQL = "INSERT INTO 'student2homework' ('student_id','homework_id','coursePoints') VALUES (?,?,?);";
        var statement = connection.prepareStatement(SQL);

        for (var student : students) {
            for (var theme : student.getCourseThemes()) {
                for (var hw : theme.getHomeWorkList()) {
                    statement.clearParameters();
                    statement.setString(1, student.getId());
                    statement.setLong(2, getHomeworkId(connection, hw));
                    statement.setLong(3, hw.getCoursePoints());
                    statement.addBatch();
                }
            }
        }
        statement.executeBatch();
        statement.close();
    }

    private static void insertStudentExercise(Connection connection, List<Student> students) throws Exception {
        final var SQL = "INSERT INTO 'student2exercise' ('student_id','exercise_id','coursePoints') VALUES (?,?,?);";
        var statement = connection.prepareStatement(SQL);

        for (var student : students) {
            for (var theme : student.getCourseThemes()) {
                for (var ex : theme.getExerciseList()) {
                    statement.clearParameters();
                    statement.setString(1, student.getId());
                    statement.setLong(2, getExerciseId(connection, ex));
                    statement.setLong(3, ex.getCoursePoints());
                    statement.addBatch();
                }
            }
        }
        statement.executeBatch();
        statement.close();
    }


    private static HashMap<String, Long> exerciseId = new HashMap<>();

    private static long getExerciseId(Connection connection, Exercise ex) throws Exception {
        if (exerciseId.containsKey(ex.getName()))
            return exerciseId.get(ex.getName());

        final var SQL = new Formatter(Locale.US).format("SELECT id FROM exercise where name='%s';", ex.getName()).toString();
        var statement = connection.createStatement();
        var rs = statement.executeQuery(SQL);
        var hwId = rs.getLong("id");
        statement.close();
        return hwId;
    }


    private static HashMap<String, Long> themeIds = new HashMap<>();

    private static long getThemeId(Connection connection, CourseTheme theme) throws Exception {
        if (themeIds.containsKey(theme.getName()))
            return themeIds.get(theme.getName());

        final var SQL = new Formatter(Locale.US).format("SELECT id FROM themes where name='%s';", theme.getName()).toString();
        var statement = connection.createStatement();
        var rs = statement.executeQuery(SQL);
        var themeId = rs.getLong("id");
        themeIds.put(theme.getName(), themeId);
        statement.close();
        return themeId;
    } // если у темы был id возвращаем его, иначе добавляем

    private static HashMap<String, Long> homeworksId = new HashMap<>();

    private static long getHomeworkId(Connection connection, HomeWork hw) throws Exception {
        if (homeworksId.containsKey(hw.getName()))
            return homeworksId.get(hw.getName());

        final var SQL = new Formatter(Locale.US).format("SELECT id FROM homework where name='%s';", hw.getName()).toString();
        var statement = connection.createStatement();
        var rs = statement.executeQuery(SQL);
        var hwId = rs.getLong("id");
        homeworksId.put(hw.getName(), hwId);
        statement.close();
        return hwId;
    }

}
