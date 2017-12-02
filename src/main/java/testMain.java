import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class testMain {
    public static void main(String[] args) {
        final short type = 689;
        final String roomid = "74751";
        final TcpClient tcpClient = TcpClient.getInstance();
        tcpClient.startClient("openbarrage.douyutv.com", 8601);
        MessageBean mb1 = new MessageBean(type, "type@=loginreq/roomid@=" + roomid + "/");
        MessageBean mb2 = new MessageBean(type, "type@=joingroup/rid@=" + roomid + "/gid@=-9999/");
        //输出服务器信息
        new Thread(new Runnable() {
            public void run() {
                InputStream in = tcpClient.getIn();
                while (true) {
                    try {
                        MessageBean msgb = new MessageBean(in);
                        String content = msgb.getContentString();
                        if (!"".equals(content)&&content.substring(0,13).equals("type@=chatmsg"))
                            Logger.getLogger("数据接收").log(Level.INFO,content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        tcpClient.send(mb1);
        tcpClient.send(mb2);

        //定时心跳包
//        new Thread(new Runnable() {
//            public void run() {
//                while(true){
//                    tcpClient.send(new MessageBean(type,"type@=mrkl/"));
//                    try {
//                        sleep(30000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Logger.getLogger("心跳包出错").log(Level.INFO,"发送出错");
//                    }
//
//                }
//            }
//        }).start();
    }
}
