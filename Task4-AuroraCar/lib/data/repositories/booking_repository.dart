import '../models/booking.dart';
import '../services/database_service.dart';

abstract class BookingRepository {
  Future<List<Booking>> loadBookings();
  Future<Booking> saveBooking(Booking booking);
  Future<void> deleteBooking(int id);
  Future<void> clearBookingsCache();
}

class LocalBookingRepository implements BookingRepository {
  LocalBookingRepository(this._databaseService);

  final DatabaseService _databaseService;

  @override
  Future<List<Booking>> loadBookings() async {
    final rows = await _databaseService.loadBookingMaps();
    return rows.map(Booking.fromMap).toList()
      ..sort((left, right) => right.bookedAt.compareTo(left.bookedAt));
  }

  @override
  Future<Booking> saveBooking(Booking booking) async {
    final rows = await _databaseService.loadBookingMaps();
    final nextId =
        rows
            .map((item) => (item['id'] as num?)?.toInt() ?? 0)
            .fold<int>(
              0,
              (current, value) => value > current ? value : current,
            ) +
        1;
    final savedBooking = Booking(
      id: nextId,
      carId: booking.carId,
      carName: booking.carName,
      durationType: booking.durationType,
      duration: booking.duration,
      totalPrice: booking.totalPrice,
      bookedAt: booking.bookedAt,
      status: booking.status,
    );
    rows.insert(0, savedBooking.toMap());
    await _databaseService.saveBookingMaps(rows);
    return savedBooking;
  }

  @override
  Future<void> deleteBooking(int id) async {
    final rows = await _databaseService.loadBookingMaps();
    rows.removeWhere((item) => item['id'] == id);
    await _databaseService.saveBookingMaps(rows);
  }

  @override
  Future<void> clearBookingsCache() async {
    await _databaseService.clearBookings();
  }
}
