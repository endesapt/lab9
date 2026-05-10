import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import QtQuick.Dialogs

ApplicationWindow {
    id: root
    width: 420
    height: 820
    visible: true
    title: qsTr("Guess Number")

    Rectangle {
        anchors.fill: parent
        gradient: Gradient {
            GradientStop { position: 0.0; color: "#F6FBFF" }
            GradientStop { position: 1.0; color: "#E3F2EE" }
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
        contentHeight: contentColumn.implicitHeight + 36

        ColumnLayout {
            id: contentColumn
            width: parent.width - 28
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.top: parent.top
            anchors.topMargin: 18
            spacing: 12

            Label {
                Layout.fillWidth: true
                text: qsTr("Guess a number from 1 to 100")
                horizontalAlignment: Text.AlignHCenter
                font.pixelSize: 26
                font.bold: true
                color: "#1E3A3C"

                SequentialAnimation on opacity {
                    running: true
                    loops: 1
                    NumberAnimation { from: 0.0; to: 1.0; duration: 500 }
                }
            }

            Rectangle {
                Layout.fillWidth: true
                radius: 12
                color: "#FFFFFF"
                border.color: "#D0E2DE"
                border.width: 1
                implicitHeight: statsLayout.implicitHeight + 16

                GridLayout {
                    id: statsLayout
                    anchors.fill: parent
                    anchors.margins: 8
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
                radius: 12
                color: "#FFFFFF"
                border.color: "#D0E2DE"
                border.width: 1
                implicitHeight: gameLayout.implicitHeight + 18

                ColumnLayout {
                    id: gameLayout
                    anchors.fill: parent
                    anchors.margins: 10
                    spacing: 10

                    TextField {
                        id: guessField
                        Layout.fillWidth: true
                        placeholderText: qsTr("Enter number")
                        inputMethodHints: Qt.ImhDigitsOnly
                    }

                    RowLayout {
                        Layout.fillWidth: true
                        spacing: 8

                        Button {
                            id: tryButton
                            Layout.fillWidth: true
                            text: qsTr("Try")
                            onClicked: gameViewModel.submitGuess(guessField.text)
                            scale: down ? 0.96 : 1.0

                            Behavior on scale {
                                NumberAnimation { duration: 120; easing.type: Easing.OutCubic }
                            }
                        }

                        Button {
                            Layout.fillWidth: true
                            text: qsTr("New game")
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
                radius: 12
                color: "#FFFFFF"
                border.color: "#D0E2DE"
                border.width: 1
                implicitHeight: settingsLayout.implicitHeight + 16

                ColumnLayout {
                    id: settingsLayout
                    anchors.fill: parent
                    anchors.margins: 8
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
                }
            }

            Rectangle {
                Layout.fillWidth: true
                radius: 12
                color: "#FFFFFF"
                border.color: "#D0E2DE"
                border.width: 1
                implicitHeight: accountLayout.implicitHeight + 16

                ColumnLayout {
                    id: accountLayout
                    anchors.fill: parent
                    anchors.margins: 8
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
                    }

                    TextField {
                        id: accountMailField
                        Layout.fillWidth: true
                        placeholderText: qsTr("Email")
                        text: gameViewModel.accountEmail
                    }

                    Button {
                        text: qsTr("Save account")
                        onClicked: gameViewModel.saveAccount(accountNameField.text, accountMailField.text)
                    }
                }
            }
        }
    }
}
