#pragma once

#include <QString>

#include <memory>

#include <QSettings>

struct PersistentState {
    int launchCount = 0;
    QString appId;

    int maxAttempts = 7;
    int totalPoints = 0;
    int bestScore = 0;

    QString accountName = QStringLiteral("Guest");
    QString accountEmail = QStringLiteral("guest@example.com");
    QString languageCode = QStringLiteral("en");
};

class SettingsRepository
{
public:
    explicit SettingsRepository(const QString &iniFilePath = QString());

    PersistentState initializeState();
    void saveState(const PersistentState &state);

private:
    std::unique_ptr<QSettings> settings_;
};
