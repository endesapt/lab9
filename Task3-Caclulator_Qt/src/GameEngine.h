#pragma once

class GameEngine
{
public:
    enum class GuessStatus {
        TooLow,
        TooHigh,
        Win,
        Lose
    };

    struct GuessResult {
        GuessStatus status;
        int remainingAttempts;
        int pointsAwarded;
    };

    explicit GameEngine(int maxAttempts = 7);

    void setMaxAttempts(int maxAttempts);
    int maxAttempts() const;

    void startNewRound();
    GuessResult submitGuess(int guess);

    int remainingAttempts() const;
    int targetNumber() const;

    void setTargetNumberForTesting(int number);

private:
    int maxAttempts_;
    int attemptsUsed_;
    int targetNumber_;
};
