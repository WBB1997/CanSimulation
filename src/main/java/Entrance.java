import Util.ByteUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Util.ByteUtil.Motorola;

public class Entrance {
    private final static int receivePort = 9988;
    private final static int sendPort = 8888;
    private Thread receiveThread;
    private byte[] orgBytes = {0x00, (byte) 0x80, 0x03, 0x00, 0x00, 0x00, 0x00, 0x1f};

    public static void main(String[] args) {
        new Entrance();
    }

    public Entrance() {
        receiveThread = new Thread(new receiveClass());
        receiveThread.start();
    }

    private class receiveClass implements Runnable {
        public void run() {
            System.out.println("receiveThread start");
            try {
                DatagramSocket ds = new DatagramSocket(receivePort);
                while (true) {
                    byte[] bys = new byte[8];
                    DatagramPacket dp = new DatagramPacket(bys, bys.length);
                    ds.receive(dp);
                    System.out.println(ByteUtil.bytesToHex(dp.getData()));
                    dealWith(dp.getData());
                    orgBytes = dp.getData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 处理收到的Bytes
    private void dealWith(byte[] bytes) {
        if (compare(bytes, 0, 2)) {
            System.out.println("远光灯 " + getState(bytes, 0, 2));
        }
        if (compare(bytes, 2, 2)) {
            System.out.println("近光灯 " + getState(bytes, 2, 2));
        }
        if (compare(bytes, 4, 2)) {
            System.out.println("左转向灯 " + getState(bytes, 4, 2));
        }
        if (compare(bytes, 6, 2)) {
            System.out.println("右转向灯 " + getState(bytes, 6, 2));
        }
        if (compare(bytes, 8, 2)) {
            System.out.println("后雾灯 " + getState(bytes, 8, 2));
        }
        if (compare(bytes, 10, 2)) {
            System.out.println("门锁控制 " + getState(bytes, 10, 2));
        }
        if (compare(bytes, 12, 2)) {
            System.out.println("低速报警 " + getState(bytes, 12, 2));
        }
        if (compare(bytes, 14, 2)) {
            double count = countBit(bytes, 14, 2);
            if (count == 0)
                System.out.println("自动驾驶模式");
            else if (count == 1)
                System.out.println("远程驾驶模式");
        }
        if (compare(bytes, 16, 2)) {
            int count = (int) countBit(bytes, 16, 2);
            switch (count) {
                case 0:
                    System.out.println("空调制冷模式");
                    break;
                case 1:
                    System.out.println("空调制热模式");
                    break;
                case 2:
                    System.out.println("空调关闭模式");
                    break;
            }
        }
        if (compare(bytes, 18, 3)) {
            int count = (int) countBit(bytes, 18, 3);
            switch (count) {
                case 0:
                    System.out.println("空调档位OFF");
                    break;
                case 1:
                    System.out.println("空调档位1档");
                    break;
                case 2:
                    System.out.println("空调档位2档");
                    break;
                case 3:
                    System.out.println("空调档位3档");
                    break;
                case 4:
                    System.out.println("空调档位4档");
                    break;
                case 5:
                    System.out.println("空调档位5档");
                    break;
            }
        }
        if (compare(bytes, 21, 1)) {
            System.out.println("低速报警 " + (getState(bytes, 21, 1).equals("关") ? "制动液面报警" : "制动液面正常"));
        }
        if (compare(bytes, 22, 2)) {
            System.out.println("危险报警灯 " + getState(bytes, 22, 2));
        }
        if (compare(bytes, 24, 8)) {
            System.out.println("风扇转速占比 " + countBit(bytes, 24, 8) + "%");
        }
        if (compare(bytes, 38, 2)) {
            System.out.println("除雾控制 " + getState(bytes, 38, 2));
        }
        if (compare(bytes, 36, 2)) {
            System.out.println("低速报警 " + (getState(bytes, 36, 2).equals("开") ? "HMI系统运行正常" : "HMI系统故障"));
        }
        if (compare(bytes, 48, 20)) {
            System.out.println("总里程 " + countBit(bytes, 48, 20) + "KM");
        }
        if (compare(bytes, 62, 2)) {
            int count = (int) countBit(bytes, 62, 2);
            switch (count) {
                case 0:
                    System.out.println("启动自动泊车");
                    break;
                case 1:
                    System.out.println("停止自动泊车");
                    break;
            }
        }
        if (compare(bytes, 61, 1)) {
            int count = (int) countBit(bytes, 61, 1);
            switch (count) {
                case 0:
                    System.out.println("本地输入播放");
                    break;
                case 1:
                    System.out.println("外部输入播放");
                    break;
            }
        }
        if (compare(bytes, 56, 5)) {
            int count = (int) countBit(bytes, 56, 5);
            switch (count) {
                case 0:
                    System.out.println("静音");
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                    System.out.println("音量等级 " + count);
                    break;
            }
        }
    }

    private double countBit(byte[] bytes, int startBitIndex, int bitLength){
        return ByteUtil.countBit(bytes,0,startBitIndex,bitLength,Motorola);
    }

    private boolean compare(byte[] bytes, int startBitIndex, int bitLength) {
        return ByteUtil.countBit(bytes, 0, startBitIndex, bitLength, Motorola) != ByteUtil.countBit(orgBytes, 0, startBitIndex, bitLength, Motorola);
    }

    private String getState(byte[] bytes, int startBitIndex, int bitLength){
        if(ByteUtil.countBit(bytes, 0, startBitIndex, bitLength, Motorola) == 1)
            return "关";
        else if(ByteUtil.countBit(bytes, 0, startBitIndex, bitLength, Motorola) == 2)
            return "开";
        return "";
    }

    public void send(byte[] bytes) {
        try {
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, inet, sendPort);
            DatagramSocket ds = new DatagramSocket();
            ds.send(dp);
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
