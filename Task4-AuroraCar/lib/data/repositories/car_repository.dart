import '../models/car.dart';

abstract class CarRepository {
  List<Car> fetchCars();
}

class DemoCarRepository implements CarRepository {
  @override
  List<Car> fetchCars() {
    return const [
      Car(
        id: 'ev-1',
        name: 'Minsk Sprint',
        description: 'Compact EV for quick city trips',
        latitude: 53.9045,
        longitude: 27.5615,
        hourlyRate: 52,
        dailyRate: 189,
        batteryPercent: 84,
        rangeKm: 280,
        seats: 4,
      ),
      Car(
        id: 'ev-2',
        name: 'Nemiga Wagon',
        description: 'Family wagon with roomy trunk',
        latitude: 53.9054,
        longitude: 27.5472,
        hourlyRate: 68,
        dailyRate: 245,
        batteryPercent: 67,
        rangeKm: 350,
        seats: 5,
      ),
      Car(
        id: 'ev-3',
        name: 'Victory Mini',
        description: 'Easy parking near the center',
        latitude: 53.9168,
        longitude: 27.5827,
        hourlyRate: 49,
        dailyRate: 176,
        batteryPercent: 93,
        rangeKm: 240,
        seats: 4,
      ),
    ];
  }
}
