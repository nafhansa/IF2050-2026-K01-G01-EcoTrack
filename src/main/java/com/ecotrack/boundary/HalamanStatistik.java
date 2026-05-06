package com.ecotrack.boundary;

import com.ecotrack.controller.StatistikController;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class HalamanStatistik extends BorderPane {

    private final StatistikController controller;
    private ComboBox<String> filterPeriode;
    private VBox contentArea;

    public HalamanStatistik(StatistikController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        // Header
        VBox header = new VBox(8);
        header.setPadding(new Insets(UIConstants.PADDING_CONTENT));

        Label title = new Label("Dashboard Statistik");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Monitoring serapan karbon dan penghijauan kota");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        filterPeriode = new ComboBox<>();
        filterPeriode.getItems().addAll("Bulanan", "Tahunan", "Semua");
        filterPeriode.setValue("Bulanan");
        filterPeriode.setOnAction(e -> tampilkanGrafik(filterPeriode.getValue()));

        header.getChildren().addAll(title, subtitle);
        setTop(header);

        // Content area
        contentArea = new VBox(UIConstants.GAP_CARD);
        contentArea.setPadding(new Insets(0, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT));
        setCenter(contentArea);

        tampilkanGrafik("Bulanan");
    }

    public void tampilkanGrafik(String filterPeriode) {
        Map<String, Object> statistik = controller.hitungStatistikTotal(filterPeriode);
        updateCards(statistik);
    }

    public void pilihFilterPeriode() {
        // Handled by ComboBox action
    }

    public void updateCards(Map<String, Object> statistik) {
        // Update summary cards dengan data dari controller
    }
}
