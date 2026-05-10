#pragma once

#include "GameEngine.h"
#include "SettingsRepository.h"

#include <QObject>
#include <QString>
#include <QStringList>

class GameViewModel : public QObject
{
    Q_OBJECT
    Q_PROPERTY(int launchCount READ launchCount NOTIFY appInfoChanged)
    Q_PROPERTY(QString appId READ appId NOTIFY appInfoChanged)

    Q_PROPERTY(int maxAttempts READ maxAttempts WRITE setMaxAttempts NOTIFY settingsChanged)
    Q_PROPERTY(int remainingAttempts READ remainingAttempts NOTIFY gameChanged)
    Q_PROPERTY(int totalPoints READ totalPoints NOTIFY settingsChanged)
    Q_PROPERTY(int bestScore READ bestScore NOTIFY settingsChanged)

    Q_PROPERTY(QString accountName READ accountName NOTIFY accountChanged)
    Q_PROPERTY(QString accountEmail READ accountEmail NOTIFY accountChanged)
    Q_PROPERTY(QString languageCode READ languageCode NOTIFY settingsChanged)

    Q_PROPERTY(QString lastResult READ lastResult NOTIFY gameChanged)
    Q_PROPERTY(QStringList historyEntries READ historyEntries NOTIFY historyChanged)

public:
    explicit GameViewModel(QObject *parent = nullptr);

    int launchCount() const;
    QString appId() const;

    int maxAttempts() const;
    void setMaxAttempts(int maxAttempts);

    int remainingAttempts() const;
    int totalPoints() const;
    int bestScore() const;

    QString accountName() const;
    QString accountEmail() const;
    QString languageCode() const;

    QString lastResult() const;
    QStringList historyEntries() const;

    Q_INVOKABLE void submitGuess(const QString &guessText);
    Q_INVOKABLE void startNewGame();
    Q_INVOKABLE void saveAccount(const QString &name, const QString &email);
    Q_INVOKABLE void changeLanguage(const QString &code);
    Q_INVOKABLE void clearHistory();

signals:
    void appInfoChanged();
    void settingsChanged();
    void accountChanged();
    void gameChanged();
    void historyChanged();

    void attemptDialogRequested(const QString &title, const QString &message);
    void languageChangeRequested(const QString &languageCode);

private:
    void prependHistoryEntry(const QString &message);
    void saveStateSafe();
    void setLastResult(const QString &message);

    SettingsRepository repository_;
    PersistentState state_;
    GameEngine engine_;
    bool roundFinished_;
    QString lastResult_;
};
