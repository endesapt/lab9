import 'package:flutter/material.dart';

import '../../data/services/settings_service.dart';

class ThemeProvider extends ChangeNotifier {
  ThemeProvider(this._settingsService);

  final SettingsService _settingsService;

  ThemeMode _themeMode = ThemeMode.system;

  ThemeMode get themeMode => _themeMode;

  Future<void> loadThemeMode() async {
    try {
      _themeMode = await _settingsService.loadThemeMode();
      notifyListeners();
    } catch (error, stackTrace) {
      debugPrint('Failed to load theme mode: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }

  Future<void> setThemeMode(ThemeMode mode) async {
    if (_themeMode == mode) {
      return;
    }

    try {
      _themeMode = mode;
      notifyListeners();
      await _settingsService.saveThemeMode(mode);
    } catch (error, stackTrace) {
      debugPrint('Failed to save theme mode: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }
}
