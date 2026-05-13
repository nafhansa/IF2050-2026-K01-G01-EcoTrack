package com.ecotrack.boundary;

import com.ecotrack.controller.DataPohonController;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.FileManager;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import com.ecotrack.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;

import java.io.File;

public class FormDataPohon {

    private Object dataInput;

    private final DataPohonController controller;
    private Stage modalStage;
    private String fileFotoPath;
    private boolean isEditMode = false;
    private DataPohon existingData;

    public FormDataPohon(DataPohonController controller) {
        this.controller = controller;
    }

    public void setEditData(DataPohon data) {
        this.isEditMode = true;
        this.existingData = data;
    }

    public void tampilkanForm() {
        // Algo-045
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

        Button btnUpload = new Button("Unggah Foto");
        btnUpload.setStyle("-fx-border-color: #E0E0E0; -fx-background-color: transparent; -fx-text-fill: #424242");
        btnUpload.setOnAction(e -> unggahFoto());

        Button btnBatal = new Button("Batal");
        btnBatal.setStyle("-fx-border-color: #E0E0E0; -fx-background-color: transparent; -fx-text-fill: #9E9E9E; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnBatal.setOnAction(e -> tutupModal());

        Button btnTambah = new Button("Tambah Pohon");
        btnTambah.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);

        HBox buttonBox = new HBox(16, btnBatal, btnTambah);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        form.getChildren().addAll(title, sub,
            new Label("Nama Pohon"), fieldNama,
            new Label("Usia Pohon (tahun)"), fieldUsia,
            new Label("Kapasitas Serapan Karbon (kg/tahun)"), fieldSerapan,
            btnUpload,
            buttonBox);

        Scene scene = new Scene(form, UIConstants.MODAL_WIDTH, 500);
        modalStage.setScene(scene);
        modalStage.showAndWait();

        if (isEditMode) {
            fieldNama.setText(existingData.getNamaPohon());
            fieldUsia.setText(String.valueOf(existingData.getUsia()));
            fieldSerapan.setText(String.valueOf(existingData.getSerapanKarbon()));
            title.setText("Edit Data Pohon");
            btnTambah.setText("Simpan Perubahan");
        }

        btnTambah.setOnAction(e -> {
            DataPohon data = isEditMode ? existingData : new DataPohon();
            data.setNamaPohon(fieldNama.getText());
            try {
                data.setUsia(Integer.parseInt(fieldUsia.getText()));
            } catch (NumberFormatException ex) {
                data.setUsia(0);
            }
            try {
                data.setSerapanKarbon(Float.parseFloat(fieldSerapan.getText()));
            } catch (NumberFormatException ex) {
                data.setSerapanKarbon(0);
            }
            if (fileFotoPath != null) {
                data.setFileFoto(fileFotoPath);
            }
            if (isEditMode) {
                controller.ubahDataPohon(data);
                tampilkanStatus("Berhasil memperbarui data!");
            } else {
                controller.simpanDataPohon(data);
                tampilkanStatus("Berhasil menambah pohon!");
            }
            tutupModal();
        });

    }

    public boolean validasiFile(File file) {
        // Algo-046
        return file != null;
    }

    public boolean validasiData(Object dataInput) {
        // Algo-047
        if (dataInput instanceof DataPohon) {
            DataPohon d = (DataPohon) dataInput;
            return d.getNamaPohon() != null && !d.getNamaPohon().isEmpty()
                && d.getUsia() >= 0
                && d.getSerapanKarbon() >= 0;
        }
        return false;
    }

    public void prosesInputPohon(Object data) {
        // Algo-048
        if (validasiData(data)) {
            String result = controller.prosesInputPohon(data);
            tampilkanStatus(result);
            tutupModal();
        } else {
            tampilkanStatus("Data pohon tidak valid");
        }
    }

    public void simpanDataPohon(Object data) {
        // Algo-049
        String result = controller.simpanDataPohon(data);
        tampilkanStatus(result);
    }

    public void tampilkanStatus(String status) {
        // Algo-050
        Alert alert = new Alert(Alert.AlertType.INFORMATION, status, ButtonType.OK);
        alert.showAndWait();
    }

    public void unggahFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Unggah Foto Pohon");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fileChooser.showOpenDialog(modalStage);
        if (selected != null) {
            fileFotoPath = selected.getAbsolutePath();
            tampilkanStatus("Foto berhasil dipilih: " + selected.getName());
        }
    }

    public void tutupModal() {
        if (modalStage != null) {
            modalStage.close();
        }
    }
}
