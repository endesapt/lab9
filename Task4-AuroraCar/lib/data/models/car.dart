class Car {
  const Car({
    required this.id,
    required this.name,
    required this.description,
    required this.latitude,
    required this.longitude,
    required this.hourlyRate,
    required this.dailyRate,
    required this.batteryPercent,
    required this.rangeKm,
    required this.seats,
  });

  final String id;
  final String name;
  final String description;
  final double latitude;
  final double longitude;
  final int hourlyRate;
  final int dailyRate;
  final int batteryPercent;
  final int rangeKm;
  final int seats;
}
