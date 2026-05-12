package com.ecotrack.util;

import javafx.scene.paint.Color;
import java.util.Map;

public class UIConstants {

    // =============================================
    // COLOR PALETTE
    // =============================================

    // Sidebar
    public static final String SIDEBAR_BG = "#0F766E";
    public static final String SIDEBAR_GRADIENT = "linear-gradient(to bottom right, #0F766E, #094C63)";
    public static final String SIDEBAR_ACTIVE_BG = "#A3E635";
    public static final String SIDEBAR_ACTIVE_TEXT = "#052E16";
    public static final String SIDEBAR_INACTIVE_TEXT = "#CBFBF1";
    public static final String SIDEBAR_LOGO_COLOR = "#FFFFFF";

    // Content
    public static final String CONTENT_BG = "#F0FAF5";
    public static final String CARD_BG_WHITE = "#FFFFFF";

    // Card Gradients
    public static final String CARD_GREEN_GRADIENT_START = "#4CAF7D";
    public static final String CARD_GREEN_GRADIENT_END = "#2E7D52";
    public static final String CARD_TEAL_GRADIENT_START = "#2D6E6E";
    public static final String CARD_TEAL_GRADIENT_END = "#1B4A4A";

    // Accents
    public static final String ACCENT_LIME = "#A8E063";
    public static final String ACCENT_LIME_HOVER = "#95D14F";
    public static final String ACCENT_TEAL = "#26A69A";
    public static final String ACCENT_TEAL_DARK = "#00897B";
    public static final String ACCENT_RED = "#EF5350";

    // Text
    public static final String TEXT_PAGE_TITLE = "#1B3A2D";
    public static final String TEXT_SUBTITLE = "#6B9080";
    public static final String TEXT_TABLE_HEADER = "#9E9E9E";
    public static final String TEXT_TABLE_DATA = "#212121";

    // JavaFX Color equivalents
    public static final Color COLOR_SIDEBAR_BG = Color.web(SIDEBAR_BG);
    public static final Color COLOR_SIDEBAR_ACTIVE = Color.web(SIDEBAR_ACTIVE_BG);
    public static final Color COLOR_CONTENT_BG = Color.web(CONTENT_BG);
    public static final Color COLOR_ACCENT_LIME = Color.web(ACCENT_LIME);
    public static final Color COLOR_ACCENT_LIME_HOVER = Color.web(ACCENT_LIME_HOVER);
    public static final Color COLOR_ACCENT_TEAL = Color.web(ACCENT_TEAL);
    public static final Color COLOR_ACCENT_RED = Color.web(ACCENT_RED);
    public static final Color COLOR_CARD_GREEN_START = Color.web(CARD_GREEN_GRADIENT_START);
    public static final Color COLOR_CARD_GREEN_END = Color.web(CARD_GREEN_GRADIENT_END);
    public static final Color COLOR_CARD_TEAL_START = Color.web(CARD_TEAL_GRADIENT_START);
    public static final Color COLOR_CARD_TEAL_END = Color.web(CARD_TEAL_GRADIENT_END);

    // =============================================
    // BADGE COLORS - JENIS POHON
    // =============================================

    public static final Map<String, String[]> POHON_BADGE_COLORS = Map.of(
        "Mahoni",   new String[]{"#E8F5E9", "#2E7D52"},
        "Jati",     new String[]{"#FFF9C4", "#F9A825"},
        "Trembesi", new String[]{"#E0F2F1", "#00796B"},
        "Akasia",   new String[]{"#FFF3E0", "#E65100"},
        "Mangrove", new String[]{"#E3F2FD", "#1565C0"},
        "Default",  new String[]{"#F3E5F5", "#6A1B9A"}
    );

    // Kondisi badge colors
    public static final String BADGE_KONDISI_MATI_BG = "#FFEBEE";
    public static final String BADGE_KONDISI_MATI_TEXT = "#C62828";
    public static final String BADGE_KONDISI_RUSAK_BG = "#FFF3E0";
    public static final String BADGE_KONDISI_RUSAK_TEXT = "#E65100";
    public static final String BADGE_KONDISI_DITEBANG_BG = "#F5F5F5";
    public static final String BADGE_KONDISI_DITEBANG_TEXT = "#616161";

    // =============================================
    // FONT SIZES
    // =============================================

    public static final double FONT_PAGE_TITLE = 22;
    public static final double FONT_SUBTITLE = 12;
    public static final double FONT_CARD_NUMBER = 36;
    public static final double FONT_CARD_LABEL = 12;
    public static final double FONT_TABLE_HEADER = 11;
    public static final double FONT_TABLE_DATA = 13;
    public static final double FONT_MODAL_TITLE = 16;
    public static final double FONT_FIELD_LABEL = 13;

    // =============================================
    // BORDER RADIUS
    // =============================================

    public static final double RADIUS_CARD = 16;
    public static final double RADIUS_MODAL = 12;
    public static final double RADIUS_BUTTON = 8;
    public static final double RADIUS_INPUT = 8;

    // =============================================
    // SPACING & SIZING
    // =============================================

    public static final double SIDEBAR_WIDTH = 256;
    public static final double GAP_CARD = 16;
    public static final double PADDING_CONTENT = 24;
    public static final double PADDING_MODAL = 24;
    public static final double MODAL_WIDTH = 400;
    public static final double MODAL_WIDTH_PENANAMAN = 420;
    public static final double BUTTON_HEIGHT = 48;

    // =============================================
    // ROW HOVER
    // =============================================

    public static final String ROW_HOVER_BG = "#F0FFF4";
    public static final String ROW_SEPARATOR = "#F0F0F0";

    // =============================================
    // SIDEBAR MENU ITEMS
    // =============================================

    public static final String[] MENU_ITEMS = {
        "Dashboard Statistik",
        "Data Pohon",
        "Data Penanaman",
        "Lapor Kondisi Pohon"
    };
}
