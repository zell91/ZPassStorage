package com.zstorage.gui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import com.zstorage.backup.Backup;
import com.zstorage.backup.BackupRestorer;
import com.zstorage.backup.exception.AlreadyExistsAccountException;
import com.zstorage.backup.exception.BackupException;
import com.zstorage.credential.Account;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;
import com.zstorage.gui.components.Ellipsis;
import com.zstorage.gui.components.TextFieldMessage;
import com.zstorage.gui.components.TextFieldMessage.Transition;
import com.zstorage.login.Login;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LoginController implements Initializable{

	@FXML
	private BorderPane root;
	
	@FXML
	private GridPane form;
	
	@FXML
	private StackPane container, headContainer, userMsgPane, loginMessagePane;
	
	@FXML
	private ImageView logo;
	
	@FXML
	private Label title, userLbl, passLbl, backupLbl, nameLbl;
	
	@FXML
	private VBox userForm, passForm, linkForm, userSignUp, passSignUp, backup, nameSignUp;
	
	@FXML
	private HBox userLblBox, passLblBox, backupLblBox, nameLblBox;
	
	@FXML
	private TextField userText, userSignText, backupPathText, nameSignText;
	
	@FXML
	private PasswordField passText, passSignText;
	
	@FXML
	private TextFieldMessage userTextMessage, passTextMessage, notValidAccountMessage, emptyUserMessage, existUserMessage, emptyPassMessage, successLoginMessage, createdAccountMessage, successBackupMessage, failedBackupMessage, existAccountMessage, invalidBackupFileMessage, validBackupFileMessage;
	
	@FXML
	private Button loginBtn, signUpBtn, cancelBtn, restoreBtn;
	
	@FXML
	private Hyperlink forgotPass, createAccount;
	
	@FXML
	private Ellipsis ellipsis;
			
	@Override
	public void initialize(URL fxml, ResourceBundle bundle) {
		this.form.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> value) {
				if(value.next()) {
					userText.clear();
					userSignText.clear();
					passSignText.clear();
					backupPathText.clear();
					passText.clear();					
				}
			}
			
		});
	}
	
	@FXML
	public void login(ActionEvent event) {
		Login login = Login.getDefault();

		boolean validForm = true;
		
		if(this.userText.getText().isEmpty()) {
			this.userTextMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(this.passText.getText().isEmpty()) {
			this.passTextMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(!validForm) return;
		
		ProgressIndicator progressIndicator = new ProgressIndicator();		
		progressIndicator.setPrefSize(18, 18);		
		progressIndicator.setDisable(false);
		
		this.form.setDisable(true);
		this.loginBtn.setGraphic(progressIndicator);
		
		Thread loginThread = new Thread(()->{
			User user = new User(this.userText.getText().toCharArray());
			Password password = new Password(this.passText.getText().toCharArray());
			
			Account account = login.login(user, password);
						
			if(account != null) {
				Platform.runLater(()->{
					this.successLoginMessage.showFor(Transition.FADE_IN_OUT, 400);
					showAccount(account);
				});				
			}else {
				Platform.runLater(()->this.notValidAccountMessage.showFor(Transition.FADE_IN_OUT, 900));
			}
			
			Platform.runLater(()->{		
				this.form.setDisable(false);
				this.loginBtn.setGraphic(null);
				this.passText.clear();
			});
		});
		
		loginThread.setName("LoginThread");
		loginThread.setDaemon(true);
		loginThread.start();

	}

	private void showAccount(Account account) {
		try {
			FXMLLoader fxml = new FXMLLoader(this.getClass().getResource("fxml/zpass_storage.fxml"));		

			Parent root = fxml.load();

			ZStorageController controller = (ZStorageController) fxml.getController();
	
			controller.setAccount(account, new String(userText.getText()));
			controller.loginView(this.container);			

			Stage stage = (Stage) this.root.getScene().getWindow();
			
			if(!stage.isMaximized()) {
				stage.getScene().getWindow().setWidth(1000);
				stage.getScene().getWindow().setHeight(700);
				stage.centerOnScreen();
			}else {
				stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if(!newValue) {
							if(!((BorderPane)stage.getScene().getRoot()).getCenter().equals(container)) {
								stage.getScene().getWindow().setWidth(1000);
								stage.getScene().getWindow().setHeight(700);
								stage.centerOnScreen();
							}
							stage.maximizedProperty().removeListener(this);
						}
					}					
				});
			}
			
			this.root.setCenter(root);


		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@FXML
	public void showSignUp(ActionEvent event) {
		this.form.getChildren().setAll(this.headContainer, this.nameSignUp, this.userSignUp, this.passSignUp, this.signUpBtn, this.cancelBtn);
	}
	
	@FXML
	public void createAccount(ActionEvent event) {
		boolean validForm = true;
		
		if(this.nameSignText.getText().isEmpty())
			this.nameSignText.setText(Account.DEFAULT_NAME);
		
		if(this.userSignText.getText().isEmpty()) {
			this.emptyUserMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(this.passSignText.getText().isEmpty()) {
			this.emptyPassMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(!validForm) return;
		
		ProgressIndicator progressIndicator = new ProgressIndicator();		
		progressIndicator.setPrefSize(18, 18);		
		progressIndicator.setDisable(false);
		
		this.form.setDisable(true);
		this.signUpBtn.setGraphic(progressIndicator);
				
		Thread createAccountThread = new Thread(()->{			
			try {
				User username = new User(this.userSignText.getText().toCharArray());
				Password password = new Password(this.passSignText.getText().toCharArray());
				
				if(!Login.checkUser(username)) {	
					Account account = Account.createNew(this.nameSignText.getText(), username, password);
					
					Platform.runLater(()->this.backToLoginVew());
					
					Login.update(new File(Login.keyPath, "user" + account.hashCode()));
					
					Platform.runLater(()->this.createdAccountMessage.showFor(Transition.FADE_IN_OUT, 900));
						
				}else {
					Platform.runLater(()->this.existUserMessage.showFor(Transition.FADE_IN_OUT, 900));
				}
			
			} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			} finally {				
				Platform.runLater(()->{		
					this.passSignText.clear();			
					this.form.setDisable(false);
					this.signUpBtn.setGraphic(null);
				});
				
			}			
		});
		
		createAccountThread.setName("CreateAccountThread");
		createAccountThread.setDaemon(false);
		createAccountThread.start();
	}
	
	@FXML
	public void cancelAccount(ActionEvent event) {		
		this.backToLoginVew();
	}

	private void backToLoginVew() {
		this.passForm.setDisable(false);
		this.userForm.setDisable(false);
		GridPane.setConstraints(this.userForm, 0, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		GridPane.setConstraints(this.passForm, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		GridPane.setConstraints(this.cancelBtn, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);	
		GridPane.setConstraints(this.loginMessagePane, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		this.form.getChildren().setAll(this.headContainer, this.userForm, this.passForm, this.linkForm, this.loginBtn, this.loginMessagePane);
	}

	@FXML
	private void backupExportView(ActionEvent event) {
		GridPane.setConstraints(this.userForm, 0, 3, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		GridPane.setConstraints(this.passForm, 0, 4, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		GridPane.setConstraints(this.cancelBtn, 0, 7, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		GridPane.setConstraints(this.loginMessagePane, 0, 8, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, null);
		this.passForm.setDisable(true);
		this.userForm.setDisable(true);
		this.form.getChildren().setAll(this.headContainer, this.ellipsis, this.backup, this.userForm, this.passForm, this.restoreBtn, this.cancelBtn, this.loginMessagePane);
	}
	
	@FXML
	public void loadBackup(MouseEvent event) {
		if(event.getButton().equals(MouseButton.PRIMARY)) {
			FileChooser fileChooser = new FileChooser();
			
			File raw = fileChooser.showOpenDialog(this.root.getScene().getWindow());
			
			if(raw != null) {
				this.backupPathText.setText(raw.toString());
				this.ellipsis.play();

				Thread loadBackupThread = new Thread(()->{
					try {
						Backup.toBackup(raw);
						this.userForm.setDisable(false);
						this.passForm.setDisable(false);
						this.restoreBtn.setDisable(false);
						Platform.runLater(()->this.validBackupFileMessage.showFor(Transition.FADE_IN_OUT, 900));
					} catch (BackupException e) {
						this.backupPathText.clear();
						this.userForm.setDisable(true);
						this.passForm.setDisable(true);
						this.restoreBtn.setDisable(true);
						Platform.runLater(()->this.invalidBackupFileMessage.showFor(Transition.FADE_IN_OUT, 900));
					}finally {
						this.ellipsis.stop();
					}
				});
				
				loadBackupThread.setName("LoadBacupThread");
				loadBackupThread.setDaemon(true);
				loadBackupThread.start();

			}			
		}
	}
	
	@FXML
	public void restoreBackup(ActionEvent event) {		
		boolean validForm = true;
		
		if(this.userText.getText().isEmpty()) {
			this.userTextMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(this.passText.getText().isEmpty()) {
			this.passTextMessage.showFor(Transition.FADE_IN_OUT, 900);
			validForm = false;
		}
		
		if(!validForm) return;
		
		this.form.setDisable(true);
		
		ProgressIndicator progressIndicator = new ProgressIndicator();		
		progressIndicator.setPrefSize(18, 18);		
		progressIndicator.setDisable(false);
		
		this.restoreBtn.setGraphic(progressIndicator);
		
		Thread restoreBackupThread = new Thread(()->{
			try {
				Backup backup = Backup.toBackup(new File(this.backupPathText.getText()));
				
				User user = new User(this.userText.getText().toCharArray());
				Password password = new Password(this.passText.getText().toCharArray());
				
				BackupRestorer restorer = BackupRestorer.getDefault();
				
				File accountFile = restorer.restore(backup, user, password);
				
				if(accountFile != null) {
					Login.update(accountFile);
					Platform.runLater(()->{
						this.backToLoginVew();
						this.successBackupMessage.showFor(Transition.FADE_IN_OUT, 900);
					});
				}else {
					Platform.runLater(()->this.notValidAccountMessage.showFor(Transition.FADE_IN_OUT, 900));
				}
			}catch(AlreadyExistsAccountException ex) {
				Platform.runLater(()->this.existAccountMessage.showFor(Transition.FADE_IN_OUT, 900));
			}catch(BackupException ex) {
				Platform.runLater(()->this.failedBackupMessage.showFor(Transition.FADE_IN_OUT, 900));
			}finally {
				Platform.runLater(()->{
					this.passText.clear();
					this.restoreBtn.setGraphic(null);
					this.form.setDisable(false);
				});
			}
		});
		
		restoreBackupThread.setDaemon(true);
		restoreBackupThread.setName("RestoreBackupThread");
		restoreBackupThread.start();		
	}
		
	@FXML
	public void actionTextField(ActionEvent event) {
		Button button = (Button) this.form.getChildren().stream().filter(item->item instanceof Button).findFirst().orElse(null);
		
		if(button != null) button.fire();
	}
}
