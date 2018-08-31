package com.zstorage.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.zstorage.backup.BackupExporter;
import com.zstorage.backup.exception.BackupException;
import com.zstorage.cipher.encrypt.PasswordEncrypter;
import com.zstorage.cipher.encrypt.UserEncrypter;
import com.zstorage.credential.Account;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;
import com.zstorage.gui.components.EntryView;
import com.zstorage.store.StoredObject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ZStorageController implements Initializable{

	private Account account;
	
	
	private StackPane loginRoot;
	
	@FXML
	private StackPane root;
	
	@FXML
	private ImageView avatar;
	
	@FXML
	private BorderPane pane;
	
	@FXML
	private VBox content, entriesBox, progressBox;
	
	@FXML
	private HBox bottom, buttonBox;
	
	@FXML
	private Label user, stateLbl;
	
	@FXML
	private Button addBtn;

	@FXML
	private ScrollPane scrollContent;
	
	@FXML
	private Object avatarBox;
	
	@Override
	public void initialize(URL fxml, ResourceBundle bundle) {
		
	}

	public void setAccount(Account account, String user) {
		this.account = account;
		this.user.setText(account.getName());
		try {
			this.avatar.setImage(new Image(this.account.getAvatar().toURI().toString()));
		} catch (FileNotFoundException e) {
			this.stateLbl.setText("Avatar image not found");
			if(!Account.DEFAULT_AVATAR.exists())
				this.stateLbl.setText("Avatar default image not found");
			else
				this.avatar.setImage(new Image(Account.DEFAULT_AVATAR.toURI().toString()));
		}
		buildGrid();
	}

	private void buildGrid() {
		List<StoredObject> stored = this.account.getStoredObjects();
		
		Iterator<StoredObject> iter = stored.iterator();
		
		while(iter.hasNext()) {
			StoredObject obj = iter.next();
			
			EntryView entry = new EntryView(obj);
			
			entry.setOnAction(e->this.editMode(entry));
			entry.setOnCancel(e->this.deleteEntry(entry));
			
			this.entriesBox.getChildren().add(0, entry);
		}
	}

	public void loginView(StackPane container) {
		this.loginRoot = container;
	}

	@FXML
	public void addEntry(ActionEvent event) {				
		this.scrollContent.setVvalue(0.0);
		Node node = this.scrollContent.lookup(".scroll-bar:vertical");
		if(node != null) node.setDisable(true);
		this.scrollContent.setVmax(0.0);

		EntryView entry = EntryView.newEntry(account.getSecretKey());
		
		this.editMode(entry);
		
		entry.setOnAction(e->this.saveEntry(entry));
		entry.setOnCancel(e->this.cancelEntry(entry));
		
		this.entriesBox.getChildren().add(0, entry);
	}

	private void saveEntry(EntryView entry) {
		if(entry.getUserText().isEmpty() || entry.getPasswordText().isEmpty()) {
			return;
		}
		
		boolean invalid = this.account.getStoredObjects().stream().anyMatch(s->{		 		
	 		return !entry.getStoredObject().equals(s) &&
	 				entry.getUserText().equals(new String(s.getUser().getCredential())) &&
	 				entry.getPasswordText().equals(new String(s.getPassword().getCredential())) &&
	 				(entry.getNote() == s.getNote() || entry.getNote().equals(s.getNote()));
	 	});

		if(invalid) return;
				
		User user = new User(entry.getUserText().toCharArray());
		Password password = new Password(entry.getPasswordText().toCharArray());

		PasswordEncrypter passEnc = PasswordEncrypter.getInstance(this.account.getSecretKey());
		UserEncrypter userEnc = UserEncrypter.getInstance(this.account.getSecretKey());
		
		entry.getStoredObject().setCryptPassword(passEnc.crypt(password));
		entry.getStoredObject().setCryptUser(userEnc.crypt(user));
		entry.getStoredObject().setNote(entry.getNote());		
		
		entry.editMode(false);	
		this.buttonBox.setDisable(false);
		this.entriesBox.getChildren().stream().filter(e->!e.equals(entry)).forEach(e->e.setDisable(false));
		
		Node node = this.scrollContent.lookup(".scroll-bar:vertical");
		if(node != null) node.setDisable(false);
		this.scrollContent.setVmax(1);
		
		entry.setOnAction(e->editMode(entry));
		entry.setOnCancel(e->deleteEntry(entry));
				
		if(!this.account.getStoredObjects().contains(entry.getStoredObject()))
			this.account.addStoredObject(entry.getStoredObject());
		else
			this.account.store();
	}	

	private void cancelEntry(EntryView entry) {
		this.buttonBox.setDisable(false);
		this.entriesBox.getChildren().stream().filter(e->!e.equals(entry)).forEach(e->e.setDisable(false));

		if(!this.account.getStoredObjects().contains(entry.getStoredObject())) {
			this.deleteEntry(entry);			
			return;
		}
		
		entry.setUserText(new String(entry.getStoredObject().getUser().getCredential()));
		entry.setPasswordText(new String(entry.getStoredObject().getPassword().getCredential()));
		entry.setNoteText(entry.getStoredObject().getNote());
		
		entry.editMode(false);

		Node node = this.scrollContent.lookup(".scroll-bar:vertical");
		if(node != null) node.setDisable(false);
		this.scrollContent.setVmax(1);
		
		entry.setOnAction(e->editMode(entry));
		entry.setOnCancel(e->deleteEntry(entry));
	}
	
	public void editMode(EntryView entry) {		
		this.buttonBox.setDisable(true);	
		this.entriesBox.getChildren().stream().filter(e->!e.equals(entry)).forEach(e->e.setDisable(true));

		entry.editMode(true);
		
		entry.setOnAction(e->this.saveEntry(entry));
		entry.setOnCancel(e->this.cancelEntry(entry));
	}

	private void deleteEntry(EntryView entry) {
		this.account.removeStoredObject(entry.getStoredObject());
		this.entriesBox.getChildren().remove(entry);
		this.entriesBox.requestFocus();
	}
	
	@FXML
	public void logout(ActionEvent event){
		this.account = null;
		this.entriesBox.getChildren().clear();
		
		System.gc();
		
		Stage stage = (Stage)this.root.getScene().getWindow();
		((BorderPane)stage.getScene().getRoot()).setCenter(this.loginRoot);

		if(!stage.isMaximized()) {			
			((Pane)stage.getScene().getRoot()).setPrefWidth(400.0);
			((Pane)stage.getScene().getRoot()).setPrefHeight(700.0);
			stage.sizeToScene();			
			stage.centerOnScreen();
		}else {
			stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if(!newValue) {
						if(((BorderPane)stage.getScene().getRoot()).getCenter().equals(loginRoot)) {
							((Pane)stage.getScene().getRoot()).setMaxWidth(400.0);
							((Pane)stage.getScene().getRoot()).setPrefHeight(700.0);
							stage.sizeToScene();
							stage.centerOnScreen();	
						}	
						stage.maximizedProperty().removeListener(this);
					}			
				}				
			});
		}
	}
	
	@FXML
	public void backup(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("*.backup");
		File backup = fileChooser.showSaveDialog(this.root.getScene().getWindow());
		
		if(backup != null) {
			this.progressBox.setVisible(true);
			long start = System.currentTimeMillis();
			
			Thread exportBackupThread = new Thread(()->{
				String message = null;
				BackupExporter exporter = BackupExporter.getDefault();
				try {
					exporter.export(this.account, backup);
					message = "Backup file created in " + backup;
				} catch (BackupException e) {	
					message = "Impossible create backup file created in " + backup;
					e.printStackTrace();
				}finally {
					long time = 500 - (System.currentTimeMillis() - start);
					
					if(time > 0) {
						synchronized(exporter) {
							try {
								exporter.wait(time);
							} catch (InterruptedException e) {	}
						}
					}
					
					final String _message = message;
					
					Platform.runLater(()->{
						this.progressBox.setVisible(false);
						this.stateLbl.setText(_message);
					});
				}
			});
			
			exportBackupThread.setName("ExportBackupThread");
			exportBackupThread.setDaemon(true);
			exportBackupThread.start();
		}
	}
}
