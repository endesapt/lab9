import 'package:car_rental_aurora/data/models/booking.dart';
import 'package:car_rental_aurora/data/repositories/booking_repository.dart';
import 'package:car_rental_aurora/data/repositories/car_repository.dart';
import 'package:car_rental_aurora/presentation/providers/rental_provider.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';

class MockBookingRepository extends Mock implements BookingRepository {}

class FakeBooking extends Fake implements Booking {}

void main() {
  late MockBookingRepository bookingRepository;
  late RentalProvider provider;

  setUpAll(() {
    registerFallbackValue(FakeBooking());
  });

  setUp(() {
    bookingRepository = MockBookingRepository();
    provider = RentalProvider(
      bookingRepository: bookingRepository,
      carRepository: DemoCarRepository(),
    );
  });

  test('initializes cars and loads bookings', () async {
    when(() => bookingRepository.loadBookings()).thenAnswer(
      (_) async => [
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
      ],
    );

    await provider.initialize();

    expect(provider.cars, isNotEmpty);
    expect(provider.selectedCar, isNotNull);
    expect(provider.bookings, hasLength(1));
  });

  test('creates booking and prepends it to history', () async {
    when(() => bookingRepository.loadBookings()).thenAnswer((_) async => []);
    when(() => bookingRepository.saveBooking(any())).thenAnswer((
      invocation,
    ) async {
      final booking = invocation.positionalArguments.first as Booking;
      return Booking(
        id: 7,
        carId: booking.carId,
        carName: booking.carName,
        durationType: booking.durationType,
        duration: booking.duration,
        totalPrice: booking.totalPrice,
        bookedAt: booking.bookedAt,
        status: booking.status,
      );
    });

    await provider.initialize();
    final result = await provider.createBooking(
      durationType: 'day',
      duration: 2,
    );

    expect(result, isTrue);
    expect(provider.bookings, hasLength(1));
    expect(provider.bookings.first.totalPrice, 378);
    verify(() => bookingRepository.saveBooking(any())).called(1);
  });

  test('cancels booking and removes it from history', () async {
    when(() => bookingRepository.loadBookings()).thenAnswer(
      (_) async => [
        Booking(
          id: 5,
          carId: 'ev-1',
          carName: 'Minsk Sprint',
          durationType: 'hour',
          duration: 1,
          totalPrice: 52,
          bookedAt: DateTime(2026, 4, 26, 12),
          status: 'paid_demo',
        ),
      ],
    );
    when(() => bookingRepository.deleteBooking(5)).thenAnswer((_) async {});

    await provider.initialize();
    final result = await provider.cancelBooking(5);

    expect(result, isTrue);
    expect(provider.bookings, isEmpty);
    verify(() => bookingRepository.deleteBooking(5)).called(1);
  });

  test('clears cached bookings', () async {
    when(() => bookingRepository.loadBookings()).thenAnswer(
      (_) async => [
        Booking(
          id: 2,
          carId: 'ev-2',
          carName: 'Nemiga Wagon',
          durationType: 'day',
          duration: 1,
          totalPrice: 245,
          bookedAt: DateTime(2026, 4, 27, 9),
          status: 'paid_demo',
        ),
      ],
    );
    when(() => bookingRepository.clearBookingsCache()).thenAnswer((_) async {});

    await provider.initialize();
    await provider.clearBookingsCache();

    expect(provider.bookings, isEmpty);
    verify(() => bookingRepository.clearBookingsCache()).called(1);
  });
}
