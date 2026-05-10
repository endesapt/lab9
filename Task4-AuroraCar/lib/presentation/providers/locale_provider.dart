import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../../data/services/settings_service.dart';

class LocaleProvider extends ChangeNotifier {
  LocaleProvider(this._settingsService);

  final SettingsService _settingsService;
  Locale _locale = AppLocalizations.supportedLocales.first;

  Locale get locale => _locale;

  Future<void> loadLocale() async {
    try {
      final savedLocale = await _settingsService.loadLocale();
      if (savedLocale != null) {
        _locale = savedLocale;
        notifyListeners();
      }
    } catch (error, stackTrace) {
      debugPrint('Failed to load locale: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }

  Future<void> setLocale(Locale locale) async {
    if (_locale == locale) {
      return;
    }

    try {
      _locale = locale;
      notifyListeners();
      await _settingsService.saveLocale(locale);
    } catch (error, stackTrace) {
      debugPrint('Failed to save locale: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }
}
