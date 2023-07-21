/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: angel
 * Date: 2023-07-21
 * Time: 10:31
 */
public class TankMsg {
    public int x,y;
    public TankMsg(int x,int y){
        this.x=x;
        this.y=y;
    }
    @Override
    public String toString(){
        return "TankMsg:"+x+","+y;
    }
}
