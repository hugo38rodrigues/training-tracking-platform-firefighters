package ui;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.components.Content;
import ui.components.Sidebard;

public class App extends Application {

    private AppContext context;

    @Override
    public void init() throws Exception {
        String databasePath = System.getProperty("app.db", "../database.db");
        context = new AppContext(databasePath);
    }

    @Override
    public void stop() throws Exception {
        if (context != null) {
            context.close();
        }
    }

    private void loadFonts() {
        String[] files = {
                "Barlow-Regular.ttf", "Barlow-SemiBold.ttf", "Barlow-Bold.ttf", "BarlowCondensed-Bold.ttf"
        };
        for (String file : files) {
            try (InputStream in = getClass().getResourceAsStream("/ui/fonts/" + file)) {
                if (in == null) {
                    System.err.println("Police introuvable : " + file);
                    continue;
                }
                Font font = Font.loadFont(in, 12);
                System.out.println(file + " → famille \"" + font.getFamily() + "\"");
            } catch (IOException e) {
                System.err.println("Impossible de lire " + file + " : " + e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage stage) {
        loadFonts();
        BorderPane root = new BorderPane();
        root.setLeft(Sidebard.create()); // zone gauche : le menu
        root.setCenter(Content.create(context)); // zone centrale : la page

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("Suivi des formations");
        stage.setScene(scene);
        stage.show();
    }

    
}