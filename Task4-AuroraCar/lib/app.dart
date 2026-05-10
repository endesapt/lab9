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
import 'presentation/providers/session_provider.dart';
import 'presentation/providers/theme_provider.dart';
import 'presentation/screens/home_screen.dart';
import 'presentation/screens/login_screen.dart';

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
          create: (_) => ThemeProvider(settingsService)..loadThemeMode(),
        ),
        ChangeNotifierProvider(
          create: (_) => SessionProvider(settingsService)..loadSession(),
        ),
        ChangeNotifierProvider(
          create: (_) => RentalProvider(
            bookingRepository: bookingRepository,
            carRepository: carRepository,
          )..initialize(),
        ),
      ],
      child: Consumer3<LocaleProvider, ThemeProvider, SessionProvider>(
        builder: (context, localeProvider, themeProvider, sessionProvider, _) {
          return MaterialApp(
            debugShowCheckedModeBanner: false,
            title: 'Aurora Drive',
            theme: buildAppTheme(brightness: Brightness.light),
            darkTheme: buildAppTheme(brightness: Brightness.dark),
            themeMode: themeProvider.themeMode,
            locale: localeProvider.locale,
            supportedLocales: AppLocalizations.supportedLocales,
            localizationsDelegates: const [
              AppLocalizations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            home: sessionProvider.isLoading
                ? const Scaffold(
                    body: Center(child: CircularProgressIndicator()),
                  )
                : sessionProvider.isAuthenticated
                ? const HomeScreen()
                : const LoginScreen(),
          );
        },
      ),
    );
  }
}
