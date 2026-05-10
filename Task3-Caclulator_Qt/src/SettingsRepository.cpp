#include "SettingsRepository.h"

#include <QSettings>
#include <QUuid>

#include <stdexcept>

namespace {
const char *kLaunchCount = "app/launchCount";
const char *kAppId = "app/id";

const char *kMaxAttempts = "game/maxAttempts";
const char *kTotalPoints = "game/totalPoints";
const char *kBestScore = "game/bestScore";

const char *kAccountName = "account/name";
const char *kAccountEmail = "account/email";
const char *kLanguageCode = "app/languageCode";
}

SettingsRepository::SettingsRepository(const QString &iniFilePath)
{
    if (iniFilePath.isEmpty()) {
        settings_ = std::make_unique<QSettings>(QStringLiteral("MobileLab"), QStringLiteral("GuessNumberTask4"));
    } else {
        settings_ = std::make_unique<QSettings>(iniFilePath, QSettings::IniFormat);
    }
}

PersistentState SettingsRepository::initializeState()
{
    PersistentState state;

    state.launchCount = settings_->value(kLaunchCount, 0).toInt();
    state.appId = settings_->value(kAppId).toString();

    state.maxAttempts = settings_->value(kMaxAttempts, 7).toInt();
    state.totalPoints = settings_->value(kTotalPoints, 0).toInt();
    state.bestScore = settings_->value(kBestScore, 0).toInt();

    state.accountName = settings_->value(kAccountName, QStringLiteral("Guest")).toString();
    state.accountEmail = settings_->value(kAccountEmail, QStringLiteral("guest@example.com")).toString();
    state.languageCode = settings_->value(kLanguageCode, QStringLiteral("en")).toString();

    if (state.maxAttempts <= 0) {
        throw std::runtime_error("Stored maxAttempts is invalid");
    }

    if (state.appId.isEmpty()) {
        state.appId = QUuid::createUuid().toString(QUuid::WithoutBraces);
    }

    ++state.launchCount;

    settings_->setValue(kLaunchCount, state.launchCount);
    settings_->setValue(kAppId, state.appId);
    settings_->sync();

    if (settings_->status() != QSettings::NoError) {
        throw std::runtime_error("Failed to initialize application settings");
    }

    return state;
}

void SettingsRepository::saveState(const PersistentState &state)
{
    if (state.maxAttempts <= 0) {
        throw std::invalid_argument("maxAttempts must be positive");
    }

    if (state.accountName.trimmed().isEmpty()) {
        throw std::invalid_argument("accountName cannot be empty");
    }

    settings_->setValue(kLaunchCount, state.launchCount);
    settings_->setValue(kAppId, state.appId);

    settings_->setValue(kMaxAttempts, state.maxAttempts);
    settings_->setValue(kTotalPoints, state.totalPoints);
    settings_->setValue(kBestScore, state.bestScore);

    settings_->setValue(kAccountName, state.accountName);
    settings_->setValue(kAccountEmail, state.accountEmail);
    settings_->setValue(kLanguageCode, state.languageCode);
    settings_->sync();

    if (settings_->status() != QSettings::NoError) {
        throw std::runtime_error("Failed to write application settings");
    }
}
