package com.ecotrack.util;

import com.ecotrack.entity.User;

/*
 * Session adalah penyimpan state runtime yang bersifat global/sederhana.
 *
 * Saat ini hanya menyimpan user yang sedang login.
 * Dipakai oleh boundary untuk menampilkan nama user, serta mengisi id_user
 * pada data yang disimpan ke database.
 */
public class Session {
    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User u) { currentUser = u; }
}
