package com.bjmashibing.system.io;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class SocketNIO {

    public static void main(String[] args) throws Exception {

        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false); //重点  OS  NONBLOCKING!!! 设置非阻塞  ，为true就是设置为阻塞

        ss.setOption(StandardSocketOptions.TCP_NODELAY, false);
//        StandardSocketOptions.TCP_NODELAY
//        StandardSocketOptions.SO_KEEPALIVE
//        StandardSocketOptions.SO_LINGER
//        StandardSocketOptions.SO_RCVBUF
//        StandardSocketOptions.SO_SNDBUF
//        StandardSocketOptions.SO_REUSEADDR




        while (true) {
            Thread.sleep(1000);
            //因为ss.configureBlocking(false);所以不会阻塞！！  OS是-1  java中返回NULL
            //r如果来客户端的连接，accept 返回的是这个客户端的fd（文件描述符）
            //NONBLOCKING 就是代码能往下面走，只不过有不同的情况
            //此处是连接socket
            // BIO就是阻塞代码就不会往下走，一直等
            SocketChannel client = ss.accept();

            if (client == null) {
                System.out.println("null.....");
            } else {
                //重点 socket（服务端的listen socket<连接请求三次握手后，往我这里扔，
                // 我去通过accept得到连接的socket>，连接socket<连接后的数据读写使用的>）
                //client.configureBlocking(false) 为了读取数据不阻塞
                client.configureBlocking(false);//设置为false时
                int port = client.socket().getPort();
                System.out.println("client...port: " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  //可以在堆里   堆外

            for (SocketChannel c : clients) {   //串行化！！！！  多线程！！
                int num = c.read(buffer);  // >0  -1  0   //不会阻塞
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + " : " + b);
                    buffer.clear();
                }


            }
        }
    }

}
