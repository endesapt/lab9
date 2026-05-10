#include <QtTest>

#include "../src/GameEngine.h"
#include "../src/GameViewModel.h"
#include "../src/SettingsRepository.h"

#include <QTemporaryDir>

class GuessGameTests : public QObject
{
    Q_OBJECT

private slots:
    void gameEngine_winAwardsPoints();
    void gameEngine_invalidGuessThrows();
    void settingsRepository_generatesIdAndPersistsState();
    void settingsRepository_persistsHistoryEntries();
    void gameViewModel_tracksRoundHistory();
};

void GuessGameTests::gameEngine_winAwardsPoints()
{
    GameEngine engine(5);
    engine.startNewRound();
    engine.setTargetNumberForTesting(42);

    const auto result = engine.submitGuess(42);

    QCOMPARE(result.status, GameEngine::GuessStatus::Win);
    QCOMPARE(result.remainingAttempts, 4);
    QCOMPARE(result.pointsAwarded, 5);
}

void GuessGameTests::gameEngine_invalidGuessThrows()
{
    GameEngine engine(5);
    engine.startNewRound();

    QVERIFY_THROWS_EXCEPTION(std::invalid_argument, engine.submitGuess(0));
    QVERIFY_THROWS_EXCEPTION(std::invalid_argument, engine.submitGuess(101));
}

void GuessGameTests::settingsRepository_generatesIdAndPersistsState()
{
    QTemporaryDir dir;
    QVERIFY(dir.isValid());

    const QString iniPath = dir.filePath("settings.sqlite");

    SettingsRepository repository(iniPath);
    PersistentState state = repository.initializeState();

    QVERIFY(!state.appId.isEmpty());
    QCOMPARE(state.launchCount, 1);

    state.maxAttempts = 9;
    state.totalPoints = 20;
    state.bestScore = 20;
    state.accountName = QStringLiteral("Tester");
    state.accountEmail = QStringLiteral("tester@example.com");
    state.languageCode = QStringLiteral("ru");
    repository.saveState(state);

    SettingsRepository repositoryReloaded(iniPath);
    const PersistentState reloaded = repositoryReloaded.initializeState();

    QCOMPARE(reloaded.launchCount, 2);
    QCOMPARE(reloaded.maxAttempts, 9);
    QCOMPARE(reloaded.totalPoints, 20);
    QCOMPARE(reloaded.bestScore, 20);
    QCOMPARE(reloaded.accountName, QStringLiteral("Tester"));
    QCOMPARE(reloaded.accountEmail, QStringLiteral("tester@example.com"));
    QCOMPARE(reloaded.languageCode, QStringLiteral("ru"));
}

void GuessGameTests::settingsRepository_persistsHistoryEntries()
{
    QTemporaryDir dir;
    QVERIFY(dir.isValid());

    const QString databasePath = dir.filePath("history.sqlite");

    SettingsRepository repository(databasePath);
    PersistentState state = repository.initializeState();
    state.accountName = QStringLiteral("Tester");
    state.historyEntries = {
        QStringLiteral("Round started"),
        QStringLiteral("Too low"),
        QStringLiteral("You guessed it")
    };
    repository.saveState(state);

    SettingsRepository repositoryReloaded(databasePath);
    const PersistentState reloaded = repositoryReloaded.initializeState();

    QCOMPARE(reloaded.historyEntries.size(), 3);
    QCOMPARE(reloaded.historyEntries.first(), QStringLiteral("Round started"));
    QCOMPARE(reloaded.historyEntries.last(), QStringLiteral("You guessed it"));
}

void GuessGameTests::gameViewModel_tracksRoundHistory()
{
    GameViewModel viewModel;
    const int historyBefore = viewModel.historyEntries().size();

    viewModel.startNewGame();
    viewModel.submitGuess(QStringLiteral("50"));

    QVERIFY(viewModel.historyEntries().size() >= historyBefore + 1);
    QVERIFY(!viewModel.lastResult().isEmpty());

    viewModel.clearHistory();
    QVERIFY(viewModel.historyEntries().isEmpty());
}

QTEST_MAIN(GuessGameTests)
#include "test_guessgame.moc"
