import 'package:car_rental_aurora/app.dart';
import 'package:car_rental_aurora/data/models/booking.dart';
import 'package:car_rental_aurora/data/repositories/booking_repository.dart';
import 'package:car_rental_aurora/data/repositories/car_repository.dart';
import 'package:car_rental_aurora/data/services/settings_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

class MemoryBookingRepository implements BookingRepository {
  MemoryBookingRepository([List<Booking>? initialBookings]) {
    if (initialBookings != null) {
      _items.addAll(initialBookings);
    }
  }

  final List<Booking> _items = [];

  @override
  Future<List<Booking>> loadBookings() async => List.unmodifiable(_items);

  @override
  Future<Booking> saveBooking(Booking booking) async {
    final saved = Booking(
      id: _items.length + 1,
      carId: booking.carId,
      carName: booking.carName,
      durationType: booking.durationType,
      duration: booking.duration,
      totalPrice: booking.totalPrice,
      bookedAt: booking.bookedAt,
      status: booking.status,
    );
    _items.insert(0, saved);
    return saved;
  }

  @override
  Future<void> deleteBooking(int id) async {
    _items.removeWhere((booking) => booking.id == id);
  }

  @override
  Future<void> clearBookingsCache() async {
    _items.clear();
  }
}

class FakeSettingsService extends SettingsService {
  Locale? storedLocale;
  ThemeMode storedThemeMode = ThemeMode.system;
  String? storedSessionEmail = 'demo@aurora.app';

  @override
  Future<Locale?> loadLocale() async => storedLocale;

  @override
  Future<void> saveLocale(Locale locale) async {
    storedLocale = locale;
  }

  @override
  Future<ThemeMode> loadThemeMode() async => storedThemeMode;

  @override
  Future<void> saveThemeMode(ThemeMode mode) async {
    storedThemeMode = mode;
  }

  @override
  Future<String?> loadSessionEmail() async => storedSessionEmail;

  @override
  Future<void> saveSessionEmail(String email) async {
    storedSessionEmail = email;
  }

  @override
  Future<void> clearSession() async {
    storedSessionEmail = null;
  }
}

void main() {
  testWidgets('shows login screen and signs in with valid credentials', (
    tester,
  ) async {
    final settingsService = FakeSettingsService()..storedSessionEmail = null;

    await tester.pumpWidget(
      CarRentalApp(
        settingsService: settingsService,
        bookingRepository: MemoryBookingRepository(),
        carRepository: DemoCarRepository(),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Welcome back'), findsOneWidget);

    await tester.enterText(
      find.byKey(const ValueKey('login-email')),
      'student@example.com',
    );
    await tester.enterText(
      find.byKey(const ValueKey('login-password')),
      '12345',
    );
    await tester.tap(find.byKey(const ValueKey('login-submit')));
    await tester.pumpAndSettle();

    expect(find.text('Cars'), findsOneWidget);
  });

  testWidgets('shows cars and switches locale to Russian', (tester) async {
    final settingsService = FakeSettingsService();

    await tester.pumpWidget(
      CarRentalApp(
        settingsService: settingsService,
        bookingRepository: MemoryBookingRepository(),
        carRepository: DemoCarRepository(),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Aurora Drive'), findsOneWidget);
    expect(find.text('Cars'), findsOneWidget);

    await tester.tap(find.text('Settings'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Русский'));
    await tester.pumpAndSettle();

    expect(find.text('Авто'), findsOneWidget);
  });

  testWidgets('shows existing booking and cancels it', (tester) async {
    await tester.pumpWidget(
      CarRentalApp(
        settingsService: FakeSettingsService(),
        bookingRepository: MemoryBookingRepository([
          Booking(
            id: 1,
            carId: 'ev-1',
            carName: 'Minsk Sprint',
            durationType: 'hour',
            duration: 1,
            totalPrice: 52,
            bookedAt: DateTime(2026, 4, 26, 12),
            status: 'paid_demo',
          ),
        ]),
        carRepository: DemoCarRepository(),
      ),
    );
    await tester.pumpAndSettle();

    await tester.tap(find.text('Bookings'));
    await tester.pumpAndSettle();

    expect(find.text('Minsk Sprint'), findsOneWidget);

    await tester.tap(find.byKey(const ValueKey('cancel-booking-1')));
    await tester.pumpAndSettle();

    expect(find.text('No bookings yet'), findsOneWidget);
  });
}
