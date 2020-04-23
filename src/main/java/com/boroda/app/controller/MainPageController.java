package com.boroda.app.controller;

import com.boroda.app.model.ChatClient;
import com.boroda.app.model.ChatServer;
import com.boroda.app.service.ChatSettingsComponent;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.util.CharsetUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Data;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Data
@Component
@FxmlView("MainPage.fxml")
public class MainPageController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField textField;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button startBtn;

    @FXML
    private Button connectBtn;

    @FXML
    private Label statusField;

    @FXML
    private Button disconnectBtn;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatServer chatServer;

    @Autowired
    private ChatSettingsComponent settingsComponent;

    @Autowired
    private FxWeaver fxWeaver;

    private String hostIp;
    private  int hostPort;
    private boolean isServerStarted = false;

    @FXML
    private void sendMessage() {
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                if(isServerStarted) {
                    chatServer.send(textField.getText());
                } else {
                    chatClient.send(textField.getText());
                }
                return null;
            }
            @Override
            protected void succeeded() {
                chatArea.appendText(String.format("[%s] %s \r\n", settingsComponent.getNickname(), textField.getText()));
                textField.clear();
            }

            @Override
            protected void failed() {

                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();
                statusField.setText("Offline");

            }

        };
        new Thread(task).start();

    }

    @FXML
    private void openSettings(ActionEvent event) throws IOException {
        Parent settingsPageParent = fxWeaver.loadView(SettingsPageController.class);
        Scene settingsScene = new Scene(settingsPageParent);
        Stage appStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(settingsScene);
        appStage.show();
    }

    @FXML
    private void startChat() {
        if(isServerStarted) {
            chatServer.shutdownServer();
            chatClient.disconnect();
            isServerStarted = false;
        }
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                chatServer.setPort(settingsComponent.getPort());
                chatServer.run();
                return null;
            }

            @Override
            protected void succeeded() {
                isServerStarted = true;
                statusField.setText("Online");
            }

            @Override
            protected void failed() {

                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Client");
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();

                statusField.setText("Offline");
            }

        };
        new Thread(task).start();
    }

    @FXML
    private void connectToChat() {
        if(isServerStarted) {
            chatServer.shutdownServer();
            chatClient.disconnect();
            isServerStarted = false;
        }
        hostIpPopup();
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {
                chatClient.setHost(hostIp);
                chatClient.setPort(hostPort);
                chatClient.run();
                return null;
            }

            @Override
            protected void succeeded() {
                statusField.setText("Online");
            }

            @Override
            protected void failed() {

                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Client");
                alert.setHeaderText(exc.getClass().getName());
                alert.setContentText(exc.getMessage());
                alert.showAndWait();

                statusField.setText("Offline");
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void disconnect() {
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {
                if(isServerStarted) {
                    chatServer.shutdownServer();
                    isServerStarted = false;
                } else if(chatClient.getChannel() != null) {
                    chatClient.disconnect();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                statusField.setText("Offline");
            }

            @Override
            protected void failed() {

                Throwable exc = getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Client");
                alert.setHeaderText(exc.getClass().getName());
                alert.setContentText(exc.getMessage());
                alert.showAndWait();

                statusField.setText("Offline");
            }
        };
        new Thread(task).start();

    }

    private void hostIpPopup(){
        TextInputDialog dialog = new TextInputDialog("127.0.0.1:8080");

        dialog.setTitle("Host ip address");
        dialog.setHeaderText("Enter host ip address with port(127.0.0.1:8080):");
        dialog.setContentText("Address:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(address -> {
            final String[] ip = address.split(":");
            this.hostIp = ip[0];
            this.hostPort = Integer.parseInt(ip[1]);
        });
    }
}
