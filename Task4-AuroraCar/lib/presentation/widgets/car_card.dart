import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../../data/models/car.dart';

class CarCard extends StatelessWidget {
  const CarCard({
    super.key,
    required this.car,
    required this.isSelected,
    required this.onTap,
    required this.onBook,
  });

  final Car car;
  final bool isSelected;
  final VoidCallback onTap;
  final VoidCallback onBook;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(24),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 250),
        padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(
          color: isSelected
              ? Theme.of(context).colorScheme.primaryContainer
              : Colors.white,
          borderRadius: BorderRadius.circular(24),
          border: Border.all(
            color: isSelected
                ? Theme.of(context).colorScheme.primary
                : Colors.transparent,
            width: 1.5,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    car.name,
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                ),
                TweenAnimationBuilder<double>(
                  tween: Tween(begin: 0.94, end: isSelected ? 1.02 : 1),
                  duration: const Duration(milliseconds: 250),
                  builder: (context, value, child) {
                    return Transform.scale(scale: value, child: child);
                  },
                  child: Chip(label: Text('${car.batteryPercent}%')),
                ),
              ],
            ),
            const SizedBox(height: 6),
            Text(car.description),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                Chip(
                  label: Text(
                    '${loc.text('price')}: ${car.hourlyRate} ${loc.text('currency')} ${loc.text('perHour')}',
                  ),
                ),
                Chip(
                  label: Text(
                    '${car.dailyRate} ${loc.text('currency')} ${loc.text('perDay')}',
                  ),
                ),
                Chip(
                  label: Text(
                    '${loc.text('range')}: ${car.rangeKm} ${loc.text('km')}',
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton(
                key: ValueKey('book-${car.id}'),
                onPressed: onBook,
                child: Text(loc.text('bookNow')),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
