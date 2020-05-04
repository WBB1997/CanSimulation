import eventbus.MessageWrap;
import eventbus.MyEventBus;
import huat.wubeibei.candataconvert.DataConvert;
import huat.wubeibei.candataconvert.JSONStreamListener;
import huat.wubeibei.candataconvert.command.MessageName;
import util.ByteUtil;

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
    private final static int sendPort = 9999;
    private final static String ServiceIp = "192.168.0.101"; // ServiceIp
    private final static int MessageLength = 10;
    private final Thread CanReceiveThread = new Thread(new CanReceive()); // Service接收线程
    private final HashMap<String, ExecutorService> MapThreadPool = new HashMap<String, ExecutorService>() { // 接收线程池Map
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
    private void sendData(final String msg_name) {
        if (MapThreadPool.containsKey(msg_name)) {
            MapThreadPool.get(msg_name).execute(() -> {
                try {
                    // 获取需要发送的Byte
                    byte[] bytes = dataConvert.getByte(msg_name);
                    DatagramPacket datagramPacket;
                    DatagramSocket datagramSocket = new DatagramSocket();
                    datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ServiceIp), sendPort);
                    datagramSocket.send(datagramPacket);
                    System.out.println("Send->" + ServiceIp + ": " + ByteUtil.bytesToHex(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 发送修改信号值数据
    public void modifyCommand(String msg_name, String signal_name, double value) {
        dataConvert.setSignalValue(msg_name, signal_name, value);
    }

    // 发送send数据
    public void sendCommand(String msg_name) {
        sendData(msg_name);
    }

    // 发送ModifyandSend数据
    public void modifyAndSend(String msg_name, String signal_name, double value) {
        modifyCommand(msg_name, signal_name, value);
        sendCommand(msg_name);
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
                    System.out.println("Receive->" + datagramPacket.getAddress() + ": " + ByteUtil.bytesToHex(receiveMsg));
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
