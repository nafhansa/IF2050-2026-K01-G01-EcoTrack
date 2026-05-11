package com.ecotrack;

import com.ecotrack.boundary.*;
import com.ecotrack.controller.*;
import com.ecotrack.util.UIConstants;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane root;
    private VBox sidebar;
    private StackPane contentArea;

    private DataPohonController pohonController;
    private PenanamanController penanamanController;
    private LaporanPohonController laporanController;
    private StatistikController statistikController;

    @Override
    public void start(Stage primaryStage) {
        initializeControllers();

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIConstants.CONTENT_BG);

        sidebar = createSidebar();
        root.setLeft(sidebar);

        contentArea = new StackPane();
        root.setCenter(contentArea);

        loadPage("Dashboard Statistik");

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("EcoTrack - Manajemen Pendataan Pohon");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeControllers() {
        pohonController = new DataPohonController();
        penanamanController = new PenanamanController();
        laporanController = new LaporanPohonController();
        statistikController = new StatistikController();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(UIConstants.SIDEBAR_WIDTH);
        sidebar.setStyle("-fx-background-color: " + UIConstants.SIDEBAR_BG);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setSpacing(8);

        Label logo = new Label("\uD83C\uDF3F EcoTrack");
        logo.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + UIConstants.SIDEBAR_LOGO_COLOR);
        logo.setPadding(new Insets(0, 0, 24, 12));

        sidebar.getChildren().add(logo);

        for (int i = 0; i < UIConstants.MENU_ITEMS.length; i++) {
            String menuName = UIConstants.MENU_ITEMS[i];
            Button menuItem = createMenuItem(UIConstants.MENU_ICONS[i] + "  " + menuName, i == 0);
            menuItem.setOnAction(e -> {
                sidebar.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .forEach(node -> {
                        Button btn = (Button) node;
                        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT +
                            "; -fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 8; -fx-alignment: center-left; -fx-max-width: Infinity");
                    });
                menuItem.setStyle("-fx-background-color: " + UIConstants.SIDEBAR_ACTIVE_BG + "; -fx-text-fill: black; " +
                    "-fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 8; -fx-alignment: center-left; -fx-max-width: Infinity; -fx-font-weight: bold");

                loadPage(menuName);
            });
            sidebar.getChildren().add(menuItem);
        }

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        return sidebar;
    }

    private Button createMenuItem(String text, boolean isActive) {
        Button btn = new Button(text);
        if (isActive) {
            btn.setStyle("-fx-background-color: " + UIConstants.SIDEBAR_ACTIVE_BG + "; -fx-text-fill: black; " +
                "-fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 8; -fx-alignment: center-left; -fx-max-width: Infinity; -fx-font-weight: bold");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UIConstants.SIDEBAR_INACTIVE_TEXT +
                "; -fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 8; -fx-alignment: center-left; -fx-max-width: Infinity");
        }
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void loadPage(String menuName) {
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
        launch(args);
    }
}
