import 'package:flutter/widgets.dart';

import 'app.dart';
import 'data/repositories/booking_repository.dart';
import 'data/repositories/car_repository.dart';
import 'data/services/database_service.dart';
import 'data/services/settings_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final settingsService = SettingsService();
  final databaseService = DatabaseService();
  final bookingRepository = SqfliteBookingRepository(databaseService);
  final carRepository = DemoCarRepository();

  runApp(
    CarRentalApp(
      settingsService: settingsService,
      bookingRepository: bookingRepository,
      carRepository: carRepository,
    ),
  );
}
