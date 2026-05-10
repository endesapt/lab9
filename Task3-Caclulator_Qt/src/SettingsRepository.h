#pragma once

#include <QString>
#include <QStringList>

#include <memory>

class SettingsStorageBackend;

struct PersistentState {
    int launchCount = 0;
    QString appId;

    int maxAttempts = 7;
    int totalPoints = 0;
    int bestScore = 0;

    QString accountName = QStringLiteral("Guest");
    QString accountEmail = QStringLiteral("guest@example.com");
    QString languageCode = QStringLiteral("en");
    QStringList historyEntries;
};

class SettingsRepository
{
public:
    explicit SettingsRepository(const QString &iniFilePath = QString());
    ~SettingsRepository();

    PersistentState initializeState();
    void saveState(const PersistentState &state);

private:
    std::unique_ptr<SettingsStorageBackend> backend_;
};
