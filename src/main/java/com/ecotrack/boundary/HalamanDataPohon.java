package com.ecotrack.boundary;

import com.ecotrack.controller.DataPohonController;
import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.UIConstants;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Callback;

import java.util.List;

public class HalamanDataPohon extends BorderPane {

    private final DataPohonController controller;
    private TableView<DataPohon> table;
    private Label totalLabel;

    // ─── Color palette dari referensi ─────────────────────────────────────────
    private static final Color TEAL_DARK     = Color.web("#115E59");
    private static final Color TEAL_MED      = Color.web("#0D9488");
    private static final Color TEAL_LIGHT    = Color.web("#CCFBF1");
    private static final Color LIME          = Color.web("#A3E635");
    private static final Color LIME_BG       = Color.web("#ECFCCB");
    private static final Color OLIVE         = Color.web("#4D7C0F");
    private static final Color OLIVE_BG      = Color.web("#D9F99D");
    private static final Color RED_DEL       = Color.web("#FF6467");
    private static final Color WHITE         = Color.WHITE;

    public HalamanDataPohon(DataPohonController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        // Set Background menggunakan UIConstants
        setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);
        setPadding(new Insets(32));

        VBox content = new VBox(24);

        // Header, Total badge, dan Table card
        content.getChildren().add(buildHeader());
        content.getChildren().add(buildTotalBadge());
        content.getChildren().add(buildTableCard());

