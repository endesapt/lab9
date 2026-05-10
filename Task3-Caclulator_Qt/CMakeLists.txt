cmake_minimum_required(VERSION 3.16)

project(task4 VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

enable_testing()

find_package(Qt6 REQUIRED COMPONENTS Quick QuickControls2 LinguistTools Test)

qt_standard_project_setup(REQUIRES 6.10)

qt_add_executable(apptask4
    main.cpp
    src/GameEngine.cpp
    src/GameEngine.h
    src/SettingsRepository.cpp
    src/SettingsRepository.h
    src/GameViewModel.cpp
    src/GameViewModel.h
)

qt_add_qml_module(apptask4
    URI task4
    QML_FILES
        Main.qml
)

qt_add_translations(TARGETS apptask4
    TS_FILES
        i18n/app_en.ts
        i18n/app_ru.ts
        i18n/app_be.ts
    RESOURCE_PREFIX /i18n
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(apptask4 PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.apptask4
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
)

target_link_libraries(apptask4
    PRIVATE
        Qt6::Quick
        Qt6::QuickControls2
)

qt_add_executable(guessgame_tests
    tests/test_guessgame.cpp
    src/GameEngine.cpp
    src/SettingsRepository.cpp
    src/GameEngine.h
    src/SettingsRepository.h
)

target_link_libraries(guessgame_tests
    PRIVATE
        Qt6::Core
        Qt6::Test
)

add_test(NAME guessgame_tests COMMAND $<TARGET_FILE:guessgame_tests>)

if(NOT ANDROID)
    add_custom_target(run_unit_tests ALL
        COMMAND ${CMAKE_COMMAND} -E env
            "PATH=$<TARGET_FILE_DIR:Qt6::Core>;$<TARGET_FILE_DIR:guessgame_tests>;$ENV{PATH}"
            $<TARGET_FILE:guessgame_tests>
        DEPENDS guessgame_tests
        COMMENT "Running unit tests"
    )
endif()

install(TARGETS apptask4
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
