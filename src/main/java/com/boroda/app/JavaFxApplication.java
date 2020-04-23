package com.boroda.app;

import com.boroda.app.controller.MainPageController;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends javafx.application.Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(SpringBootApplication.class)
                .run(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainPageController.class);
        primaryStage.setTitle("Boroda chat");
        primaryStage.setScene(new Scene(root, 550, 400));
        primaryStage.show();
    }


    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }
}
