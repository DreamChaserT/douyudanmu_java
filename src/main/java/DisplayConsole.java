import java.util.Map;

public class DisplayConsole implements Display {
    //在控制台输出弹幕数据
    public void display(Map<String, String> map) {
        String type = map.get("type");
        String nn = map.get("nn");
        String txt = map.get("txt");
        if (null != type && type.equals("chatmsg")) {
            if (null != nn && null != txt) {
                String res = nn + ":" + txt;
                System.out.println(res);
            }
        }
    }
}
