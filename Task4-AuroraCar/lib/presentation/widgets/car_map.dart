import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';

import '../../data/models/car.dart';

class CarMap extends StatelessWidget {
  const CarMap({
    super.key,
    required this.cars,
    required this.selectedCar,
    required this.onCarTap,
  });

  final List<Car> cars;
  final Car? selectedCar;
  final ValueChanged<Car> onCarTap;

  @override
  Widget build(BuildContext context) {
    final center = selectedCar == null
        ? const LatLng(53.9006, 27.5590)
        : LatLng(selectedCar!.latitude, selectedCar!.longitude);

    return ClipRRect(
      borderRadius: BorderRadius.circular(28),
      child: FlutterMap(
        options: MapOptions(
          initialCenter: center,
          initialZoom: 12.8,
        ),
        children: [
          TileLayer(
            urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
            userAgentPackageName: 'car_rental_aurora',
          ),
          MarkerLayer(
            markers: cars.map((car) {
                final isSelected = selectedCar?.id == car.id;
                return Marker(
                  point: LatLng(car.latitude, car.longitude),
                  width: 72,
                  height: 72,
                  child: GestureDetector(
                    key: ValueKey('marker-${car.id}'),
                    onTap: () => onCarTap(car),
                    child: AnimatedContainer(
                      duration: const Duration(milliseconds: 250),
                      decoration: BoxDecoration(
                        color: isSelected
                            ? Theme.of(context).colorScheme.secondary
                            : Theme.of(context).colorScheme.primary,
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withValues(alpha: 0.18),
                            blurRadius: 10,
                            offset: const Offset(0, 6),
                          ),
                        ],
                      ),
                      child: const Icon(
                        Icons.electric_car,
                        color: Colors.white,
                      ),
                    ),
                  ),
                );
              }).toList(),
          ),
        ],
      ),
    );
  }
}
