package uichart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class Foo extends JFrame {
    private CategoryDataset getNamesFrequency(Connection connection) throws Exception {
        final var SQL = "select exercise.name,coursepoints,count(student_id) as students from student2exercise join exercise on exercise.id=exercise_id group by exercise.name,coursepoints limit 10";
        //выбирает имя упражнения , количество баллов за упражнение и количество студентов,
        // выполнивших это упражнение, из таблиц "student2exercise" и "exercise", группируя результаты по
        // имени упражнения и количеству баллов.
        var statement = connection.createStatement();
        var rs = statement.executeQuery(SQL);

        var dataset = new DefaultCategoryDataset();

        while (rs.next()) {
            var name = rs.getString("name");
            var coursepoints = rs.getInt("coursepoints");
            var students = rs.getInt("students");
            dataset.setValue(students,coursepoints+"",name);
        }

        statement.close();

        return dataset;
    }

    public CategoryDataset createDataset(Connection connection) throws Exception {
        return getNamesFrequency(connection);
    }
    //Чтение данных и создание объекта набора данных Dataset


    private JFreeChart createChart(CategoryDataset dataset)
    {
        JFreeChart chart = ChartFactory.createBarChart(
                "График распределения баллов за упражнения",
                null,                   // x-axis label
                "Количество студентов",                // y-axis label
                dataset);
//        chart.addSubtitle(new TextTitle(""));
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;
    }

    public JPanel createDemoPanel(Connection connection) throws Exception {
        JFreeChart chart = createChart(new SlidingCategoryDataset(createDataset(connection),0,100) );
        chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        ChartPanel panel = new ChartPanel(chart);
        var scroller = new JScrollBar(SwingConstants.HORIZONTAL, 0, 10, 0,
                50);
        panel.add(scroller);

        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setHorizontalAxisTrace(true);
        panel.setPreferredSize(new Dimension(600, 300));
        add(panel);
        pack();
        setTitle("Баллы студентов за упражнения");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        EventQueue.invokeLater(() -> {
            setVisible(true);
        });
        return panel;
    }
}
