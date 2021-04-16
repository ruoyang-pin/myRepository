package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.bson.io.Bits;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class NioClientTest {
    private final String host;
    private final int port;

    public NioClientTest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();


        } finally {
             group.shutdownGracefully().sync();
        }
        

    }

    public static void main(String[] args) throws Exception {
        new NioClientTest("localhost", 9091).start();
    }


}