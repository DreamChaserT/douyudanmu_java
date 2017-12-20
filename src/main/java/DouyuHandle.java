import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * 处理斗鱼弹幕信息
 */
public class DouyuHandle {

    /**
     * 弹幕处理方法(提取弹幕信息)
     * 启动心跳包
     * 从inputStream读取弹幕数据为map
     * @param handlemethod，具体弹幕数据处理方法
     */
    public static void handleMessage(final Display handlemethod, String roomId) {
        final TcpClient tcpClient = new TcpClient(Config.host,Config.port);
        MessageBean mb1 = new MessageBean(Config.send_type, "type@=loginreq/roomid@=" + roomId + "/");
        MessageBean mb2 = new MessageBean(Config.send_type, "type@=joingroup/rid@=" + roomId + "/gid@=-9999/");
        //输出服务器信息


        //弹幕处理线程
        Runnable tasks = new Runnable() {
            public void run() {
                InputStream in = tcpClient.getIn();
                while (true) {
                    try {
                        final MessageBean finalMsgb = new MessageBean(in);
                        new Thread(new Runnable() {
                            public void run() {
                                SoftReference<String> content = finalMsgb.getContentString();
                                if (null == content) {
                                    //内容为null
                                    return;
                                }
                                String content_item = content.get();
                                if (null != content_item) {
                                    Map<String,String> map = Serialization.Serialization(content_item);
                                    handlemethod.display(map);
                                }
                            }
                        }).start();


                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                }
            }
        };
        ExecutorService es = Executors.newFixedThreadPool(5);
        es.execute(tasks);

        tcpClient.send(mb1);//登录
        tcpClient.send(mb2);//入组

        //定时心跳包
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    tcpClient.send(new MessageBean(Config.send_type, "type@=keeplive/tick@=" + System.currentTimeMillis() / 1000 + "/"));
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Logger.getLogger("心跳包出错").log(Level.INFO, "发送出错");
                    }

                }
            }
        }).start();

    }
}
