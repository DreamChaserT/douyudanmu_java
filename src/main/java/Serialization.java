import java.util.HashMap;
import java.util.Map;

//数据序列化
public class Serialization {
    public static Map Serialization(String src) {
        HashMap<String, String> res = new HashMap<String, String>();
        //根据/切分数据
        String[] pars = src.split("/");
        int size = pars.length;
        for (String single : pars) {
            //每组数据根据@=划分
            String[] kv = single.split("@=");
            if (kv.length != 2)
                continue;//分隔符数量错误，忽略
            //处理键值对中的转义字符
            String key = handleExpression(kv[0]);
            String value = handleExpression(kv[1]);
            res.put(key, value);
        }
        return res;
    }

    private static String handleExpression(String src) {
        String res1 = src.replaceAll("@A", "@");
        res1.replaceAll("@S", "/");
        return res1;
    }
}


