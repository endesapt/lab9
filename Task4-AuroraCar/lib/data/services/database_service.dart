import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

class DatabaseService {
  static const _bookingsKey = 'bookings_cache';

  Future<List<Map<String, Object?>>> loadBookingMaps() async {
    final preferences = await SharedPreferences.getInstance();
    final rawList = preferences.getStringList(_bookingsKey) ?? const [];

    return rawList
        .map((item) => jsonDecode(item) as Map<String, dynamic>)
        .map((item) => item.map((key, value) => MapEntry(key, value)))
        .toList();
  }

  Future<void> saveBookingMaps(List<Map<String, Object?>> items) async {
    final preferences = await SharedPreferences.getInstance();
    final encoded = items.map(jsonEncode).toList();
    await preferences.setStringList(_bookingsKey, encoded);
  }

  Future<void> clearBookings() async {
    final preferences = await SharedPreferences.getInstance();
    await preferences.remove(_bookingsKey);
  }
}
