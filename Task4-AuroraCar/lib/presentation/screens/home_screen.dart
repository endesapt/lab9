import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../data/models/booking.dart';
import '../../data/models/car.dart';
import '../providers/rental_provider.dart';
import '../widgets/booking_sheet.dart';
import '../widgets/car_card.dart';
import '../widgets/car_map.dart';
import 'settings_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);
    final pages = const [_CarsTab(), _BookingsTab(), SettingsScreen()];

    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth >= 960;

        return Scaffold(
          appBar: AppBar(
            title: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(loc.text('appTitle')),
                Text(
                  loc.text('tagline'),
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ],
            ),
          ),
          body: isWide
              ? Row(
                  children: [
                    NavigationRail(
                      selectedIndex: _selectedIndex,
                      onDestinationSelected: (index) {
                        setState(() => _selectedIndex = index);
                      },
                      labelType: NavigationRailLabelType.all,
                      destinations: [
                        NavigationRailDestination(
                          icon: const Icon(Icons.directions_car_outlined),
                          selectedIcon: const Icon(Icons.directions_car),
                          label: Text(loc.text('carsTab')),
                        ),
                        NavigationRailDestination(
                          icon: const Icon(Icons.receipt_long_outlined),
                          selectedIcon: const Icon(Icons.receipt_long),
                          label: Text(loc.text('bookingsTab')),
                        ),
                        NavigationRailDestination(
                          icon: const Icon(Icons.settings_outlined),
                          selectedIcon: const Icon(Icons.settings),
                          label: Text(loc.text('settingsTitle')),
                        ),
                      ],
                    ),
                    const VerticalDivider(width: 1),
                    Expanded(
                      child: AnimatedSwitcher(
                        duration: const Duration(milliseconds: 220),
                        child: KeyedSubtree(
                          key: ValueKey(_selectedIndex),
                          child: pages[_selectedIndex],
                        ),
                      ),
                    ),
                  ],
                )
              : AnimatedSwitcher(
                  duration: const Duration(milliseconds: 220),
                  child: KeyedSubtree(
                    key: ValueKey(_selectedIndex),
                    child: pages[_selectedIndex],
                  ),
                ),
          bottomNavigationBar: isWide
              ? null
              : NavigationBar(
                  selectedIndex: _selectedIndex,
                  onDestinationSelected: (index) {
                    setState(() => _selectedIndex = index);
                  },
                  destinations: [
                    NavigationDestination(
                      icon: const Icon(Icons.directions_car_outlined),
                      selectedIcon: const Icon(Icons.directions_car),
                      label: loc.text('carsTab'),
                    ),
                    NavigationDestination(
                      icon: const Icon(Icons.receipt_long_outlined),
                      selectedIcon: const Icon(Icons.receipt_long),
                      label: loc.text('bookingsTab'),
                    ),
                    NavigationDestination(
                      icon: const Icon(Icons.settings_outlined),
                      selectedIcon: const Icon(Icons.settings),
                      label: loc.text('settingsTitle'),
                    ),
                  ],
                ),
        );
      },
    );
  }
}

class _CarsTab extends StatelessWidget {
  const _CarsTab();

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);

    return Consumer<RentalProvider>(
      builder: (context, rental, _) {
        if (rental.isLoading) {
          return Center(child: Text(loc.text('loading')));
        }

        return LayoutBuilder(
          builder: (context, constraints) {
            final selectedCar = rental.selectedCar;
            final isDesktopLayout = constraints.maxWidth >= 1080;
            final gridColumns = constraints.maxWidth >= 1400
                ? 3
                : constraints.maxWidth >= 760
                ? 2
                : 1;

            final cardsGrid = GridView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: rental.cars.length,
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: gridColumns,
                mainAxisSpacing: 12,
                crossAxisSpacing: 12,
                childAspectRatio: gridColumns == 1 ? 1.16 : 1.1,
              ),
              itemBuilder: (context, index) {
                final car = rental.cars[index];
                return TweenAnimationBuilder<double>(
                  key: ValueKey(car.id),
                  tween: Tween(begin: 0, end: 1),
                  duration: const Duration(milliseconds: 450),
                  builder: (context, value, child) {
                    return Opacity(
                      opacity: value,
                      child: Transform.translate(
                        offset: Offset(0, 18 * (1 - value)),
                        child: child,
                      ),
                    );
                  },
                  child: CarCard(
                    car: car,
                    isSelected: selectedCar?.id == car.id,
                    onTap: () => rental.selectCar(car),
                    onBook: () => _openBookingSheet(context, car),
                  ),
                );
              },
            );

            if (isDesktopLayout) {
              return SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 24),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Expanded(
                      flex: 5,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            loc.text('mapTitle'),
                            style: Theme.of(context).textTheme.titleLarge,
                          ),
                          const SizedBox(height: 8),
                          Text(loc.text('mapHint')),
                          const SizedBox(height: 12),
                          SizedBox(
                            height: 340,
                            child: CarMap(
                              cars: rental.cars,
                              selectedCar: selectedCar,
                              onCarTap: rental.selectCar,
                            ),
                          ),
                          const SizedBox(height: 16),
                          AnimatedSwitcher(
                            duration: const Duration(milliseconds: 350),
                            child: selectedCar == null
                                ? const SizedBox.shrink()
                                : _SelectedCarPanel(car: selectedCar),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(width: 20),
                    Expanded(
                      flex: 6,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            loc.text('availableCars'),
                            style: Theme.of(context).textTheme.titleLarge,
                          ),
                          const SizedBox(height: 12),
                          cardsGrid,
                        ],
                      ),
                    ),
                  ],
                ),
              );
            }

            return ListView(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 24),
              children: [
                Text(
                  loc.text('mapTitle'),
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 8),
                Text(loc.text('mapHint')),
                const SizedBox(height: 12),
                SizedBox(
                  height: 250,
                  child: CarMap(
                    cars: rental.cars,
                    selectedCar: selectedCar,
                    onCarTap: rental.selectCar,
                  ),
                ),
                const SizedBox(height: 16),
                AnimatedSwitcher(
                  duration: const Duration(milliseconds: 350),
                  child: selectedCar == null
                      ? const SizedBox.shrink()
                      : _SelectedCarPanel(car: selectedCar),
                ),
                const SizedBox(height: 16),
                Text(
                  loc.text('availableCars'),
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 12),
                cardsGrid,
              ],
            );
          },
        );
      },
    );
  }

  Future<void> _openBookingSheet(BuildContext context, Car car) async {
    context.read<RentalProvider>().selectCar(car);
    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      builder: (_) => BookingSheet(car: car),
    );
  }
}

