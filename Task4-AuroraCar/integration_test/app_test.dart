import 'package:car_rental_aurora/app.dart';
import 'package:car_rental_aurora/data/models/booking.dart';
import 'package:car_rental_aurora/data/repositories/booking_repository.dart';
import 'package:car_rental_aurora/data/repositories/car_repository.dart';
import 'package:car_rental_aurora/data/services/settings_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

class MemoryBookingRepository implements BookingRepository {
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
}

class FakeSettingsService extends SettingsService {
  @override
  Future<Locale?> loadLocale() async => const Locale('en');

  @override
  Future<void> saveLocale(Locale locale) async {}
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

    expect(find.text('Fake payment only. No real transaction happens.'), findsOneWidget);

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
}
