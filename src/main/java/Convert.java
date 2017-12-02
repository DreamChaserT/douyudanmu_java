public class Convert {
    enum StoreType{
        little,big
    }
    public static byte[] intTobyte(int src,StoreType storeType){
        byte[] res=new byte[4];
        switch(storeType){
            case big:
                res[0]=(byte)((src>>24)&0xff);
                res[1]=(byte)((src>>16)&0xff);
                res[2]=(byte)((src>>8)&0xff);
                res[3]=(byte)((src)&0xff);
                break;
            case little:
                res[0]=(byte)((src)&0xff);
                res[1]=(byte)((src>>8)&0xff);
                res[2]=(byte)((src>>16)&0xff);
                res[3]=(byte)((src>>24)&0xff);
                break;
        }
        return res;
    }

    public static int byteToint(byte[] src,StoreType storeType){
        int res=0;
        switch(storeType){
            case big:
                res=(src[3]&0xff)
                |((src[2]<<8)&0xff00)
                |((src[1]<<16)&0xff0000)
                |((src[0]<<24)&0xff000000);
                break;
            case little:
                res+=(src[0]&0xff)
                |((src[1]<<8)&0xff00)
                |((src[2]<<16)&0xff0000)
                |((src[3]<<24)&0xff000000);
                break;
        }
        return res;
    }


    public static byte[] shortTobyte(short src,StoreType storeType){
        byte[] res=new byte[2];
        switch(storeType){
            case big:
                res[0]=(byte)((src>>>8)&0xff);
                res[1]=(byte)((src)&0xff);
                break;
            case little:
                res[0]=(byte)((src)&0xff);
                res[1]=(byte)((src>>>8)&0xff);
                break;
        }
        return res;
    }

    public static short byteToshort(byte[] src,StoreType storeType){
        short res=0;
        switch(storeType){
            case big:
                res+=(short) (((src[1])&0xff)
                |((src[0]<<8)&0xff00));
                break;
            case little:
                res=(short) ((((src[0])&0xff)
                |((src[1]<<8)&0xff00)));
                break;
        }
        return res;
    }

    public static char[] byteTochar(byte[] src){
        int length=src.length;
        if(length>1000)
            return new String("").toCharArray();
//        System.out.println("++++++++++++++++++++length:"+length);
        char[] res=new char[length];
        for(int i=0;i<length;++i){
            res[i]= (char) src[i];
        }
        return res;
    }
}
