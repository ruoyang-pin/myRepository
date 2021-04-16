package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Objects;

public class NioServerTest {
    private int port;

    public NioServerTest(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup connectGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //create serverBootstrap instance
            ServerBootstrap b = new ServerBootstrap();
            b.group(connectGroup, workGroup).channel(NioServerSocketChannel.class).localAddress(port)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch)  {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //Binds server, waits for server to close, and releases resources]
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        }finally {
            connectGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NioServerTest(9091).start();

    }


}
