package com.zstorage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ZPassStorage extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader fxml = new FXMLLoader(getClass().getResource("gui/fxml/login.fxml"));
		
		Parent root = fxml.load();
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("ZPassStorage by zell91");
		primaryStage.show();
	}

}
