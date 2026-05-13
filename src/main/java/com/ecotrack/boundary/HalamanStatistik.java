package com.ecotrack.boundary;

import com.ecotrack.controller.StatistikController;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class HalamanStatistik extends BorderPane {

    // Halaman dashboard statistik.
    // Menampilkan ringkasan total (cards) dan grafik bar estimasi karbon per lokasi.

    private final StatistikController controller;
    private VBox contentArea;
    private HBox cardsRow;
    private VBox chartArea;
    private Label totalPohonLabel;
    private Label totalKarbonLabel;
    private Label totalLokasiLabel;
    private Label totalJenisLabel;

    public HalamanStatistik(StatistikController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        // Susun layout: header -> ringkasan -> area grafik, lalu ambil data awal.
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);
        setPadding(new Insets(UIConstants.PADDING_CONTENT));

        VBox root = new VBox(24);

        // Header
        Label title = new Label("Dashboard Statistik");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Monitoring serapan karbon dan penghijauan kota");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        VBox header = new VBox(4, title, subtitle);
        root.getChildren().add(header);

        // Summary Cards
        cardsRow = new HBox(UIConstants.GAP_CARD);
        cardsRow.getChildren().addAll(
            buildCard("Total Pohon", "0", UIConstants.CARD_GREEN_GRADIENT_START, UIConstants.CARD_GREEN_GRADIENT_END, "\uD83C\uDF33"),
            buildCard("Serapan Karbon", "0 kg", UIConstants.CARD_TEAL_GRADIENT_START, UIConstants.CARD_TEAL_GRADIENT_END, "\u267B\uFE0F"),
            buildCard("Lokasi", "0", UIConstants.ACCENT_LIME, "#86EF3A", "\uD83D\uDCCD"),
            buildCard("Jenis Pohon", "0", "#0D9488", "#0F766E", "\uD83C\uDF3F")
        );
        root.getChildren().add(cardsRow);

        // Chart Area
        chartArea = new VBox(16);
        Label chartTitle = new Label("Estimasi Karbon per Lokasi");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);
        chartArea.getChildren().add(chartTitle);
        root.getChildren().add(chartArea);

        VBox.setVgrow(chartArea, Priority.ALWAYS);
        setCenter(root);

        ambilData();
    }

    private VBox buildCard(String label, String value, String gradStart, String gradEnd, String icon) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);
        card.setMinHeight(120);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + gradStart + ", " + gradEnd + "); -fx-background-radius: " + UIConstants.RADIUS_CARD + ";");

        DropShadow shadow = new DropShadow(8, 0, 4, Color.rgb(0, 0, 0, 0.15));
        card.setEffect(shadow);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 28px;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: " + UIConstants.FONT_CARD_NUMBER + "px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: " + UIConstants.FONT_CARD_LABEL + "px; -fx-text-fill: rgba(255,255,255,0.85);");

        card.getChildren().addAll(iconLbl, valueLbl, labelLbl);
        return card;
    }

    public void ambilData() {
        // Ambil data dari controller dan refresh UI.
        Map<String, Object> dataStatistik = controller.ambilData();
        if (dataStatistik != null && !dataStatistik.isEmpty()) {
            updateCards(dataStatistik);
            tampilkanGrafik(dataStatistik);
        } else {
            tampilkanPesanError("Belum ada data penghijauan");
        }
    }

    public void updateCards(Map<String, Object> statistik) {
        // Render angka ringkasan pada kartu (total pohon, karbon, lokasi, jenis).
        int totalPohon = (int) statistik.get("totalPohon");
        double totalKarbon = (double) statistik.get("totalKarbon");
        List<DataPohon> dataPohon = (List<DataPohon>) statistik.get("dataPohon");
        List<DataPenanaman> dataPenanaman = (List<DataPenanaman>) statistik.get("dataPenanaman");

        long totalLokasi = dataPenanaman.stream().map(DataPenanaman::getLokasi).distinct().count();
        long totalJenis = dataPohon.stream().map(DataPohon::getNamaPohon).distinct().count();

        // Update card values
        VBox card1 = (VBox) cardsRow.getChildren().get(0);
        Label val1 = (Label) card1.getChildren().get(1);
        val1.setText(String.valueOf(totalPohon));

        VBox card2 = (VBox) cardsRow.getChildren().get(1);
        Label val2 = (Label) card2.getChildren().get(1);
        val2.setText(String.format("%.0f kg", totalKarbon));

        VBox card3 = (VBox) cardsRow.getChildren().get(2);
        Label val3 = (Label) card3.getChildren().get(1);
        val3.setText(String.valueOf(totalLokasi));

        VBox card4 = (VBox) cardsRow.getChildren().get(3);
        Label val4 = (Label) card4.getChildren().get(1);
        val4.setText(String.valueOf(totalJenis));
    }

    public void tampilkanGrafik(Map<String, Object> dataStatistik) {
        // Render grafik bar: lokasi vs estimasi karbon.
        List<DataPenanaman> dataPenanaman = (List<DataPenanaman>) dataStatistik.get("dataPenanaman");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Lokasi");
        xAxis.setStyle("-fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + "; -fx-font-size: 12px;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Estimasi Karbon (kg)");
        yAxis.setStyle("-fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + "; -fx-font-size: 12px;");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE + "; -fx-background-radius: " + UIConstants.RADIUS_CARD + "; -fx-padding: 16;");
        barChart.setPrefHeight(350);
        barChart.setCategoryGap(20);
        barChart.setBarGap(8);

        DropShadow chartShadow = new DropShadow(6, 0, 3, Color.rgb(0, 0, 0, 0.1));
        barChart.setEffect(chartShadow);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (DataPenanaman dp : dataPenanaman) {
            series.getData().add(new XYChart.Data<>(dp.getLokasi(), dp.getEstimasiKarbon()));
        }
        barChart.getData().add(series);

        // Style bars
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: " + UIConstants.ACCENT_TEAL + ";");
        }

        chartArea.getChildren().clear();
        Label chartTitle = new Label("Estimasi Karbon per Lokasi");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);
        chartArea.getChildren().addAll(chartTitle, barChart);
    }

    public void tampilkanPesanError(String pesan) {
        // Fallback UI jika data kosong.
        Label errorLbl = new Label(pesan);
        errorLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: " + UIConstants.ACCENT_RED + "; -fx-padding: 40 0 0 0;");
        errorLbl.setAlignment(Pos.CENTER);
        chartArea.getChildren().clear();
        chartArea.getChildren().add(errorLbl);
    }
}
