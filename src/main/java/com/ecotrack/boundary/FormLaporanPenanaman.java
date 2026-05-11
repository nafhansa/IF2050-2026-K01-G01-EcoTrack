package com.ecotrack.boundary;

import com.ecotrack.controller.PenanamanController;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormLaporanPenanaman {

    private final PenanamanController controller;
    private Stage modalStage;

    public FormLaporanPenanaman(PenanamanController controller) {
        this.controller = controller;
    }

    public void tampilkanForm() {
        // Algo-041
        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Catat Penanaman Baru");

        VBox form = new VBox(16);
        form.setPadding(new Insets(UIConstants.PADDING_MODAL));
        form.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE);

        Label title = new Label("Form Penanaman Baru");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_MODAL_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label sub = new Label("Masukkan data kegiatan penanaman pohon");
        sub.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        TextField fieldLokasi = new TextField();
        fieldLokasi.setPromptText("Contoh: Taman Kota A, Blok B");

        TextField fieldJenis = new TextField();
        fieldJenis.setPromptText("Contoh: Mahoni");

        TextField fieldJumlah = new TextField();
        fieldJumlah.setPromptText("Contoh: 10");

        DatePicker datePick = new DatePicker();
        datePick.setPromptText("Pilih tanggal penanaman");

        Button btnBatal = new Button("Batal");
        btnBatal.setStyle("-fx-border-color: #E0E0E0; -fx-background-color: transparent; -fx-text-fill: #9E9E9E; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnBatal.setOnAction(e -> tutupModal());

        Button btnSimpan = new Button("Simpan Penanaman");
        btnSimpan.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON);
        btnSimpan.setOnAction(e -> {
            DataPenanaman data = new DataPenanaman();
            data.setLokasi(fieldLokasi.getText());
            data.setJenisPohon(fieldJenis.getText());
            try {
                data.setJumlahPohon(Integer.parseInt(fieldJumlah.getText()));
            } catch (NumberFormatException ex) {
                data.setJumlahPohon(0);
            }
            java.util.Date tanggal = java.sql.Date.valueOf(datePick.getValue());
            data.setTanggal(tanggal);

            kumpulkanDataInput(data);
        });

        HBox buttonBox = new HBox(16, btnBatal, btnSimpan);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(title, sub,
            new Label("Lokasi"), fieldLokasi,
            new Label("Jenis Pohon"), fieldJenis,
            new Label("Jumlah Pohon"), fieldJumlah,
            new Label("Tanggal Penanaman"), datePick,
            buttonBox);

        Scene scene = new Scene(form, UIConstants.MODAL_WIDTH_PENANAMAN, 480);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    public void kumpulkanDataInput(DataPenanaman data) {
        // Algo-042
        if (validasiData(data)) {
            String result = controller.simpanDataPenanaman(data);
            tampilkanStatus(result);
            tutupModal();
        } else {
            tampilkanStatus("Data penanaman tidak valid");
        }
    }

    public boolean validasiData(DataPenanaman data) {
        // Algo-043
        if (data.getLokasi() == null || data.getLokasi().isEmpty()) return false;
        if (data.getJenisPohon() == null || data.getJenisPohon().isEmpty()) return false;
        if (data.getJumlahPohon() <= 0) return false;
        return true;
    }

    public void tampilkanStatus(String status) {
        // Algo-044
        Alert alert = new Alert(Alert.AlertType.INFORMATION, status, ButtonType.OK);
        alert.showAndWait();
    }

    public void tutupModal() {
        if (modalStage != null) {
            modalStage.close();
        }
    }
}
