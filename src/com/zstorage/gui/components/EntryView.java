package com.zstorage.gui.components;

import java.io.File;
import java.security.Key;

import com.zstorage.credential.EntryCredential;
import com.zstorage.store.StoredObject;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EntryView extends VBox {
	
	private TextField user, password, note;
	
	private GridPane userBox, passBox, noteBox;
	
	private Button showBtn, editBtn, delBtn;
	
	private StoredObject storedObject;

	private HBox btnBox;
	
	private boolean passwordVisible;
	
	public EntryView(StoredObject storedObject) {
		this.storedObject = storedObject;
		this.user = new TextField();
		this.password = new TextField();
		this.note = new TextField(storedObject.getNote());

		this.setup();
	
		this.user.setText(new String(storedObject.getUser().getCredential()));
		
		StringBuilder _pass = new StringBuilder();
		
		for(int i = 0; i<storedObject.getPassword().getCredential().length;i++) {
			_pass.append("*");
		}
		
		this.password.setText(_pass.toString());
		this.user.setEditable(false);
		this.password.setEditable(false);
		this.note.setEditable(false);				
	}
	
	private void setup() {
		this.showBtn = new Button();
		this.showBtn.setGraphic(new ImageView());
		((ImageView)this.showBtn.getGraphic()).setImage(new Image("file:" + new File("images/show.png").getAbsolutePath()));
		((ImageView)this.showBtn.getGraphic()).setFitWidth(22);
		((ImageView)this.showBtn.getGraphic()).setFitHeight(22);
		((ImageView)this.showBtn.getGraphic()).setPreserveRatio(true);
		this.showBtn.setCursor(Cursor.HAND);
		this.showBtn.setOnAction(e->showHidePassword());
		
		this.showBtn.getStyleClass().add("show-btn");
		
		this.editBtn = new Button("Edit");
		this.delBtn = new Button("Delete");

		this.editBtn.getStyleClass().add("entry-btn");
		this.delBtn.getStyleClass().add("entry-btn");
		
		this.btnBox = new HBox(this.editBtn, this.delBtn);
		this.btnBox.setSpacing(10);
		this.btnBox.setAlignment(Pos.CENTER);
		this.btnBox.getStyleClass().add("button-box");

		Label userLbl = new Label("Username:");
		this.userBox = new GridPane();
		this.userBox.setHgap(10);
		this.userBox.add(userLbl, 0, 0);
		this.userBox.add(this.user, 1, 0);
		this.userBox.getStyleClass().addAll("text-box", "user-box");
		
		Label passLbl = new Label("Password:");
		this.passBox = new GridPane();
		this.passBox.setHgap(10);
		this.passBox.add(passLbl, 0, 0);
		this.passBox.add(this.password, 1, 0);
		this.passBox.add(this.showBtn, 2, 0);
		this.passBox.getStyleClass().addAll("text-box", "pass-box");
		
		Label noteLbl = new Label("Note:");
		this.noteBox = new GridPane();
		this.noteBox.setHgap(10);
		this.noteBox.add(noteLbl, 0, 0);
		this.noteBox.add(this.note, 1, 0);
		this.noteBox.getStyleClass().addAll("text-box", "note-box");
		
		this.userBox.setAlignment(Pos.CENTER);
		this.passBox.setAlignment(Pos.CENTER);
		this.noteBox.setAlignment(Pos.CENTER);

		userLbl.setAlignment(Pos.CENTER_RIGHT);
		passLbl.setAlignment(Pos.CENTER_RIGHT);
		noteLbl.setAlignment(Pos.CENTER_RIGHT);

		userLbl.setMaxWidth(100);
		passLbl.setMaxWidth(100);
		noteLbl.setMaxWidth(100);

		GridPane.setHalignment(userLbl, HPos.RIGHT);
		GridPane.setHalignment(passLbl, HPos.RIGHT);
		GridPane.setHalignment(noteLbl, HPos.RIGHT);

		ColumnConstraints colCon0 = new ColumnConstraints();
		ColumnConstraints colCon1 = new ColumnConstraints();
		ColumnConstraints colCon2= new ColumnConstraints();
		
		colCon0.setPercentWidth(33);
		colCon1.setPercentWidth(33);
		colCon2.setPercentWidth(33);
		
		userBox.getColumnConstraints().addAll(colCon0, colCon1, colCon2);
		passBox.getColumnConstraints().addAll(colCon0, colCon1, colCon2);
		noteBox.getColumnConstraints().addAll(colCon0, colCon1, colCon2);
		
		
		this.getChildren().addAll(this.userBox, this.passBox, this.noteBox, this.btnBox);
		
		this.setAlignment(Pos.CENTER);
		this.setMaxWidth(800);
		
		this.getStyleClass().add("entry-box");
	}
		
	public void setOnAction(EventHandler<ActionEvent> handler) {
		this.editBtn.setOnAction(handler);
	}
	
	public void setOnCancel(EventHandler<ActionEvent> handler) {
		this.delBtn.setOnAction(handler);
	}

	public static EntryView newEntry(Key key) {
		EntryCredential credential = new EntryCredential(new byte[0], new byte[0], key);
		StoredObject newStored = new StoredObject(credential);
		
		return new EntryView(newStored);
	}
	

	public String getUserText() {
		return this.user.getText();
	}
	
	public String getPasswordText() {
		return this.password.getText();
	}

	public StoredObject getStoredObject() {
		return this.storedObject;
	}

	public String getNote() {
		return note.getText();
	}
	
	public void showHidePassword() {
		String pass = null;
				
		if(passwordVisible) {
			StringBuilder _pass = new StringBuilder();
			
			for(int i = 0; i<storedObject.getPassword().getCredential().length;i++) {
				_pass.append("*");
			}
			pass = _pass.toString();
			
			((ImageView)this.showBtn.getGraphic()).setImage(new Image("file:" + new File("images/show.png").getAbsolutePath()));
		}else {
			pass = new String(this.storedObject.getPassword().getCredential());				
			((ImageView)this.showBtn.getGraphic()).setImage(new Image("file:" + new File("images/hide.png").getAbsolutePath()));
		}

		this.password.setText(pass);
		passwordVisible = !passwordVisible;
	}
	
	public void editMode(boolean editable) {
		if(!isPasswordVisible() || !editable) {
			this.showHidePassword();
		}
		
		this.showBtn.setDisable(editable);
		this.user.setEditable(editable);
		this.password.setEditable(editable);
		this.note.setEditable(editable);
		
		this.user.requestFocus();
		this.user.deselect();
		this.user.positionCaret(this.user.getLength());
		
		this.user.pseudoClassStateChanged(PseudoClass.getPseudoClass("editable"), editable);
		this.password.pseudoClassStateChanged(PseudoClass.getPseudoClass("editable"), editable);
		this.note.pseudoClassStateChanged(PseudoClass.getPseudoClass("editable"), editable);
		
		this.editBtn.setText(editable ? "Save" : "Edit");
		this.delBtn.setText(editable ? "Cancel" : "Delete");		
	}
	
	public boolean isPasswordVisible() {
		return this.passwordVisible;
	}

	public void setUserText(String user) {
		this.user.setText(user);
	}

	public void setPasswordText(String password) {
		this.password.setText(password);
	}

	public void setNoteText(String note) {
		this.note.setText(note);
	}
	
}
