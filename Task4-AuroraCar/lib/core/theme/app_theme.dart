import 'package:flutter/material.dart';

ThemeData buildAppTheme({required Brightness brightness}) {
  const seed = Color(0xFF0F766E);
  final isDark = brightness == Brightness.dark;

  return ThemeData(
    useMaterial3: true,
    brightness: brightness,
    colorScheme: ColorScheme.fromSeed(
      seedColor: seed,
      brightness: brightness,
      primary: seed,
      secondary: const Color(0xFFE88D2B),
      surface: isDark ? const Color(0xFF112126) : const Color(0xFFF5F1E8),
    ),
    scaffoldBackgroundColor: isDark
        ? const Color(0xFF091316)
        : const Color(0xFFF5F1E8),
    appBarTheme: AppBarTheme(
      backgroundColor: isDark
          ? const Color(0xFF091316)
          : const Color(0xFFF5F1E8),
      foregroundColor: isDark
          ? const Color(0xFFE4F4EF)
          : const Color(0xFF14323B),
      elevation: 0,
    ),
    cardTheme: CardThemeData(
      color: isDark ? const Color(0xFF142126) : Colors.white,
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
    ),
    chipTheme: ChipThemeData(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      side: BorderSide.none,
      selectedColor: isDark ? const Color(0xFF1F5D56) : const Color(0xFFBDE5DA),
      backgroundColor: isDark
          ? const Color(0xFF203036)
          : const Color(0xFFE6EFEA),
    ),
  );
}
