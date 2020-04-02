import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.sun.org.apache.xpath.internal.compiler.Keywords;
import eventbus.MessageWrap;
import eventbus.MyEventBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static huat.wubeibei.candataconvert.command.MessageName.*;
import static huat.wubeibei.candataconvert.command.SignalName.*;
import static java.lang.System.exit;

public class Main extends Application {
    private Handler handler;
    private Thread nowSpeedThread;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        handler = new Handler();
        MyEventBus.register(handler);
        MyEventBus.register(this);
        initUI(primaryStage);
        addListener();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        exit(0);
    }

    private void initUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");
        initTop(root);
        initMid(root);
        Scene scene = new Scene(root, 470, 180);
        primaryStage.setScene(scene);
    }

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
        hBox.getChildren().addAll(left, right);
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
        blowingLevel.setMax(5);
        blowingLevel.setValue(0);
        blowingLevel.setShowTickLabels(true);
        blowingLevel.setShowTickMarks(false);
        blowingLevel.setMajorTickUnit(1);
        blowingLevel.setSnapToTicks(true);
        blowingLevel.setBlockIncrement(1);
        blowingLevel.setMinorTickCount(0);
        blowingLevel.setBlockIncrement(1);
        blowingLevel.setMinWidth(150);
        // 添加到主面板
        root.setTop(hBox);
    }

    private void initMid(BorderPane root) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #991f27;"); //背景色
        hBox.setPadding(new Insets(5, 5, 5, 5));
        VBox left = new VBox();
        BorderPane mid = new BorderPane();
        HBox right = new HBox();
        HBox leftTop = new HBox();
        HBox leftBottom = new HBox();
        hBox.getChildren().addAll(left, mid, right);
        // 给左右盒子添加按钮
        left.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        left.setSpacing(10); //节点之间的间距
        left.setStyle("-fx-background-color: #336699;"); //背景色
        leftTop.getChildren().addAll(HighBeam, LeftTurningLamp, FrontFogLamp);
        leftBottom.getChildren().addAll(LowBeam, RightTurningLamp, RearFogLamp);
        leftTop.setSpacing(10);
        leftBottom.setSpacing(10);
        left.getChildren().addAll(leftTop, leftBottom);
        mid.setPadding(new Insets(10, 10, 10, 10)); //节点到边缘的距离
        mid.setStyle("-fx-background-color: #336699;"); //背景色
        mid.setCenter(DangerAlarmLamp);
        right.setPadding(new Insets(10, 5, 10, 15));
        right.setStyle("-fx-background-color: #336699;"); //背景色
        right.setSpacing(10);
        right.setAlignment(Pos.CENTER);
        right.getChildren().addAll(minSpeed, new Label("~"), maxSpeed);
        minSpeed.setMaxWidth(70);
        maxSpeed.setMaxWidth(70);
        // 添加到Group
        ToggleGroup g1 = new ToggleGroup();
        ToggleGroup g2 = new ToggleGroup();
        HighBeam.setToggleGroup(g1);
        LowBeam.setToggleGroup(g1);
        LeftTurningLamp.setToggleGroup(g2);
        RightTurningLamp.setToggleGroup(g2);
        //添加边框
        // 添加到主面板
        root.setCenter(hBox);
    }

    private RadioButton conditionCold = new RadioButton(), conditionWarm = new RadioButton(), conditionClose = new RadioButton();
    private TextField remainMile = new TextField(), Battery = new TextField();
    private Slider blowingLevel = new Slider();
    private ToggleButton HighBeam = new ToggleButton("远光灯"), LowBeam = new ToggleButton("近光灯");
    private ToggleButton LeftTurningLamp = new ToggleButton("左转灯"), RightTurningLamp = new ToggleButton("右转灯");
    private ToggleButton FrontFogLamp = new ToggleButton("前雾灯"), RearFogLamp = new ToggleButton("后雾灯");
    private ToggleButton DangerAlarmLamp = new ToggleButton("危险警报");
    private TextField minSpeed = new TextField(), maxSpeed = new TextField();

    // 添加监听
    private void addListener() {
        // 空调的三个选项
        conditionCold.setOnAction(new conditionListener());
        conditionWarm.setOnAction(new conditionListener());
        conditionClose.setOnAction(new conditionListener());
        // 七个灯
        HighBeam.setOnAction(new lampListener());
        LowBeam.setOnAction(new lampListener());
        LeftTurningLamp.setOnAction(new lampListener());
        RightTurningLamp.setOnAction(new lampListener());
        FrontFogLamp.setOnAction(new lampListener());
        RearFogLamp.setOnAction(new lampListener());
        DangerAlarmLamp.setOnAction(new lampListener());
        // 空调风速
        blowingLevel.valueProperty().addListener((ov, old_val, new_val) -> {
            if (!conditionClose.isSelected()) {
                handler.modifyAndSend(BCM1.toString(), BCM_ACBlowingLevel.toString(), new_val.intValue());
            }
        });
        // 剩余里程
        remainMile.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int value = Integer.parseInt(remainMile.getText());
                    handler.modifyAndSend(VCU2.toString(), can_RemainKm.toString(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //电量
        Battery.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int value = Integer.parseInt(Battery.getText());
                    handler.modifyAndSend(BMS1.toString(), BMS_SOC.toString(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // 速度
        maxSpeed.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    minSpeed.setDisable(true);
                    maxSpeed.setDisable(true);
                    int min = Integer.parseInt(minSpeed.getText());
                    int max = Integer.parseInt(maxSpeed.getText());
                    nowSpeedThread = new Thread(() -> {
                        for (int i = min; i <= max; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.modifyAndSend(ESC3.toString(), Wheel_Speed_ABS.toString(), i);
                        }
                        Platform.runLater(() -> {
                            minSpeed.setDisable(false);
                            maxSpeed.setDisable(false);
                        });
                    });
                    nowSpeedThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class conditionListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            double value = 3;
            if (conditionClose.isSelected()) {
                value = 2;
                handler.modifyAndSend(BCM1.toString(), BCM_ACBlowingLevel.toString(), 0);
                blowingLevel.setValue(0);
            } else if (conditionCold.isSelected()) {
                value = 0;
            } else if (conditionWarm.isSelected()) {
                value = 1;
            }
            handler.modifyAndSend(VCU1.toString(), VCU_ACWorkingStatus.toString(), value);
        }
    }

    private class lampListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_LeftTurningLamp.toString(), boolToInt(LeftTurningLamp.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_RightTurningLamp.toString(), boolToInt(RightTurningLamp.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_HighBeam.toString(), boolToInt(HighBeam.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_LowBeam.toString(), boolToInt(LowBeam.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_FrontFogLamp.toString(), boolToInt(FrontFogLamp.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_RearFogLamp.toString(), boolToInt(RearFogLamp.isSelected()));
            handler.modifyCommand(BCM1.toString(), BCM_Flg_Stat_DangerAlarmLamp.toString(), boolToInt(DangerAlarmLamp.isSelected()));
            handler.sendCommand(BCM1.toString());
        }
    }

    private int boolToInt(boolean value) {
        return value ? 1 : 0;
    }


    // 接收CLient发送的数据
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        System.out.println(jsonObject);
        String msgName = jsonObject.getString("msg_name");
        String signalName = jsonObject.getString("signal_name");
        double value = jsonObject.getDoubleValue("value");
        Platform.runLater(() -> {
            if (msgName != null && msgName.equals(HMI.toString())) {
                if (signalName.equals(HMI_Dig_Ord_RightTurningLamp.toString()) && value != 0) {
                    RightTurningLamp.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_RightTurningLamp.toString(), boolToInt(RightTurningLamp.isSelected()));
                } else if (signalName.equals(HMI_Dig_Ord_LeftTurningLamp.toString()) && value != 0) {
                    LeftTurningLamp.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_LeftTurningLamp.toString(), boolToInt(LeftTurningLamp.isSelected()));
                } else if (signalName.equals(HMI_Dig_Ord_DangerAlarmLamp.toString())&& value != 0) {
                    DangerAlarmLamp.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_DangerAlarmLamp.toString(), boolToInt(DangerAlarmLamp.isSelected()));
                } else if (signalName.equals(HMI_Dig_Ord_RearFogLamp.toString())&& value != 0) {
                    RearFogLamp.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_RearFogLamp.toString(), boolToInt(RearFogLamp.isSelected()));
                }  else if (signalName.equals(HMI_Dig_Ord_LoWBeam.toString())&& value != 0) {
                    LowBeam.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_LowBeam.toString(), boolToInt(LowBeam.isSelected()));
                } else if (signalName.equals(HMI_Dig_Ord_air_grade.toString())&& value != 0) {
                    blowingLevel.setValue(value);
                } else if (signalName.equals(HMI_Dig_Ord_FrontFogLamp.toString())&& value != 0) {
                    FrontFogLamp.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_FrontFogLamp.toString(), boolToInt(FrontFogLamp.isSelected()));
                } else if (signalName.equals(HMI_Dig_Ord_HighBeam.toString())&& value != 0) {
                    HighBeam.setSelected(value == 2);
                    handler.modifyAndSend(BCM1.toString(), BCM_Flg_Stat_HighBeam.toString(), boolToInt(HighBeam.isSelected()));
                }else if (signalName.equals(HMI_Dig_Ord_air_model.toString())&& value != 3) {
                    switch ((int) value){
                        case 0:
                            conditionCold.setSelected(true);
                            blowingLevel.setDisable(false);
                            break;
                        case 1:
                            conditionWarm.setSelected(true);
                            blowingLevel.setDisable(false);
                            break;
                        case 2:
                            conditionClose.setSelected(true);
                            blowingLevel.setValue(0);
                            blowingLevel.setDisable(true);
                            break;
                    }
                }
            }
        });
    }
}
