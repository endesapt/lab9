#include <QtTest>

#include "../src/GameEngine.h"
#include "../src/SettingsRepository.h"

#include <QTemporaryDir>

class GuessGameTests : public QObject
{
    Q_OBJECT

private slots:
    void gameEngine_winAwardsPoints();
    void gameEngine_invalidGuessThrows();
    void settingsRepository_generatesIdAndPersistsState();
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

    const QString iniPath = dir.filePath("settings.ini");

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

QTEST_MAIN(GuessGameTests)
#include "test_guessgame.moc"
