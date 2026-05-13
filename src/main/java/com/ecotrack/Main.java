package com.ecotrack;

/*
 * Entry point aplikasi JavaFX EcoTrack.
 *
 * Tanggung jawab utama kelas ini:
 * - Menjalankan proses login (modal) sebelum user masuk aplikasi.
 * - Menyimpan user aktif ke Session.
 * - Membangun layout utama (sidebar + area konten) dan navigasi menu.
 * - Melakukan routing sederhana: nama menu -> halaman (boundary) yang ditampilkan.
 *
 */

import com.ecotrack.boundary.*;
import com.ecotrack.boundary.LoginPage;
import com.ecotrack.util.Session;
import com.ecotrack.controller.*;
import com.ecotrack.util.UIConstants;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.paint.Color;

public class Main extends Application {

    private BorderPane root;
    private VBox sidebar;
    private StackPane contentArea;

    private DataPohonController pohonController;
    private PenanamanController penanamanController;
    private LaporanPohonController laporanController;
    private StatistikController statistikController;

    // Path SVG untuk ikon menu sidebar (1 menu bisa terdiri dari beberapa path).
    private static final String[][] MENU_SVG_PATHS = {
        // 1. Dashboard
        {"M2.5 2.5V15.8333C2.5 16.2754 2.67559 16.6993 2.98816 17.0118C3.30072 17.3244 3.72464 17.5 4.16667 17.5H17.5",
        "M15 14.1667V7.5",
        "M10.8333 14.1667V4.16675",
        "M6.66667 14.1667V11.6667"}, 
        // 2. Data Pohon
        {"M6.66667 15.8333C5.95167 15.8349 5.2551 15.6066 4.6798 15.182C4.10451 14.7574 3.68099 14.1591 3.47177 13.4754C3.26255 12.7917 3.27871 12.0588 3.51788 11.385C3.75705 10.7112 4.20655 10.1321 4.8 9.73333C4.43411 9.27616 4.2143 8.71943 4.16918 8.13561C4.12406 7.55178 4.25571 6.9679 4.54701 6.45993C4.8383 5.95196 5.27575 5.54343 5.80243 5.2875C6.32911 5.03158 6.92062 4.94011 7.5 5.025V5C7.5 4.33696 7.76339 3.70107 8.23223 3.23223C8.70107 2.76339 9.33696 2.5 10 2.5C10.663 2.5 11.2989 2.76339 11.7678 3.23223C12.2366 3.70107 12.5 4.33696 12.5 5V5.03333C13.0794 4.94844 13.6709 5.03991 14.1976 5.29584C14.7243 5.55176 15.1617 5.96029 15.453 6.46826C15.7443 6.97623 15.8759 7.56012 15.8308 8.14394C15.7857 8.72776 15.5659 9.28449 15.2 9.74167C15.7897 10.1418 16.2357 10.7204 16.4725 11.3926C16.7092 12.0648 16.7243 12.7952 16.5155 13.4766C16.3067 14.158 15.885 14.7546 15.3123 15.1787C14.7396 15.6029 14.046 15.8323 13.3333 15.8333H6.66667Z", "M10 15.8333V18.3333"},
        // 3. Data Penanaman
        {"M9.16667 16.6667C7.70338 16.6712 6.29188 16.1255 5.2121 15.1379C4.13232 14.1504 3.46314 12.793 3.33728 11.3352C3.21142 9.8773 3.63808 8.42538 4.53262 7.26737C5.42717 6.10935 6.72427 5.32983 8.16667 5.08341C12.9167 4.16675 14.1667 3.73341 15.8333 1.66675C16.6667 3.33341 17.5 5.15008 17.5 8.33341C17.5 12.9167 13.5167 16.6667 9.16667 16.6667Z", "M1.66667 17.5C1.66667 15 3.20833 13.0333 5.9 12.5C7.91667 12.1 10 10.8333 10.8333 10"},
        // 4. Lapor Kondisi
        {"M12.5 1.66675H5C4.55797 1.66675 4.13405 1.84234 3.82149 2.1549C3.50893 2.46746 3.33333 2.89139 3.33333 3.33341V16.6667C3.33333 17.1088 3.50893 17.5327 3.82149 17.8453C4.13405 18.1578 4.55797 18.3334 5 18.3334H15C15.442 18.3334 15.8659 18.1578 16.1785 17.8453C16.4911 17.5327 16.6667 17.1088 16.6667 16.6667V5.83341L12.5 1.66675Z", "M11.6667 1.66675V5.00008C11.6667 5.44211 11.8423 5.86603 12.1548 6.17859C12.4674 6.49115 12.8913 6.66675 13.3333 6.66675H16.6667", "M8.33333 7.5H6.66667", "M13.3333 10.8333H6.66667", "M13.3333 14.1667H6.66667"}
    };

