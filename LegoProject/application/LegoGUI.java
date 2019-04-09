package application;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import models.AlgorithmLego;
import models.Board;
import models.Cube;
import models.Chromosome;

public class LegoGUI implements Initializable{
	private @FXML Pane pnBoard;
	private @FXML Label lblFit, lblParts, lblMsg;
	private AlgorithmLego lego;
	
	private static Board board;
	private static ArrayList<Cube> available_bricks;
	private static int popSize;
	private Chromosome opt;
	
	public static void setStaticValues(Board b,ArrayList<Cube> available_bricks, int popSize) {
		LegoGUI.board=b;
		LegoGUI.available_bricks=available_bricks;
		LegoGUI.popSize=popSize;
	}
	
	private void paintBoard(boolean isRandomColors) {
		Color fill, stroke;
		if(isRandomColors) {
			Random r = new Random();
			fill = Color.rgb(r.nextInt(256),r.nextInt(256),r.nextInt(256));
			stroke = Color.rgb(r.nextInt(256),r.nextInt(256),r.nextInt(256));
		}
		else {
			fill=Color.rgb(192, 240, 255);
			stroke=Color.rgb(47, 0, 255);
		}
		for (CubeInGUI bg : opt.cubes_in_GUI) {
			Rectangle rec = new Rectangle(bg.width*50, bg.height*50, fill);
			rec.setStroke(stroke);
			pnBoard.getChildren().add(rec);
			rec.setX(bg.x*50);
			rec.setY(bg.y*50);
			//Circle c = new Circle(bg.x*50+10, bg.y*50 + 10, 5);
			//pnBoard.getChildren().add(c);
			Label l = new Label(bg.height+"x"+bg.width);
			pnBoard.getChildren().add(l);
			l.setLayoutX(bg.x*50);
			l.setLayoutY(bg.y*50);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			FileOutputStream fos = new FileOutputStream("output.txt");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			PrintStream ps = new PrintStream(bos,true);
			System.setOut(ps);
			
			this.lego = new AlgorithmLego(LegoGUI.board,LegoGUI.available_bricks,popSize);
			
			fos.close();
			
			LegoGUI.board = lego.getBoard();
			pnBoard.setPrefWidth(board.cols*50);
			pnBoard.setPrefHeight(board.rows*50);
			opt = lego.getBestChromInAllGens();
			
			paintBoardInWhite();
			
			boolean createGrids = false;
			paintBoard(createGrids);
			if(createGrids)
				makeGrid();
			lblFit.setText(Integer.toString(opt.fitness));
			lblParts.setText(Integer.toString(opt.num_of_parts));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			lblMsg.setText(e.getMessage());
		}
	}
	
	private void paintBoardInWhite() {
		for (int i = 0; i < board.rows; i++) {
			for (int j = 0; j < board.cols; j++) {
				Color fill = Color.WHITE;
				Color stroke = Color.BLACK;
				Rectangle rec = new Rectangle(1*50, 1*50, fill);
				rec.setStroke(stroke);
				pnBoard.getChildren().add(rec);
				rec.setX(j*50);
				rec.setY(i*50);
			}
		}
	}
	
	private void makeGrid() {
		Line lr;
		for (int i = 1; i < board.rows; i++) {
			lr = new Line(-100, 0, 100, 0);
			lr.setLayoutX(100);
			lr.setLayoutY(i*50);
			pnBoard.getChildren().add(lr);
		}
		
		Line lc;
		for (int i = 0; i < board.cols*5; i++) {
			lc = new Line(0, -100, 0, 100);
			lc.setLayoutY(100);
			lc.setLayoutX(i*50);
			pnBoard.getChildren().add(lc);
		}
	}
	
	private @FXML void clickedBack(ActionEvent ev) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/application/MainFXML.fxml"));
			Main_Lego.primaryStage.setScene(new Scene(root));
			Main_Lego.primaryStage.setX(0);
			Main_Lego.primaryStage.setY(0);
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			Main_Lego.primaryStage.sizeToScene();
			if(Main_Lego.primaryStage.getHeight()>=primScreenBounds.getHeight())
				Main_Lego.primaryStage.setHeight(primScreenBounds.getHeight());
			else if(Main_Lego.primaryStage.getWidth()>=primScreenBounds.getWidth())
				Main_Lego.primaryStage.setWidth(primScreenBounds.getWidth());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}