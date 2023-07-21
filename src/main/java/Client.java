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
    private Channel channel=null;
    public void connect(){
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

            f.addListener(new ChannelFutureListener() {//判断客户端是否链接到服务器
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        System.out.println("not connected!");
                    }else {
                        System.out.println("connected!");
                        //连接成功后初始化channel
                        channel=future.channel();
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
    public void send(String msg){//通过初始化好的channel，将传入的字符串写给服务器
        ByteBuf buf=Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }
    public static void main(String[] args) {
        Client c=new Client();
        c.connect();
    }
    public void closeConnect(){
        this.send("_bye_");
        //channel.close();
    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch)throws Exception{
        ch.pipeline()
                .addLast(new TankMsgEncoder())//对传出的信息进行处理
                .addLast(new ClientHandler());//对服务器传回的消息进行处理
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
            String msgAccepted=new String(bytes);
            ClientFrame.INSTANCE.updateText(msgAccepted);
//            System.out.println(buf);
//            System.out.println(buf.refCnt());
        } finally {
            if (buf != null)
                ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new TankMsg(5,8));
    }
}
