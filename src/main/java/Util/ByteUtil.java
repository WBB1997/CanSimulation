package Util;

public class ByteUtil {
    public static final int Motorola = 0;
    public static final int Intel = 1;

    //byte转16进制
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2)
                sb.append(0);
            sb.append(hex);
        }
        return sb.toString();
    }

    //16进制转byte
    private static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //16进制转byte
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    // 设置某一位
    private static void setBit(byte[] bytes, int byteOffset, int bitIndex, boolean changed) {
        int i = byteOffset + bitIndex / 8;
        int j = bitIndex % 8;
        if (changed)
            bytes[i] |= (0b00000001 << j);
        else
            bytes[i] &= ~(0b00000001 << j);
    }

    // 查看一个Byte的某位是否为1
    private static boolean viewBinary(byte Byte, int position) {
        return (Byte & 0b00000001 << position) != 0;
    }

    // 计算值
    public static double countBit(byte[] bytes, int byteOffset, int startBitIndex, int bitLength, int state) {
        double count = 0;
        if (state == Motorola) {
            int index = startBitIndex;
            for (int i = 0; i < bitLength; i++) {
                if (viewBinary(bytes[byteOffset + index / 8], index % 8)) {
                    count += Math.pow(2, i);
                }
                index++;
                if (index % 8 == 0) {
                    index -= 2 * 8;
                }
            }
        } else if (state == Intel) {
            for (int i = startBitIndex; i < startBitIndex + bitLength; i++) {
                if (viewBinary(bytes[byteOffset + i / 8], i % 8)) {
                    count += Math.pow(2, i - startBitIndex);
                }
            }
        }
        return count;
    }

    public static double countBit(byte[] bytes, int byteOffset, int startBitIndex, int bitLength, String state) {
        double count = 0;
        if (state.equals("Motorola")) {
            int index = startBitIndex;
            for (int i = 0; i < bitLength; i++) {
                if (viewBinary(bytes[byteOffset + index / 8], index % 8)) {
                    count += Math.pow(2, i);
                }
                index++;
                if (index % 8 == 0) {
                    index -= 2 * 8;
                }
            }
        } else if (state.equals("Intel")) {
            for (int i = startBitIndex; i < startBitIndex + bitLength; i++) {
                if (viewBinary(bytes[byteOffset + i / 8], i % 8)) {
                    count += Math.pow(2, i - startBitIndex);
                }
            }
        }
        return count;
    }

    public static void setBits(byte[] targetBytes, int srcNum, int byteOffset, int startBitIndex, int bitLength, int state) {
        byte[] SrcBytes = new byte[8];
        if (state == Intel) {
            for (int i = 0; i < SrcBytes.length * 8; i++) {
                if (srcNum / 2 > 0) {
                    setBit(SrcBytes, i / 8, i % 8, srcNum % 2 == 1);
                    srcNum /= 2;
                } else {
                    setBit(SrcBytes, i / 8, i % 8, srcNum % 2 == 1);
                    break;
                }
            }
            for (int i = startBitIndex; i < startBitIndex + bitLength; i++) {
                boolean flag = viewBinary(SrcBytes[(i - startBitIndex) / 8], (i - startBitIndex) % 8);
                setBit(targetBytes, byteOffset, i, flag);
            }
        } else if (state == Motorola) {
            boolean flag = true;
            for (int i = SrcBytes.length * 8 - 8; i >= 0; i -= 8) {
                for (int j = i; j - i < 8; j++) {
                    if (srcNum / 2 > 0) {
                        setBit(SrcBytes, j / 8, j % 8, srcNum % 2 == 1);
                        srcNum /= 2;
                    } else {
                        setBit(SrcBytes, j / 8, j % 8, srcNum % 2 == 1);
                        flag = false;
                        break;
                    }
                }
                if (!flag)
                    break;
            }
            int index = startBitIndex;
            for (int i = 0; i < bitLength; i++) {
                flag = viewBinary(SrcBytes[(SrcBytes.length * 8 - i - 1) / 8], i % 8);
                setBit(targetBytes, byteOffset, index, flag);
                index++;
                if (index % 8 == 0) {
                    index -= 2 * 8;
                }
            }
        }
    }

    public static void setBits(byte[] targetBytes, int srcNum, int byteOffset, int startBitIndex, int bitLength, String state) {
        byte[] SrcBytes = new byte[8];
        if (state.equals("Intel")) {
            for (int i = 0; i < SrcBytes.length * 8; i++) {
                if (srcNum / 2 > 0) {
                    setBit(SrcBytes, i / 8, i % 8, srcNum % 2 == 1);
                    srcNum /= 2;
                } else {
                    setBit(SrcBytes, i / 8, i % 8, srcNum % 2 == 1);
                    break;
                }
            }
            for (int i = startBitIndex; i < startBitIndex + bitLength; i++) {
                boolean flag = viewBinary(SrcBytes[(i - startBitIndex) / 8], (i - startBitIndex) % 8);
                setBit(targetBytes, byteOffset, i, flag);
            }
        } else if (state.equals("Motorola")) {
            boolean flag = true;
            for (int i = SrcBytes.length * 8 - 8; i >= 0; i -= 8) {
                for (int j = i; j - i < 8; j++) {
                    if (srcNum / 2 > 0) {
                        setBit(SrcBytes, j / 8, j % 8, srcNum % 2 == 1);
                        srcNum /= 2;
                    } else {
                        setBit(SrcBytes, j / 8, j % 8, srcNum % 2 == 1);
                        flag = false;
                        break;
                    }
                }
                if (!flag)
                    break;
            }
            int index = startBitIndex;
            for (int i = 0; i < bitLength; i++) {
                flag = viewBinary(SrcBytes[(SrcBytes.length * 8 - i - 1) / 8], i % 8);
                setBit(targetBytes, byteOffset, index, flag);
                index++;
                if (index % 8 == 0) {
                    index -= 2 * 8;
                }
            }
        }
    }
}
