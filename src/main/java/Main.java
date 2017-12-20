import java.util.Scanner;

public class Main {
    private static String getRoomId() {
        System.out.print("请输入房间Id:");
        Scanner sc = new Scanner(System.in);
        String roomId = sc.next();
        return roomId;
    }

    public static void main(String[] args){
        String roomId=getRoomId();
        System.out.println("================================");
        DouyuHandle.handleMessage(new DisplayConsole(),roomId);
    }
}
