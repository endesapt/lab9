import 'package:car_rental_aurora/app.dart';
import 'package:car_rental_aurora/data/models/booking.dart';
import 'package:car_rental_aurora/data/repositories/booking_repository.dart';
import 'package:car_rental_aurora/data/repositories/car_repository.dart';
import 'package:car_rental_aurora/data/services/settings_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

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
  FakeSettingsService({this.sessionEmail = 'demo@aurora.app'});

  String? sessionEmail;

  @override
  Future<Locale?> loadLocale() async => const Locale('en');

  @override
  Future<void> saveLocale(Locale locale) async {}

  @override
  Future<ThemeMode> loadThemeMode() async => ThemeMode.system;

  @override
  Future<void> saveThemeMode(ThemeMode mode) async {}

  @override
  Future<String?> loadSessionEmail() async => sessionEmail;

  @override
  Future<void> saveSessionEmail(String email) async {
    sessionEmail = email;
  }

  @override
  Future<void> clearSession() async {
    sessionEmail = null;
  }
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('books a car with fake payment flow', (tester) async {
    await tester.pumpWidget(
      CarRentalApp(
        settingsService: FakeSettingsService(),
        bookingRepository: MemoryBookingRepository(),
        carRepository: DemoCarRepository(),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Cars'), findsOneWidget);

    await tester.dragUntilVisible(
      find.byKey(const ValueKey('book-ev-1')),
      find.byType(Scrollable).first,
      const Offset(0, -250),
    );
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const ValueKey('book-ev-1')));
    await tester.pumpAndSettle();

    expect(
      find.text('Fake payment only. No real transaction happens.'),
      findsOneWidget,
    );

    await tester.tap(find.byKey(const ValueKey('confirm-booking')));
    await tester.pumpAndSettle();

    expect(find.text('Booking saved'), findsOneWidget);

    await tester.tap(find.text('Bookings'));
    await tester.pumpAndSettle();

    expect(find.text('Minsk Sprint'), findsOneWidget);

    await tester.tap(find.byKey(const ValueKey('cancel-booking-1')));
    await tester.pumpAndSettle();

    expect(find.text('No bookings yet'), findsOneWidget);
  });

  testWidgets('signs in from login screen and restores main navigation', (
    tester,
  ) async {
    await tester.pumpWidget(
      CarRentalApp(
        settingsService: FakeSettingsService(sessionEmail: null),
        bookingRepository: MemoryBookingRepository(),
        carRepository: DemoCarRepository(),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Welcome back'), findsOneWidget);

    await tester.enterText(
      find.byKey(const ValueKey('login-email')),
      'tester@aurora.app',
    );
    await tester.enterText(
      find.byKey(const ValueKey('login-password')),
      '12345',
    );
    await tester.tap(find.byKey(const ValueKey('login-submit')));
    await tester.pumpAndSettle();

    expect(find.text('Cars'), findsOneWidget);
    expect(find.text('Settings'), findsOneWidget);
  });

  testWidgets('clears cached bookings from settings', (tester) async {
    await tester.pumpWidget(
      CarRentalApp(
        settingsService: FakeSettingsService(),
        bookingRepository: MemoryBookingRepository([
          Booking(
            id: 1,
            carId: 'ev-1',
            carName: 'Minsk Sprint',
            durationType: 'hour',
            duration: 2,
            totalPrice: 104,
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

    await tester.tap(find.text('Settings'));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const ValueKey('clear-cache')));
    await tester.pumpAndSettle();

    await tester.tap(find.text('Bookings'));
    await tester.pumpAndSettle();
    expect(find.text('No bookings yet'), findsOneWidget);
  });
}
