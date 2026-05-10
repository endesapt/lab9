import 'package:flutter/foundation.dart';

import '../../data/models/booking.dart';
import '../../data/models/car.dart';
import '../../data/repositories/booking_repository.dart';
import '../../data/repositories/car_repository.dart';

class RentalProvider extends ChangeNotifier {
  RentalProvider({
    required BookingRepository bookingRepository,
    required CarRepository carRepository,
  }) : _bookingRepository = bookingRepository,
       _carRepository = carRepository;

  final BookingRepository _bookingRepository;
  final CarRepository _carRepository;

  List<Car> _cars = const [];
  List<Booking> _bookings = const [];
  Car? _selectedCar;
  bool _isLoading = true;

  List<Car> get cars => _cars;
  List<Booking> get bookings => _bookings;
  Car? get selectedCar => _selectedCar;
  bool get isLoading => _isLoading;

  Future<void> initialize() async {
    _cars = _carRepository.fetchCars();
    _selectedCar = _cars.isEmpty ? null : _cars.first;
    notifyListeners();

    try {
      _bookings = await _bookingRepository.loadBookings();
    } catch (error, stackTrace) {
      debugPrint('Failed to load bookings: $error');
      debugPrintStack(stackTrace: stackTrace);
      _bookings = const [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void selectCar(Car car) {
    _selectedCar = car;
    notifyListeners();
  }

  int calculatePrice({
    required Car car,
    required String durationType,
    required int duration,
  }) {
    final rate = durationType == 'hour' ? car.hourlyRate : car.dailyRate;
    return rate * duration;
  }

  Future<bool> createBooking({
    required String durationType,
    required int duration,
  }) async {
    final car = _selectedCar;
    if (car == null) {
      return false;
    }

    final booking = Booking(
      carId: car.id,
      carName: car.name,
      durationType: durationType,
      duration: duration,
      totalPrice: calculatePrice(
        car: car,
        durationType: durationType,
        duration: duration,
      ),
      bookedAt: DateTime.now(),
      status: 'paid_demo',
    );

    try {
      final savedBooking = await _bookingRepository.saveBooking(booking);
      _bookings = [savedBooking, ..._bookings];
      notifyListeners();
      return true;
    } catch (error, stackTrace) {
      debugPrint('Failed to save booking: $error');
      debugPrintStack(stackTrace: stackTrace);
      return false;
    }
  }

  Future<bool> cancelBooking(int id) async {
    try {
      await _bookingRepository.deleteBooking(id);
      _bookings = _bookings.where((booking) => booking.id != id).toList();
      notifyListeners();
      return true;
    } catch (error, stackTrace) {
      debugPrint('Failed to cancel booking: $error');
      debugPrintStack(stackTrace: stackTrace);
      return false;
    }
  }

  Future<void> clearBookingsCache() async {
    try {
      await _bookingRepository.clearBookingsCache();
      _bookings = const [];
      notifyListeners();
    } catch (error, stackTrace) {
      debugPrint('Failed to clear cache: $error');
      debugPrintStack(stackTrace: stackTrace);
    }
  }
}
