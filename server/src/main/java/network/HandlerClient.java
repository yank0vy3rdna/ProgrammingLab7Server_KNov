package network;

import communication.Request;
import communication.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class HandlerClient {
    public final int SIZE_BUFFER = 100000;

    private SocketAddress socketAddress;
    private DatagramChannel datagramChannel;

    public void bind(int port) throws IOException {
        socketAddress = new InetSocketAddress(port);
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(socketAddress);
    }

    protected void send(byte[] bytes, SocketAddress address) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        datagramChannel.send(byteBuffer, address);
    }

    public void sendResponses(ArrayList<Response> responses, SocketAddress address) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(responses);
        objectOutputStream.flush();
        objectOutputStream.close();
        send(byteArrayOutputStream.toByteArray(), address);
    }

    public void sendAddressedResponse(AddressedResponse response) throws IOException {
        sendResponses(response.getResponses(), response.getSocketAddress());
    }

    protected byte[] receive() throws IOException {
        SocketAddress receivedAddress;
        ByteBuffer byteBuffer = ByteBuffer.allocate(SIZE_BUFFER);
        do {
            receivedAddress = datagramChannel.receive(byteBuffer);
        } while (receivedAddress == null);
        socketAddress = receivedAddress;
        byteBuffer.flip();
        byte[] data = new byte[byteBuffer.limit()];
        byteBuffer.get(data, 0, byteBuffer.limit());
        return data;
    }

    protected byte[] receive(int delay) throws IOException {
        SocketAddress receivedAddress;
        long startTime = System.currentTimeMillis();
        ByteBuffer byteBuffer = ByteBuffer.allocate(SIZE_BUFFER);
        do {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= delay) {
                return new byte[0];
            }
            receivedAddress =  datagramChannel.receive(byteBuffer);
        } while (receivedAddress == null);
        socketAddress = receivedAddress;
        byteBuffer.flip();
        byte[] data = new byte[byteBuffer.limit()];
        byteBuffer.get(data, 0, byteBuffer.limit());
        return data;
    }

    public ArrayList<Request> receiveRequests() throws IOException, ClassNotFoundException {
        return receiveRequests(0);
    }

    public ArrayList<Request> receiveRequests(int delay) throws IOException, ClassNotFoundException {
        byte[] bytes;
        if (delay == 0) {
            bytes = receive();
        }
        else {
            bytes = receive(delay);
        }
        if(bytes.length == 0) {
            return new ArrayList<>();
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        ArrayList<Request> requests = (ArrayList<Request>) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return requests;
    }

    public AddressedRequest receiveAddressedRequest() throws IOException, ClassNotFoundException {
        AddressedRequest addressedRequest = new AddressedRequest(receiveRequests());
        addressedRequest.setSocketAddress(socketAddress);
        return addressedRequest;
    }

}
