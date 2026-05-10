import 'package:flutter/foundation.dart';

import '../../data/services/settings_service.dart';

class SessionProvider extends ChangeNotifier {
  SessionProvider(this._settingsService);

  final SettingsService _settingsService;

  String? _email;
  bool _isLoading = true;

  String? get email => _email;
  bool get isLoading => _isLoading;
  bool get isAuthenticated => _email != null;

  Future<void> loadSession() async {
    try {
      _email = await _settingsService.loadSessionEmail();
    } catch (error, stackTrace) {
      debugPrint('Failed to load session: $error');
      debugPrintStack(stackTrace: stackTrace);
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> signIn({required String email, required String password}) async {
    final normalizedEmail = email.trim();

    if (!_isValidEmail(normalizedEmail) || password.trim().length < 4) {
      return false;
    }

    try {
      _email = normalizedEmail;
      notifyListeners();
      await _settingsService.saveSessionEmail(normalizedEmail);
      return true;
    } catch (error, stackTrace) {
      debugPrint('Failed to save session: $error');
      debugPrintStack(stackTrace: stackTrace);
      return false;
    }
  }

  Future<void> signOut() async {
    try {
      _email = null;
      notifyListeners();
      await _settingsService.clearSession();
    } catch (error, stackTrace) {
      debugPrint('Failed to clear session: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }

  bool _isValidEmail(String value) {
    return value.contains('@') && value.contains('.');
  }
}
