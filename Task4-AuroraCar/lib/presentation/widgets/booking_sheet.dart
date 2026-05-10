import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../data/models/car.dart';
import '../providers/rental_provider.dart';

class BookingSheet extends StatefulWidget {
  const BookingSheet({super.key, required this.car});

  final Car car;

  @override
  State<BookingSheet> createState() => _BookingSheetState();
}

class _BookingSheetState extends State<BookingSheet> {
  String _durationType = 'hour';
  double _duration = 2;
  bool _submitting = false;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);
    final provider = context.watch<RentalProvider>();
    final total = provider.calculatePrice(
      car: widget.car,
      durationType: _durationType,
      duration: _duration.round(),
    );

    return SafeArea(
      child: Padding(
        padding: EdgeInsets.fromLTRB(
          20,
          8,
          20,
          MediaQuery.of(context).viewInsets.bottom + 20,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              widget.car.name,
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 4),
            Text(widget.car.description),
            const SizedBox(height: 16),
            Text(
              loc.text('duration'),
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            SegmentedButton<String>(
              segments: [
                ButtonSegment(value: 'hour', label: Text(loc.text('hourly'))),
                ButtonSegment(value: 'day', label: Text(loc.text('daily'))),
              ],
              selected: {_durationType},
              onSelectionChanged: (value) {
                setState(() {
                  _durationType = value.first;
                  _duration = _durationType == 'hour' ? 2 : 1;
                });
              },
            ),
            const SizedBox(height: 12),
            Text(
              '${loc.text('duration')}: ${_duration.round()} ${_durationType == 'hour' ? loc.text('hourly') : loc.text('daily')}',
            ),
            Slider(
              key: const ValueKey('duration-slider'),
              value: _duration,
              min: 1,
              max: _durationType == 'hour' ? 12 : 7,
              divisions: _durationType == 'hour' ? 11 : 6,
              label: _duration.round().toString(),
              onChanged: (value) => setState(() => _duration = value),
            ),
            const SizedBox(height: 8),
            Card(
              color: Theme.of(context).colorScheme.secondaryContainer,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(loc.text('rentalSummary')),
                    const SizedBox(height: 8),
                    Text(
                      '${loc.text('price')}: $total ${loc.text('currency')}',
                    ),
                    const SizedBox(height: 4),
                    Text(loc.text('fakePaymentHint')),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: _submitting
                        ? null
                        : () => Navigator.of(context).pop(),
                    child: Text(loc.text('cancel')),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: FilledButton(
                    key: const ValueKey('confirm-booking'),
                    onPressed: _submitting
                        ? null
                        : () => _submit(
                            context,
                            _durationType,
                            _duration.round(),
                          ),
                    child: Text(
                      _submitting
                          ? loc.text('loading')
                          : loc.text('payAndBook'),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _submit(
    BuildContext context,
    String durationType,
    int duration,
  ) async {
    setState(() => _submitting = true);
    final loc = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    final provider = context.read<RentalProvider>();
    final navigator = Navigator.of(context);
    final saved = await provider.createBooking(
      durationType: durationType,
      duration: duration,
    );

    if (!mounted) {
      return;
    }

    setState(() => _submitting = false);

    if (saved) {
      navigator.pop();
      messenger.showSnackBar(SnackBar(content: Text(loc.text('bookingSaved'))));
    } else {
      messenger.showSnackBar(SnackBar(content: Text(loc.text('errorTitle'))));
    }
  }
}
