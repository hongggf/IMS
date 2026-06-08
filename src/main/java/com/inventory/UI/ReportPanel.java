package com.inventory.UI;

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

    private JPanel chartsContainer;

    public ReportPanel(ProductService service, Runnable onUpdate) {
        this.service = service;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(18, 18, 22));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);

        chartsContainer = new JPanel(new GridLayout(1, 2, 15, 15));
        chartsContainer.setOpaque(false);

        add(chartsContainer, BorderLayout.CENTER);

        refresh();
    }

    // ================= HEADER =================
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

    // ================= REFRESH (LIVE UPDATE) =================
    public void refresh() {

        chartsContainer.removeAll();

        chartsContainer.add(createStockChart());
        chartsContainer.add(createSalesChart());

        chartsContainer.revalidate();
        chartsContainer.repaint();

        if (onUpdate != null) onUpdate.run();
    }

    // ================= STOCK CHART (LIVE PRODUCT DATA) =================
    private ChartPanel createStockChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Product> products = service.getAll();

        for (Product p : products) {
            dataset.addValue(p.getStock(), "Stock", p.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Stock Levels",
                "Product",
                "Qty",
                dataset
        );

        styleChart(chart);

        CategoryPlot plot = chart.getCategoryPlot();

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(99, 102, 241));
        renderer.setBarPainter(new BarRenderer().getBarPainter());

        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    // ================= SALES CHART (STATIC FOR NOW OR EXTEND LATER) =================
    private ChartPanel createSalesChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1200, "Sales", "Jan");
        dataset.addValue(900, "Sales", "Feb");
        dataset.addValue(1500, "Sales", "Mar");
        dataset.addValue(1100, "Sales", "Apr");

        JFreeChart chart = ChartFactory.createLineChart(
                "Sales Trend",
                "Month",
                "Revenue",
                dataset
        );

        styleChart(chart);

        CategoryPlot plot = chart.getCategoryPlot();

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        renderer.setSeriesPaint(0, new Color(34, 197, 94));
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));

        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    // ================= GLOBAL STYLE =================
    private void styleChart(JFreeChart chart) {

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