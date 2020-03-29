import eventbus.MyEventBus;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Handler handler;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        handler = new Handler();
        MyEventBus.register(handler);
        MyEventBus.register(this);
        primaryStage.setHeight(100);
        primaryStage.setWidth(100);
        primaryStage.show();
    }
}
