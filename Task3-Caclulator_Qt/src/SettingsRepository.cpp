#include "SettingsRepository.h"

#include <QCoreApplication>
#include <QDir>
#include <QFileInfo>
#include <QSettings>
#include <QSqlDatabase>
#include <QSqlError>
#include <QSqlQuery>
#include <QStandardPaths>
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
const char *kHistoryEntries = "game/historyEntries";

QString ensureStorageDirectory()
{
    QString basePath = QStandardPaths::writableLocation(QStandardPaths::AppDataLocation);
    if (basePath.isEmpty()) {
        basePath = QDir::tempPath() + QStringLiteral("/MobileLabGuessNumber");
    }

    QDir dir(basePath);
    if (!dir.exists() && !dir.mkpath(QStringLiteral("."))) {
        throw std::runtime_error("Failed to create storage directory");
    }

    return dir.absolutePath();
}

QString sqlConnectionName()
{
    return QStringLiteral("guessnumber_%1").arg(QUuid::createUuid().toString(QUuid::WithoutBraces));
}

void ensureSqlSuccess(const QSqlQuery &query, const char *message)
{
    if (!query.lastError().isValid()) {
        return;
    }

    throw std::runtime_error(QStringLiteral("%1: %2").arg(message, query.lastError().text()).toStdString());
}
}

class SettingsStorageBackend
{
public:
    virtual ~SettingsStorageBackend() = default;
    virtual PersistentState initializeState() = 0;
    virtual void saveState(const PersistentState &state) = 0;
};

class SettingsBackend : public SettingsStorageBackend
{
public:
    explicit SettingsBackend(const QString &iniFilePath)
    {
        if (iniFilePath.isEmpty()) {
            settings_ = std::make_unique<QSettings>(QStringLiteral("MobileLab"), QStringLiteral("GuessNumberTask3"));
        } else {
            settings_ = std::make_unique<QSettings>(iniFilePath, QSettings::IniFormat);
        }
    }

    PersistentState initializeState() override
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
        state.historyEntries = settings_->value(kHistoryEntries).toStringList();

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

    void saveState(const PersistentState &state) override
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
        settings_->setValue(kHistoryEntries, state.historyEntries);
        settings_->sync();

        if (settings_->status() != QSettings::NoError) {
            throw std::runtime_error("Failed to write application settings");
        }
    }

private:
    std::unique_ptr<QSettings> settings_;
};

#if !defined(Q_OS_WASM)
class SqliteBackend : public SettingsStorageBackend
{
public:
    explicit SqliteBackend(const QString &databasePath)
        : connectionName_(sqlConnectionName())
    {
        database_ = QSqlDatabase::addDatabase(QStringLiteral("QSQLITE"), connectionName_);
        database_.setDatabaseName(databasePath);

        if (!database_.open()) {
            throw std::runtime_error(database_.lastError().text().toStdString());
        }

        QSqlQuery query(database_);
        if (!query.exec(QStringLiteral(
                "CREATE TABLE IF NOT EXISTS app_state ("
                "key TEXT PRIMARY KEY,"
                "value TEXT NOT NULL)"))) {
            ensureSqlSuccess(query, "Failed to create app_state table");
        }

        if (!query.exec(QStringLiteral(
                "CREATE TABLE IF NOT EXISTS history ("
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                "entry TEXT NOT NULL)"))) {
            ensureSqlSuccess(query, "Failed to create history table");
        }
    }

    ~SqliteBackend() override
    {
        if (database_.isOpen()) {
            database_.close();
        }
        const QString connectionName = connectionName_;
        database_ = QSqlDatabase();
        QSqlDatabase::removeDatabase(connectionName);
    }

    PersistentState initializeState() override
    {
        PersistentState state;

        state.launchCount = readInt(kLaunchCount, 0);
        state.appId = readString(kAppId);
        state.maxAttempts = readInt(kMaxAttempts, 7);
        state.totalPoints = readInt(kTotalPoints, 0);
        state.bestScore = readInt(kBestScore, 0);
        state.accountName = readString(kAccountName, QStringLiteral("Guest"));
        state.accountEmail = readString(kAccountEmail, QStringLiteral("guest@example.com"));
        state.languageCode = readString(kLanguageCode, QStringLiteral("en"));
        state.historyEntries = readHistory();

        if (state.maxAttempts <= 0) {
            throw std::runtime_error("Stored maxAttempts is invalid");
        }

        if (state.appId.isEmpty()) {
            state.appId = QUuid::createUuid().toString(QUuid::WithoutBraces);
        }

        ++state.launchCount;
        writeValue(kLaunchCount, QString::number(state.launchCount));
        writeValue(kAppId, state.appId);
        return state;
    }

