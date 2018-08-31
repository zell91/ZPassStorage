package com.zstorage.gui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Ellipsis extends StackPane {
	
	private IntegerProperty numDot = new SimpleIntegerProperty(5);
	private List<TranslateTransition> tts = new ArrayList<>();
	
	public Ellipsis() {
		super();
		this.getStyleClass().add("ellipsis");
		this.setup();
		this.setAlignment(Pos.CENTER_LEFT);
		this.setVisible(false);
	}
	
	public IntegerProperty numDotProperty() {
		return this.numDot;
	}
	
	public void setNumDot(int numDot) {
		this.numDot.set(numDot);
		this.setup();
	}
	
	public int getNumDot() {
		return numDot.get();
	}
	
	public void setup() {	
		this.getChildren().clear();
		this.tts.clear();

		for(int i=0; i<this.getNumDot();i++) {
			Region dot = new Region();
			
			dot.setMinSize(3
					, 3);
			dot.setMaxSize(3, 3);
			dot.setBackground(new Background(new BackgroundFill[] {
					new BackgroundFill(Color.WHITE, new CornerRadii(50), null)
			}));
			
			dot.getStyleClass().add("dot");
			this.getChildren().add(dot);			
		}
	}
	
	public void play(){		
		double delay = 0.0;
		for(Node node : this.getChildren()) {
			if(node instanceof Region) {
				TranslateTransition tt = new TranslateTransition(Duration.millis(800), node);
				//FadeTransition ft = new FadeTransition(Duration.millis(800), node);
				
				double width = this.getWidth();
				/*
				ft.setFromValue(0.0);
				ft.setToValue(1.0);
				ft.setCycleCount(-1);
				ft.setAutoReverse(true);
				ft.setDelay(Duration.millis(delay));
				*/
				tt.setFromX(20);
				tt.setToX(width - 20);
				tt.setCycleCount(-1);
				tt.setAutoReverse(false);
				tt.setDelay(Duration.millis(delay));
				delay+=200;
				tts.add(tt);
				tt.play();
			}
		}
		
		this.setVisible(true);
	}
	
	public void stop(){		
		for(TranslateTransition tt : tts) {
			tt.stop();
		}
		
		this.setVisible(false);
	}

}
