package com.ecotrack.boundary;

import com.ecotrack.controller.LaporanPohonController;
import com.ecotrack.entity.LaporanPohon;
import com.ecotrack.util.FileManager;
import com.ecotrack.util.Session;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

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
        setPadding(new Insets(32));

        VBox header = new VBox(4);
        header.setPadding(new Insets(0, 0, 32, 0));

        Label title = new Label("Lapor Kondisi Pohon");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Catat dan pantau kondisi pohon dengan bukti visual");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        header.getChildren().addAll(title, subtitle);
        setTop(header);


        HBox splitPane = new HBox(UIConstants.GAP_CARD);
        splitPane.setAlignment(Pos.TOP_LEFT);

        VBox formPanel = createFormPanel();
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        VBox riwayatPanel = createRiwayatPanel();
        riwayatPanel.setPrefWidth(548);

        splitPane.getChildren().addAll(formPanel, riwayatPanel);
        setCenter(splitPane);

        // Scroll
        ScrollPane scrollPane = new ScrollPane(splitPane);
        
        scrollPane.setStyle("-fx-background: #ECFFFB; -fx-background-color: #ECFFFB; -fx-border-color: #ECFFFB;");
        
        scrollPane.setFitToWidth(true); 
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Tambahkan baris ini!
        
        setCenter(scrollPane);

        tampilkanRiwayatLaporan();
    }

    private VBox createFormPanel() {
        VBox panel = new VBox();
        panel.setPrefWidth(548);
        panel.setMinHeight(885);
        panel.setPadding(new Insets(0));
        panel.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE + "; -fx-border-color: #99F6E4; -fx-background-radius: " + UIConstants.RADIUS_CARD + "; -fx-border-radius: " + UIConstants.RADIUS_CARD + "; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 3, 0, 0, 1);");

        VBox panelHeader = new VBox(4);
        panelHeader.setPadding(new Insets(24));
        panelHeader.setStyle("-fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Form Laporan Baru");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label sub = new Label("Masukkan detail kondisi pohon yang ditemukan");
        sub.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        panelHeader.getChildren().addAll(title, sub);

        VBox formContent = new VBox(24);
        formContent.setPadding(new Insets(24));
        
        // lokasi
        VBox lokasiGroup = new VBox(8);
        Label lblLokasi = new Label("Lokasi");
        lblLokasi.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0F766E;");
        TextField fieldLokasi = new TextField();
        fieldLokasi.setPromptText("Contoh: Taman Kota A, Blok B");
        fieldLokasi.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 14; -fx-border-color: #99F6E4; -fx-border-radius: 14; -fx-padding: 8 12 8 12;");
        lokasiGroup.getChildren().addAll(lblLokasi, fieldLokasi);

        // kondisi
        VBox statusGroup = new VBox(8);
        Label lblStatus = new Label("Status Kondisi");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0F766E;");
        ComboBox<String> comboKondisi = new ComboBox<>();
        comboKondisi.getItems().addAll("Rusak", "Mati", "Ditebang"); 
        comboKondisi.setPromptText("Pilih status kondisi");
        comboKondisi.setMaxWidth(Double.MAX_VALUE);
        comboKondisi.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 14; -fx-border-color: #99F6E4; -fx-border-radius: 14; -fx-padding: 4 8 4 8;");
        statusGroup.getChildren().addAll(lblStatus, comboKondisi);
        
        // catatan
        VBox catatanGroup = new VBox(8);
        Label lblCatatan = new Label("Catatan Tambahan");
        lblCatatan.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0F766E;");
        TextArea catatanArea = new TextArea();
        catatanArea.setPromptText("Deskripsi kondisi atau keterangan tambahan...");
        catatanArea.setStyle("-fx-background-insets: 0; -fx-background-color: #F0FDFA; -fx-background-radius: 14; -fx-border-color: #99F6E4; -fx-border-radius: 14; -fx-padding: 8 12 8 12;");
        catatanGroup.getChildren().addAll(lblCatatan, catatanArea);

        // upload foto
        VBox fotoGroup = new VBox(8);
        Label lblFoto = new Label("Foto Bukti");
        lblFoto.setStyle(" -fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0F766E;");
        
        VBox uploadArea = new VBox(12);
        uploadArea.setAlignment(Pos.CENTER);
        uploadArea.setPrefHeight(282);
        uploadArea.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 16; -fx-border-color: #5EEAD4; -fx-border-radius: 16; -fx-border-width: 2; -fx-border-style: dashed;");
        
        uploadArea.setOnDragOver(this::handleDragOver);
        uploadArea.setOnDragDropped(this::handleDrop);
        uploadArea.setOnMouseClicked(e -> handleFileUpload(uploadArea));

        SVGPath iconSvg = new SVGPath();
        iconSvg.setContent("M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15M17 8L12 3M12 3L7 8M12 3V15");
        iconSvg.setStroke(Color.web("#0D9488"));
        iconSvg.setStrokeWidth(2);
        iconSvg.setFill(Color.TRANSPARENT);
        
        StackPane uploadIcon = new StackPane(iconSvg);
        uploadIcon.setPrefSize(56, 56);
        uploadIcon.setMaxSize(56, 56);
        uploadIcon.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #99F6E4; -fx-border-radius: 14;");

        Label uploadText = new Label("Drag & drop foto di sini");
        uploadText.setStyle("-fx-font-weight: bold; -fx-text-fill: #115E59");

        Label orText = new Label("atau");
        orText.setStyle("-fx-font-size: 12; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        Button browseBtn = new Button("Browse File");
        browseBtn.setStyle("-fx-background-color: white; -fx-text-fill: #0F766E; -fx-font-weight: bold; -fx-background-radius: 14; -fx-border-color: #5EEAD4; -fx-border-radius: 14; -fx-padding: 8 24 8 24;");
        browseBtn.setOnAction(e -> handleFileUpload(uploadArea));
        
        Label formatText = new Label("Mendukung format PNG, JPG, atau JPEG");
        formatText.setStyle("-fx-font-family: 'Plus Jakarta Sans'; -fx-font-size: 12px; -fx-text-fill: #0D9488;");
        
        uploadArea.getChildren().addAll(uploadIcon, uploadText, orText, browseBtn, formatText);
        fotoGroup.getChildren().addAll(lblFoto, uploadArea);

        Button btnSimpan = new Button("Simpan Laporan");
        btnSimpan.setPrefHeight(48);
        btnSimpan.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: #052E16; -fx-font-weight: bold; -fx-background-radius: " + UIConstants.RADIUS_BUTTON + "; -fx-effect: dropshadow(gaussian, rgba(163, 230, 53, 0.4), 8, 0, 0, 4);");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);

        btnSimpan.setOnAction(e -> {
            LaporanPohon data = new LaporanPohon();
            data.setIdUser(Session.getCurrentUser() != null ? Session.getCurrentUser().getIdUser() : null);
            data.setLokasi(fieldLokasi.getText());
            data.setKondisi(comboKondisi.getValue());
            data.setFileFoto(fileFoto);
            dataInput = data;
            prosesLaporan(data, fileFoto != null ? new File(fileFoto) : null);
        });

        formContent.getChildren().addAll(lokasiGroup, statusGroup, catatanGroup, fotoGroup, btnSimpan);
        panel.getChildren().addAll(panelHeader, formContent);

        return panel;
    }

    private VBox createRiwayatPanel() {
       VBox panel = new VBox();
        panel.setPrefWidth(548);
        panel.setMinHeight(885);
        panel.setPadding(new Insets(0));
        panel.setStyle("-fx-background-color: " + UIConstants.CARD_BG_WHITE + "; -fx-border-color: #99F6E4; -fx-background-radius: " + UIConstants.RADIUS_CARD + "; -fx-border-radius: " + UIConstants.RADIUS_CARD + "; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 3, 0, 0, 1);");

        VBox panelHeader = new VBox(4);
        panelHeader.setPadding(new Insets(24));
        panelHeader.setStyle("-fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Riwayat Laporan");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label sub = new Label("Laporan kondisi pohon yang telah Anda buat sebelumnya");
        sub.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        panelHeader.getChildren().addAll(title, sub);

        riwayatList = new ListView<>();
        riwayatList.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent; -fx-padding: 16;");
        VBox.setVgrow(riwayatList, Priority.ALWAYS);

        // empty state
        VBox emptyState = new VBox(16);
        emptyState.setAlignment(Pos.TOP_CENTER);
        emptyState.setPadding(new Insets(64, 0, 0, 0));

        SVGPath docIcon = new SVGPath();
        docIcon.setContent("M9 12H15M9 16H15M17 21H7C5.89543 21 5 20.1046 5 19V5C5 3.89543 5.89543 3 7 3H12.5858C12.851 3 13.1054 3.10536 13.2929 3.29289L18.7071 8.70711C18.8946 8.89464 19 9.149 19 9.41421V19C19 20.1046 18.1046 21 17 21Z");
        docIcon.setStroke(Color.web("#5EEAD4"));
        docIcon.setStrokeWidth(2.67);
        docIcon.setFill(Color.TRANSPARENT);
        
        StackPane iconBox = new StackPane(docIcon);
        iconBox.setPrefSize(64, 64);
        iconBox.setMaxSize(64, 64);
        iconBox.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 16; -fx-border-color: #CCFBF1; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 3, 0, 0, 1);");

        Label emptyTitle = new Label("Belum ada laporan");
        emptyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #115E59; -fx-font-size: 16px;");
        
        Label emptyDesc = new Label("Mulai tambahkan laporan kondisi pohon menggunakan form");
        emptyDesc.setWrapText(true);
        emptyDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        emptyDesc.setMaxWidth(220); 
        emptyDesc.setStyle("-fx-font-weight: normal; -fx-text-fill: #0D9488; -fx-font-size: 14px;");

        emptyState.getChildren().addAll(iconBox, emptyTitle, emptyDesc);
        
        riwayatList.setPlaceholder(emptyState);

        panel.getChildren().addAll(panelHeader, riwayatList);

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
            tampilkanRiwayatLaporan();
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
        List<LaporanPohon> laporanList = controller.ambilDataLaporan();
        riwayatList.getItems().clear();
        if (laporanList != null && !laporanList.isEmpty()) {
            riwayatList.getItems().addAll(laporanList);
            riwayatList.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(LaporanPohon item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        VBox cell = new VBox(4);
                        cell.setPadding(new Insets(12));
                        cell.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 12; -fx-border-color: #CCFBF1; -fx-border-radius: 12; -fx-border-width: 1;");

                        Label kondisiLbl = new Label(item.getKondisi());
                        kondisiLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #115E59;");

                        Label lokasiLbl = new Label(item.getLokasi());
                        lokasiLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #0D9488;");

                        Label karbonLbl = new Label(String.format("Estimasi Karbon: %.1f kg", item.getEstimasiKarbon()));
                        karbonLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #5EEAD4;");

                        cell.getChildren().addAll(kondisiLbl, lokasiLbl, karbonLbl);
                        setGraphic(cell);
                        setText(null);
                    }
                }
            });
        }
    }

    public void handleFileUpload(VBox uploadArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fileChooser.showOpenDialog(uploadArea.getScene().getWindow());
        if (selected != null) {
            fileFoto = selected.getAbsolutePath();
            Label uploadText = (Label) uploadArea.getChildren().get(1);
            uploadText.setText("File: " + selected.getName());
        }
    }

    public void handleDragOver(DragEvent e) {
        if (e.getDragboard().hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    public void handleDrop(DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            String name = file.getName().toLowerCase();
            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                fileFoto = file.getAbsolutePath();
                VBox uploadArea = (VBox) e.getSource();
                Label uploadText = (Label) uploadArea.getChildren().get(1);
                uploadText.setText("File: " + file.getName());
                e.setDropCompleted(true);
            }
        }
        e.consume();
    }
}
