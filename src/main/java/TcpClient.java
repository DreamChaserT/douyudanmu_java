import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpClient {
    private static TcpClient ourInstance = new TcpClient();
    private Socket tcpSocket = null;
    private OutputStream out = null;//客户端->服务器
    private InputStream in = null;//服务器->客户端

    public static TcpClient getInstance() {
        return ourInstance;
    }

    private TcpClient() {

    }

    //启动数据连接，只能启动一次
    public synchronized boolean startClient(String host, int port) {
        try {
            if (null == tcpSocket) {
                tcpSocket = new Socket(host, port);
                in = tcpSocket.getInputStream();
                out = tcpSocket.getOutputStream();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            try {
                if (null != tcpSocket) {
                    tcpSocket.close();
                    tcpSocket = null;
                }
                if (null != in) {
                    in.close();
                    in = null;
                }
                if (null != out) {
                    out.close();
                    out = null;
                }
            }catch (IOException ioe2){
                ioe2.printStackTrace();
            }
            return false;
        }
        return true;
    }

    //发送数据
    public boolean send(MessageBean messageBean) {
        //检测连接是否正常
        if (!isConnected()) {
            return false;
        }
        try {
            out.write(messageBean.getBytes());
            out.flush();
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "客户端发送");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "数据发送出错");
            return false;
        }
        return true;
    }

    //检测连接是否正常
    private boolean isConnected() {
        if (null == tcpSocket || !tcpSocket.isConnected()) {
            return false;
        }
        return true;
    }

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }
}
