import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.ref.Reference;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: angel
 * Date: 2023-07-20
 * Time: 22:11
 */
public class Server {
    public static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static void main(String[] args) {
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);//负责客户端的链接
        EventLoopGroup workerGroup=new NioEventLoopGroup(2);//负责处理每个客户端的信息

        try {
            ServerBootstrap b=new ServerBootstrap();
            ChannelFuture f=b.group(bossGroup, workerGroup)//指定线程池的两个组
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p1=ch.pipeline();//处理客户端的通道
                            p1.addLast(new ServerChildHandler());
                        }
                    })//对Client的处理
                    .bind(8888)//监听8888端口
                    .sync();//等待完成

            System.out.println("Sever started");

            //图形用户界面摁下某个按钮的时候才调用close
            f.channel().closeFuture().sync();//close()->ChannelFuture

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

class  ServerChildHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf=null;
        try{
            buf = (ByteBuf) msg;

            byte[]bytes=new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));
            Server.clients.writeAndFlush(msg);

//            System.out.println(buf);
//            System.out.println(buf.refCnt());
        }finally {
//            if(buf!=null) ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}