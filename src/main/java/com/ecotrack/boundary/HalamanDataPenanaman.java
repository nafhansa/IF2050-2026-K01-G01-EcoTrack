package com.ecotrack.boundary;

import com.ecotrack.controller.PenanamanController;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.text.SimpleDateFormat;
import java.util.List;

public class HalamanDataPenanaman extends BorderPane {

    // Halaman riwayat penanaman.
    // Menampilkan ringkasan total (kartu) dan tabel riwayat; user dapat menambah
    // pencatatan penanaman baru atau menghapus riwayat tertentu.

    private final PenanamanController controller;
    private TableView<DataPenanaman> tabelPenanaman;
    private Label lblTotalPohonDitanam;
    private Label lblEstimasiSerapan;

    public HalamanDataPenanaman(PenanamanController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        // Susun komponen: header -> kartu ringkasan -> tabel.
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);
        setPadding(new Insets(32));

        VBox mainContainer = new VBox(32);

        // header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.BOTTOM_LEFT);
        headerBox.setPadding(new Insets(0, 0, 0, 0)); 

        VBox titleBox = new VBox(4);
        Label title = new Label("Data Penanaman");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);
        
        Label subtitle = new Label("Catat dan kelola riwayat penanaman pohon");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);
        
        titleBox.getChildren().addAll(title, subtitle);

        Button btnTambah = new Button("Catat Penanaman Baru");
        btnTambah.setGraphic(createPlusIcon());
        btnTambah.setGraphicTextGap(8);
        btnTambah.setStyle("-fx-background-color: #A3E635; -fx-text-fill: #052E16; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 14px; -fx-pref-height: 36px; -fx-padding: 0 16px 0 12px; -fx-effect: dropshadow(gaussian, rgba(163, 230, 53, 0.30), 6, 0, 0, 4); -fx-cursor: hand;");
        btnTambah.setOnAction(e -> tampilkanModalTambah());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleBox, spacer, btnTambah);

        // card
        HBox statsBox = new HBox(24);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        VBox cardPohon = new VBox();
        cardPohon.setStyle("-fx-background-color: linear-gradient(to bottom right, #B0E559, #84CC16); -fx-background-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(132, 204, 22, 0.30), 6, 0, 0, 4);");
        cardPohon.setPadding(new Insets(24));
        HBox.setHgrow(cardPohon, Priority.ALWAYS);

        HBox cardPohonContent = new HBox();
        cardPohonContent.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitleCard1 = new Label("Total Pohon Ditanam");
        lblTitleCard1.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: rgba(5, 46, 22, 0.7);");
        lblTotalPohonDitanam = new Label("0");
        lblTotalPohonDitanam.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #052E16; -fx-padding: -4px 0 0 0;");
        Label lblSubCard1 = new Label("Akumulasi seluruh pencatatan");
        lblSubCard1.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: rgba(5, 46, 22, 0.8); -fx-padding: 16px 0 0 0;");
        
        VBox textCard1 = new VBox(0, lblTitleCard1, lblTotalPohonDitanam, lblSubCard1);
        
        Region cardSpacer1 = new Region();
        HBox.setHgrow(cardSpacer1, Priority.ALWAYS);
        
        StackPane iconBg1 = new StackPane(createTreeLeafIcon());
        iconBg1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 14px; -fx-min-width: 56px; -fx-min-height: 56px; -fx-max-width: 56px; -fx-max-height: 56px;");
        
        cardPohonContent.getChildren().addAll(textCard1, cardSpacer1, iconBg1);
        cardPohon.getChildren().add(cardPohonContent);

        VBox cardSerapan = new VBox();
        cardSerapan.setStyle("-fx-background-color: linear-gradient(to bottom right, #0D9488, #0F766E); -fx-background-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(13, 148, 136, 0.30), 6, 0, 0, 4);");
        cardSerapan.setPadding(new Insets(24));
        HBox.setHgrow(cardSerapan, Priority.ALWAYS);

        HBox cardSerapanContent = new HBox();
        cardSerapanContent.setAlignment(Pos.CENTER_LEFT);

        Label lblTitleCard2 = new Label("Estimasi Total Serapan");
        lblTitleCard2.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #CBFBF1;");
        lblEstimasiSerapan = new Label("0");
        lblEstimasiSerapan.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: -4px 0 0 0;");
        Label lblSubCard2 = new Label("kg CO₂/tahun kapasitas tahunan");
        lblSubCard2.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: #CBFBF1; -fx-padding: 16px 0 0 0;");
        
        VBox textCard2 = new VBox(0, lblTitleCard2, lblEstimasiSerapan, lblSubCard2);
        
        Region cardSpacer2 = new Region();
        HBox.setHgrow(cardSpacer2, Priority.ALWAYS);
        
        StackPane iconBg2 = new StackPane(createWindIcon());
        iconBg2.setStyle("-fx-background-color: rgba(204, 251, 241, 0.2); -fx-background-radius: 14px; -fx-min-width: 56px; -fx-min-height: 56px; -fx-max-width: 56px; -fx-max-height: 56px;");

        cardSerapanContent.getChildren().addAll(textCard2, cardSpacer2, iconBg2);
        cardSerapan.getChildren().add(cardSerapanContent);

        statsBox.getChildren().addAll(cardPohon, cardSerapan);

        // tabel
        VBox tableContainer = new VBox();
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-color: #99F6E4; -fx-border-width: 1px; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 3, 0, 0, 1);");
        
        Label tableTitle = new Label("Riwayat Penanaman");
        tableTitle.setPrefHeight(83);
        tableTitle.setAlignment(Pos.CENTER_LEFT);
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #115E59; -fx-font-family: 'Plus Jakarta Sans', sans-serif; -fx-padding: 0 24px; -fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 0 0 1px 0; -fx-background-radius: 16px 16px 0 0;");
        tableTitle.setMaxWidth(Double.MAX_VALUE);

        tabelPenanaman = new TableView<>();
        tabelPenanaman.setStyle("-fx-base: #F0FDFA; -fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        
        String css = ".table-view .column-header-background { -fx-background-color: #F0FDFA; -fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 0 0 1px 0; -fx-padding: 0; }" +
                     ".table-view .column-header { -fx-background-color: transparent; -fx-border-color: transparent; -fx-size: 48px; }" +
                     ".table-view .filler { -fx-background-color: transparent; }" +
                     ".table-view .corner { -fx-background-color: transparent; }" +
                     ".table-view .table-cell { -fx-border-color: transparent; -fx-padding: 0; }";
        tabelPenanaman.getStylesheets().add("data:text/css," + css.replace(" ", "%20").replace("\n", "%0A"));

        tabelPenanaman.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabelPenanaman, Priority.ALWAYS);
        
        setupTabel();

        tableContainer.getChildren().addAll(tableTitle, tabelPenanaman);
        
        mainContainer.getChildren().addAll(headerBox, statsBox, tableContainer);
        setCenter(mainContainer);

        ambilDataPenanaman();
    }

    private void setupTabel() {
        // Setup kolom tabel + row factory agar styling sesuai desain.
        tabelPenanaman.setRowFactory(tv -> {
            TableRow<DataPenanaman> row = new TableRow<>();
            row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 1px; -fx-cell-size: 65px;");
            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (isNowHovered && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #F0FDFA; -fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 1px; -fx-cell-size: 65px;");
                } else {
                    row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #CCFBF1 transparent; -fx-border-width: 1px; -fx-cell-size: 65px;");
                }
            });
            return row;
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        TableColumn<DataPenanaman, java.util.Date> colTanggal = new TableColumn<>();
        colTanggal.setGraphic(createHeaderLabel("TANGGAL"));
        colTanggal.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTanggal()));
        colTanggal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Label lbl = new Label(dateFormat.format(item));
                    lbl.setStyle("-fx-text-fill: #0D9488; -fx-font-weight: 600; -fx-font-size: 14px;");
                    lbl.setPadding(new Insets(0, 0, 0, 16));
                    setGraphic(lbl);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<DataPenanaman, String> colLokasi = new TableColumn<>();
        colLokasi.setGraphic(createHeaderLabel("LOKASI"));
        colLokasi.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLokasi()));
        colLokasi.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);
                    
                    StackPane iconBg = new StackPane(createPinIcon());
                    iconBg.setStyle("-fx-background-color: #CCFBF1; -fx-border-color: #99F6E4; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-min-width: 32px; -fx-min-height: 32px;");
                    
                    Label text = new Label(item);
                    text.setStyle("-fx-text-fill: #115E59; -fx-font-weight: bold; -fx-font-size: 14px;");
                    box.getChildren().addAll(iconBg, text);
                    setGraphic(box);
                }
            }
        });

        TableColumn<DataPenanaman, String> colJenis = new TableColumn<>();
        colJenis.setGraphic(createHeaderLabel("JENIS POHON"));
        colJenis.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getJenisPohon()));
        colJenis.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Label pill = new Label(item);
                    pill.setStyle("-fx-background-color: #ECFCCB; -fx-border-color: #D9F99D; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-text-fill: #3F6212; -fx-font-weight: 600; -fx-padding: 4px 12px; -fx-font-size: 14px;");
                    setGraphic(pill);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<DataPenanaman, Integer> colJumlah = new TableColumn<>();
        colJumlah.setGraphic(createHeaderLabel("JUMLAH"));
        colJumlah.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getJumlahPohon()));
        colJumlah.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Text val = new Text(String.valueOf(item) + " ");
                    val.setStyle("-fx-fill: #115E59; -fx-font-weight: bold; -fx-font-size: 18px;");
                    
                    Text unit = new Text("pohon");
                    unit.setStyle("-fx-fill: #0D9488; -fx-font-weight: 600; -fx-font-size: 12px;");
                    
                    TextFlow flow = new TextFlow(val, unit);
                    setGraphic(flow);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<DataPenanaman, Float> colSerapan = new TableColumn<>();
        colSerapan.setGraphic(createHeaderLabel("EST. SERAPAN (KG/THN)"));
        colSerapan.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEstimasiKarbon()));
        colSerapan.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    String formatted = String.format("%,.0f ", item).replace(',', '.');
                    Text val = new Text(formatted);
                    val.setStyle("-fx-fill: #0F766E; -fx-font-weight: 800; -fx-font-size: 18px;");
                    
                    Text unit = new Text("kg");
                    unit.setStyle("-fx-fill: #0D9488; -fx-font-weight: 600; -fx-font-size: 12px;");
                    
                    TextFlow flow = new TextFlow(val, unit);
                    setGraphic(flow);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<DataPenanaman, Void> colAksi = new TableColumn<>();
        Label headerAksi = createHeaderLabel(" ");
        headerAksi.setMaxWidth(Double.MAX_VALUE);
        headerAksi.setAlignment(Pos.CENTER_RIGHT); 
        colAksi.setGraphic(headerAksi);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button();

            {
                btnDelete.setGraphic(createDeleteIcon());
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 8px; -fx-background-radius: 10px;");

                btnDelete.hoverProperty().addListener((obs, oldV, newV) -> {
                    if (newV) btnDelete.setStyle("-fx-background-color: #FFE4E6; -fx-cursor: hand; -fx-padding: 8px; -fx-background-radius: 10px;");
                    else btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 8px; -fx-background-radius: 10px;");
                });

                btnDelete.setOnAction(e -> {
                    DataPenanaman data = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data penanaman di " + data.getLokasi() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) hapusData(data.getIdPenanaman());
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    HBox box = new HBox(8, btnDelete);
                    box.setAlignment(Pos.CENTER_RIGHT); 
                    box.setPadding(new Insets(0, 16, 0, 0));
                    setGraphic(box);
                }
            }
        });

        tabelPenanaman.getColumns().addAll(colTanggal, colLokasi, colJenis, colJumlah, colSerapan, colAksi);
    }

    private Label createHeaderLabel(String text) {
        Label lbl = new Label(text.toUpperCase());
        lbl.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12px; -fx-font-weight: bold; -fx-letter-spacing: 0.60px;");
        return lbl;
    }

    private Group createPlusIcon() {
        SVGPath p1 = new SVGPath();
        p1.setContent("M3.33337 8H12.6667");
        p1.setStroke(Color.web("#052E16"));
        p1.setStrokeWidth(1.33333);
        p1.setStrokeLineCap(StrokeLineCap.ROUND);
        p1.setStrokeLineJoin(StrokeLineJoin.ROUND);

        SVGPath p2 = new SVGPath();
        p2.setContent("M8 3.33337V12.6667");
        p2.setStroke(Color.web("#052E16"));
        p2.setStrokeWidth(1.33333);
        p2.setStrokeLineCap(StrokeLineCap.ROUND);
        p2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        
        return new Group(p1, p2);
    }

    private Group createTreeLeafIcon() {
        SVGPath p1 = new SVGPath();
        p1.setContent("M12.8333 23.3334C10.7847 23.3396 8.80862 22.5756 7.29693 21.193C5.78524 19.8104 4.84839 17.9102 4.67219 15.8692C4.49598 13.8282 5.0933 11.7955 6.34567 10.1742C7.59804 8.55302 9.41398 7.46169 11.4333 7.11671C18.0833 5.83337 19.8333 5.22671 22.1667 2.33337C23.3333 4.66671 24.5 7.21004 24.5 11.6667C24.5 18.0834 18.9233 23.3334 12.8333 23.3334Z");
        p1.setStroke(Color.web("#0A7637"));
        p1.setStrokeWidth(2.33333);
        p1.setStrokeLineCap(StrokeLineCap.ROUND);
        p1.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p1.setFill(Color.TRANSPARENT);

        SVGPath p2 = new SVGPath();
        p2.setContent("M2.33331 24.5C2.33331 21 4.49165 18.2467 8.25998 17.5C11.0833 16.94 14 15.1667 15.1666 14");
        p2.setStroke(Color.web("#0A7637"));
        p2.setStrokeWidth(2.33333);
        p2.setStrokeLineCap(StrokeLineCap.ROUND);
        p2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p2.setFill(Color.TRANSPARENT);

        return new Group(p1, p2);
    }

    private Group createWindIcon() {
        SVGPath p1 = new SVGPath();
        p1.setContent("M14.9334 22.8666C15.2292 23.0885 15.5734 23.2368 15.9378 23.2995C16.3021 23.3622 16.6762 23.3374 17.0291 23.2272C17.382 23.1169 17.7037 22.9244 17.9676 22.6655C18.2315 22.4065 18.4301 22.0886 18.547 21.7378C18.6639 21.3871 18.6958 21.0136 18.64 20.6481C18.5843 20.2826 18.4425 19.9356 18.2263 19.6356C18.0101 19.3357 17.7257 19.0914 17.3966 18.923C17.0675 18.7545 16.7031 18.6666 16.3334 18.6666H2.33337");
        p1.setStroke(Color.web("#99F6E4"));
        p1.setStrokeWidth(2.33333);
        p1.setStrokeLineCap(StrokeLineCap.ROUND);
        p1.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p1.setFill(Color.TRANSPARENT);

        SVGPath p2 = new SVGPath();
        p2.setContent("M20.4167 9.33329C20.715 8.93556 21.11 8.62068 21.5642 8.41856C22.0185 8.21643 22.5168 8.13376 23.012 8.17841C23.5071 8.22306 23.9827 8.39354 24.3934 8.67366C24.8041 8.95378 25.1364 9.33424 25.3588 9.77892C25.5811 10.2236 25.6861 10.7177 25.6638 11.2144C25.6414 11.7111 25.4925 12.1938 25.2311 12.6167C24.9697 13.0396 24.6046 13.3887 24.1704 13.6308C23.7361 13.8729 23.2472 14 22.75 14H2.33337");
        p2.setStroke(Color.web("#99F6E4"));
        p2.setStrokeWidth(2.33333);
        p2.setStrokeLineCap(StrokeLineCap.ROUND);
        p2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p2.setFill(Color.TRANSPARENT);

        SVGPath p3 = new SVGPath();
        p3.setContent("M11.4334 5.13329C11.7292 4.91146 12.0734 4.76309 12.4378 4.70041C12.8021 4.63772 13.1762 4.66252 13.5291 4.77276C13.882 4.883 14.2037 5.07552 14.4676 5.33446C14.7315 5.5934 14.9301 5.91135 15.047 6.26209C15.1639 6.61284 15.1958 6.98635 15.14 7.35185C15.0843 7.71734 14.9425 8.06435 14.7263 8.36428C14.5101 8.66422 14.2257 8.90849 13.8966 9.07696C13.5675 9.24544 13.2031 9.33329 12.8334 9.33329H2.33337");
        p3.setStroke(Color.web("#99F6E4"));
        p3.setStrokeWidth(2.33333);
        p3.setStrokeLineCap(StrokeLineCap.ROUND);
        p3.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p3.setFill(Color.TRANSPARENT);

        return new Group(p1, p2, p3);
    }

    private Group createPinIcon() {
        SVGPath p1 = new SVGPath();
        p1.setContent("M13.3334 6.66659C13.3334 9.99525 9.64069 13.4619 8.40069 14.5326C8.28517 14.6194 8.14455 14.6664 8.00002 14.6664C7.85549 14.6664 7.71487 14.6194 7.59935 14.5326C6.35935 13.4619 2.66669 9.99525 2.66669 6.66659C2.66669 5.2521 3.22859 3.89554 4.22878 2.89535C5.22898 1.89516 6.58553 1.33325 8.00002 1.33325C9.41451 1.33325 10.7711 1.89516 11.7713 2.89535C12.7715 3.89554 13.3334 5.2521 13.3334 6.66659Z");
        p1.setStroke(Color.web("#0F766E"));
        p1.setStrokeWidth(1.33333);
        p1.setStrokeLineCap(StrokeLineCap.ROUND);
        p1.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p1.setFill(Color.TRANSPARENT);

        SVGPath p2 = new SVGPath();
        p2.setContent("M8 8.66675C9.10457 8.66675 10 7.77132 10 6.66675C10 5.56218 9.10457 4.66675 8 4.66675C6.89543 4.66675 6 5.56218 6 6.66675C6 7.77132 6.89543 8.66675 8 8.66675Z");
        p2.setStroke(Color.web("#0F766E"));
        p2.setStrokeWidth(1.33333);
        p2.setStrokeLineCap(StrokeLineCap.ROUND);
        p2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p2.setFill(Color.TRANSPARENT);

        return new Group(p1, p2);
    }

    private SVGPath createDeleteIcon() {
        SVGPath path = new SVGPath();
        path.setContent("M2 4H14 M12.6667 4V13.3333C 12.6667 14 12 14.6667 11.3334 14.6667H 4.6667C 4 14.6667 3.3334 14 3.3334 13.3333V4 M5.3334 4.00004V2.66671C 5.3334 2.00004 6 1.33337 6.6667 1.33337H 9.3334C 10 1.33337 10.6667 2.00004 10.6667 2.66671V4.00004 M6.6666 7.33337V11.3334 M9.3334 7.33337V11.3334");
        path.setStroke(Color.web("#FF6467"));
        path.setStrokeWidth(1.33333);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.setFill(Color.TRANSPARENT);
        return path;
    }

    public void ambilDataPenanaman() {
        // Ambil data dari controller, isi TableView, lalu hitung total ringkasan.
        List<DataPenanaman> dataList = controller.ambilDataPenanaman();
        tabelPenanaman.getItems().clear(); 
        
        int totalPohon = 0;
        float totalSerapan = 0;

        if (dataList != null && !dataList.isEmpty()) {
            tabelPenanaman.getItems().addAll(dataList);
            
            for (DataPenanaman d : dataList) {
                totalPohon += d.getJumlahPohon();
                totalSerapan += d.getEstimasiKarbon();
            }
        } 
        
        lblTotalPohonDitanam.setText(String.format("%,d", totalPohon).replace(',', '.'));
        lblEstimasiSerapan.setText(String.format("%,.0f", totalSerapan).replace(',', '.'));
    }

    public void hapusData(String idPenanaman) {
        // Hapus lalu refresh.
        controller.hapusDataPenanaman(idPenanaman);
        ambilDataPenanaman();
    }

    public void tampilkanModalTambah() {
        // Tampilkan modal input penanaman baru, lalu refresh tabel.
        FormLaporanPenanaman formModal = new FormLaporanPenanaman(controller);
        formModal.tampilkanForm();
        ambilDataPenanaman();
    }
}