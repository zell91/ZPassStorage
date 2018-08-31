package com.zstorage.gui.components;


import java.io.File;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class TextFieldMessage extends StackPane {

	private ObjectProperty<Type> typeProperty = new SimpleObjectProperty<>();
	
	private Label label;
	private HBox wrapper;
	private ImageView graphic;

	private boolean isPlaying;
		
	public enum Transition{
		FADE_IN_OUT, FADE_IN, FADE_OUT, WIPE_IN_HORIZONTAL, WIPE_OUT_HORIZONTAL, WIPE_IN_OUT_HORIZONTAL, WIPE_IN_VERTICAL, WIPE_OUT_VERTICAL, WIPE_IN_OUT_VERTICAL, ZOOM_IN_OUT, ZOOM_IN, ZOOM_OUT;
	}
	
	public TextFieldMessage() {
		super();
		this.setup();
	}
	
	private void setup() {
		this.setMinHeight(0.0);
		this.setMaxHeight(0.0);
		this.label = new Label();
		this.label.setWrapText(true);
		this.wrapper = new HBox(this.label);
		this.label.setGraphic(this.graphic = new ImageView());
		this.graphic.setFitWidth(16);
		this.label.setGraphicTextGap(5);
		this.graphic.setFitHeight(16);
		this.wrapper.setPadding(new Insets(3,3,3,3));
		this.wrapper.setAlignment(Pos.CENTER);
		this.getChildren().add(this.wrapper);	
		this.setOpacity(0.0);
		
		StackPane.setMargin(this.wrapper, new Insets(25,0, 0,0));
	}
		
	public void showFor(Transition transition, long millis) {
		if(isPlaying) return;
			
		isPlaying = true;
		
		this.show(transition);

		Thread animation = new Thread(()->{			
			synchronized(this) {
				try {
					this.wait(700 + millis);
				} catch (InterruptedException e) {	}
			}
			
			Platform.runLater(()->this.hide(transition));			
		});
		
		animation.setName("AnimationThread");
		animation.setDaemon(true);
		
		animation.start();
	}
	
	public void show(Transition transition) {
		this.animate(transition, true);
	}

	public void hide(Transition transition) {
		this.animate(transition, false);
	}
	
	private void animate(Transition transition, boolean show) {
		if(!transition.name().endsWith("_IN_OUT")) {
			if((show && transition.name().endsWith("_OUT")) || (!show && transition.name().endsWith("_IN")))
				throw new IllegalArgumentException();				
		}
		
		double tFrom = -25.0;
		double tTo = 0.0;
		
		double oFrom = show ? 0.0 : 1.0;
		double oTo = show ? 1.0 : 0.0;
		
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), this);
		Animation animation = null;
		
		translateTransition.setFromY(tFrom);
		translateTransition.setToY(tTo);
		translateTransition.setAutoReverse(false);

		this.setOpacity(1.0);

		switch(transition) {
			case FADE_IN:					
			case FADE_OUT:
			case FADE_IN_OUT:
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(700), this);
				fadeTransition.setFromValue(oFrom);
				fadeTransition.setToValue(oTo);		
				fadeTransition.setAutoReverse(false);				
				animation = fadeTransition;
				break;
			case ZOOM_IN:
			case ZOOM_OUT:
			case ZOOM_IN_OUT:
				this.setOpacity(1.0);
				this.setScaleX(show ? 0.0 : 1.0);
				this.setScaleY(show ? 0.0 : 1.0);
				
				Timeline timeline = new Timeline();
								
				WritableValue<Double> writiable = new WritableValue<Double>() {
					@Override
					public Double getValue() {
						return getScaleX();
					}

					@Override
					public void setValue(Double value) {
						setScaleX(value);
						setScaleY(value);
					}					
				};
				
				KeyFrame keyFrame = new KeyFrame(Duration.millis(300), new KeyValue(writiable, show ? 1.0 : 0.0));				
				
				timeline.setAutoReverse(false);
				timeline.setCycleCount(1);
				timeline.getKeyFrames().addAll(keyFrame);					
				animation = timeline;
				break;
			case WIPE_IN_HORIZONTAL:
			case WIPE_OUT_HORIZONTAL:
			case WIPE_IN_OUT_HORIZONTAL:
				this.setOpacity(1.0);
				this.setScaleX(show ? 0.0 : 1.0);
				
				timeline = new Timeline();
								
				writiable = new WritableValue<Double>() {
					@Override
					public Double getValue() {
						return getScaleX();
					}

					@Override
					public void setValue(Double value) {
						setScaleX(value);
					}					
				};
				
				keyFrame = new KeyFrame(Duration.millis(300), new KeyValue(writiable, show ? 1.0 : 0.0));				
				
				timeline.setAutoReverse(false);
				timeline.setCycleCount(1);
				timeline.getKeyFrames().addAll(keyFrame);					
				animation = timeline;
				break;	
			case WIPE_IN_VERTICAL:
			case WIPE_OUT_VERTICAL:
			case WIPE_IN_OUT_VERTICAL:
				this.setOpacity(1.0);
				this.setScaleY(show ? 0.0 : 1.0);
				
				timeline = new Timeline();
								
				writiable = new WritableValue<Double>() {
					@Override
					public Double getValue() {
						return getScaleY();
					}

					@Override
					public void setValue(Double value) {
						setScaleY(value);
					}					
				};
				
				keyFrame = new KeyFrame(Duration.millis(300), new KeyValue(writiable, show ? 1.0 : 0.0));				
				
				timeline.setAutoReverse(false);
				timeline.setCycleCount(1);
				timeline.getKeyFrames().addAll(keyFrame);					
				animation = timeline;
				break;						
			default:
				return;
		}
		
		if(this.getTranslateY() != -25.0 || this.getTranslateY() != 0.0) {
			if(show)
				translateTransition.play();
			else
				animation.setOnFinished(e->isPlaying = false);
			animation.play();
		}
	}

	
	public void show() {
		this.setOpacity(1);
	}

	public void hide() {
		this.setOpacity(0.0);
	}

	private void setStyleClasses() {		
		this.getStyleClass().clear();
		this.getStyleClass().add("text-field-message");
		
		Background background;;
		Color textColor;
		
		switch(this.getType()) {
			case ERROR:
				background = new Background(new BackgroundFill[] {
						new BackgroundFill(new Color(.8, 0, .0, 1), null, new Insets(1.5, 1.5, 1.5, 1.5)),
						new BackgroundFill(new Color(1, 1, 1, .6), null, new Insets(1, 1, 1, 1))
				});
				
				textColor = Color.DARKRED.brighter();
				this.graphic.setImage(new Image("file:" + new File("images/error.png").getAbsolutePath()));
				this.getStyleClass().add("text-field-error");
				break;
			case INFORMATION:
				background = new Background(new BackgroundFill[] {
						new BackgroundFill(new Color(.0, .5, 1, 1), null, new Insets(0, 0, 0, 0)),
						new BackgroundFill(new Color(1, 1, 1, .6), null, new Insets(.5, .5, .5, .5))
				});
				textColor = new Color(.0, .0, .4, .8);
				this.graphic.setImage(new Image("file:" + new File("images/info.png").getAbsolutePath()));
				this.getStyleClass().add("text-field-info");
				break;
			case WARNING:
				background = new Background(new BackgroundFill[] {
						new BackgroundFill(new Color(.95, .89, .7, 1), null, new Insets(1.5, 1.5, 1.5, 1.5)),
						new BackgroundFill(new Color(1, 1, 1, .0), null, new Insets(1, 1, 1, 1))
				});
				textColor = new Color(.45, .25, .0, .9);
				this.graphic.setImage(new Image("file:" + new File("images/warning1.png").getAbsolutePath())); 
				this.getStyleClass().add("text-field-warning");
				break;
			case SUCCESS:
				background = new Background(new BackgroundFill[] {
						new BackgroundFill(new Color(.6, .9, .7, .9), null, new Insets(1.5, 1.5, 1.5, 1.5)),
						new BackgroundFill(new Color(1, 1, 1, .0), null, new Insets(1, 1, 1, 1))
				});
				textColor = new Color(0, .2, .0, .9);
				this.graphic.setImage(new Image("file:" + new File("images/success.png").getAbsolutePath()));
				this.getStyleClass().add("text-field-success");
				break;
			default:
				return;
		}
		
		this.wrapper.setBackground(background);
		this.label.setTextFill(textColor);
	}

	public ObjectProperty<Type> typeProperty() {
		return this.typeProperty;
	}
	
	public Type getType() {
		return this.typeProperty.get();
	}
	
	public void setType(Type type) {
		this.typeProperty.set(type);
		this.setStyleClasses();
	}
	
	public StringProperty textProperty() {
		return this.label.textProperty();
	}
	
	public String getText() {
		return this.label.textProperty().get();
	}
	
	public void setText(String text) {
		this.label.textProperty().set(text);
	}
	
	
}
