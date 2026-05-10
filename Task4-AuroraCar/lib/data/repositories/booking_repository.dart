import 'package:sqflite/sqflite.dart';

import '../models/booking.dart';
import '../services/database_service.dart';

abstract class BookingRepository {
  Future<List<Booking>> loadBookings();
  Future<Booking> saveBooking(Booking booking);
  Future<void> deleteBooking(int id);
}

class SqfliteBookingRepository implements BookingRepository {
  SqfliteBookingRepository(this._databaseService);

  final DatabaseService _databaseService;

  @override
  Future<List<Booking>> loadBookings() async {
    final db = await _databaseService.database;
    final rows = await db.query(
      'bookings',
      orderBy: 'bookedAt DESC',
    );
    return rows.map(Booking.fromMap).toList();
  }

  @override
  Future<Booking> saveBooking(Booking booking) async {
    final db = await _databaseService.database;
    final id = await db.insert(
      'bookings',
      booking.toMap()..remove('id'),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
    return Booking(
      id: id,
      carId: booking.carId,
      carName: booking.carName,
      durationType: booking.durationType,
      duration: booking.duration,
      totalPrice: booking.totalPrice,
      bookedAt: booking.bookedAt,
      status: booking.status,
    );
  }

  @override
  Future<void> deleteBooking(int id) async {
    final db = await _databaseService.database;
    await db.delete(
      'bookings',
      where: 'id = ?',
      whereArgs: [id],
    );
  }
}