class _SelectedCarPanel extends StatelessWidget {
  const _SelectedCarPanel({required this.car});

  final Car car;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);

    return Card(
      key: ValueKey('selected-${car.id}'),
      child: Padding(
        padding: const EdgeInsets.all(18),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              loc.text('selectedCar'),
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            Text(car.name, style: Theme.of(context).textTheme.headlineSmall),
            const SizedBox(height: 4),
            Text(car.description),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                Chip(
                  label: Text('${loc.text('battery')}: ${car.batteryPercent}%'),
                ),
                Chip(
                  label: Text(
                    '${loc.text('range')}: ${car.rangeKm} ${loc.text('km')}',
                  ),
                ),
                Chip(label: Text('${loc.text('seats')}: ${car.seats}')),
                Chip(
                  label: Text(
                    '${loc.text('price')}: ${car.dailyRate} ${loc.text('currency')} ${loc.text('perDay')}',
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _BookingsTab extends StatelessWidget {
  const _BookingsTab();

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);

    return Consumer<RentalProvider>(
      builder: (context, rental, _) {
        if (rental.isLoading) {
          return Center(child: Text(loc.text('loading')));
        }

        if (rental.bookings.isEmpty) {
          return Center(child: Text(loc.text('bookingsEmpty')));
        }

        return LayoutBuilder(
          builder: (context, constraints) {
            final columns = constraints.maxWidth >= 1200 ? 2 : 1;

            if (columns == 1) {
              return ListView.separated(
                padding: const EdgeInsets.all(16),
                itemCount: rental.bookings.length,
                separatorBuilder: (context, index) =>
                    const SizedBox(height: 12),
                itemBuilder: (context, index) {
                  final booking = rental.bookings[index];
                  return _BookingTile(booking: booking);
                },
              );
            }

            return GridView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: rental.bookings.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                childAspectRatio: 1.8,
              ),
              itemBuilder: (context, index) {
                final booking = rental.bookings[index];
                return _BookingTile(booking: booking);
              },
            );
          },
        );
      },
    );
  }
}

class _BookingTile extends StatelessWidget {
  const _BookingTile({required this.booking});

  final Booking booking;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);
    final durationLabel = booking.durationType == 'hour'
        ? loc.text('hourly')
        : loc.text('daily');
    final messenger = ScaffoldMessenger.of(context);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CircleAvatar(
              backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
              child: const Icon(Icons.directions_car),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    booking.carName,
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 6),
                  Text(
                    '${loc.text('bookedAt')}: ${booking.bookedAt.toLocal().toString().substring(0, 16)}\n'
                    '${booking.duration} $durationLabel • ${loc.text('statusPaid')}',
                  ),
                  const SizedBox(height: 10),
                  Wrap(
                    spacing: 10,
                    runSpacing: 10,
                    crossAxisAlignment: WrapCrossAlignment.center,
                    children: [
                      Text(
                        '${booking.totalPrice} ${loc.text('currency')}',
                        style: Theme.of(context).textTheme.titleSmall,
                      ),
                      OutlinedButton(
                        key: ValueKey('cancel-booking-${booking.id}'),
                        onPressed: booking.id == null
                            ? null
                            : () async {
                                final canceled = await context
                                    .read<RentalProvider>()
                                    .cancelBooking(booking.id!);

                                if (!context.mounted) {
                                  return;
                                }

                                messenger.showSnackBar(
                                  SnackBar(
                                    content: Text(
                                      canceled
                                          ? loc.text('bookingCanceled')
                                          : loc.text('errorTitle'),
                                    ),
                                  ),
                                );
                              },
                        child: Text(loc.text('cancelBooking')),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
