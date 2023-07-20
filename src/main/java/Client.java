import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: angel
 * Date: 2023-07-20
 * Time: 10:37
 */
public class Client {
    public static void main(String[] args) {
        //创建group线程池,创建线程处理链接和读取
        EventLoopGroup group=new NioEventLoopGroup(1);

        Bootstrap b=new Bootstrap();

        try {//链接服务器
            ChannelFuture f=b.group(group)
                    .channel(NioSocketChannel.class)//链接到服务器的NIO非阻塞版
                    .handler(new ClientChannelInitializer())//处理出现的特殊事件
                    //异步方法，无论有没有连接数都会执行下一行代码
                    .connect("localhost",8888)
                    ;

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()){
                        System.out.println("not connected!");
                    }else {
                        System.out.println("connected!");
                    }
                }
            });

            f.sync();
            System.out.println("...");

            f.channel().closeFuture().sync();

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch)throws Exception{
        ch.pipeline().addLast(new ClientHandler());
    }
}
class  ClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(new String(bytes));

//            System.out.println(buf);
//            System.out.println(buf.refCnt());
        } finally {
            if (buf != null) ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel第一次连上可用，写出一个字符串
        ByteBuf buf= Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buf);
    }
}
