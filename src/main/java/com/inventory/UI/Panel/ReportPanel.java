package com.inventory.UI.Panel;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class ReportPanel extends JPanel {

    private final ProductService service;
    private final Runnable onUpdate;
    private JPanel chartContainer;

    public ReportPanel(ProductService service, Runnable onUpdate) {
        this.service = service;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(18, 18, 22));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        chartContainer = new JPanel(new GridLayout(1, 2, 15, 15));
        chartContainer.setOpaque(false);
        add(chartContainer, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Reports & Analytics");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel sub = new JLabel("Live inventory performance overview");
        sub.setForeground(new Color(150, 150, 150));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        return header;
    }

    public void refresh() {
        chartContainer.removeAll();
        chartContainer.add(createStockChart());
        chartContainer.add(createSalesChart());
        chartContainer.revalidate();
        chartContainer.repaint();

        if (onUpdate != null) onUpdate.run();
    }

    private ChartPanel createStockChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Product> products = service.getAll();

        for (Product p : products) {
            dataset.addValue(p.getStock(), "Stock", p.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Current Stock Levels", "Product", "Quantity", dataset);

        style(chart);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(99, 102, 241));
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private ChartPanel createSalesChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Product> products = service.getAll();

        // Simulate realistic sales trend based on stock value
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May"};
        double base = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum() / 10;

        for (int i = 0; i < months.length; i++) {
            dataset.addValue(base * (0.7 + Math.random() * 0.6), "Revenue", months[i]);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Sales Trend (Last 5 Months)", "Month", "Revenue ($)", dataset);

        style(chart);

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(34, 197, 94));
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private void style(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(18, 18, 22));
        chart.getTitle().setPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(25, 25, 30));
        plot.setRangeGridlinePaint(new Color(60, 60, 70));
        plot.setDomainGridlinesVisible(false);

        plot.getDomainAxis().setTickLabelPaint(new Color(180, 180, 180));
        plot.getRangeAxis().setTickLabelPaint(new Color(180, 180, 180));
    }
}