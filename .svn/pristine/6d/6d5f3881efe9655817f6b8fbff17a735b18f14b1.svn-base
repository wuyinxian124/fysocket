package edu.fy.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
 
public class ServerTest5_1 {
    private static String clientChannel = "clientChannel";
    private static String serverChannel = "serverChannel";
    private static String channelType = "channelType";
 
    /**
     * ServerSocketChannel represents a channel for sockets that listen to
     * incoming connections.
     *
     * @throws IOException
     */
    public static void main(String[] args) {
    	try {
			new ServerTest5_1().statrt();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    private void statrt() throws IOException{

        int port = 8877;
        String localhost = "localhost";
 
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(localhost, port));
        channel.configureBlocking(false);
        Selector selector = Selector.open();
 
        SelectionKey socketServerSelectionKey = channel.register(selector,
                SelectionKey.OP_ACCEPT);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(channelType, serverChannel);
        socketServerSelectionKey.attach(properties);
        
        for (;;) {
 
            if (selector.select() == 0)
                continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (((Map<String, String>) key.attachment()).get(channelType).equals(
                        serverChannel)) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
                            .channel();
                    SocketChannel clientSocketChannel = serverSocketChannel
                            .accept();
 
                    if (clientSocketChannel != null) {
                        clientSocketChannel.configureBlocking(false);
                        SelectionKey clientKey = clientSocketChannel.register(
                                selector, SelectionKey.OP_READ,
                                SelectionKey.OP_WRITE);
                        
                        Map<String, String> clientproperties = new HashMap<String, String>();
                        clientproperties.put(channelType, clientChannel);
                        clientKey.attach(clientproperties);
 
                        CharBuffer buffer = CharBuffer.wrap("Hello client");
                        while (buffer.hasRemaining()) {
                            clientSocketChannel.write(Charset.defaultCharset()
                                    .encode(buffer));
                        }
                        buffer.clear();
                    }
 
                } else {
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    int bytesRead = 0;
                    if (key.isReadable()) {
                        if ((bytesRead = clientChannel.read(buffer)) > 0) {
                            buffer.flip();
                            System.out.println(Charset.defaultCharset().decode(
                                    buffer));
                            System.out.println("");
                            buffer.clear();
                        }
                        if (bytesRead < 0) {
                            clientChannel.close();
                        }
                    }
 
                }
 
                iterator.remove();
 
            }
        }
 
    
    }
}