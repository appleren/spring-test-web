package com.bjmashibing.system.io;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class OSFileIO {

    static byte[] data = "123456789\n".getBytes();
    static String path = "/root/testfileio/out.txt";


    public static void main(String[] args) throws Exception {


        switch (args[0]) {
            case "0":
                testBasicFileIO();
                break;
            case "1":
                testBufferedFileIO();
                break;
            case "2":
                testRandomAccessFileWrite();
            case "3":
//                whatByteBuffer();
            default:

        }
    }


    //最基本的file写

    public static void testBasicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        while (true) {
            Thread.sleep(10);
            out.write(data);

        }

    }

    //测试buffer文件IO
    //  jvm  8kB   syscall  write(8KBbyte[])

    public static void testBufferedFileIO() throws Exception {
        File file = new File(path);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        while (true) {
            Thread.sleep(10);
            out.write(data);
        }
    }


    //测试文件NIO
    public static void testRandomAccessFileWrite() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        raf.write("hello mashibing\n".getBytes());
        raf.write("hello seanzhou\n".getBytes());
        System.out.println("write------------");
        System.in.read();

        //seek设置偏移量指针到4这个位置，然后从者个位置开始写数据，体现出随机读写能力
        raf.seek(4);
        raf.write("ooxx".getBytes());

        System.out.println("seek---------");
        System.in.read();

        //文件通道。只有文件上的通道存在.map的，只有文件才能做内存映射
        FileChannel rafchannel = raf.getChannel();
        //mmap  堆外  和文件映射到内核的pagecache的   byte  not  objtect
        // mmaped映射：是mmap调用的一个进程和内核共享的内存区域且这个内存区域是pagecache 到文件的映射
        //在堆上性能小于堆外小于mmap映射，当然只限于file文件
        MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);

        map.put("@@@".getBytes());  //不是系统调用  但是数据会到达 内核的pagecache
        //曾经我们是需要out.write()  这样的系统调用，才能让程序的data 进入内核的pagecache
        //曾经必须有用户态内核态切换
        //mmap的内存映射，依然是内核的pagecache体系所约束的！！！
        //换言之，丢数据
        //你可以去github上找一些 其他C程序员写的jni扩展库，使用linux内核的Direct IO
        //直接IO是忽略linux的pagecache
        //是把pagecache  交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性/dirty(脏)。。。一系列复杂问题

        System.out.println("map--put--------");
        System.in.read();
        //强制刷写
        map.force(); //  flush
        raf.seek(0);
        ByteBuffer buffer = ByteBuffer.allocate(8192);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        int read = rafchannel.read(buffer);   //buffer.put()
        System.out.println(buffer);
        buffer.flip();
        System.out.println(buffer);

        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.print(((char) buffer.get(i)));
        }


    }


    @Test
    public void whatByteBuffer() {
        /**
         * 分配到堆内上，需要走到堆外，然后走系统调用
         * 堆内：说的是jvm的堆里的字节数组
         * 堆外：多的是jvm堆外，也就是java进程的堆里的
         *
         */
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /**
         * 分配到堆外,性能稍高一些
         */
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


        System.out.println("postition: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer);

        buffer.put("123".getBytes());

        System.out.println("-------------put:123......");
        System.out.println("mark: " + buffer);

        buffer.flip();   //读写交替

        System.out.println("-------------flip......");
        System.out.println("mark: " + buffer);

        buffer.get();

        System.out.println("-------------get......");
        System.out.println("mark: " + buffer);

        buffer.compact();

        System.out.println("-------------compact......");
        System.out.println("mark: " + buffer);

        buffer.clear();

        System.out.println("-------------clear......");
        System.out.println("mark: " + buffer);

    }


}