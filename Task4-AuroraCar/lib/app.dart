import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';

import 'core/l10n/app_localizations.dart';
import 'core/theme/app_theme.dart';
import 'data/repositories/booking_repository.dart';
import 'data/repositories/car_repository.dart';
import 'data/services/settings_service.dart';
import 'presentation/providers/locale_provider.dart';
import 'presentation/providers/rental_provider.dart';
import 'presentation/screens/home_screen.dart';

class CarRentalApp extends StatelessWidget {
  const CarRentalApp({
    super.key,
    required this.settingsService,
    required this.bookingRepository,
    required this.carRepository,
  });

  final SettingsService settingsService;
  final BookingRepository bookingRepository;
  final CarRepository carRepository;

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => LocaleProvider(settingsService)..loadLocale(),
        ),
        ChangeNotifierProvider(
          create: (_) => RentalProvider(
            bookingRepository: bookingRepository,
            carRepository: carRepository,
          )..initialize(),
        ),
      ],
      child: Consumer<LocaleProvider>(
        builder: (context, localeProvider, _) {
          return MaterialApp(
            debugShowCheckedModeBanner: false,
            title: 'Aurora Drive',
            theme: buildAppTheme(),
            locale: localeProvider.locale,
            supportedLocales: AppLocalizations.supportedLocales,
            localizationsDelegates: const [
              AppLocalizations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            home: const HomeScreen(),
          );
        },
      ),
    );
  }
}
