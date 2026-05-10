import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../presentation/providers/locale_provider.dart';
import '../../presentation/providers/rental_provider.dart';
import '../../presentation/providers/session_provider.dart';
import '../../presentation/providers/theme_provider.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context);
    final themeProvider = context.watch<ThemeProvider>();
    final localeProvider = context.watch<LocaleProvider>();
    final sessionProvider = context.watch<SessionProvider>();

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(
          loc.text('settingsTitle'),
          style: Theme.of(context).textTheme.headlineSmall,
        ),
        const SizedBox(height: 16),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  loc.text('themeTitle'),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 12),
                SegmentedButton<ThemeMode>(
                  segments: [
                    ButtonSegment(
                      value: ThemeMode.system,
                      label: Text(loc.text('themeSystem')),
                    ),
                    ButtonSegment(
                      value: ThemeMode.light,
                      label: Text(loc.text('themeLight')),
                    ),
                    ButtonSegment(
                      value: ThemeMode.dark,
                      label: Text(loc.text('themeDark')),
                    ),
                  ],
                  selected: {themeProvider.themeMode},
                  onSelectionChanged: (selection) {
                    themeProvider.setThemeMode(selection.first);
                  },
                ),
                const SizedBox(height: 18),
                Text(
                  loc.text('language'),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 12),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: [
                    _LocaleChip(
                      label: 'English',
                      locale: const Locale('en'),
                      selected: localeProvider.locale.languageCode == 'en',
                    ),
                    _LocaleChip(
                      label: 'Русский',
                      locale: const Locale('ru'),
                      selected: localeProvider.locale.languageCode == 'ru',
                    ),
                    _LocaleChip(
                      label: 'Беларуская',
                      locale: const Locale('be'),
                      selected: localeProvider.locale.languageCode == 'be',
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  loc.text('cacheTitle'),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 10),
                Text(loc.text('cacheHint')),
                const SizedBox(height: 16),
                OutlinedButton.icon(
                  key: const ValueKey('clear-cache'),
                  onPressed: () async {
                    await context.read<RentalProvider>().clearBookingsCache();
                    if (!context.mounted) {
                      return;
                    }
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text(loc.text('cacheCleared'))),
                    );
                  },
                  icon: const Icon(Icons.delete_outline),
                  label: Text(loc.text('clearCache')),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  loc.text('accountTitle'),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 10),
                Text(sessionProvider.email ?? ''),
                const SizedBox(height: 6),
                Text('${loc.text('versionLabel')}: 1.0.0+1'),
                const SizedBox(height: 16),
                FilledButton.tonalIcon(
                  key: const ValueKey('sign-out'),
                  onPressed: () async {
                    await context.read<SessionProvider>().signOut();
                  },
                  icon: const Icon(Icons.logout),
                  label: Text(loc.text('signOut')),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _LocaleChip extends StatelessWidget {
  const _LocaleChip({
    required this.label,
    required this.locale,
    required this.selected,
  });

  final String label;
  final Locale locale;
  final bool selected;

  @override
  Widget build(BuildContext context) {
    return FilterChip(
      selected: selected,
      label: Text(label),
      onSelected: (_) => context.read<LocaleProvider>().setLocale(locale),
    );
  }
}
