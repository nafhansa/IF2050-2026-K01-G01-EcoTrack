package com.ecotrack.boundary;

import com.ecotrack.controller.UserController;
import com.ecotrack.entity.User;
import com.ecotrack.util.Session;
import com.ecotrack.util.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage {

    public static User showLogin(Stage owner) {
        // Halaman login ditampilkan sebagai dialog modal.
        // Return value:
        // - User terpilih jika login berhasil
        // - null jika user menutup dialog / batal
        UserController uc = new UserController();
        List<User> users = uc.findAll();

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Login - EcoTrack");

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + UIConstants.CONTENT_BG + ";");

        Label title = new Label("Selamat Datang di EcoTrack");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: " + UIConstants.TEXT_PAGE_TITLE + "; -fx-font-weight: bold;");

        Label subtitle = new Label("Silakan pilih akun dan masukkan kata sandi");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIConstants.TEXT_SUBTITLE + ";");

        // Dropdown menampilkan nama + role, tetapi value sebenarnya disimpan di map.
        ComboBox<String> combo = new ComboBox<>();
        Map<String, User> map = new HashMap<>();
        for (User u : users) {
            String disp = u.getNama() + " (" + u.getRole() + ")";
            combo.getItems().add(disp);
            map.put(disp, u);
        }
        if (!combo.getItems().isEmpty()) combo.getSelectionModel().selectFirst();
        combo.setPrefWidth(320);
        combo.setStyle("-fx-background-radius: " + UIConstants.RADIUS_INPUT + "; -fx-padding: 8;");

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Kata sandi");
        pwd.setPrefWidth(320);
        pwd.setStyle("-fx-background-radius: " + UIConstants.RADIUS_INPUT + "; -fx-padding: 8;");

        Label error = new Label("");
        error.setStyle("-fx-text-fill: " + UIConstants.ACCENT_RED + ";");

        Button loginBtn = new Button("Masuk");
        loginBtn.setPrefWidth(320);
        loginBtn.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        loginBtn.setStyle("-fx-background-color: " + UIConstants.ACCENT_LIME + "; -fx-text-fill: black; -fx-background-radius: " + UIConstants.RADIUS_BUTTON + "; -fx-font-weight: bold;");

        final User[] result = new User[1];

        // Saat tombol "Masuk" ditekan, lakukan autentikasi berdasarkan id_user.
        loginBtn.setOnAction(e -> {
            String sel = combo.getSelectionModel().getSelectedItem();
            String entered = pwd.getText();
            if (sel != null && map.containsKey(sel)) {
                User candidate = map.get(sel);
                UserController uc2 = new UserController();
                boolean ok = uc2.authenticate(candidate.getIdUser(), entered);
                if (ok) {
                    result[0] = candidate;
                    // Set session agar halaman lain bisa akses user aktif.
                    Session.setCurrentUser(result[0]);
                    dialog.close();
                    return;
                } else {
                    error.setText("Kata sandi salah");
                }
            }
        });

        root.getChildren().addAll(title, subtitle, combo, pwd, error, loginBtn);

        Scene scene = new Scene(root, 380, 220);
        dialog.setScene(scene);
        dialog.showAndWait();

        return result[0];
    }
}
