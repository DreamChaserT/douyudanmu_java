import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @title 斗鱼自定义协议bean
 */
public class MessageBean {
    //数据包真实长度
    private int length_t;

    //协议数据部分
    private byte[] length;//不计算此长度
    private byte[] length2;
    private byte[] type;
    private byte encrypt;
    private byte left;
    private byte[] content;

    /**
     * 根据参数生成数据Bean
     *
     * @param type    the type
     * @param content the content
     */
    public MessageBean(short type, String content) {
        content += '\0';
        this.length_t = 12 + content.length();
        //长度只计算一次
        this.length = Convert.intTobyte(length_t - 4, Convert.StoreType.little);
        this.length2 = this.length;
        this.type = Convert.shortTobyte(type, Convert.StoreType.little);
        this.encrypt = 0x0;
        this.left = 0x0;
        this.content = content.getBytes();
    }

    /**
     * 根据数据流生成bean
     *
     * @param in the in
     */
    public MessageBean(InputStream in) {
        if (null == in)
            return;
        length = new byte[4];
        length2 = new byte[4];
        type = new byte[2];
        encrypt = 0x0;
        left = 0x0;


        byte[] head = new byte[12];
        try {
            //先读取头部信息
            in.read(head);
            System.arraycopy(head, 0, length, 0, 4);
            System.arraycopy(head, 4, length2, 0, 4);
            System.arraycopy(head, 8, type, 0, 2);
            int _length1 = Convert.byteToint(length, Convert.StoreType.little);
            int _length2 = Convert.byteToint(length2, Convert.StoreType.little);

            if (_length1 != _length2||_length1>1000||_length2>1000) {
                return;
            }
            encrypt = head[10];
            left = head[11];
            this.length_t = Convert.byteToint(length, Convert.StoreType.little) + 4;

            //读取内容信息
            int content_length = length_t - 12;
            if (content_length < 1)
                return;
            content = new byte[content_length];
            in.read(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }


    /**
     * 将bean转换为byte[]格式
     *
     * @return the byte [ ]
     */
    public byte[] getBytes() {
        byte[] res = new byte[length_t];
        System.arraycopy(length, 0, res, 0, 4);
        System.arraycopy(length2, 0, res, 4, 4);
        System.arraycopy(type, 0, res, 8, 2);
        res[10] = encrypt;
        res[11] = left;
        System.arraycopy(content, 0, res, 12, content.length);
        return res;
    }

    /**
     * 获取content内容
     *
     * @return the content string
     */
    public SoftReference<String> getContentString() {

        if (null != content) {
            try {
                SoftReference<String> reference = new SoftReference<String>(new String(content));
                return reference;
            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger(this.toString()).log(Level.WARNING, "=====================================" + this.length_t);
            }

        }
        return null;

    }



}
