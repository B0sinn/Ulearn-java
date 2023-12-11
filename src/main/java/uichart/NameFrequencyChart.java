package uichart;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.*;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class NameFrequencyChart extends JFrame{

    private CategoryDataset getNamesFrequency(Connection connection) throws Exception {
        final var SQL = "select * from (select surname, COUNT(id) as freq from students group by name) as tab where tab.freq>1; ";
        var statement = connection.createStatement();
        var rs = statement.executeQuery(SQL);

        var dataset = new DefaultCategoryDataset();

        while (rs.next()) {
            var name = rs.getString("surname");
            var freq = rs.getInt("freq");
            dataset.setValue(freq,name,"имена");
        }

        statement.close();

        return dataset;
    }

    public CategoryDataset createDataset(Connection connection) throws Exception {
        return getNamesFrequency(connection);
    }


    private JFreeChart createChart(CategoryDataset dataset)
    {
        JFreeChart chart = ChartFactory.createBarChart(
                "График частоты имен студентов",
                null,                   // x-axis label
                "Сколько раз встречается имя",                // y-axis label
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
        JFreeChart chart = createChart(createDataset(connection));
        chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600, 300));
        add(panel);
        pack();
        setTitle("График частоты имен студентов");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        EventQueue.invokeLater(() -> {
            setVisible(true);
        });
        return panel;
    }
}