    @Override
    public void start(Stage primaryStage) {
        // 1) Tampilkan login dialog terlebih dahulu (blocking).
        //    Jika user membatalkan login, aplikasi ditutup.
        com.ecotrack.entity.User logged = LoginPage.showLogin(primaryStage);
        if (logged == null) {
            // user batal atau tidak ada akun => keluar aplikasi
            System.exit(0);
        }

        // 2) Simpan user yang berhasil login untuk dipakai di halaman lain.
        Session.setCurrentUser(logged);

        // 3) Inisialisasi controller (jembatan boundary <-> entity/db).
        initializeControllers();

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        sidebar = createSidebar();
        root.setLeft(sidebar);

        contentArea = new StackPane();
        root.setCenter(contentArea);

        // 4) Halaman default saat pertama kali masuk.
        loadPage("Dashboard Statistik");

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("EcoTrack - Manajemen Pendataan Pohon");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeControllers() {
        // Controller dibuat sekali agar bisa dipakai ulang antar halaman.
        pohonController = new DataPohonController();
        penanamanController = new PenanamanController();
        laporanController = new LaporanPohonController();
        statistikController = new StatistikController();
    }

    private VBox createSidebar() {
        // Sidebar berisi:
        // - Nama user yang sedang login
        // - Logo/brand
        // - Tombol menu navigasi
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(UIConstants.SIDEBAR_WIDTH);
        sidebar.setPadding(new Insets(0)); 
        sidebar.setStyle("-fx-background-color: " + UIConstants.SIDEBAR_GRADIENT + ";" + "-fx-background-radius: 0 32 32 0;" + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 5, 8);");
        sidebar.setSpacing(8);

        // Container Header
        VBox headerContainer = new VBox();
        headerContainer.setMinHeight(131);
        headerContainer.setPrefHeight(131);
        headerContainer.setMaxHeight(131);
        headerContainer.setAlignment(Pos.CENTER);

        headerContainer.setStyle("-fx-padding: 24 24 1 24; " + "-fx-border-color: transparent transparent #0F766E transparent; " +"-fx-border-width: 0 0 1 0;");
        headerContainer.setPadding(new Insets(0));
        VBox.setMargin(headerContainer, new Insets(0, 0, 16, 0));

        // Tampilkan nama user di sidebar.
        Label userLabel = new Label(Session.getCurrentUser() != null ? Session.getCurrentUser().getNama() : "");
        userLabel.setStyle("-fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT + "; -fx-padding: 0 0 12 12;");
        sidebar.getChildren().add(userLabel);

        HBox logoBox = new HBox(12); 
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setMinHeight(40);
        logoBox.setMaxHeight(40);
        logoBox.setMaxWidth(Double.MAX_VALUE);

        // Box Icon 
        StackPane iconBox = new StackPane();
        iconBox.setMinSize(40, 40);
        iconBox.setPrefSize(40, 40);

        // Logo EcoTrack dibuat dari SVG path supaya konsisten dan scalable.
        Group logoSvg = new Group();
        SVGPath logoP1 = new SVGPath();
        logoP1.setContent("M12 28.5C10.713 28.5029 9.45917 28.0919 8.42364 27.3276C7.38812 26.5634 6.62578 25.4864 6.24918 24.2557C5.87258 23.025 5.90168 21.7059 6.33218 20.493C6.76269 19.2801 7.57178 18.2378 8.64 17.52C7.9814 16.6971 7.58574 15.695 7.50452 14.6441C7.4233 13.5932 7.66028 12.5422 8.18461 11.6279C8.70894 10.7135 9.49635 9.97818 10.4444 9.51751C11.3924 9.05685 12.4571 8.8922 13.5 9.045V9C13.5 7.80653 13.9741 6.66193 14.818 5.81802C15.6619 4.97411 16.8065 4.5 18 4.5C19.1935 4.5 20.3381 4.97411 21.182 5.81802C22.0259 6.66193 22.5 7.80653 22.5 9V9.06C23.5429 8.9072 24.6076 9.07185 25.5556 9.53251C26.5036 9.99318 27.2911 10.7285 27.8154 11.6429C28.3397 12.5572 28.5767 13.6082 28.4955 14.6591C28.4143 15.71 28.0186 16.7121 27.36 17.535C28.4215 18.2553 29.2243 19.2968 29.6504 20.5067C30.0766 21.7167 30.1038 23.0314 29.7279 24.2579C29.3521 25.4844 28.593 26.5582 27.5621 27.3217C26.5313 28.0852 25.2828 28.4981 24 28.5H12Z");
        logoP1.setFill(Color.WHITE);
        
        SVGPath logoP2 = new SVGPath();
        logoP2.setContent("M18 28.5V33");
        logoP2.setFill(Color.TRANSPARENT);
        logoP2.setStroke(Color.WHITE);
        logoP2.setStrokeWidth(2.5);
        logoP2.setStrokeLineCap(StrokeLineCap.ROUND);
        logoP2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        
        logoSvg.getChildren().addAll(logoP1, logoP2);
        iconBox.getChildren().add(logoSvg);

        Label text = new Label("EcoTrack");
        text.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        logoBox.getChildren().addAll(iconBox, text);
        headerContainer.getChildren().add(logoBox);
        sidebar.getChildren().add(headerContainer);

        // container Menu
        VBox menuContainer = new VBox(8); 
        menuContainer.setPadding(new Insets(16, 16, 0, 16));
        
        for (int i = 0; i < UIConstants.MENU_ITEMS.length; i++) {
            String menuName = UIConstants.MENU_ITEMS[i];
            Button menuItem = createMenuItem(menuName, MENU_SVG_PATHS[i], i == 0);
            
            menuItem.setOnAction(e -> {
                String baseStyle = "-fx-font-size: 14px; -fx-padding: 12 16 12 16; -fx-background-radius: 14; -fx-alignment: center-left; -fx-max-width: Infinity; ";
                
                menuContainer.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .forEach(node -> {
                        Button btn = (Button) node;
                        btn.setUserData(false); 
                        btn.setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT + "; -fx-font-weight: 500;");
                        updateMenuIconColor(btn, "#96F7E4");
                    });
                
                menuItem.setUserData(true);
                menuItem.setStyle(baseStyle + "-fx-background-color: " + UIConstants.SIDEBAR_ACTIVE_BG + "; -fx-text-fill: " + UIConstants.SIDEBAR_ACTIVE_TEXT + "; -fx-font-weight: bold;");
                updateMenuIconColor(menuItem, UIConstants.SIDEBAR_ACTIVE_TEXT);

                // Routing: ganti halaman berdasarkan nama menu.
                loadPage(menuName);
            });
            menuContainer.getChildren().add(menuItem);
        }

        VBox.setVgrow(menuContainer, Priority.ALWAYS);
        sidebar.getChildren().add(menuContainer);

        return sidebar;
    }

    private Button createMenuItem(String text, String[] svgPaths, boolean isActive) {
        // Membuat tombol menu dengan ikon SVG dan state aktif/non-aktif.
        Button btn = new Button(text);
        btn.setUserData(isActive);
        btn.setPrefHeight(44);
        
        // Icon
        Group iconGroup = new Group();
        for (String path : svgPaths) {
            SVGPath sp = new SVGPath();
            sp.setContent(path);
            sp.setFill(Color.TRANSPARENT);
            sp.setStroke(Color.web(isActive ? UIConstants.SIDEBAR_ACTIVE_TEXT : "#96F7E4"));
            sp.setStrokeWidth(1.8);
            sp.setStrokeLineCap(StrokeLineCap.ROUND);
            sp.setStrokeLineJoin(StrokeLineJoin.ROUND);
            iconGroup.getChildren().add(sp);
        }
        btn.setGraphic(iconGroup);
        btn.setGraphicTextGap(12);

        String baseStyle = "-fx-font-size: 14px; -fx-padding: 12 16 12 16; -fx-background-radius: 14; -fx-alignment: center-left; -fx-max-width: Infinity; ";
        String inactiveStyle = baseStyle + "-fx-background-color: transparent; -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT + "; -fx-font-weight: 500;";
        String hoverStyle = baseStyle + "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT + "; -fx-font-weight: 500;";
        String pressedStyle = baseStyle + "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT + "; -fx-font-weight: 500;";
        String activeStyle = baseStyle + "-fx-background-color: " + UIConstants.SIDEBAR_ACTIVE_BG + "; -fx-text-fill: " + UIConstants.SIDEBAR_ACTIVE_TEXT + "; -fx-font-weight: bold;";

        btn.setStyle(isActive ? activeStyle : inactiveStyle);
        
        btn.setOnMouseEntered(e -> { if (Boolean.FALSE.equals(btn.getUserData())) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (Boolean.FALSE.equals(btn.getUserData())) btn.setStyle(inactiveStyle); });
        btn.setOnMousePressed(e -> { if (Boolean.FALSE.equals(btn.getUserData())) btn.setStyle(pressedStyle); });
        btn.setOnMouseReleased(e -> { if (Boolean.FALSE.equals(btn.getUserData())) btn.setStyle(hoverStyle); });

        return btn;
    }

    private void updateMenuIconColor(Button btn, String colorHex) {
        // Saat state tombol berubah (aktif/non-aktif), warna stroke ikon ikut di-update.
        if (btn.getGraphic() instanceof Group) {
            Group g = (Group) btn.getGraphic();
            for (javafx.scene.Node n : g.getChildren()) {
                if (n instanceof SVGPath) {
                    ((SVGPath) n).setStroke(Color.web(colorHex));
                }
            }
        }
    }

    private void loadPage(String menuName) {
        // Router sederhana: string nama menu -> instance halaman.
        switch (menuName) {
            case "Dashboard Statistik":
                contentArea.getChildren().setAll(new HalamanStatistik(statistikController));
                break;
            case "Data Pohon":
                contentArea.getChildren().setAll(new HalamanDataPohon(pohonController));
                break;
            case "Data Penanaman":
                contentArea.getChildren().setAll(new HalamanDataPenanaman(penanamanController));
                break;
            case "Lapor Kondisi Pohon":
                contentArea.getChildren().setAll(new FormLaporanPohon(laporanController));
                break;
            default:
                contentArea.getChildren().setAll(new Label("Page not found"));
        }
    }

    public static void main(String[] args) {
        // Bootstrap JavaFX.
        launch(args);
    }
}
