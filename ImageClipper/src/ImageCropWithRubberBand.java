
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import javafx.scene.input.MouseEvent;


public class ImageCropWithRubberBand extends Application { int p=1;
    RubberBandSelection rubberBandSelection;
    File file = new File("img" + p++);
    
    ImageView imageView;
    


    Stage secondStage = new Stage();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage PrimaryStage) {
        PrimaryStage.setTitle("File Chooser");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Open Picture...");
        final Button quitButton = new Button ("Exit");


        openButton.setOnAction((final ActionEvent e) -> {
            configureFileChooser(fileChooser);
            File file1 = fileChooser.showOpenDialog(PrimaryStage);
            if (file1 != null) {
                openFile(file1);
            }
        });
	
	   
        



        quitButton.setOnAction(e -> 
    {   Boolean answer = ConfirmBox.display("Exit", "Sure you want to exit");
    if(answer)
        
        PrimaryStage.close();
        } 
);


        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
	GridPane.setConstraints(quitButton, 1, 0);

        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, quitButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        PrimaryStage.setScene(new Scene(rootGroup));
        PrimaryStage.show();
    }

    private static void configureFileChooser(
        final FileChooser fileChooser) {      
            fileChooser.setTitle("View Pictures");
            fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
            );                 
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
            );
    }

	




	public void openFile(File file) {

        secondStage.setTitle("Image Crop");

		

        BorderPane root = new BorderPane();

        // container for image layers
        ScrollPane scrollPane = new ScrollPane();

        // image layer: a group of images
        Group imageLayer = new Group(); 

        // load the image

        Label name = new Label(file.getAbsolutePath());
	
        Image image = new Image(file.toURI().toString());
        // the container for the image as a javafx node
        imageView= new ImageView( image);

        // add image to layer
        imageLayer.getChildren().add( imageView);

        // use scrollpane for image view in case the image is large
        scrollPane.setContent(imageLayer);
	imageView.setPreserveRatio(true);
	imageView.fitWidthProperty().bind(secondStage.widthProperty());
        imageView.fitHeightProperty().bind(secondStage.heightProperty());
        // put scrollpane in scene
        root.setCenter(scrollPane);

        // rubberband selection
        rubberBandSelection = new RubberBandSelection(imageLayer);

        // create context menu and menu items
        ContextMenu contextMenu = new ContextMenu();

        MenuItem cropMenuItem = new MenuItem("Crop");
        cropMenuItem.setOnAction((ActionEvent e) -> {
            // get bounds for image crop
            Bounds selectionBounds = rubberBandSelection.getBounds();
            
            // show bounds info
            System.out.println( "Selected area: " + selectionBounds);
            
            // crop the image
            crop( selectionBounds);
        });
        contextMenu.getItems().add( cropMenuItem);

        // set context menu on image layer
        imageLayer.setOnMousePressed((MouseEvent event) -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(imageLayer, event.getScreenX(), event.getScreenY());
            }
        });

        secondStage.setScene(new Scene(root, 1366, 768));
        secondStage.show();
    }

	
	
   


    private void crop( Bounds bounds) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        file = fileChooser.showSaveDialog( secondStage);
        if (file == null)
            return;

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D( bounds.getMinX(), bounds.getMinY(), width, height));

        WritableImage wi = new WritableImage( width, height);
        imageView.snapshot(parameters, wi);

        // save image 
        // !!! has bug because of transparency (use approach below) !!!
        // --------------------------------
//        try {
//          ImageIO.write(SwingFXUtils.fromFXImage( wi, null), "jpg", file);
//      } catch (IOException e) {
//          e.printStackTrace();
//      }


        // save image (without alpha)
        // --------------------------------
        BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(wi, null);
        BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

        Graphics2D graphics = bufImageRGB.createGraphics();
        graphics.drawImage(bufImageARGB, 0, 0, null);

        try {

            ImageIO.write(bufImageRGB, "jpg", file); 

            System.out.println( "Image saved to " + file.getAbsolutePath());

        } catch (IOException e) {
        }

        graphics.dispose();

    }
}
