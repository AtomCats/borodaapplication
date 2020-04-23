package com.boroda.app.controller;

import com.boroda.app.service.ChatSettingsComponent;
import io.netty.util.internal.StringUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Data
@Component
@FxmlView("SettingsPage.fxml")
public class SettingsPageController {
    @FXML
    private TextField nicknameField = new TextField("User");

    @FXML
    private RadioButton chatBtn;

    @FXML
    private RadioButton groupChatBtn;

    @FXML
    private TextField portField = new TextField("5555");

    @Autowired
    private ChatSettingsComponent chatSettingsComponent;

    @Autowired
    private FxWeaver fxWeaver;

    @FXML
    private void saveChanges(ActionEvent event) throws IOException {
        if (!StringUtil.isNullOrEmpty(nicknameField.getText())) {
            chatSettingsComponent.setNickname(nicknameField.getText());
        }
        if (!StringUtil.isNullOrEmpty(portField.getText())) {
            chatSettingsComponent.setPort(Integer.parseInt(portField.getText()));
        }
        if (groupChatBtn.isSelected()) {
            chatSettingsComponent.setGroupChat(true);
        } else {
            chatSettingsComponent.setGroupChat(false);
        }

        Parent mainPageParent = fxWeaver.loadView(MainPageController.class);
        Scene mainScene = new Scene(mainPageParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(mainScene);
        appStage.show();
    }
}
