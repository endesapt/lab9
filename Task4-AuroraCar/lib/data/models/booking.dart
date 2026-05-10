class Booking {
  const Booking({
    this.id,
    required this.carId,
    required this.carName,
    required this.durationType,
    required this.duration,
    required this.totalPrice,
    required this.bookedAt,
    required this.status,
  });

  final int? id;
  final String carId;
  final String carName;
  final String durationType;
  final int duration;
  final int totalPrice;
  final DateTime bookedAt;
  final String status;

  Map<String, Object?> toMap() {
    return {
      'id': id,
      'carId': carId,
      'carName': carName,
      'durationType': durationType,
      'duration': duration,
      'totalPrice': totalPrice,
      'bookedAt': bookedAt.toIso8601String(),
      'status': status,
    };
  }

  factory Booking.fromMap(Map<String, Object?> map) {
    return Booking(
      id: map['id'] as int?,
      carId: map['carId'] as String,
      carName: map['carName'] as String,
      durationType: map['durationType'] as String,
      duration: map['duration'] as int,
      totalPrice: map['totalPrice'] as int,
      bookedAt: DateTime.parse(map['bookedAt'] as String),
      status: map['status'] as String,
    );
  }
}
