import 'package:flutter/material.dart';

ThemeData buildAppTheme() {
  const seed = Color(0xFF0F766E);

  return ThemeData(
    useMaterial3: true,
    colorScheme: ColorScheme.fromSeed(
      seedColor: seed,
      primary: seed,
      secondary: const Color(0xFFE88D2B),
      surface: const Color(0xFFF5F1E8),
    ),
    scaffoldBackgroundColor: const Color(0xFFF5F1E8),
    appBarTheme: const AppBarTheme(
      backgroundColor: Color(0xFFF5F1E8),
      foregroundColor: Color(0xFF14323B),
      elevation: 0,
    ),
    cardTheme: CardThemeData(
      color: Colors.white,
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
    ),
    chipTheme: ChipThemeData(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      side: BorderSide.none,
      selectedColor: const Color(0xFFBDE5DA),
      backgroundColor: const Color(0xFFE6EFEA),
    ),
  );
}
