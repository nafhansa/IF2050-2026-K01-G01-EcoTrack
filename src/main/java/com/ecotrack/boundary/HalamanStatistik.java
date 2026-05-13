package com.ecotrack.boundary;

import com.ecotrack.controller.StatistikController;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HalamanStatistik extends BorderPane {

    private final StatistikController controller;

    private HBox cardsRow;
    private HBox chartsRow;

    // Card value labels
    private Label totalPohonValueLabel;
    private Label serapanKarbonValueLabel;
    private Label rataRataValueLabel;

    // Card sub-labels
    private Label totalPohonSubLabel;
    private Label serapanKarbonSubLabel;
    private Label rataRataSubLabel;

    public HalamanStatistik(StatistikController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG + ";");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox root = new VBox(28);
        root.setPadding(new Insets(32, 32, 32, 32));
        root.setStyle("-fx-background-color: " + UIConstants.CONTENT_BG + ";");

        // ── Header row ──────────────────────────────────────────────────────────
        HBox headerRow = buildHeaderRow();
        root.getChildren().add(headerRow);

        // ── Stat Cards ──────────────────────────────────────────────────────────
        cardsRow = new HBox(20);
        cardsRow.setFillHeight(true);
        root.getChildren().add(cardsRow);

        buildStatCards();

        // ── Charts row ──────────────────────────────────────────────────────────
        chartsRow = new HBox(20);
        chartsRow.setFillHeight(true);
        HBox.setHgrow(chartsRow, Priority.ALWAYS);
        root.getChildren().add(chartsRow);

        scrollPane.setContent(root);
        setCenter(scrollPane);

        ambilData();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // HEADER
    // ────────────────────────────────────────────────────────────────────────────

    private HBox buildHeaderRow() {
        Label title = new Label("Dashboard Statistik");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + ";");

        Label subtitle = new Label("Monitoring serapan karbon dan penghijauan kota");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "px; "
                + "-fx-text-fill: " + UIConstants.TEXT_SUBTITLE + ";");

        VBox titleBox = new VBox(4, title, subtitle);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Period dropdown
        ComboBox<String> periodCombo = new ComboBox<>();
        periodCombo.getItems().addAll("Bulanan", "Mingguan", "Tahunan");
        periodCombo.setValue("Bulanan");
        periodCombo.setStyle(
                "-fx-background-color: white; "
                + "-fx-border-color: #E2E8F0; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10; "
                + "-fx-padding: 8 16 8 16; "
                + "-fx-font-size: 13px; "
                + "-fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + "; "
                + "-fx-cursor: hand;");
        periodCombo.setPrefWidth(150);

        DropShadow comboShadow = new DropShadow(6, 0, 2, Color.rgb(0, 0, 0, 0.06));
        periodCombo.setEffect(comboShadow);

        HBox headerRow = new HBox(16, titleBox, spacer, periodCombo);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        return headerRow;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // STAT CARDS
    // ────────────────────────────────────────────────────────────────────────────

    private void buildStatCards() {
        // Card 1 – Total Pohon (green gradient)
        String[] card1Colors = { "#4CAF7D", "#2E7D52" };
        VBox card1 = buildStatCard(
                "Total Pohon",
                "0",
                "6 bulan terakhir",
                "+0% dari periode lalu",
                card1Colors,
                true
        );
        totalPohonValueLabel = (Label) ((VBox) card1.getChildren().get(2)).getChildren().get(0);
        totalPohonSubLabel   = (Label) ((VBox) card1.getChildren().get(2)).getChildren().get(1);

        // Card 2 – Serapan Karbon (dark teal)
        String[] card2Colors = { "#1B5E5E", "#0D3B3B" };
        VBox card2 = buildStatCard(
                "Serapan Karbon",
                "0 kg CO₂",
                "per tahun",
                "+0% dari periode lalu",
                card2Colors,
                true
        );
        serapanKarbonValueLabel = (Label) ((VBox) card2.getChildren().get(2)).getChildren().get(0);
        serapanKarbonSubLabel   = (Label) ((VBox) card2.getChildren().get(2)).getChildren().get(1);

        // Card 3 – Rata-rata (light teal / mint)
        String[] card3Colors = { "#A7F3D0", "#6EE7C7" };
        VBox card3 = buildStatCard(
                "Rata-rata Serapan",
                "0",
                "kg CO₂ / periode",
                "Konsisten meningkat",
                card3Colors,
                false
        );
        rataRataValueLabel = (Label) ((VBox) card3.getChildren().get(2)).getChildren().get(0);
        rataRataSubLabel   = (Label) ((VBox) card3.getChildren().get(2)).getChildren().get(1);

        // Make all cards grow equally
        for (VBox card : new VBox[]{ card1, card2, card3 }) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        cardsRow.getChildren().setAll(card1, card2, card3);
    }

    /**
     * Builds one stat card.
     *
     * @param title       e.g. "Total Pohon"
     * @param value       big number
     * @param subText     small text below value
     * @param badgeText   badge / change text
     * @param colors      [gradStart, gradEnd]
     * @param darkText    true → white text (dark bg), false → dark text (light bg)
     */
    private VBox buildStatCard(String title, String value, String subText,
                               String badgeText, String[] colors, boolean darkText) {

        String textColor      = darkText ? "white"   : UIConstants.TEXT_PAGE_TITLE;
        String subColor       = darkText ? "rgba(255,255,255,0.75)" : UIConstants.TEXT_SUBTITLE;
        String badgeBg        = darkText ? "rgba(255,255,255,0.20)" : "rgba(15,118,110,0.12)";
        String badgeTextColor = darkText ? "white"   : UIConstants.ACCENT_TEAL;

        VBox card = new VBox();
        card.setMinHeight(160);
        card.setPrefHeight(175);
        card.setPadding(new Insets(24));
        card.setSpacing(0);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, "
                + colors[0] + ", " + colors[1] + "); "
                + "-fx-background-radius: " + UIConstants.RADIUS_CARD + "; "
        );

        DropShadow shadow = new DropShadow(14, 0, 5, Color.rgb(0, 0, 0, 0.13));
        card.setEffect(shadow);

        // Decorative circles (purely visual overlay)
        StackPane decorLayer = buildDecorLayer(darkText);

        // Title label
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: " + subColor + ";");
        titleLbl.setWrapText(false);

        // Value label
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; "
                + "-fx-text-fill: " + textColor + ";");

        // Sub text
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + subColor + ";");

        VBox textBox = new VBox(2, valueLbl, subLbl);

        // Badge / change pill
        Label badgeLbl = new Label("↗  " + badgeText);
        badgeLbl.setStyle(
                "-fx-background-color: " + badgeBg + "; "
                + "-fx-text-fill: " + badgeTextColor + "; "
                + "-fx-font-size: 11px; "
                + "-fx-background-radius: 20; "
                + "-fx-padding: 4 10 4 10;");

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        card.getChildren().addAll(titleLbl, decorLayer, textBox, topSpacer, badgeLbl);
        return card;
    }

    /** Decorative translucent circles for card depth */
    private StackPane buildDecorLayer(boolean dark) {
        String circleColor = dark ? "rgba(255,255,255,0.07)" : "rgba(15,118,110,0.07)";

        Circle c1 = new Circle(50);
        c1.setFill(Color.web(circleColor));
        c1.setTranslateX(80);
        c1.setTranslateY(-10);

        Circle c2 = new Circle(30);
        c2.setFill(Color.web(circleColor));
        c2.setTranslateX(120);
        c2.setTranslateY(10);

        StackPane layer = new StackPane(c1, c2);
        layer.setMaxHeight(10);
        layer.setMinHeight(10);
        layer.setPrefHeight(10);
        layer.setMouseTransparent(true);
        return layer;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CHARTS
    // ────────────────────────────────────────────────────────────────────────────

    private void buildCharts(Map<String, Object> dataStatistik) {
        chartsRow.getChildren().clear();

        // ── Left: Line chart – Jumlah Pohon ─────────────────────────────────────
        VBox lineChartCard = buildChartCard(buildLineChart(dataStatistik));
        lineChartCard.getChildren().add(0, buildChartHeader("Jumlah Pohon", "Pertumbuhan pohon yang ditanam"));
        HBox.setHgrow(lineChartCard, Priority.ALWAYS);

        // ── Right: Bar chart – Serapan Karbon ───────────────────────────────────
        VBox barChartCard = buildChartCard(buildBarChart(dataStatistik));
        barChartCard.getChildren().add(0, buildChartHeader("Serapan Karbon", "Estimasi penyerapan CO₂ (kg/tahun)"));
        HBox.setHgrow(barChartCard, Priority.ALWAYS);

        chartsRow.getChildren().addAll(lineChartCard, barChartCard);
    }

    private VBox buildChartCard(Region chart) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: white; "
                + "-fx-background-radius: " + UIConstants.RADIUS_CARD + ";");
        DropShadow shadow = new DropShadow(10, 0, 4, Color.rgb(0, 0, 0, 0.08));
        card.setEffect(shadow);
        VBox.setVgrow(chart, Priority.ALWAYS);
        card.getChildren().add(chart);
        return card;
    }

    private VBox buildChartHeader(String title, String subtitle) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; "
                + "-fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + ";");

        Label subtitleLbl = new Label(subtitle);
        subtitleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE + ";");

        return new VBox(2, titleLbl, subtitleLbl);
    }

    /** Line chart – Jumlah pohon per lokasi (pakai data penanaman sebagai proxy bulan) */
    private LineChart<String, Number> buildLineChart(Map<String, Object> dataStatistik) {
        List<DataPenanaman> dataPenanaman =
                (List<DataPenanaman>) dataStatistik.get("dataPenanaman");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-font-size: 11px; -fx-text-fill: #9E9E9E;");
        xAxis.setTickLabelFill(Color.web("#9E9E9E"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-font-size: 11px;");
        yAxis.setTickLabelFill(Color.web("#9E9E9E"));

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(false);
        lineChart.setStyle(
                "-fx-background-color: transparent; "
                + "-fx-plot-background-color: transparent;");
        lineChart.setPrefHeight(260);
        lineChart.setMinHeight(240);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Group by lokasi, sum jumlah pohon per lokasi as data points
        Map<String, Long> pohonPerLokasi = dataPenanaman.stream()
                .collect(Collectors.groupingBy(DataPenanaman::getLokasi, Collectors.counting()));

        pohonPerLokasi.forEach((lokasi, count) ->
                series.getData().add(new XYChart.Data<>(shortenLabel(lokasi), count)));

        lineChart.getData().add(series);

        // Style the line green
        lineChart.applyCss();
        lineChart.layout();

        series.getNode().setStyle(
                "-fx-stroke: " + UIConstants.ACCENT_LIME + "; "
                + "-fx-stroke-width: 3px;");

        for (XYChart.Data<String, Number> d : series.getData()) {
            if (d.getNode() != null) {
                d.getNode().setStyle(
                        "-fx-background-color: " + UIConstants.ACCENT_LIME + ", white; "
                        + "-fx-background-radius: 8; "
                        + "-fx-padding: 6;");
            }
        }

        return lineChart;
    }

    /** Bar chart – Estimasi karbon per lokasi */
    private BarChart<String, Number> buildBarChart(Map<String, Object> dataStatistik) {
        List<DataPenanaman> dataPenanaman =
                (List<DataPenanaman>) dataStatistik.get("dataPenanaman");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-font-size: 11px;");
        xAxis.setTickLabelFill(Color.web("#9E9E9E"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-font-size: 11px;");
        yAxis.setTickLabelFill(Color.web("#9E9E9E"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setCategoryGap(16);
        barChart.setBarGap(4);
        barChart.setStyle("-fx-background-color: transparent; -fx-plot-background-color: transparent;");
        barChart.setPrefHeight(260);
        barChart.setMinHeight(240);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Group karbon per lokasi
        Map<String, Double> karbonPerLokasi = dataPenanaman.stream()
                .collect(Collectors.groupingBy(
                        DataPenanaman::getLokasi,
                        Collectors.summingDouble(DataPenanaman::getEstimasiKarbon)));

        karbonPerLokasi.forEach((lokasi, karbon) ->
                series.getData().add(new XYChart.Data<>(shortenLabel(lokasi), karbon)));

        barChart.getData().add(series);

        barChart.applyCss();
        barChart.layout();

        for (XYChart.Data<String, Number> d : series.getData()) {
            if (d.getNode() != null) {
                d.getNode().setStyle("-fx-bar-fill: " + UIConstants.ACCENT_TEAL + ";");
            }
        }

        return barChart;
    }

    /** Shorten long location names for axis labels */
    private String shortenLabel(String label) {
        if (label == null) return "-";
        return label.length() > 12 ? label.substring(0, 11) + "…" : label;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // DATA LOADING
    // ────────────────────────────────────────────────────────────────────────────

    public void ambilData() {
        Map<String, Object> dataStatistik = controller.ambilData();
        if (dataStatistik != null && !dataStatistik.isEmpty()) {
            updateCards(dataStatistik);
            buildCharts(dataStatistik);
        } else {
            tampilkanPesanError("Belum ada data penghijauan");
        }
    }

    public void updateCards(Map<String, Object> statistik) {
        int totalPohon          = (int)    statistik.get("totalPohon");
        double totalKarbon      = (double) statistik.get("totalKarbon");
        List<DataPenanaman> dp  = (List<DataPenanaman>) statistik.get("dataPenanaman");

        // Total pohon card
        totalPohonValueLabel.setText(String.valueOf(totalPohon));

        // Serapan karbon card
        serapanKarbonValueLabel.setText(String.format("%.0f", totalKarbon));
        serapanKarbonSubLabel.setText("kg CO₂ per tahun");

        // Rata-rata card
        long lokCount = dp.stream().map(DataPenanaman::getLokasi).distinct().count();
        double avg    = lokCount > 0 ? totalKarbon / lokCount : 0;
        rataRataValueLabel.setText(String.format("%.0f", avg));
        rataRataSubLabel.setText("kg CO₂ / periode");
    }

    public void tampilkanPesanError(String pesan) {
        chartsRow.getChildren().clear();
        Label errorLbl = new Label(pesan);
        errorLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: " + UIConstants.ACCENT_RED + "; -fx-padding: 40 0 0 0;");
        errorLbl.setAlignment(Pos.CENTER);
        chartsRow.getChildren().add(errorLbl);
    }
}