        setCenter(content);
        ambilDataPohon();
    }

    // ─── Main Content ─────────────────────────────────────────────────────────
    private Node buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.BOTTOM_CENTER);
        header.setPadding(new Insets(0, 0, 32, 0)); // Sesuai padding Lapor Kondisi

        // Title block disamakan dengan Lapor Kondisi Pohon (menggunakan UIConstants)
        VBox titleBlock = new VBox(4);
        Label title = new Label("Data Pohon");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_PAGE_TITLE + "; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE);

        Label subtitle = new Label("Kelola master data referensi jenis pohon");
        subtitle.setStyle("-fx-font-size: " + UIConstants.FONT_SUBTITLE + "; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE);

        titleBlock.getChildren().addAll(title, subtitle);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);

        // Add button
        Button addBtn = new Button("＋  Tambah Pohon");
        addBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        addBtn.setTextFill(Color.web("#052E16"));
        addBtn.setCursor(Cursor.HAND);
        addBtn.setPadding(new Insets(8, 20, 8, 20));
        addBtn.setBackground(new Background(new BackgroundFill(
            LIME, new CornerRadii(14), Insets.EMPTY)));
        DropShadow btnShadow = new DropShadow(6, 0, 3, Color.rgb(163, 230, 53, 0.35));
        addBtn.setEffect(btnShadow);
        addBtn.setOnAction(e -> tampilkanModalTambah());
        addBtn.setOnMouseEntered(e -> addBtn.setBackground(new Background(new BackgroundFill(
            Color.web("#BEF264"), new CornerRadii(14), Insets.EMPTY))));
        addBtn.setOnMouseExited(e -> addBtn.setBackground(new Background(new BackgroundFill(
            LIME, new CornerRadii(14), Insets.EMPTY))));

        header.getChildren().addAll(titleBlock, addBtn);
        return header;
    }

    private Node buildTotalBadge() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));

        Label treeIcon = new Label("🌳");
        treeIcon.setFont(Font.font(20));

        Label text = new Label("Total Jenis Pohon");
        text.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        text.setTextFill(OLIVE);

        StackPane badge = new StackPane();
        badge.setMinSize(40, 36);
        badge.setMaxSize(50, 36);
        Rectangle badgeBg = new Rectangle(40, 36, LIME_BG);
        badgeBg.setArcWidth(10); badgeBg.setArcHeight(10);
        badgeBg.setStroke(OLIVE_BG); badgeBg.setStrokeWidth(1);

        totalLabel = new Label("0");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        totalLabel.setTextFill(Color.web("#3F6212"));
        badge.getChildren().addAll(badgeBg, totalLabel);

        row.getChildren().addAll(treeIcon, text, badge);
        return row;
    }

    private Node buildTableCard() {
        VBox card = new VBox();
        card.setBackground(new Background(new BackgroundFill(
            WHITE, new CornerRadii(16), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(
            Color.web("#99F6E4"), BorderStrokeStyle.SOLID,
            new CornerRadii(16), BorderWidths.DEFAULT)));
        DropShadow cardShadow = new DropShadow(3, 0, 1, Color.rgb(0, 0, 0, 0.08));
        card.setEffect(cardShadow);
        VBox.setVgrow(card, Priority.ALWAYS);

        // Card header
        HBox cardHeader = new HBox();
        cardHeader.setPadding(new Insets(20, 24, 20, 24));
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setBorder(new Border(new BorderStroke(
            Color.web("#CCFBF1"), BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        Label cardTitle = new Label("Daftar Pohon");
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardTitle.setTextFill(TEAL_DARK);
        cardHeader.getChildren().add(cardTitle);

        // Table
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        card.getChildren().addAll(cardHeader, table);
        return card;
    }

    // ─── TableView ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private TableView<DataPohon> buildTable() {
        TableView<DataPohon> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setFixedCellSize(69);
        tv.setPrefHeight(400);
        tv.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #CCFBF1;"
        );

        // ── Column: Nama Pohon ──
        TableColumn<DataPohon, String> colNama = new TableColumn<>("NAMA POHON");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaPohon"));
        colNama.setPrefWidth(280);
        colNama.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                StackPane icon = new StackPane();
                Rectangle iconBg = new Rectangle(36, 36, LIME_BG);
                iconBg.setArcWidth(10); iconBg.setArcHeight(10);
                iconBg.setStroke(OLIVE_BG); iconBg.setStrokeWidth(1);
                Label tree = new Label("🌳");
                tree.setFont(Font.font(14));
                icon.getChildren().addAll(iconBg, tree);

                Label nameLbl = new Label(item);
                nameLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
                nameLbl.setTextFill(TEAL_DARK);

                row.getChildren().addAll(icon, nameLbl);
                setGraphic(row);
                setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 24;");
            }
        });

        // ── Column: Usia ──
        TableColumn<DataPohon, Integer> colUsia = new TableColumn<>("USIA (TAHUN)");
        colUsia.setCellValueFactory(new PropertyValueFactory<>("usia"));
        colUsia.setPrefWidth(180);
        colUsia.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox chip = new HBox();
                chip.setAlignment(Pos.CENTER_LEFT);
                chip.setPadding(new Insets(4, 12, 4, 12));
                chip.setBackground(new Background(new BackgroundFill(
                    TEAL_LIGHT, new CornerRadii(10), Insets.EMPTY)));
                chip.setBorder(new Border(new BorderStroke(
                    Color.web("#99F6E4"), BorderStrokeStyle.SOLID,
                    new CornerRadii(10), BorderWidths.DEFAULT)));
                Label lbl = new Label(item + " tahun");
                lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
                lbl.setTextFill(TEAL_MED);
                chip.getChildren().add(lbl);
                setGraphic(chip);
                setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 8;");
            }
        });

        // ── Column: Serapan Karbon ──
        TableColumn<DataPohon, Float> colKarbon = new TableColumn<>("SERAPAN KARBON (KG/TAHUN)");
        colKarbon.setCellValueFactory(new PropertyValueFactory<>("serapanKarbon"));
        colKarbon.setPrefWidth(340);
        colKarbon.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox row = new HBox(2);
                row.setAlignment(Pos.CENTER_LEFT);
                Label num = new Label(item.toString());
                num.setFont(Font.font("System", FontWeight.BOLD, 16));
                num.setTextFill(TEAL_DARK);
                Label unit = new Label(" kg");
                unit.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                unit.setTextFill(TEAL_MED);
                row.getChildren().addAll(num, unit);
                setGraphic(row);
                setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 8;");
            }
        });

        // ── Column: Aksi ──
        TableColumn<DataPohon, Void> colAksi = new TableColumn<>("AKSI");
        colAksi.setPrefWidth(180);
        colAksi.setCellFactory(buildAksiCellFactory());

        // Style column headers
        for (TableColumn<?, ?> col : new TableColumn[]{colNama, colUsia, colKarbon, colAksi}) {
            col.setStyle(
                "-fx-background-color: #F0FDFA;" +
                "-fx-text-fill: #0D9488;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11px;"
            );
            col.setSortable(false);
        }

        tv.getColumns().addAll(colNama, colUsia, colKarbon, colAksi);

        // Row factory for alternating / hover
        tv.setRowFactory(t -> {
            TableRow<DataPohon> row = new TableRow<>();
            row.setStyle("-fx-background-color: white;");
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) row.setStyle("-fx-background-color: #F0FDFA;");
            });
            row.setOnMouseExited(e  -> row.setStyle("-fx-background-color: white;"));
            return row;
        });

        tv.setPlaceholder(new Label("Belum ada data pohon."));
        return tv;
    }

    private Callback<TableColumn<DataPohon, Void>, TableCell<DataPohon, Void>> buildAksiCellFactory() {
        return col -> new TableCell<>() {
            private final Button editBtn   = buildIconBtn("✏️", TEAL_MED,  false);
            private final Button deleteBtn = buildIconBtn("🗑️", RED_DEL,  true);
            private final HBox   actions   = new HBox(8, editBtn, deleteBtn);

            {
                actions.setAlignment(Pos.CENTER_RIGHT);
                actions.setPadding(new Insets(0, 8, 0, 0));

                editBtn.setOnAction(e -> {
                    DataPohon p = getTableView().getItems().get(getIndex());
                    tampilkanModalEdit(p);
                });
                deleteBtn.setOnAction(e -> {
                    DataPohon p = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus pohon " + p.getNamaPohon() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) hapusData(p.getIdPohon());
                    });
                });
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
                setStyle("-fx-background-color: transparent;");
            }
        };
    }

    private Button buildIconBtn(String icon, Color accent, boolean isDanger) {
        Button btn = new Button(icon);
        btn.setFont(Font.font(14));
        btn.setPrefSize(36, 36);
        btn.setMinSize(36, 36);
        btn.setCursor(Cursor.HAND);
        btn.setBackground(Background.EMPTY);
        btn.setBorder(Border.EMPTY);
        btn.setOnMouseEntered(e -> btn.setBackground(new Background(new BackgroundFill(
            isDanger ? Color.web("#FEE2E2") : TEAL_LIGHT,
            new CornerRadii(10), Insets.EMPTY))));
        btn.setOnMouseExited(e -> btn.setBackground(Background.EMPTY));
        return btn;
    }

    // ─── Controller Logics ────────────────────────────────────────────────────
    public void ambilDataPohon() {
        List<DataPohon> dataPohonList = controller.ambilDataPohon();
        table.getItems().clear();
        if (dataPohonList != null && !dataPohonList.isEmpty()) {
            table.getItems().addAll(dataPohonList);
            totalLabel.setText(String.valueOf(dataPohonList.size()));
        } else {
            totalLabel.setText("0");
        }
    }

    public void hapusData(String idPohon) {
        controller.hapusDataPohon(idPohon);
        ambilDataPohon();
    }

    public void tampilkanModalTambah() {
        FormDataPohon formModal = new FormDataPohon(controller);
        formModal.tampilkanForm();
        ambilDataPohon();
    }

    private void tampilkanModalEdit(DataPohon data) {
        FormDataPohon formModal = new FormDataPohon(controller);
        formModal.setEditData(data);
        formModal.tampilkanForm();
        ambilDataPohon();
    }
}