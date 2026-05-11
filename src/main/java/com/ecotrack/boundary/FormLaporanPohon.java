package com.ecotrack.boundary;

import com.ecotrack.controller.LaporanPohonController;
import com.ecotrack.entity.LaporanPohon;
import com.ecotrack.util.FileManager;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.util.List;

public class FormLaporanPohon extends BorderPane {

    private String fileFoto;
    private Object dataInput;

    private final LaporanPohonController controller;
    private VBox contentArea;
    private ListView<LaporanPohon> riwayatList;

    public FormLaporanPohon(LaporanPohonController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        VBox header = new VBox(8);
        header.setPadding(new Insets(UIConstants.PADDING_CONTENT));

        Label title = new Label("Lapor Kondisi Pohon");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Catat dan pantau kondisi pohon dengan bukti visual");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        header.getChildren().addAll(title, subtitle);
        setTop(header);

        HBox splitPane = new HBox(UIConstants.GAP_CARD);
        splitPane.setPadding(new Insets(0, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT, UIConstants.PADDING_CONTENT));

        VBox formPanel = createFormPanel();
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        VBox riwayatPanel = createRiwayatPanel();
        riwayatPanel.setPrefWidth(300);

        splitPane.getChildren().addAll(formPanel, riwayatPanel);
        setCenter(splitPane);

        tampilkanRiwayatLaporan();
    }

    private VBox createFormPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(UIConstants.PADDING_CONTENT));
        panel.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE + "; -fx-background-radius: " + UIConstants.RADIUS_CARD);

        Label title = new Label("Form Laporan Baru");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label sub = new Label("Masukkan detail kondisi pohon yang ditemukan");
        sub.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        TextField fieldLokasi = new TextField();
        fieldLokasi.setPromptText("Contoh: Taman Kota A, Blok B");

        ComboBox<String> comboKondisi = new ComboBox<>();
        comboKondisi.getItems().addAll("rusak", "mati", "ditebang");
        comboKondisi.setPromptText("Pilih status kondisi");

        TextArea catatanArea = new TextArea();
        catatanArea.setPromptText("Deskripsi kondisi atau keterangan tambahan...");
        catatanArea.setPrefRowCount(4);

        VBox uploadArea = new VBox(8);
        uploadArea.setPadding(new Insets(16));
        uploadArea.setStyle("-fx-border-color: #A8E063; -fx-border-style: dashed; -fx-border-width: 2; -fx-border-radius: " + UIConstants.RADIUS_MODAL);
        uploadArea.setOnDragOver(this::handleDragOver);
        uploadArea.setOnDragDropped(this::handleDrop);
        uploadArea.setOnMouseClicked(e -> handleFileUpload());

        Label uploadIcon = new Label("\u2B06");
        uploadIcon.setStyle("-fx-font-size: 32; -fx-text-fill: #9E9E9E");

        Label uploadText = new Label("Drag & drop foto di sini");
        uploadText.setStyle("-fx-font-weight: bold; -fx-text-fill: #424242");

        Label orText = new Label("atau");
        orText.setStyle("-fx-font-size: 12; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        Button browseBtn = new Button("Browse File");
        browseBtn.setStyle("-fx-border-color: #E0E0E0; -fx-background-color: transparent; -fx-text-fill: #424242");

        Label formatText = new Label("Mendukung format PNG, JPG, atau JPEG");
        formatText.setStyle("-fx-font-size: 10; -fx-text-fill: #9E9E9E");

        uploadArea.getChildren().addAll(uploadIcon, uploadText, orText, browseBtn, formatText);

        Button btnSimpan = new Button("Simpan Laporan");
        btnSimpan.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON + "; -fx-pref-height: " + UIConstants.BUTTON_HEIGHT);
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setOnAction(e -> {
            LaporanPohon data = new LaporanPohon();
            data.setLokasi(fieldLokasi.getText());
            data.setKondisi(comboKondisi.getValue());
            data.setFileFoto(fileFoto);
            dataInput = data;
            prosesLaporan(data, fileFoto != null ? new File(fileFoto) : null);
        });

        panel.getChildren().addAll(title, sub,
            new Label("Lokasi"), fieldLokasi,
            new Label("Status Kondisi"), comboKondisi,
            new Label("Catatan Tambahan"), catatanArea,
            new Label("Foto Bukti"), uploadArea,
            btnSimpan);

        return panel;
    }

    private VBox createRiwayatPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(UIConstants.PADDING_CONTENT));
        panel.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE + "; -fx-background-radius: " + UIConstants.RADIUS_CARD);

        Label title = new Label("Riwayat Laporan");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        riwayatList = new ListView<>();
        riwayatList.setPlaceholder(new Label("Belum ada laporan"));

        panel.getChildren().addAll(title, riwayatList);
        return panel;
    }

    public boolean validasiData(Object dataInput) {
        // Algo-019
        if (dataInput instanceof LaporanPohon) {
            LaporanPohon l = (LaporanPohon) dataInput;
            return l.getKondisi() != null && l.getLokasi() != null;
        }
        return false;
    }

    public void prosesLaporan(Object dataInput, File fileFoto) {
        // Algo-020
        if (validasiData(dataInput)) {
            if (dataInput instanceof LaporanPohon) {
                LaporanPohon l = (LaporanPohon) dataInput;
                if (fileFoto != null) {
                    l.setFileFoto(fileFoto.getAbsolutePath());
                }
            }
            String result = controller.prosesLaporan((LaporanPohon) dataInput);
            tampilkanStatus(result);
        } else {
            tampilkanStatus("Data laporan belum lengkap");
        }
    }

    public void tampilkanStatus(String status) {
        // Algo-021
        Alert alert = new Alert(Alert.AlertType.INFORMATION, status, ButtonType.OK);
        alert.showAndWait();
    }

    public void tampilkanRiwayatLaporan() {
        riwayatList.getItems().clear();
    }

    public void handleFileUpload() {
        // FileChooser implementation
    }

    public void handleDragOver(DragEvent e) {
        // Highlight border on drag over
    }

    public void handleDrop(DragEvent e) {
        // Handle dropped file
    }
}
