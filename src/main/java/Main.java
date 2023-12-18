import au.com.bytecode.opencsv.CSVReader;
import data.Database;// хранит информацию о всех студентах и их темах в базе данных SQLite.
import data.entity.CourseTheme;
import data.entity.Exercise;
import data.entity.HomeWork;
import data.entity.Student;
import uichart.Foo;
import uichart.NameFrequencyChart;
import vk.VKParser;

import java.io.FileReader;//чтение файла возвращает данные в байт формате
import java.io.IOException;
import java.sql.DriverManager;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {
        var students = getStudents();//список студентов из csv файла
        Database.saveDB(students);//сохраняем данные из файла в БД

        Class.forName("org.sqlite.JDBC");// подключаем класс для реализации драйвера SQLlite
        var connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");// устанавливаем соединение с БД
        new NameFrequencyChart().createDemoPanel(connection);//отрисовка графиков
        new Foo().createDemoPanel(connection);//

        // запускается один раз для сбора данных из группы вк создание json
       VKParser.parseGroupMembers();
    }


    private static List<Student> getStudents() throws IOException {
        var reader = new CSVReader(new FileReader("basicprogramming.csv"), ',', '"');

        var themesHead = reader.readNext(); //первая строка csv файла, темы курса
        var themesCSVid = new HashMap<String, Integer>(); // словарь с названием темы и индексом столбца в csv файле

        //заполнение массива
        for (var i = 0; i < themesHead.length; i++) {
            themesCSVid.put(themesHead[i], i);
        }

        var exerciseAndHomework = reader.readNext();
        reader.readNext(); //пропуск 3 строки с максимумом по каждой теме

        String[] strokaData; //столбцы строки с данными студента
        var students = new ArrayList<Student>();

        while ((strokaData = reader.readNext()) != null) {
            //strokaData[0] - первый столбец с ФИ
            // fi - фамилия и имя из strokaData[0]
            var fi = strokaData[0].split(" ", 2);

            //если нет фамилии, то пропускаем
            if (fi.length < 2)
                continue;

            //создаем студента с ФИ, почтой, ulearn id и группой
            var student = new Student(strokaData[1], fi[0], fi[1], strokaData[2], strokaData[3]);
            students.add(student);

//            System.out.println(student);
//            System.out.println(Arrays.toString(strokaData));

            var themesIndex = new ArrayList<Integer>(); //list, который нужен для подсчета количества дз и упражнений в теме, чтобы не хардкодить количество дз и упр

            //добавляем в themesIndex отсортированные индексы тем
            themesCSVid.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue())
                    .forEach(x -> themesIndex.add(x.getValue()));

            String[] finalData = strokaData;

            //пробегаемся по отсортированному словарю тем
            themesCSVid.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue())
                    .forEach(x -> {
                        //создаем новую тему
                        var course = new CourseTheme(x.getKey());

                        var startInd = x.getValue();//началный индекс баллов темы
                        var end = themesIndex.indexOf(startInd) + 1;
                        int endInd = startInd; //конечный индекс баллов темы

                        //считаем конечный индекс баллов темы, вычитая из следуюшего в themesIndex индекса предыдущий
                        if (end >= themesIndex.size())
                            endInd = finalData.length;
                        else
                            endInd = themesIndex.get(end);

                        for (int i = startInd; i < endInd; i++) {
                            //добавляем в темы курса студента упражнения и дз
                            if (exerciseAndHomework[i].startsWith("Упр:"))
                                course.getExerciseList().add(new Exercise(exerciseAndHomework[i], Integer.parseInt(finalData[i])));
                            else if (exerciseAndHomework[i].startsWith("ДЗ:"))
                                course.getHomeWorkList().add(new HomeWork(exerciseAndHomework[i], Integer.parseInt(finalData[i])));

                        }
                        //добавляем тему курса студенту
                        student.getCourseThemes().add(course);
                    });
        }

        return students;

    }
}
