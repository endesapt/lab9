#include "GameEngine.h"

#include <QRandomGenerator>

#include <stdexcept>

GameEngine::GameEngine(int maxAttempts)
    : maxAttempts_(maxAttempts), attemptsUsed_(0), targetNumber_(1)
{
    if (maxAttempts_ <= 0) {
        throw std::invalid_argument("maxAttempts must be positive");
    }

    startNewRound();
}

void GameEngine::setMaxAttempts(int maxAttempts)
{
    if (maxAttempts <= 0) {
        throw std::invalid_argument("maxAttempts must be positive");
    }

    maxAttempts_ = maxAttempts;
}

int GameEngine::maxAttempts() const
{
    return maxAttempts_;
}

void GameEngine::startNewRound()
{
    attemptsUsed_ = 0;
    targetNumber_ = QRandomGenerator::global()->bounded(1, 101);
}

GameEngine::GuessResult GameEngine::submitGuess(int guess)
{
    if (guess < 1 || guess > 100) {
        throw std::invalid_argument("guess must be between 1 and 100");
    }

    ++attemptsUsed_;
    const int remaining = maxAttempts_ - attemptsUsed_;

    if (guess == targetNumber_) {
        return {GuessStatus::Win, remaining, remaining + 1};
    }

    if (attemptsUsed_ >= maxAttempts_) {
        return {GuessStatus::Lose, 0, 0};
    }

    if (guess < targetNumber_) {
        return {GuessStatus::TooLow, remaining, 0};
    }

    return {GuessStatus::TooHigh, remaining, 0};
}

int GameEngine::remainingAttempts() const
{
    return maxAttempts_ - attemptsUsed_;
}

int GameEngine::targetNumber() const
{
    return targetNumber_;
}

void GameEngine::setTargetNumberForTesting(int number)
{
    if (number < 1 || number > 100) {
        throw std::invalid_argument("target number must be between 1 and 100");
    }

    targetNumber_ = number;
}
