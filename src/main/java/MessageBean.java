import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBean {
    private int length_t;
    private byte[] length;
    private byte[] length2;
    private byte[] type;
    private byte encrypt;
    private byte left;
    private byte[] content;

    //根据参数生成数据Bean
    public MessageBean(short type, String content) {
        content += '\0';
        length_t = 12 + content.length();
        //长度只计算一次
        this.length = Convert.intTobyte(length_t - 4, Convert.StoreType.little);
        this.length2 = this.length;
        this.type = Convert.shortTobyte(type, Convert.StoreType.little);
        this.encrypt = 0x0;
        this.left = 0x0;
        this.content = content.getBytes();
    }

    //根据输入流生成数据Bean
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
//                Logger.getLogger("数据包丢弃").log(Level.WARNING, "数据包丢弃");

                return;
            }
//            System.out.println("++++++++++++++++++++length1:" + _length1+"++++++++++++++++++++length2:" + _length2);
            encrypt = head[10];
            left = head[11];
            length_t = Convert.byteToint(length, Convert.StoreType.little) + 4;
//            System.out.println("++++++++++++++++++++length:" + length_t);

            //读取内容信息
            int content_length = length_t - 12;
            if (content_length < 1)
                return;
            content = new byte[content_length];
            in.read(content);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


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
