package com.ecotrack.boundary;

import com.ecotrack.controller.PenanamanController;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class HalamanDataPenanaman extends BorderPane {

    private final PenanamanController controller;
    private TableView<DataPenanaman> tabelPenanaman;
    private VBox contentArea;

    public HalamanDataPenanaman(PenanamanController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        // Header
        VBox header = new VBox(8);
        header.setPadding(new Insets(UIConstants.PADDING_CONTENT));

        Label title = new Label("Data Penanaman");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Catat dan kelola riwayat penanaman pohon");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        Button btnCatat = new Button("+ Catat Penanaman Baru");
        btnCatat.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnCatat.setOnAction(e -> tampilkanFormInput());

        HBox headerRow = new HBox(16);
        headerRow.getChildren().addAll(title, btnCatat);
        HBox.setHgrow(btnCatat, Priority.ALWAYS);

        header.getChildren().addAll(headerRow, subtitle);
        setTop(header);

        // Content
        contentArea = new VBox(UIConstants.GAP_CARD);
        contentArea.setPadding(new Insets(0, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT));

        Label sectionTitle = new Label("Riwayat Penanaman");
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        tabelPenanaman = new TableView<>();
        setupTabel();

        contentArea.getChildren().addAll(sectionTitle, tabelPenanaman);
        setCenter(contentArea);

        tampilkanDaftarPenanaman();
    }

    private void setupTabel() {
        TableColumn<DataPenanaman, String> colTanggal = new TableColumn<>("TANGGAL");
        colTanggal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getTanggalPenanaman().toString()
        ));

        TableColumn<DataPenanaman, String> colLokasi = new TableColumn<>("LOKASI");
        colLokasi.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getLokasi()
        ));

        TableColumn<DataPenanaman, String> colJenis = new TableColumn<>("JENIS POHON");
        colJenis.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getJenisPohon()
        ));

        TableColumn<DataPenanaman, Integer> colJumlah = new TableColumn<>("JUMLAH");
        colJumlah.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
            data.getValue().getJumlahPohon()
        ));

        TableColumn<DataPenanaman, Float> colSerapan = new TableColumn<>("EST. SERAPAN (KG/THN)");
        colSerapan.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
            data.getValue().getEstimasiKarbon()
        ));

        tabelPenanaman.getColumns().addAll(colTanggal, colLokasi, colJenis, colJumlah, colSerapan);
    }

    public void tampilkanDaftarPenanaman() {
        List<DataPenanaman> data = controller.getDataPenanaman();
        tabelPenanaman.getItems().setAll(data);
    }

    public void tampilkanFormInput() {
        // Show modal form input penanaman
    }

    public void tutupModal() {
        // Close modal
    }

    public void populateDropdownJenisPohon() {
        // Load jenis pohon dari PohonRepository
    }
}
