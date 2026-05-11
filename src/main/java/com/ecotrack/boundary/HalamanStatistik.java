package com.ecotrack.boundary;

import com.ecotrack.controller.StatistikController;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class HalamanStatistik extends BorderPane {

    private Object grafikStatistik;
    private String filterTerpilih;

    private final StatistikController controller;
    private ComboBox<String> filterPeriode;
    private VBox contentArea;

    public HalamanStatistik(StatistikController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        VBox header = new VBox(8);
        header.setPadding(new Insets(UIConstants.PADDING_CONTENT));

        Label title = new Label("Dashboard Statistik");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Monitoring serapan karbon dan penghijauan kota");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        filterPeriode = new ComboBox<>();
        filterPeriode.getItems().addAll("Bulanan", "Tahunan", "Semua");
        filterPeriode.setValue("Bulanan");
        filterPeriode.setOnAction(e -> pilihPeriode(filterPeriode.getValue()));

        header.getChildren().addAll(title, subtitle);
        setTop(header);

        contentArea = new VBox(UIConstants.GAP_CARD);
        contentArea.setPadding(new Insets(0, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT));
        setCenter(contentArea);

        ambilData();
    }

    public void ambilData() {
        // Algo-035
        Map<String, Object> dataStatistik = controller.ambilData();
        if (dataStatistik != null && !dataStatistik.isEmpty()) {
            tampilkanStatistik(dataStatistik);
            tampilkanGrafik(dataStatistik);
        } else {
            tampilkanPesanError("Belum ada data penghijauan");
        }
    }

    public void tampilkanStatistik(Object dataStatistik) {
        // Algo-036
        updateCards((Map<String, Object>) dataStatistik);
    }

    public void pilihPeriode(String periode) {
        // Algo-037
        filterTerpilih = periode;
        Object dataStatistik = controller.terapkanFilter(filterTerpilih);
        perbaruiTampilan(dataStatistik);
    }

    public void tampilkanGrafik(Object dataStatistik) {
        // Algo-038
        grafikStatistik = dataStatistik;
    }

    public void perbaruiTampilan(Object dataStatistik) {
        // Algo-039
        tampilkanStatistik(dataStatistik);
        tampilkanGrafik(dataStatistik);
    }

    public void tampilkanPesanError(String pesan) {
        // Algo-040
        Alert alert = new Alert(Alert.AlertType.WARNING, pesan, ButtonType.OK);
        alert.showAndWait();
    }

    public void updateCards(Map<String, Object> statistik) {
        // Update summary cards dengan data dari controller
    }
}
