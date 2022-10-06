package com.swrobotics.lidar.lib;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class Lidar implements SerialPortDataListener {
    // Read/write will time out if they take more than 3 seconds
    private static final int TIMEOUT = 3000;

    private static final byte START_FLAG_1 = (byte) 0xA5;
    private static final byte START_FLAG_2 = 0x5A;

    private static final byte REQ_SCAN = 0x20;
    private static final byte REQ_STOP = 0x25;
    private static final byte REQ_RESET = 0x40;
    private static final byte REQ_GET_HEALTH = 0x52;

    private static final byte RES_SCAN = (byte) 0x81;

    private static final byte SEND_MODE_SRSR = 0x00;
    private static final byte SEND_MODE_SRMR = 0x01;
    private static final byte SEND_MODE_RES1 = 0x02;
    private static final byte SEND_MODE_RES2 = 0x03;

    private enum ReadState {
        SKIP, // Used when not reading
        NONE, // Used when reading single response, effectively disables the data listener
        RESPONSE // Used when reading repeated response
    }

    private enum SendMode {
        SINGLE_REQ_SINGLE_RES,
        SINGLE_REQ_MULTI_RES,
        RESERVED_1,
        RESERVED_2
    }

    private final SerialPort port;
    private ReadState readState;
    private byte[] responseBuf;
    private int responseIdx;
    private byte responseType;

    private boolean scanning;
    private Runnable scanCb;
    private Consumer<LidarScanEntry> dataCb;

    public Lidar(SerialPort port) {
        this.port = port;

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, TIMEOUT, TIMEOUT);
        port.setBaudRate(115200);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(1);
        port.setNumDataBits(8);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, TIMEOUT, TIMEOUT);
        if (!port.openPort()) {
            throw new RuntimeException("Failed to open serial port");
        }
        port.addDataListener(this);

        respDesc = new ResponseDescriptor();
    }

    public void reset() {
        request(REQ_RESET);
        scanning = false;

        timedBusy(2);
    }

    public CompletableFuture<LidarHealth> getHealth() {
        if (scanning)
            throw new IllegalStateException("Cannot get health while scanning");

        return null;
    }

    public void setMotorEnable(boolean on) {
        if (on)
            port.clearDTR();
        else
            port.setDTR();
    }

    public boolean beginScan(Runnable scanCb, Consumer<LidarScanEntry> dataCb) {
        this.scanCb = scanCb;
        this.dataCb = dataCb;

        request(REQ_SCAN);
        if (!readResponseDesc(RES_SCAN, "SCAN", 5, SendMode.SINGLE_REQ_MULTI_RES))
            return false;

        scanning = true;
        return true;
    }

    public void endScan() {
        request(REQ_STOP);
        scanning = false;
        timedBusy(1);
    }

    public boolean isScanning() {
        return scanning;
    }

    private void request(byte id) {
        byte[] data = {
                START_FLAG_1,
                id
        };
        port.writeBytes(data, data.length);
    }

    private boolean tryRead(byte[] dst) {
        int read = port.readBytes(dst, dst.length);
        return read == dst.length;
    }

    private boolean readResponseDesc(byte expectedType, String expectedTypeName, int expectedLen, SendMode expectedSendMode) {
        byte[] data = new byte[7];
        if (!tryRead(data)) {
            System.err.println("Failed to fully read response descriptor");
            return false;
        }

        if (data[0] != START_FLAG_1) {
            System.err.println("Response descriptor start flag 1 incorrect");
            return false;
        }

        if (data[1] != START_FLAG_2) {
            System.err.println("Response descriptor start flag 2 incorrect");
            return false;
        }

        int len = data[2] & 0xFF;
        len |= (data[3] & 0xFF) << 8;
        len |= (data[4] & 0xFF) << 16;
        len |= (data[5] & 0x3F) << 24;

        byte sendMode = (byte) ((data[5] & 0xC0) >> 6);
        SendMode send;
        switch (sendMode) {
            case SEND_MODE_SRSR: send = SendMode.SINGLE_REQ_SINGLE_RES; break;
            case SEND_MODE_SRMR: send = SendMode.SINGLE_REQ_MULTI_RES; break;
            case SEND_MODE_RES1: send = SendMode.RESERVED_1; break;
            case SEND_MODE_RES2: send = SendMode.RESERVED_2; break;
            default:
                throw new AssertionError("Somehow bitwise operators are not functioning right");
        }

        responseType = data[6];
        readState = ReadState.SKIP;

        if (len != expectedLen) {
            System.err.println("Response descriptor has incorrect data length: expected " + expectedLen + ", found " + len);
            return false;
        }

        if (responseType != expectedType) {
            System.err.println("Response descriptor has incorrect type: expected " + expectedType + " (" + expectedTypeName + "), found " + responseType);
            return false;
        }

        if (send != expectedSendMode) {
            System.err.println("Response descriptor has incorrect send mode: expected " + expectedSendMode + ", found " + send);
            return false;
        }

        responseBuf = new byte[len];
        if (send == SendMode.SINGLE_REQ_SINGLE_RES) {
            readState = ReadState.NONE;

            // Read the response immediately
            if (!tryRead(responseBuf)) {
                System.err.println("Failed to fully read immediate response buffer");
                readState = ReadState.SKIP;
                return false;
            }
        } else {
            readState = ReadState.RESPONSE;
            responseIdx = 0;
        }

        return true;
    }

    private void timedBusy(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleResponseScan() {
        byte[] d = responseBuf;
        boolean start = (d[0] & 0x01) != 0;
        boolean invStart = (d[0] & 0x02) != 0;
        if (start && invStart || (!start && !invStart)) {
            System.err.println("Inverse start bit check failed");
            // Just keep trying to scan, it could just be a temporary read error
        }


    }

    private void handleAsyncResponse() {
        switch (responseType) {
            case RES_SCAN:
                handleResponseScan();
                break;
        }
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (readState) {
            case SKIP: {
                int avail = port.bytesAvailable();
                if (avail < 0) return;

                byte[] buf = new byte[avail];
                if (!tryRead(buf))
                    System.err.println("Failed to fully read available data");
                break;
            }
            case RESPONSE: {
                while (port.bytesAvailable() > 0) {
                    byte[] inBuffer = new byte[responseBuf.length - responseIdx];
                    int read = port.readBytes(inBuffer, inBuffer.length);
                    if (read < 0)
                        return;

                    System.arraycopy(inBuffer, 0, responseBuf, responseIdx, read);
                    responseIdx += read;

                    if (responseIdx >= responseBuf.length) {
                        handleAsyncResponse();
                    }
                }
            }
        }
    }
}
