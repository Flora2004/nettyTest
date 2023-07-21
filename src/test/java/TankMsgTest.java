import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: angel
 * Date: 2023-07-21
 * Time: 10:59
 */
public class TankMsgTest {
    public void testTankMsgEncoder(){
        TankMsg msg=new TankMsg(10,10);
        EmbeddedChannel ch=new EmbeddedChannel(new TankMsgEncoder());
        ch.writeOutbound(msg);

        ByteBuf buf=(ByteBuf) ch.readOutbound();
        int x=buf.readInt();
        int y=buf.readInt();

        assert(x==10&&y==10);
        buf.release();
    }
    public void testTankMsgEncoder2(){
        ByteBuf buf= Unpooled.buffer();
        TankMsg msg=new TankMsg(10,10);
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);

        EmbeddedChannel ch=new EmbeddedChannel(new TankMsgEncoder(),new TankMsgDecoder());
        ch.writeInbound(buf.duplicate());

        TankMsg tm=(TankMsg) ch.readInbound();

        assert (tm.x==10&&tm.y==10);
    }
}
