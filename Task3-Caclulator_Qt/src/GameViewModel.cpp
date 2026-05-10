#include "GameViewModel.h"

#include <QDebug>

#include <algorithm>
#include <stdexcept>

GameViewModel::GameViewModel(QObject *parent)
    : QObject(parent), engine_(7), roundFinished_(false)
{
    try {
        state_ = repository_.initializeState();
        engine_.setMaxAttempts(state_.maxAttempts);
        engine_.startNewRound();
        emit appInfoChanged();
    } catch (const std::exception &ex) {
        qCritical() << "Settings initialization failed:" << ex.what();
        state_ = PersistentState{};
        saveStateSafe();
    }

    setLastResult(tr("Game is ready. Enter a number from 1 to 100."));
    prependHistoryEntry(tr("Application started."));
}

int GameViewModel::launchCount() const
{
    return state_.launchCount;
}

QString GameViewModel::appId() const
{
    return state_.appId;
}

int GameViewModel::maxAttempts() const
{
    return state_.maxAttempts;
}

void GameViewModel::setMaxAttempts(int maxAttempts)
{
    if (maxAttempts == state_.maxAttempts) {
        return;
    }

    try {
        engine_.setMaxAttempts(maxAttempts);
        state_.maxAttempts = maxAttempts;
        saveStateSafe();
        emit settingsChanged();
        startNewGame();
    } catch (const std::exception &ex) {
        qCritical() << "Invalid max attempts:" << ex.what();
    }
}

int GameViewModel::remainingAttempts() const
{
    return engine_.remainingAttempts();
}

int GameViewModel::totalPoints() const
{
    return state_.totalPoints;
}

int GameViewModel::bestScore() const
{
    return state_.bestScore;
}

QString GameViewModel::accountName() const
{
    return state_.accountName;
}

QString GameViewModel::accountEmail() const
{
    return state_.accountEmail;
}

QString GameViewModel::languageCode() const
{
    return state_.languageCode;
}

QString GameViewModel::lastResult() const
{
    return lastResult_;
}

QStringList GameViewModel::historyEntries() const
{
    return state_.historyEntries;
}

void GameViewModel::submitGuess(const QString &guessText)
{
    if (roundFinished_) {
        const QString message = tr("Round is finished. Start a new game.");
        setLastResult(message);
        prependHistoryEntry(message);
        emit attemptDialogRequested(tr("Attempt result"), message);
        return;
    }

    bool ok = false;
    const int guess = guessText.toInt(&ok);

    if (!ok) {
        const QString message = tr("Input error: please enter an integer number.");
        qCritical() << message;
        setLastResult(message);
        prependHistoryEntry(message);
        emit attemptDialogRequested(tr("Attempt result"), message);
        return;
    }

    try {
        const auto result = engine_.submitGuess(guess);

        QString message;
        if (result.status == GameEngine::GuessStatus::TooLow) {
            message = tr("Too low. Remaining attempts: %1").arg(result.remainingAttempts);
        } else if (result.status == GameEngine::GuessStatus::TooHigh) {
            message = tr("Too high. Remaining attempts: %1").arg(result.remainingAttempts);
        } else if (result.status == GameEngine::GuessStatus::Win) {
            roundFinished_ = true;
            state_.totalPoints += result.pointsAwarded;
            state_.bestScore = std::max(state_.bestScore, state_.totalPoints);
            saveStateSafe();
            emit settingsChanged();
            message = tr("You guessed it. +%1 points.").arg(result.pointsAwarded);
        } else {
            roundFinished_ = true;
            message = tr("No attempts left. The number was %1.").arg(engine_.targetNumber());
        }

        setLastResult(message);
        prependHistoryEntry(message);
        emit gameChanged();
        emit attemptDialogRequested(tr("Attempt result"), message);
    } catch (const std::exception &ex) {
        qCritical() << "Guess submission failed:" << ex.what();
        const QString message = tr("Input range error: use numbers from 1 to 100.");
        setLastResult(message);
        prependHistoryEntry(message);
        emit attemptDialogRequested(tr("Attempt result"), message);
    }
}

void GameViewModel::startNewGame()
{
    engine_.startNewRound();
    roundFinished_ = false;
    setLastResult(tr("New round started."));
    prependHistoryEntry(tr("New round started."));
    emit gameChanged();
}

void GameViewModel::saveAccount(const QString &name, const QString &email)
{
    try {
        state_.accountName = name.trimmed();
        state_.accountEmail = email.trimmed();
        saveStateSafe();
        emit accountChanged();
    } catch (const std::exception &ex) {
        qCritical() << "Failed to save account:" << ex.what();
    }
}

void GameViewModel::changeLanguage(const QString &code)
{
    if (code == state_.languageCode) {
        return;
    }

    state_.languageCode = code;
    saveStateSafe();
    emit settingsChanged();
    emit languageChangeRequested(code);
}

void GameViewModel::clearHistory()
{
    if (state_.historyEntries.isEmpty()) {
        return;
    }

    state_.historyEntries.clear();
    saveStateSafe();
    emit historyChanged();
}

void GameViewModel::prependHistoryEntry(const QString &message)
{
    state_.historyEntries.prepend(message);
    while (state_.historyEntries.size() > 12) {
        state_.historyEntries.removeLast();
    }
    saveStateSafe();
    emit historyChanged();
}

void GameViewModel::saveStateSafe()
{
    try {
        repository_.saveState(state_);
    } catch (const std::exception &ex) {
        qCritical() << "Failed to persist state:" << ex.what();
    }
}

void GameViewModel::setLastResult(const QString &message)
{
    if (lastResult_ == message) {
        return;
    }

    lastResult_ = message;
    emit gameChanged();
}
