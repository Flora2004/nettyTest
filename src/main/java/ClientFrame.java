import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: angel
 * Date: 2023-07-20
 * Time: 23:29
 */
public class ClientFrame extends Frame {
    public static final ClientFrame INSTANCE=new ClientFrame();
    TextArea ta=new TextArea();
    TextField tf=new TextField();

    Client c=null;

    public ClientFrame(){
        this.setSize(600,400);
        this.setLocation(100,20);
        this.add(ta,BorderLayout.CENTER);
        this.add(tf,BorderLayout.SOUTH);

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.send(tf.getText());
//                ta.setText(ta.getText()+tf.getText());
                tf.setText("");
            }
        });
//        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                c.closeConnect();
                System.exit(0);
            }
        });

    }
    private void connectToServe(){
        c=new Client();
        c.connect();
    }

    public static void main(String[] args) {
        ClientFrame frame=ClientFrame.INSTANCE;
        frame.setVisible(true);
        frame.connectToServe();
    }
    public void updateText(String msgAccepted){
        this.ta.setText(ta.getText()+"\n"+msgAccepted);
    }
}
