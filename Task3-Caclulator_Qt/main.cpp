#include <QGuiApplication>
#include <QDebug>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QTranslator>

#include "src/GameViewModel.h"

namespace {
bool loadLanguage(QTranslator &translator, QGuiApplication &app, const QString &languageCode)
{
    app.removeTranslator(&translator);

    const QString baseName = QStringLiteral("app_") + languageCode;
    const bool loaded = translator.load(baseName, QStringLiteral(":/i18n"));
    if (!loaded) {
        qCritical() << "Translator load failed for language:" << languageCode;
        return false;
    }

    app.installTranslator(&translator);
    return true;
}
}

int main(int argc, char *argv[])
{
    try {
        QGuiApplication app(argc, argv);

        GameViewModel gameViewModel;
        QTranslator translator;

        QString initialCode = gameViewModel.languageCode();
        if (initialCode.isEmpty()) {
            initialCode = QLocale::system().name().left(2);
        }

        if (!loadLanguage(translator, app, initialCode)) {
            loadLanguage(translator, app, QStringLiteral("en"));
        }

        QQmlApplicationEngine engine;
        engine.rootContext()->setContextProperty(QStringLiteral("gameViewModel"), &gameViewModel);

        QObject::connect(
            &gameViewModel,
            &GameViewModel::languageChangeRequested,
            &app,
            [&](const QString &languageCode) {
                loadLanguage(translator, app, languageCode);
                engine.retranslate();
            });

        QObject::connect(
            &engine,
            &QQmlApplicationEngine::objectCreationFailed,
            &app,
            []() { QCoreApplication::exit(-1); },
            Qt::QueuedConnection);

        engine.loadFromModule("task3", "Main");
        return QCoreApplication::exec();
    } catch (const std::exception &ex) {
        qCritical() << "Fatal startup error:" << ex.what();
    }

    return -1;
}
