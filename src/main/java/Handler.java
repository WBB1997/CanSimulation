import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import eventbus.MessageWrap;
import eventbus.MyEventBus;
import huat.wubeibei.candataconvert.DataConvert;
import huat.wubeibei.candataconvert.JSONStreamListener;
import huat.wubeibei.candataconvert.command.MessageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler {
    private DataConvert dataConvert;
    private final static int receivePort = 8888;
    private final static int sendPort = 9988;
        private final static String CANIp = "192.168.1.60"; // CAN总线IP地址
    private final static int MessageLength = 10;
    private final Thread CanReceiveThread = new Thread(new CanReceive()); // CAN总线接收线程
    private final HashMap<String, ExecutorService> MapThreadPool = new HashMap<String, ExecutorService>(){ // 接收线程池Map
        {
            put(MessageName.HMI.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.BCM1.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.VCU1.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.VCU2.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.BMS1.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.ESC3.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.HAD5.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.AD1.toString(), Executors.newSingleThreadExecutor());
            put(MessageName.AD4.toString(), Executors.newSingleThreadExecutor());
        }
    };
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(); // 发送线程池，保持发送顺序


    // 初始化
    public Handler() {
        try {
            dataConvert = new DataConvert(new FileInputStream(new File("src/main/resources/MessageLayout.xml")));
            System.out.println("CanReceiveThread Start listening!");
            // 启动接收
            CanReceiveThread.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 向服务器发送消息
    private void sendData(final String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String msgName = jsonObject.getString("msg_name");
        if (MapThreadPool.containsKey(msgName)) {
            MapThreadPool.get(msgName).execute(() -> {
                try {
                    // 获取需要发送的Byte
                    byte[] bytes = dataConvert.getByte(msgName);
                    DatagramPacket datagramPacket;
                    DatagramSocket datagramSocket = new DatagramSocket();
                    datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(CANIp), sendPort);
                    datagramSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 修改信号值
    private void setSignalValue(String msgName, String signalName, double value){
        dataConvert.setSignalValue(msgName,signalName,value);
    }

    // 接收UI界面发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        JSONObject data = jsonObject.getJSONObject("data");
        System.out.println("CanService EventBus receive: " + jsonObject);
        switch (action) {
            case "send":
                sendData(data.toJSONString());
                break;
            case "modify":
                setSignalValue(data.getString("msg_name"), data.getString("signal_name"), data.getDoubleValue("value"));
                break;
        }
    }

    // 接收主机发送的消息
    private class CanReceive implements Runnable {
        @Override
        public void run() {
            byte[] receiveMsg = new byte[MessageLength];
            DatagramSocket datagramSocket;
            DatagramPacket datagramPacket;
            try {
                datagramSocket = new DatagramSocket(receivePort);
                while (true) {
                    datagramPacket = new DatagramPacket(receiveMsg, receiveMsg.length);
                    datagramSocket.receive(datagramPacket);
                    dataConvert.getJSONString(receiveMsg, new JSONStreamListener() {
                        @Override
                        public void produce(String json) {
                            // 产生JSON数据流，放入EventBus，转发给UI界面
                            MyEventBus.post(MessageWrap.getBean(json));
                        }

                        @Override
                        public void onComplete() {
                            // 解析完成的动作
                        }

                        @Override
                        public void onError(Throwable e) {
                            // 出现异常的动作
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
