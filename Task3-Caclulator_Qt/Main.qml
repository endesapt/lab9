import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import QtQuick.Dialogs

ApplicationWindow {
    id: root
    width: 440
    height: 860
    minimumWidth: 360
    minimumHeight: 640
    visible: true
    title: qsTr("Guess Number")

    readonly property bool isAndroid: Qt.platform.os === "android"
    readonly property bool isIOS: Qt.platform.os === "ios"
    readonly property bool isLinux: Qt.platform.os === "linux"
    readonly property bool isWeb: Qt.platform.os === "wasm"
    readonly property bool isWide: width >= 920
    readonly property color surfaceColor: isLinux ? "#F7F9FA" : "#FFFFFF"
    readonly property color borderColor: isLinux ? "#556168" : "#D0E2DE"
    readonly property int cardRadius: isIOS ? 10 : 16

    menuBar: isLinux ? appMenu : null

    MenuBar {
        id: appMenu

        Menu {
            title: qsTr("Game")

            Action {
                text: qsTr("New game")
                onTriggered: gameViewModel.startNewGame()
            }

            Action {
                text: qsTr("Clear history")
                onTriggered: gameViewModel.clearHistory()
            }
        }
    }

    Rectangle {
        anchors.fill: parent
        gradient: Gradient {
            GradientStop { position: 0.0; color: root.isLinux ? "#EEF2F4" : "#F6FBFF" }
            GradientStop { position: 1.0; color: root.isLinux ? "#DDE6EA" : "#E3F2EE" }
        }
    }

    header: ToolBar {
        background: Rectangle {
            color: root.isIOS ? "#F8FBFC" : "#ECF6F4"
            border.color: root.isLinux ? "#94A5AE" : "transparent"
        }

        RowLayout {
            anchors.fill: parent
            anchors.margins: 12
            spacing: 10

            Label {
                Layout.fillWidth: true
                text: qsTr("Guess Number")
                font.pixelSize: 22
                font.bold: true
                color: "#1E3A3C"
            }

            Button {
                text: qsTr("New game")
                flat: root.isIOS
                onClicked: gameViewModel.startNewGame()
            }
        }
    }

    MessageDialog {
        id: attemptDialog
        title: qsTr("Attempt result")
        buttons: MessageDialog.Ok
    }

    Connections {
        target: gameViewModel
        function onAttemptDialogRequested(title, message) {
            attemptDialog.title = title
            attemptDialog.text = message
            attemptDialog.open()
        }
    }

    Flickable {
        anchors.fill: parent
        clip: true
        contentWidth: width
        contentHeight: mainColumn.implicitHeight + 32

        ColumnLayout {
            id: mainColumn
            width: parent.width - 24
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.top: parent.top
            anchors.topMargin: 16
            spacing: 14

            Label {
                Layout.fillWidth: true
                text: qsTr("Guess a number from 1 to 100")
                horizontalAlignment: Text.AlignHCenter
                font.pixelSize: root.isWide ? 28 : 24
                font.bold: true
                color: "#1E3A3C"

                SequentialAnimation on opacity {
                    running: true
                    loops: 1
                    NumberAnimation { from: 0.0; to: 1.0; duration: 500 }
                }
            }

            GridLayout {
                Layout.fillWidth: true
                columns: root.isWide ? 2 : 1
                rowSpacing: 14
                columnSpacing: 14

                Rectangle {
                    Layout.fillWidth: true
                    Layout.alignment: Qt.AlignTop
                    radius: root.cardRadius
                    color: root.surfaceColor
                    border.color: root.borderColor
                    border.width: root.isIOS ? 0 : 1
                    implicitHeight: statsLayout.implicitHeight + 20

                    GridLayout {
                        id: statsLayout
                        anchors.fill: parent
                        anchors.margins: 10
                        columns: 2
                        rowSpacing: 6
                        columnSpacing: 10

                        Label { text: qsTr("Launches:") }
                        Label { text: gameViewModel.launchCount }

                        Label { text: qsTr("App ID:") }
                        Label {
                            text: gameViewModel.appId
                            elide: Text.ElideRight
                            Layout.fillWidth: true
                        }

                        Label { text: qsTr("Remaining attempts:") }
                        Label { text: gameViewModel.remainingAttempts }

                        Label { text: qsTr("Total points:") }
                        Label { text: gameViewModel.totalPoints }

                        Label { text: qsTr("Best score:") }
                        Label { text: gameViewModel.bestScore }
                    }
                }

                Rectangle {
                    Layout.fillWidth: true
                    Layout.alignment: Qt.AlignTop
                    radius: root.cardRadius
                    color: root.surfaceColor
                    border.color: root.borderColor
                    border.width: root.isIOS ? 0 : 1
                    implicitHeight: gameLayout.implicitHeight + 22

                    ColumnLayout {
                        id: gameLayout
                        anchors.fill: parent
                        anchors.margins: 12
                        spacing: 10

                        TextField {
                            id: guessField
                            Layout.fillWidth: true
                            placeholderText: qsTr("Enter number")
                            inputMethodHints: Qt.ImhDigitsOnly
                            selectByMouse: true
                            background: Rectangle {
                                radius: 10
                                color: "#FFFFFF"
                                border.color: root.borderColor
                                border.width: root.isIOS ? 0 : 1
                            }
                        }

                        RowLayout {
                            Layout.fillWidth: true
                            spacing: 8

                            Button {
                                id: tryButton
                                Layout.fillWidth: true
                                text: qsTr("Try")
                                flat: root.isIOS
                                onClicked: gameViewModel.submitGuess(guessField.text)
                                scale: down ? 0.96 : 1.0

                                Behavior on scale {
                                    NumberAnimation { duration: 120; easing.type: Easing.OutCubic }
                                }
                            }

                            Button {
                                Layout.fillWidth: true
                                text: qsTr("New game")
                                flat: root.isIOS
                                onClicked: gameViewModel.startNewGame()
                            }
                        }

                        Label {
                            Layout.fillWidth: true
                            text: gameViewModel.lastResult
                            wrapMode: Text.WordWrap
                            color: "#234B47"
                        }
                    }
                }

                Rectangle {
                    Layout.fillWidth: true
                    Layout.alignment: Qt.AlignTop
                    radius: root.cardRadius
                    color: root.surfaceColor
                    border.color: root.borderColor
                    border.width: root.isIOS ? 0 : 1
                    implicitHeight: settingsLayout.implicitHeight + 20

                    ColumnLayout {
                        id: settingsLayout
                        anchors.fill: parent
                        anchors.margins: 10
                        spacing: 8

                        Label {
                            text: qsTr("Settings")
                            font.bold: true
                        }

                        RowLayout {
                            Layout.fillWidth: true
                            Label { text: qsTr("Max attempts:") }
                            SpinBox {
                                id: attemptsSpin
                                from: 1
                                to: 20
                                value: gameViewModel.maxAttempts
                                onValueModified: gameViewModel.maxAttempts = value
                            }
                        }

                        RowLayout {
                            Layout.fillWidth: true
                            Label { text: qsTr("Language:") }
                            ComboBox {
                                id: languageBox
                                Layout.fillWidth: true
                                textRole: "label"
                                model: [
                                    { label: qsTr("English"), code: "en" },
                                    { label: qsTr("Русский"), code: "ru" },
                                    { label: qsTr("Беларуская"), code: "be" }
                                ]

                                Component.onCompleted: {
                                    for (var i = 0; i < model.length; ++i) {
                                        if (model[i].code === gameViewModel.languageCode) {
                                            currentIndex = i
                                            return
                                        }
                                    }
                                }

                                onActivated: gameViewModel.changeLanguage(model[currentIndex].code)
                            }
                        }

                        Label {
                            Layout.fillWidth: true
                            wrapMode: Text.WordWrap
                            text: root.isWide
                                  ? qsTr("Wide layout is enabled for desktop and tablet screens.")
                                  : qsTr("Compact layout is enabled for phones and narrow windows.")
                        }
                    }
                }

                Rectangle {
                    Layout.fillWidth: true
                    Layout.alignment: Qt.AlignTop
                    radius: root.cardRadius
                    color: root.surfaceColor
                    border.color: root.borderColor
                    border.width: root.isIOS ? 0 : 1
                    implicitHeight: accountLayout.implicitHeight + 20

                    ColumnLayout {
                        id: accountLayout
                        anchors.fill: parent
                        anchors.margins: 10
                        spacing: 8

                        Label {
                            text: qsTr("Account")
                            font.bold: true
                        }

                        TextField {
                            id: accountNameField
                            Layout.fillWidth: true
                            placeholderText: qsTr("Name")
                            text: gameViewModel.accountName
                            background: Rectangle {
                                radius: 10
                                color: "#FFFFFF"
                                border.color: root.borderColor
                                border.width: root.isIOS ? 0 : 1
                            }
                        }

                        TextField {
                            id: accountMailField
                            Layout.fillWidth: true
                            placeholderText: qsTr("Email")
                            text: gameViewModel.accountEmail
                            background: Rectangle {
                                radius: 10
                                color: "#FFFFFF"
                                border.color: root.borderColor
                                border.width: root.isIOS ? 0 : 1
                            }
                        }

                        Button {
                            text: qsTr("Save account")
                            flat: root.isIOS
                            onClicked: gameViewModel.saveAccount(accountNameField.text, accountMailField.text)
                        }
                    }
                }
            }

            Rectangle {
                Layout.fillWidth: true
                radius: root.cardRadius
                color: root.surfaceColor
                border.color: root.borderColor
                border.width: root.isIOS ? 0 : 1
                implicitHeight: historyLayout.implicitHeight + 20

                ColumnLayout {
                    id: historyLayout
                    anchors.fill: parent
                    anchors.margins: 10
                    spacing: 10

                    RowLayout {
                        Layout.fillWidth: true

                        Label {
                            text: qsTr("Recent history")
                            font.bold: true
                        }

                        Item { Layout.fillWidth: true }

                        Button {
                            text: qsTr("Clear history")
                            flat: root.isIOS
                            onClicked: gameViewModel.clearHistory()
                        }
                    }

                    Repeater {
                        model: gameViewModel.historyEntries

                        delegate: Rectangle {
                            required property string modelData
                            Layout.fillWidth: true
                            implicitHeight: historyLabel.implicitHeight + 14
                            radius: 10
                            color: root.isLinux ? "#FFFFFF" : "#F4FAF7"
                            border.color: root.borderColor
                            border.width: root.isIOS ? 0 : 1

                            Label {
                                id: historyLabel
                                anchors.fill: parent
                                anchors.margins: 7
                                text: modelData
                                wrapMode: Text.WordWrap
                            }
                        }
                    }

                    Label {
                        visible: gameViewModel.historyEntries.length === 0
                        text: qsTr("History is empty.")
                    }
                }
            }
        }
    }
}
