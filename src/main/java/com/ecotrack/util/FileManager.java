package com.ecotrack.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileManager {
    // Folder default untuk menyimpan file yang diunggah/di-generate oleh aplikasi.
    // Disimpan relatif terhadap working directory saat aplikasi dijalankan.
    private static final String FOTO_DIR = "ecotrack_photos";
    private static final String EXPORT_DIR = "exports";

    static {
        // Pastikan folder penyimpanan tersedia sebelum dipakai.
        createDirectoriesIfNotExist();
    }

    private static void createDirectoriesIfNotExist() {
        try {
            Files.createDirectories(Path.of(FOTO_DIR));
            Files.createDirectories(Path.of(EXPORT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String saveFoto(File sourceFile, String destDir) {
        // Menyalin file foto ke folder tujuan.
        // Nama file ditambah timestamp untuk meminimalkan tabrakan nama.
        try {
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path destPath = Path.of(destDir != null ? destDir : FOTO_DIR, fileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteFile(String filePath) {
        // Menghapus file (jika ada). Return true jika file berhasil dihapus.
        try {
            return Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File getFile(String filePath) {
        // Helper untuk mengembalikan File jika path valid dan file-nya ada.
        File file = new File(filePath);
        return file.exists() ? file : null;
    }

    public static String getFotoDir() {
        return FOTO_DIR;
    }

    public static String getExportDir() {
        return EXPORT_DIR;
    }
}
