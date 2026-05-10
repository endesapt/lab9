import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SettingsService {
  static const _localeKey = 'locale_code';
  static const _themeModeKey = 'theme_mode';
  static const _sessionEmailKey = 'session_email';

  Future<Locale?> loadLocale() async {
    final preferences = await SharedPreferences.getInstance();
    final code = preferences.getString(_localeKey);
    if (code == null || code.isEmpty) {
      return null;
    }
    return Locale(code);
  }

  Future<void> saveLocale(Locale locale) async {
    final preferences = await SharedPreferences.getInstance();
    await preferences.setString(_localeKey, locale.languageCode);
  }

  Future<ThemeMode> loadThemeMode() async {
    final preferences = await SharedPreferences.getInstance();
    final value = preferences.getString(_themeModeKey);
    return switch (value) {
      'light' => ThemeMode.light,
      'dark' => ThemeMode.dark,
      _ => ThemeMode.system,
    };
  }

  Future<void> saveThemeMode(ThemeMode mode) async {
    final preferences = await SharedPreferences.getInstance();
    final value = switch (mode) {
      ThemeMode.light => 'light',
      ThemeMode.dark => 'dark',
      ThemeMode.system => 'system',
    };
    await preferences.setString(_themeModeKey, value);
  }

  Future<String?> loadSessionEmail() async {
    final preferences = await SharedPreferences.getInstance();
    final email = preferences.getString(_sessionEmailKey);
    if (email == null || email.isEmpty) {
      return null;
    }
    return email;
  }

  Future<void> saveSessionEmail(String email) async {
    final preferences = await SharedPreferences.getInstance();
    await preferences.setString(_sessionEmailKey, email);
  }

  Future<void> clearSession() async {
    final preferences = await SharedPreferences.getInstance();
    await preferences.remove(_sessionEmailKey);
  }
}
