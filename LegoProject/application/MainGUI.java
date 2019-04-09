package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import models.AlgorithmLego;
import models.Board;
import models.Cube;

public class MainGUI implements Initializable {
	private @FXML TextField txtRows, txtCols, txtPop;
	private @FXML GridPane gpCubes;
	private @FXML ScrollPane spMain;
	private @FXML Label lblX, lblMsg;
	private @FXML Button btnNext;
	private Board b = null;
	private ArrayList<RadioButton> rbs;
	private boolean isInit;
	
	private EventHandler<KeyEvent> handTXTChanged() {
		return new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(txtRows.getText().isEmpty()==false && txtCols.getText().isEmpty()==false) {
					btnNext.setOnAction(select());
					txtRows.removeEventHandler(event.getEventType(),this);
					txtCols.removeEventHandler(event.getEventType(),this);
					lblMsg.setText("");
				}
				else {
					gpCubes.getChildren().clear();
					rbs.clear();
					b=null;
				}
			}
		};
	}
	
	private EventHandler<KeyEvent> handTXTReCh() {
		return new EventHandler<KeyEvent>() {
			
			@Override
			public void handle(KeyEvent event) {
				try {
					String r = txtRows.getText(), c = txtCols.getText();
					if(r.isEmpty()==false && c.isEmpty()==false) {
						lblMsg.setText("");
						gpCubes.getChildren().clear();
						rbs=null;
						b=null;
						
						if(AlgorithmLego.isInteger(r)==false)
							throw new Exception("Row must contain integer value");
						if(AlgorithmLego.isInteger(c)==false)
							throw new Exception("Column must contain integer value");
						if(Integer.parseInt(r)>9)
							throw new Exception("Please enter Row value which smaller than 10 for better results");
						if(Integer.parseInt(c)>9)
							throw new Exception("Please enter Column value which smaller than 10 for better results");
						loadCubes();
					}
					else {
						gpCubes.getChildren().clear();
						rbs=null;
						b=null;
					}
				} catch (Exception e) {
					lblMsg.setText(e.getMessage());
					Main_Lego.primaryStage.setX(0);
					Main_Lego.primaryStage.setY(0);
					Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
					Main_Lego.primaryStage.sizeToScene();
					if(Main_Lego.primaryStage.getHeight()>=primScreenBounds.getHeight())
						Main_Lego.primaryStage.setHeight(primScreenBounds.getHeight());
					else if(Main_Lego.primaryStage.getWidth()>=primScreenBounds.getWidth())
						Main_Lego.primaryStage.setWidth(primScreenBounds.getWidth());
				}
				
			}
		};
	}
	
	private EventHandler<ActionEvent> select(){
		return new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					btnNext.setText("Next");
					lblMsg.setText("");
					loadCubes();
					txtRows.addEventFilter(KeyEvent.KEY_RELEASED, handTXTReCh());
					txtCols.addEventFilter(KeyEvent.KEY_RELEASED, handTXTReCh());
					btnNext.setOnAction(next());
				} catch (Exception e) {
					lblMsg.setText(e.getMessage());
					e.printStackTrace();
				}
			}
		};
	}
	
	private EventHandler<ActionEvent> next(){
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					if(b!=null && rbs!=null && 
							txtPop.getText().isEmpty()==false && AlgorithmLego.isInteger(txtPop.getText())) {
						if(Integer.parseInt(txtPop.getText())%2!=0)
							lblMsg.setText("Must choose even number of population");
						else {
							setNeededDataForAlgorithm();
							Parent root = FXMLLoader.load(getClass().getResource("/application/LegoFXML.fxml"));
							Main_Lego.primaryStage.setScene(new Scene(root));
							Main_Lego.primaryStage.setX(0);
							Main_Lego.primaryStage.setY(0);
							Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
							Main_Lego.primaryStage.sizeToScene();
							if(Main_Lego.primaryStage.getHeight()>=primScreenBounds.getHeight())
								Main_Lego.primaryStage.setHeight(primScreenBounds.getHeight());
							else if(Main_Lego.primaryStage.getWidth()>=primScreenBounds.getWidth())
								Main_Lego.primaryStage.setWidth(primScreenBounds.getWidth());
						}
					}
					else if(b==null || b.rows<1 || b.cols<1)
						lblMsg.setText("Must choose row and cols first");
					else if(rbs==null)
						lblMsg.setText("Must choose at leats one cube type");
					else if(txtPop.getText().isEmpty())
						lblMsg.setText("Must choose population size");
					else if(AlgorithmLego.isInteger(txtPop.getText())==false)
						lblMsg.setText("Population size must be integer value");
					}
				catch (Exception e) {
					String msg = e.getMessage();
					if(msg!=null && msg.isEmpty()==false)
						lblMsg.setText(msg);
					else {
						lblMsg.setText("Error occured");
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	private void setNeededDataForAlgorithm() throws Exception {
		int cnt_selected = 0;
		ArrayList<Cube> available_bricks = new ArrayList<>();
		for (RadioButton rb : rbs) {
			if(rb.isSelected()==true) {
				cnt_selected++;
				if(rb.getUserData().getClass().equals(String.class)) {
					String[] sizes = ((String)rb.getUserData()).split("x");
					if(!AlgorithmLego.isInteger(sizes[0])||!AlgorithmLego.isInteger(sizes[1]))
						System.err.println("ERROR sizes wrong");
					else {
						Cube b = new Cube(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]));
						available_bricks.add(b);
					}
				}
				else
					throw new Exception("ERROR user data class");
			}
		}
		if(cnt_selected<2)
			throw new Exception("Must select at least 2 types of cubes");
		LegoGUI.setStaticValues(b,available_bricks,Integer.parseInt(txtPop.getText()));
	}
	
	private void loadCubes() throws Exception {
		if (txtRows.getText().isEmpty() || txtCols.getText().isEmpty())
			throw new Exception("One or more of text fields is empty");
		if (AlgorithmLego.isInteger(txtRows.getText()) == false
				|| AlgorithmLego.isInteger(txtCols.getText()) == false)
			throw new Exception("One or more of text fields not include integer");
		int r = Integer.parseInt(txtRows.getText()), c = Integer.parseInt(txtCols.getText());
		if (r < 1 || c < 1)
			throw new Exception("One or more of text fields smaller than 1");
		b = new Board(r, c);

		paintCubes(b);
	}

	private void paintCubes(Board b) {
		rbs = new ArrayList<>();
		gpCubes.getChildren().clear();
		
		Color fill = Color.rgb(192, 240, 255), stroke = Color.rgb(47, 0, 255);
		for (int height = 1; height <= b.rows; height++) {
			Label l = null;
			VBox vb = null;
			for (int width = 1; width <= b.cols; width++) {
				if(height==b.rows && width==b.cols)
					continue;
				vb = new VBox(2.0);	vb.setAlignment(Pos.CENTER);	vb.setPadding(new Insets(5));
				Rectangle rec = new Rectangle(width * 20, height * 20, fill);	rec.setStroke(stroke);
				RadioButton rb = new RadioButton();
				l = new Label(height + "x" + width);	l.setGraphic(rb);	l.setContentDisplay(ContentDisplay.BOTTOM);
				rb.setUserData(l.getText());
				rbs.add(rb);
				vb.getChildren().add(rec);	vb.getChildren().add(l);
				gpCubes.add(vb, width-1, height-1);
				if(isInit==true) {
					if(width==1 && height!=4)
						rb.setSelected(true);
					else if(width==4 && height==1)
						rb.setSelected(true);
					else if(width==2 && height==2)
						rb.setSelected(true);
				}
			}
		}
		isInit=false;
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		Main_Lego.primaryStage.sizeToScene();
		if(Main_Lego.primaryStage.getHeight()>=primScreenBounds.getHeight())
				Main_Lego.primaryStage.setHeight(primScreenBounds.getHeight());
		else if(Main_Lego.primaryStage.getWidth()>=primScreenBounds.getWidth())
			Main_Lego.primaryStage.setWidth(primScreenBounds.getWidth());
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			if(txtRows.getText().isEmpty() || txtCols.getText().isEmpty()) {
				isInit=false;
				txtRows.addEventFilter(KeyEvent.KEY_RELEASED, handTXTChanged());
				txtCols.addEventFilter(KeyEvent.KEY_RELEASED, handTXTChanged());
			}
			else {
				isInit = true;
				btnNext.setText("Next");
				loadCubes();
				txtRows.addEventFilter(KeyEvent.KEY_RELEASED, handTXTReCh());
				txtCols.addEventFilter(KeyEvent.KEY_RELEASED, handTXTReCh());
				btnNext.setOnAction(next());
			}
		} catch (Exception e) {
			lblMsg.setText(e.getMessage());
			e.printStackTrace();
		}
	}
}