    void saveState(const PersistentState &state) override
    {
        if (state.maxAttempts <= 0) {
            throw std::invalid_argument("maxAttempts must be positive");
        }

        if (state.accountName.trimmed().isEmpty()) {
            throw std::invalid_argument("accountName cannot be empty");
        }

        writeValue(kLaunchCount, QString::number(state.launchCount));
        writeValue(kAppId, state.appId);
        writeValue(kMaxAttempts, QString::number(state.maxAttempts));
        writeValue(kTotalPoints, QString::number(state.totalPoints));
        writeValue(kBestScore, QString::number(state.bestScore));
        writeValue(kAccountName, state.accountName);
        writeValue(kAccountEmail, state.accountEmail);
        writeValue(kLanguageCode, state.languageCode);
        writeHistory(state.historyEntries);
    }

private:
    QString readString(const QString &key, const QString &defaultValue = QString()) const
    {
        QSqlQuery query(database_);
        query.prepare(QStringLiteral("SELECT value FROM app_state WHERE key = ?"));
        query.addBindValue(key);
        if (!query.exec()) {
            ensureSqlSuccess(query, "Failed to read app_state");
        }
        if (query.next()) {
            return query.value(0).toString();
        }
        return defaultValue;
    }

    int readInt(const QString &key, int defaultValue) const
    {
        bool ok = false;
        const int value = readString(key, QString::number(defaultValue)).toInt(&ok);
        return ok ? value : defaultValue;
    }

    QStringList readHistory() const
    {
        QSqlQuery query(database_);
        if (!query.exec(QStringLiteral("SELECT entry FROM history ORDER BY id DESC LIMIT 12"))) {
            ensureSqlSuccess(query, "Failed to read history");
        }

        QStringList items;
        while (query.next()) {
            items.append(query.value(0).toString());
        }
        return items;
    }

    void writeValue(const QString &key, const QString &value)
    {
        QSqlQuery query(database_);
        query.prepare(QStringLiteral("INSERT OR REPLACE INTO app_state(key, value) VALUES(?, ?)"));
        query.addBindValue(key);
        query.addBindValue(value);
        if (!query.exec()) {
            ensureSqlSuccess(query, "Failed to write app_state");
        }
    }

    void writeHistory(const QStringList &historyEntries)
    {
        QSqlQuery clearQuery(database_);
        if (!clearQuery.exec(QStringLiteral("DELETE FROM history"))) {
            ensureSqlSuccess(clearQuery, "Failed to clear history");
        }

        for (auto it = historyEntries.crbegin(); it != historyEntries.crend(); ++it) {
            QSqlQuery insertQuery(database_);
            insertQuery.prepare(QStringLiteral("INSERT INTO history(entry) VALUES(?)"));
            insertQuery.addBindValue(*it);
            if (!insertQuery.exec()) {
                ensureSqlSuccess(insertQuery, "Failed to insert history entry");
            }
        }
    }

    QString connectionName_;
    QSqlDatabase database_;
};
#endif

SettingsRepository::SettingsRepository(const QString &iniFilePath)
{
#if defined(Q_OS_WASM)
    backend_ = std::make_unique<SettingsBackend>(iniFilePath);
#else
    if (!iniFilePath.isEmpty() && QFileInfo(iniFilePath).suffix() == QStringLiteral("ini")) {
        backend_ = std::make_unique<SettingsBackend>(iniFilePath);
        return;
    }

    QString databasePath = iniFilePath;
    if (databasePath.isEmpty()) {
        databasePath = ensureStorageDirectory() + QStringLiteral("/guessnumber.sqlite");
    }

    backend_ = std::make_unique<SqliteBackend>(databasePath);
#endif
}

SettingsRepository::~SettingsRepository() = default;

PersistentState SettingsRepository::initializeState()
{
    return backend_->initializeState();
}

void SettingsRepository::saveState(const PersistentState &state)
{
    backend_->saveState(state);
}
