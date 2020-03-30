import eventbus.MyEventBus;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private Handler handler;

    public static void main(String[] args) {
        launch(args);
    }

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        handler = new Handler();
        MyEventBus.register(handler);
        MyEventBus.register(this);
        initUI(primaryStage);
    }

    private void initUI(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");
        initTop(root);
        initMid(root);
        Scene scene = new Scene(root, 470, 180);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private RadioButton conditionCold = new RadioButton(), conditionWarm = new RadioButton(), conditionClose = new RadioButton();
    private TextField remainMile = new TextField(), Battery = new TextField();
    private Slider blowingLevel = new Slider();
    private void initTop(BorderPane root) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #991f27;"); //背景色
        hBox.setPadding(new Insets(5, 5, 5, 5));
        VBox left = new VBox();
        VBox right = new VBox();
        HBox rightTop = new HBox();
        HBox rightBottom = new HBox();
        HBox leftTop = new HBox();
        HBox leftBottom = new HBox();
        hBox.getChildren().addAll(left,right);
        // 给左右盒子添加按钮
        left.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        left.setSpacing(10); //节点之间的间距
        left.setStyle("-fx-background-color: #336699;"); //背景色
        leftTop.getChildren().addAll(new Label("制冷"), conditionCold, new Label("制暖"), conditionWarm, new Label("关闭"), conditionClose);
        leftBottom.getChildren().addAll(new Label("空调档位"), blowingLevel);
        leftTop.setSpacing(15);
        leftBottom.setSpacing(10);
        left.getChildren().addAll(leftTop, leftBottom);
        right.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        right.setSpacing(10); //节点之间的间距
        right.setStyle("-fx-background-color: #336699;"); //背景色
        right.getChildren().addAll(rightTop, rightBottom);
        rightTop.setSpacing(10);
        rightBottom.setSpacing(10);
        rightTop.getChildren().addAll(new Label("剩余里程:"), remainMile);
        rightBottom.getChildren().addAll(new Label("剩余电量:"), Battery);
        ToggleGroup group = new ToggleGroup();
        conditionCold.setToggleGroup(group);
        conditionClose.setToggleGroup(group);
        conditionWarm.setToggleGroup(group);
        conditionClose.setSelected(true);
        remainMile.setMinWidth(100);
        Battery.setMinWidth(100);
        blowingLevel.setMin(0);
        blowingLevel.setMax(100);
        blowingLevel.setValue(40);
        blowingLevel.setShowTickLabels(true);
        blowingLevel.setShowTickMarks(true);
        blowingLevel.setMajorTickUnit(50);
        blowingLevel.setMinorTickCount(5);
        blowingLevel.setBlockIncrement(10);
        blowingLevel.setMinWidth(150);
        // 添加到主面板
        root.setTop(hBox);
    }

    private ToggleButton HighBeam = new ToggleButton("远光灯"), LowBeam = new ToggleButton("近光灯");
    private ToggleButton LeftTurningLamp = new ToggleButton("左转灯"), RightTurningLamp = new ToggleButton("右转灯");
    private ToggleButton FrontFogLamp = new ToggleButton("前雾灯"), RearFogLamp = new ToggleButton("后雾灯");
    private ToggleButton demisterControl = new ToggleButton("除雾");
    private TextField minSpeed = new TextField(), maxSpeed = new TextField();
    private void initMid(BorderPane root) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #991f27;"); //背景色
        hBox.setPadding(new Insets(5, 5, 5, 5));
        VBox left = new VBox();
        BorderPane mid = new BorderPane();
        HBox right = new HBox();
        HBox leftTop = new HBox();
        HBox leftBottom = new HBox();
        hBox.getChildren().addAll(left,mid, right);
        // 给左右盒子添加按钮
        left.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        left.setSpacing(10); //节点之间的间距
        left.setStyle("-fx-background-color: #336699;"); //背景色
        leftTop.getChildren().addAll(HighBeam,  LeftTurningLamp, FrontFogLamp);
        leftBottom.getChildren().addAll(LowBeam,  RightTurningLamp, RearFogLamp);
        leftTop.setSpacing(10);
        leftBottom.setSpacing(10);
        left.getChildren().addAll(leftTop, leftBottom);
        mid.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        mid.setStyle("-fx-background-color: #336699;"); //背景色
        mid.setCenter(demisterControl);
        right.setPadding(new Insets(10, 5, 10, 10));
        right.setStyle("-fx-background-color: #336699;"); //背景色
        right.setSpacing(10);
        right.setAlignment(Pos.CENTER);
        right.getChildren().addAll(minSpeed, new Label("~") , maxSpeed);
        minSpeed.setMaxWidth(85);
        maxSpeed.setMaxWidth(85);
        // 添加到Group
        ToggleGroup g1 = new ToggleGroup();
        ToggleGroup g2 = new ToggleGroup();
        HighBeam.setToggleGroup(g1);
        LowBeam.setToggleGroup(g1);
        LeftTurningLamp.setToggleGroup(g2);
        RightTurningLamp.setToggleGroup(g2);
        // 添加到主面板
        root.setCenter(hBox);
    }
}
