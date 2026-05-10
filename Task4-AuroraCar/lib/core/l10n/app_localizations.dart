import 'package:flutter/material.dart';

class AppLocalizations {
  const AppLocalizations(this.locale);

  final Locale locale;

  static const supportedLocales = [
    Locale('en'),
    Locale('ru'),
    Locale('be'),
  ];

  static const delegate = _AppLocalizationsDelegate();

  static const Map<String, Map<String, String>> _localizedValues = {
    'en': {
      'appTitle': 'Aurora Drive',
      'carsTab': 'Cars',
      'bookingsTab': 'Bookings',
      'tagline': 'Hourly and daily rentals with in-app booking',
      'availableCars': 'Available cars',
      'mapTitle': 'Map of nearby cars',
      'selectedCar': 'Selected car',
      'bookNow': 'Book now',
      'hourly': 'Hour',
      'daily': 'Day',
      'duration': 'Duration',
      'payment': 'Payment',
      'fakePaymentHint': 'Fake payment only. No real transaction happens.',
      'payAndBook': 'Pay and book',
      'cancel': 'Cancel',
      'cancelBooking': 'Cancel booking',
      'bookingCanceled': 'Booking canceled',
      'bookingsEmpty': 'No bookings yet',
      'bookingSaved': 'Booking saved',
      'language': 'Language',
      'price': 'Price',
      'perHour': '/hour',
      'perDay': '/day',
      'seats': 'Seats',
      'battery': 'Battery',
      'range': 'Range',
      'km': 'km',
      'bookingHistory': 'Booking history',
      'mapHint': 'Tap a marker or a card to inspect a car.',
      'rentalSummary': 'Rental summary',
      'close': 'Close',
      'errorTitle': 'Could not save booking',
      'bookedAt': 'Booked at',
      'statusPaid': 'Paid (demo)',
      'loading': 'Loading...',
      'currency': 'BYN',
    },
    'ru': {
      'appTitle': 'Aurora Drive',
      'carsTab': 'Авто',
      'bookingsTab': 'Брони',
      'tagline': 'Почасовая и посуточная аренда с бронированием в приложении',
      'availableCars': 'Доступные машины',
      'mapTitle': 'Карта ближайших машин',
      'selectedCar': 'Выбранный автомобиль',
      'bookNow': 'Забронировать',
      'hourly': 'Час',
      'daily': 'День',
      'duration': 'Длительность',
      'payment': 'Оплата',
      'fakePaymentHint': 'Оплата фиктивная. Реального списания не происходит.',
      'payAndBook': 'Оплатить и забронировать',
      'cancel': 'Отмена',
      'cancelBooking': 'Отменить бронь',
      'bookingCanceled': 'Бронь отменена',
      'bookingsEmpty': 'Пока нет бронирований',
      'bookingSaved': 'Бронирование сохранено',
      'language': 'Язык',
      'price': 'Цена',
      'perHour': '/час',
      'perDay': '/день',
      'seats': 'Мест',
      'battery': 'Заряд',
      'range': 'Запас хода',
      'km': 'км',
      'bookingHistory': 'История бронирований',
      'mapHint': 'Нажмите на метку или карточку, чтобы выбрать машину.',
      'rentalSummary': 'Сводка аренды',
      'close': 'Закрыть',
      'errorTitle': 'Не удалось сохранить бронь',
      'bookedAt': 'Время брони',
      'statusPaid': 'Оплачено (демо)',
      'loading': 'Загрузка...',
      'currency': 'BYN',
    },
    'be': {
      'appTitle': 'Aurora Drive',
      'carsTab': 'Аўто',
      'bookingsTab': 'Брані',
      'tagline': 'Пагадзінная і пасутачная арэнда з браніраваннем у праграме',
      'availableCars': 'Даступныя аўтамабілі',
      'mapTitle': 'Карта бліжэйшых машын',
      'selectedCar': 'Абраны аўтамабіль',
      'bookNow': 'Забраніраваць',
      'hourly': 'Гадзіна',
      'daily': 'Дзень',
      'duration': 'Працягласць',
      'payment': 'Аплата',
      'fakePaymentHint': 'Аплата фіктыўная. Рэальнага спісання няма.',
      'payAndBook': 'Аплаціць і забраніраваць',
      'cancel': 'Адмена',
      'cancelBooking': 'Адмяніць бронь',
      'bookingCanceled': 'Бронь адменена',
      'bookingsEmpty': 'Пакуль няма браніраванняў',
      'bookingSaved': 'Браніраванне захавана',
      'language': 'Мова',
      'price': 'Кошт',
      'perHour': '/гадз',
      'perDay': '/дзень',
      'seats': 'Месцаў',
      'battery': 'Зарад',
      'range': 'Запас ходу',
      'km': 'км',
      'bookingHistory': 'Гісторыя браніраванняў',
      'mapHint': 'Націсніце на маркер або картку, каб выбраць аўто.',
      'rentalSummary': 'Зводка арэнды',
      'close': 'Закрыць',
      'errorTitle': 'Не ўдалося захаваць бронь',
      'bookedAt': 'Час брані',
      'statusPaid': 'Аплочана (дэма)',
      'loading': 'Загрузка...',
      'currency': 'BYN',
    },
  };

  static AppLocalizations of(BuildContext context) {
    final result = Localizations.of<AppLocalizations>(
      context,
      AppLocalizations,
    );
    assert(result != null, 'No AppLocalizations found in context');
    return result!;
  }

  String text(String key) {
    return _localizedValues[locale.languageCode]?[key] ??
        _localizedValues['en']![key] ??
        key;
  }
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  bool isSupported(Locale locale) {
    return AppLocalizations.supportedLocales.any(
      (supported) => supported.languageCode == locale.languageCode,
    );
  }

  @override
  Future<AppLocalizations> load(Locale locale) async {
    return AppLocalizations(locale);
  }

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}
