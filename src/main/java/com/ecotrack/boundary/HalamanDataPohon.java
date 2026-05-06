package com.ecotrack.boundary;

import com.ecotrack.controller.PohonController;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class HalamanDataPohon extends BorderPane {

    private final PohonController controller;
    private TableView<DataPohon> tabelPohon;
    private VBox contentArea;

    public HalamanDataPohon(PohonController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        // Header
        VBox header = new VBox(8);
        header.setPadding(new Insets(UIConstants.PADDING_CONTENT));

        Label title = new Label("Data Pohon");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Kelola master data referensi jenis pohon");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        Button btnTambah = new Button("+ Tambah Pohon");
        btnTambah.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnTambah.setOnAction(e -> tampilkanModalTambah());

        HBox headerRow = new HBox(16);
        headerRow.getChildren().addAll(title, btnTambah);
        HBox.setHgrow(btnTambah, Priority.ALWAYS);

        header.getChildren().addAll(headerRow, subtitle);
        setTop(header);

        // Tabel
        contentArea = new VBox(UIConstants.GAP_CARD);
        contentArea.setPadding(new Insets(0, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT));

        tabelPohon = new TableView<>();
        setupTabel();

        contentArea.getChildren().add(tabelPohon);
        setCenter(contentArea);

        tampilkanDaftarPohon();
    }

    private void setupTabel() {
        TableColumn<DataPohon, String> colNama = new TableColumn<>("NAMA POHON");
        colNama.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNamaPohon()));

        TableColumn<DataPohon, Integer> colUsia = new TableColumn<>("USIA (TAHUN)");
        colUsia.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getUsia()));

        TableColumn<DataPohon, Float> colSerapan = new TableColumn<>("SERAPAN KARBON (KG/TAHUN)");
        colSerapan.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getKapasitasSerapanKarbon()));

        TableColumn<DataPohon, Void> colAksi = new TableColumn<>("AKSI");
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");

            {
                btnEdit.setStyle("-fx-text-fill: " + UIConstants.ACCENT_TEAL);
                btnDelete.setStyle("-fx-text-fill: " + UIConstants.ACCENT_RED);
                btnEdit.setOnAction(e -> {
                    DataPohon data = getTableView().getItems().get(getIndex());
                    // Edit action
                });
                btnDelete.setOnAction(e -> {
                    DataPohon data = getTableView().getItems().get(getIndex());
                    controller.hapusDataPohon(data.getIdPohon());
                    tampilkanDaftarPohon();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnEdit, btnDelete);
                    setGraphic(box);
                }
            }
        });

        tabelPohon.getColumns().addAll(colNama, colUsia, colSerapan, colAksi);
    }

    public void tampilkanDaftarPohon() {
        List<DataPohon> data = controller.getDataPohon();
        tabelPohon.getItems().setAll(data);
    }

    public void tampilkanDetailPohon(String idPohon) {
        DataPohon detail = controller.getDetailPohon(idPohon);
        // Show detail
    }

    public void tampilkanModalTambah() {
        // Show modal untuk tambah pohon
    }

    public void tutupModal() {
        // Close modal
    }
}
