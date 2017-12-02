import java.io.*;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class Main {
    private static String handleMessage(String src) {
        //处理为Map
        Map<String,String> map = Serialization.Serialization(src);
        String type = map.get("type");
        String nn = map.get("nn");
        String txt = map.get("txt");
        if (null != type && type.equals("chatmsg")) {
            if (null != nn && null != txt) {
                String res=nn + ":" + txt;
                return res;
            }
        }
        return null;
    }

    private static String getRoomId() {
        System.out.print("请输入房间Id:");
        Scanner sc = new Scanner(System.in);
        String roomId = sc.next();
        return roomId;
    }

    public static void main(String[] args) {
        final short type = 689;
        String roomid = getRoomId();
        System.out.println("roomId:"+roomid);
        System.out.println("读取中，请稍等\n==========================================");
        final TcpClient tcpClient = TcpClient.getInstance();
        tcpClient.startClient("openbarrage.douyutv.com", 8601);
        MessageBean mb1 = new MessageBean(type, "type@=loginreq/roomid@=" + roomid + "/");
        MessageBean mb2 = new MessageBean(type, "type@=joingroup/rid@=" + roomid + "/gid@=-9999/");
        //输出服务器信息

        Runnable tasks = new Runnable() {
            public void run() {
                InputStream in = tcpClient.getIn();
                while (true) {
                    try {
//                        System.out.println("running");
                        MessageBean msgb = null;
                        synchronized (in) {
                            msgb = new MessageBean(in);
                        }
                        final MessageBean finalMsgb = msgb;
                        new Thread(new Runnable() {
                            public void run() {
                                SoftReference<String> content = finalMsgb.getContentString();
                                if (null == content) {
                                    //内容为null
                                    return;
                                }
                                String content_item = content.get();
                                if (null != content_item) {
                                    String msg = handleMessage(content_item);
                                    if (null != msg)
//                                        System.out.println(Thread.currentThread().getId() + ":" + msg);
                                        System.out.println(msg);
                                }
                            }
                        }).start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        ExecutorService es = Executors.newFixedThreadPool(5);
        es.execute(tasks);
//        es.execute(tasks);
//        es.execute(tasks);
//        es.execute(tasks);
//        es.execute(tasks);

//
        tcpClient.send(mb1);//登录
        tcpClient.send(mb2);//入组

        //定时心跳包
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    tcpClient.send(new MessageBean(type, "type@=keeplive/tick@=" + System.currentTimeMillis() / 1000 + "/"));
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Logger.getLogger("心跳包出错").log(Level.INFO, "发送出错");
                    }

                }
            }
        }).start();

        //手动触发gc
//        new Thread(new Runnable() {
//            public void run() {
//                while(true){
//                    try {
//                        System.gc();
//                        sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Logger.getLogger("gc").log(Level.INFO,"出错");
//                    }
//
//                }
//            }
//        }).start();
    }
}
