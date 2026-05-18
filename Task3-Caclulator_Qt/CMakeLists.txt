cmake_minimum_required(VERSION 3.16)

project(task3 VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include(CTest)
enable_testing()

find_package(Qt6 REQUIRED COMPONENTS Quick QuickControls2 LinguistTools Test Sql)

qt_standard_project_setup(REQUIRES 6.10)

qt_add_executable(apptask3
    main.cpp
    src/GameEngine.cpp
    src/GameEngine.h
    src/SettingsRepository.cpp
    src/SettingsRepository.h
    src/GameViewModel.cpp
    src/GameViewModel.h
)

qt_add_qml_module(apptask3
    URI task3
    QML_FILES
        Main.qml
)

qt_add_translations(TARGETS apptask3
    TS_FILES
        i18n/app_en.ts
        i18n/app_ru.ts
        i18n/app_be.ts
    RESOURCE_PREFIX /i18n
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(apptask3 PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.apptask3
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    QT_ANDROID_VERSION_CODE 1
    QT_ANDROID_VERSION_NAME ${PROJECT_VERSION}
)

target_link_libraries(apptask3
    PRIVATE
        Qt6::Quick
        Qt6::QuickControls2
        Qt6::Sql
)

if(BUILD_TESTING AND NOT ANDROID AND NOT IOS AND NOT CMAKE_SYSTEM_NAME STREQUAL "Emscripten")
    qt_add_executable(guessgame_tests
        tests/test_guessgame.cpp
        src/GameEngine.cpp
        src/GameViewModel.cpp
        src/SettingsRepository.cpp
        src/GameEngine.h
        src/GameViewModel.h
        src/SettingsRepository.h
    )

    target_link_libraries(guessgame_tests
        PRIVATE
            Qt6::Core
            Qt6::Test
            Qt6::Sql
    )

    add_test(NAME guessgame_tests COMMAND $<TARGET_FILE:guessgame_tests>)

    if(WIN32)
        set(TASK3_TEST_PATH_SEPARATOR ";")
    else()
        set(TASK3_TEST_PATH_SEPARATOR ":")
    endif()

    set_tests_properties(guessgame_tests PROPERTIES
        ENVIRONMENT
            "PATH=$<TARGET_FILE_DIR:Qt6::Core>${TASK3_TEST_PATH_SEPARATOR}$<TARGET_FILE_DIR:guessgame_tests>${TASK3_TEST_PATH_SEPARATOR}$ENV{PATH}"
    )
endif()

install(TARGETS apptask3
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
