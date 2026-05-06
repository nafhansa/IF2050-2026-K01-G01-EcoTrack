package com.ecotrack.boundary;

import com.ecotrack.controller.PohonController;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FormDataPohon {

    private final PohonController controller;
    private Stage modalStage;

    public FormDataPohon(PohonController controller) {
        this.controller = controller;
    }

    public void tampilkanForm() {
        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Tambah Data Pohon");

        VBox form = new VBox(16);
        form.setPadding(new Insets(UIConstants.PADDING_MODAL));
        form.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE);

        Label title = new Label("Tambah Data Pohon");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_MODAL_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label sub = new Label("Masukkan informasi pohon baru ke dalam database");
        sub.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        TextField fieldNama = new TextField();
        fieldNama.setPromptText("Contoh: Mahoni");

        TextField fieldUsia = new TextField();
        fieldUsia.setPromptText("Contoh: 5");

        TextField fieldSerapan = new TextField();
        fieldSerapan.setPromptText("Contoh: 28.5");

        Button btnBatal = new Button("Batal");
        btnBatal.setStyle("-fx-border-color: #E0E0E0; -fx-background-color: transparent; -fx-text-fill: #9E9E9E; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnBatal.setOnAction(e -> tutupModal());

        Button btnTambah = new Button("Tambah Pohon");
        btnTambah.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnTambah.setOnAction(e -> {
            DataPohon data = new DataPohon();
            data.setNamaPohon(fieldNama.getText());
            try {
                data.setUsia(Integer.parseInt(fieldUsia.getText()));
            } catch (NumberFormatException ex) {
                data.setUsia(null);
            }
            try {
                data.setKapasitasSerapanKarbon(Float.parseFloat(fieldSerapan.getText()));
            } catch (NumberFormatException ex) {
                data.setKapasitasSerapanKarbon(null);
            }
            controller.simpanDataPohon(data);
            tutupModal();
        });

        HBox buttonBox = new HBox(16, btnBatal, btnTambah);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        form.getChildren().addAll(title, sub,
            new Label("Nama Pohon"), fieldNama,
            new Label("Usia Pohon (tahun)"), fieldUsia,
            new Label("Kapasitas Serapan Karbon (kg/tahun)"), fieldSerapan,
            buttonBox);

        Scene scene = new Scene(form, UIConstants.MODAL_WIDTH, 450);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    public void unggahFoto() {
        // FileChooser implementation
    }

    public void isiDataPohon() {
        // Populate form with existing data for edit
    }

    public void tutupModal() {
        if (modalStage != null) {
            modalStage.close();
        }
    }
}
