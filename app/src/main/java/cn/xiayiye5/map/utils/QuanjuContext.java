package cn.xiayiye5.map.utils;
import android.app.Application;  
import android.content.Context;  
  
/** 
 * 编写自己的Application，管理全局状态信息，比如Context 
 * @author yy 
 * 
 */  
public class QuanjuContext extends Application {  
    private static Context context;  
      
    @Override  
    public void onCreate() {
        //获取Context  
        super.onCreate();
        context = getApplicationContext();
    }  
      
    //返回  
    public static Context getContextObject(){  
        return context;  
    }  
}
