package application;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main_Lego extends Application {
	public static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Main_Lego.primaryStage=primaryStage;
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass().getResource("/application/MainFXML.fxml"));
			primaryStage.setScene(new Scene(root));
			
			primaryStage.setTitle("Lego Cubes");
			URL url = Main_Lego.class.getResource("/application/lego_icn.png");
			BufferedImage bi = ImageIO.read(url.openStream());
			Image icon = SwingFXUtils.toFXImage(bi, null );
			primaryStage.getIcons().add(icon);
			
			primaryStage.setX(0);
			primaryStage.setY(0);
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			Main_Lego.primaryStage.sizeToScene();
			if(Main_Lego.primaryStage.getHeight()>=primScreenBounds.getHeight())
					Main_Lego.primaryStage.setHeight(primScreenBounds.getHeight());
			else if(Main_Lego.primaryStage.getWidth()>=primScreenBounds.getWidth())
				Main_Lego.primaryStage.setWidth(primScreenBounds.getWidth());
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
