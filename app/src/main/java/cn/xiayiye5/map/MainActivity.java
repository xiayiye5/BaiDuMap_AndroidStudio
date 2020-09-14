package cn.xiayiye5.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRoutePlanOption.TransitPolicy;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

import cn.xiayiye5.map.utils.DrivingRouteOverlay;
import cn.xiayiye5.map.utils.PoiOverlay;
import cn.xiayiye5.map.utils.TransitRouteOverlay;

/**
 * @author xiayiye5
 * 2020年9月14日10:39:18
 * 百度地图页面
 */
public class MainActivity extends FragmentActivity implements OnMarkerClickListener, OnClickListener, OnLongClickListener {

    double weidu = 39.9184470000;
    // 这个是百度地图公司的经纬度坐标点
    double jindu = 116.3252280000;
    LatLng point = new LatLng(weidu, jindu);
    private IntentFilter filter;
    private MapView mv_maptest;
    public BaiduMap map;
    private View dialog_tv;
    private TextView tv_dialog;
    private EditText et_serach;
    private Button bt_search;
    private String et_result;
    private PoiInfo poiInfo;
    private LinearLayout ll_suofang;
    private LinearLayout llweixing;
    private LinearLayout ll_search;
    private EditText et_nearsearch;
    private String near_result;
    private AlertDialog alertDialog;
    private double latitude_result;
    private double longitude_result;
    private Builder dialog;
    private String str_city;
    private String str_search;
    private Button bt_xiayiye;
    private int index = 0;
    private Button bt_shangyiye;
    private boolean isShow = false;// 默认对话框关闭
    private EditText et_search_start;
    private EditText et_search_end;
    private String trim_start;
    private String trim_end;
    EditText et_search_middle;
    private String trim_middle;
    private AlertDialog create_road;
    private TransitPolicy ebusTimeFirst;
    private RadioGroup rg_group;
    private RadioGroup rg_jiache_huancheng;
    private RadioButton rb_transtrate_road1;
    private RadioButton rb_transtrate_road2;
    // 设置驾车路线换乘标示
    private int jiache = 11;
    private int huancheng = 12;
    private int jiache_huancheng = jiache;
    private RadioButton rb_nosubway;
    private RadioButton rb_time_first;
    private RadioButton rb_little_transtrate;
    private RadioButton rb_little_walking;
    int transfer_mode = 0;// 换乘方式自定义标示
    private String split_city = "北京";// 途中必须经过城市默认北京;// 切割后必须的城市出发目的地
    private String split_splans;// 切割后必须经过的地方
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = null;// 定位方法
    private ImageButton ib_location;
    private boolean open = false;//默认false去掉标尺
    //开启定位后拿到的经纬度
    private double location_latitude;
    private double location_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        initsdk();
        setContentView(R.layout.activity_main);
        // 拿到mapview控件设置默认缩放比例
        mv_maptest = (MapView) findViewById(R.id.mv_maptest);
        et_serach = (EditText) findViewById(R.id.et_serach);
        bt_search = (Button) findViewById(R.id.bt_search);
        Button bt_putong = (Button) findViewById(R.id.bt_putong);
        Button bt_weixing = (Button) findViewById(R.id.bt_weixing);
        Button bt_jiaotong = (Button) findViewById(R.id.bt_jiaotong);
        ImageButton ib_da = (ImageButton) findViewById(R.id.ib_da);
        ImageButton ib_xiao = (ImageButton) findViewById(R.id.ib_xiao);
        ll_suofang = (LinearLayout) findViewById(R.id.ll_suofang);
        llweixing = (LinearLayout) findViewById(R.id.llweixing);
        bt_xiayiye = (Button) findViewById(R.id.bt_xiayiye);
        bt_shangyiye = (Button) findViewById(R.id.bt_shangyiye);
        //初始化定位按钮
        ib_location = (ImageButton) findViewById(R.id.ib_location);
        // 首先隐藏下一页按钮
        bt_xiayiye.setVisibility(View.GONE);
        bt_shangyiye.setVisibility(View.GONE);
        ll_search = (LinearLayout) findViewById(R.id.ll_serach);
        map = mv_maptest.getMap();
        MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(14);// 设置地图默认缩放比例为10(默认为12)
        map.setMapStatus(zoom);
        MapStatusUpdate center = MapStatusUpdateFactory.newLatLng(point);
        // 设置默认中心店
        map.setMapStatus(center);
        // 去掉缩放按钮
        mv_maptest.showZoomControls(false);
        // 去掉标尺
        mv_maptest.showScaleControl(false);
        boolean menu = mv_maptest.showContextMenu();
        Log.e("测试", menu + "");

        // 写文字
        drawtext();
        // 画对话框在地图上面通过点击图片后
        dialogpic();
        // ❀图片画地图上面
        drawpictuer();

        bt_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                index = 0;
                // 拿到输入框的内容
                et_result = et_serach.getText().toString().trim();
                if (!et_result.equals("") && !et_result.equals(null)) {
                    if (et_result.contains("_")) {
                        // 如果是城市搜索格式。走城市搜索方法
                        // 切割数据
                        String[] split = et_result.split("_");
                        str_city = split[0];
                        str_search = split[1];
                        citySearch();
                    } else {
                        // 不是城市搜索格式，走范围内搜索方法
                        // 搜索资源
                        serachrescours();
                    }
                    Toast.makeText(MainActivity.this, et_result,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "请输入数据再点击查询按钮",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        // 设置三个按钮点击事件
        tv_dialog.setOnClickListener(this);
        bt_putong.setOnClickListener(this);
        bt_weixing.setOnClickListener(this);
        bt_jiaotong.setOnClickListener(this);
        ib_da.setOnClickListener(this);
        ib_xiao.setOnClickListener(this);
        bt_xiayiye.setOnClickListener(this);
        bt_shangyiye.setOnClickListener(this);
        //设置定位图标的点击事件
        ib_location.setOnClickListener(this);
        /**
         * 长按放大按钮隐藏缩放按钮
         */
        ib_da.setOnLongClickListener(this);
        bt_putong.setOnLongClickListener(this);
        tv_dialog.setOnLongClickListener(this);
        bt_search.setOnLongClickListener(this);
        bt_xiayiye.setOnLongClickListener(this);
        // 跳转到路线查询页面
        // ib_xiao.setOnLongClickListener(this);
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        myListener = new MyLocationListeners();
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        initLocation();// 定位的方法
    }

    /**
     * 换乘路线
     */
    private void translateroad(String city, String plans_from, String plans_to,
                               int select_int) {
        RoutePlanSearch rps = RoutePlanSearch.newInstance();
        rps.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener());
        TransitRoutePlanOption transit_option = new TransitRoutePlanOption();
        transit_option
                .from(PlanNode.withCityNameAndPlaceName(city, plans_from));
        transit_option.to(PlanNode.withCityNameAndPlaceName(city, plans_to));// 史各庄经纬度
        // 设置所在城市
        transit_option.city(city);
        // 设置优选条件
        switch (select_int) {
            case 0:
                ebusTimeFirst = TransitPolicy.EBUS_NO_SUBWAY;// 少地铁
                break;
            case 1:
                ebusTimeFirst = TransitPolicy.EBUS_TIME_FIRST;// 少耗时
                break;
            case 2:
                ebusTimeFirst = TransitPolicy.EBUS_TRANSFER_FIRST;// 少换乘
                break;
            case 3:
                ebusTimeFirst = TransitPolicy.EBUS_WALK_FIRST;// 少步行
                break;
        }
        transit_option.policy(ebusTimeFirst);
        rps.transitSearch(transit_option);// 发起换乘搜索
    }

    /**
     * 驾车路线初始化
     */
    private void driverPlan(String city, String plans, String plans_from,
                            String plans_to) {
        RoutePlanSearch rps = RoutePlanSearch.newInstance();
        rps.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener());
        DrivingRoutePlanOption driver_option = new DrivingRoutePlanOption();
        // driver_option.from(PlanNode.withLocation(point_from));
        // driver_option.to(PlanNode.withLocation(point_to));//史各庄经纬度
        driver_option.from(PlanNode.withCityNameAndPlaceName(city, plans_from));
        driver_option.to(PlanNode.withCityNameAndPlaceName(city, plans_to));// 史各庄经纬度
        // 设置优选条件
        driver_option.policy(DrivingPolicy.ECAR_TIME_FIRST);// 时间优先
        if (plans != "" && plans != null) {
            List<PlanNode> roate_plans = new ArrayList<PlanNode>();
            roate_plans.add(PlanNode.withCityNameAndPlaceName(city, plans));
            // roate_plans.add(PlanNode.withCityNameAndPlaceName("北京", "西直门"));
            // 设置途经点
            driver_option.passBy(roate_plans);
        }
        rps.drivingSearch(driver_option);// 发起搜索
    }

    public class MyOnGetRoutePlanResultListener implements
            OnGetRoutePlanResultListener {

        @Override
        public void onGetBikingRouteResult(BikingRouteResult arg0) {
            // 获取所有骑行规划路线
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
//			Log.e("空指针",result.toString());
            // 开车路线
            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                    map);
            // 设置覆盖物的点击事件
            map.setOnMarkerClickListener(drivingRouteOverlay);
            drivingRouteOverlay.setData(result.getRouteLines().get(0));// 获取开车路线后设置在地图上显示
            drivingRouteOverlay.addToMap();// 开车路线添加到地图上
            drivingRouteOverlay.zoomToSpan();// 使搜索的结果在可视范围内
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
            // 换乘路线结果
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            TransitRouteOverlay tro = new TransitRouteOverlay(map);
            // List<TransitRouteLine> routeLines = arg0.getRouteLines();
            // for (int i = 0; i < routeLines.size(); i++) {
            // tro.setData(routeLines.get(i));
            // }
            // 设置数据
            tro.setData(result.getRouteLines().get(0));
//			Log.e("空指针",result.getRouteLines().toString());
            tro.addToMap();// 开车路线添加到地图上
            tro.zoomToSpan();// 使搜索的结果在可视范围内
        }

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
            // 步行
        }

    }

    /**
     * 在范围内搜索的方法
     */
    private void serachrescours() {
        PoiSearch poiS = PoiSearch.newInstance();// 创建对象
        poiS.setOnGetPoiSearchResultListener(new Mypoisearch());
        PoiBoundSearchOption fanwei = new PoiBoundSearchOption();
        LatLngBounds llbs = new LatLngBounds.Builder()
                .include(new LatLng(40.7460400000, 114.9219870000))
                .include(new LatLng(38.9954100000, 117.7206750000)).build();
        fanwei.bound(llbs).keyword(et_result);// 搜索的内容
        // 设置下一页数据
        fanwei.pageNum(index);
        // 发起范围内搜索
        poiS.searchInBound(fanwei);
    }

    class Mypoisearch implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiDetailResult(PoiDetailResult arg0) {

        }

        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // 返回全城查找结果
            int currentPageNum = result.getCurrentPageNum() + 1;// 获取当前页数
            int totalPageNum = result.getTotalPageNum();// 获取总共页数
            int currentPageCapacity = result.getCurrentPageCapacity();// 获取当前页显示个数
            int totalPoiNum = result.getTotalPoiNum();// 获取总条数
            if (totalPoiNum > 10) {
                bt_xiayiye.setVisibility(View.VISIBLE);
                bt_shangyiye.setVisibility(View.VISIBLE);
                llweixing.setVisibility(View.GONE);// 隐藏卫星地图
            } else {
                bt_xiayiye.setVisibility(View.GONE);
                bt_shangyiye.setVisibility(View.GONE);
                llweixing.setVisibility(View.VISIBLE);// 显示卫星地图
            }
            // 返回搜索结果
            if (index + 1 >= totalPageNum && totalPageNum != 1) {
                index = totalPageNum;
                Toast.makeText(getApplicationContext(), "已经到最后一页了。",
                        Toast.LENGTH_SHORT).show();
            } else if (index != 0 && index != totalPageNum) {
                Toast.makeText(
                        getApplicationContext(),
                        "当前为第" + currentPageNum + "页:总页数为" + totalPageNum
                                + ":当前页显示个数为" + currentPageCapacity
                                + "个:总结果条数为" + totalPoiNum + "条",
                        Toast.LENGTH_SHORT).show();
            }
            // 在创建覆盖物之前清除上一页的覆盖物
            map.clear();// 清除覆物
            PoiOverlay pioo = new MyPoiOverlay(map);
            // 设置覆盖物marker点击事件
            map.setOnMarkerClickListener(pioo);
            // 设置数据
            pioo.setData(result);
            // 添加地图
            pioo.addToMap();
            // 缩小到搜索到的位置
            pioo.zoomToSpan();
        }

        // 写一类继承PoiOverlay
        class MyPoiOverlay extends PoiOverlay {

            public MyPoiOverlay(BaiduMap baiduMap) {
                super(baiduMap);
            }

            /*
             * 重写onPoiClick点击事件方法
             */
            @Override
            public boolean onPoiClick(int i) {
                // 拿到结果
                PoiResult poiResult = getPoiResult();
                poiInfo = poiResult.getAllPoi().get(i);
                // 拿到搜索结果的经纬度
                latitude_result = poiInfo.location.latitude;
                longitude_result = poiInfo.location.longitude;
                // 弹出吐司显示点中的点击事件
                Toast.makeText(
                        getApplicationContext(),
                        poiInfo.city + " " + poiInfo.name + " "
                                + poiInfo.address, Toast.LENGTH_LONG).show();
                // 弹出populowindow
                showPopupWindow(getCurrentFocus());
                // showPopupWindow(mv_maptest);

                // 弹出附近搜索对话框
                showDialog_NearSearch();
                return super.onPoiClick(i);
            }
        }

    }

    /**
     * 显示popupwindow的方法
     */

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = View.inflate(getApplicationContext(),
                R.layout.activity_popupwindow, null);
        // 设置按钮的点击事件
        TextView button_popuowindow = (TextView) contentView
                .findViewById(R.id.tv_popuowindow);
        button_popuowindow.setText(poiInfo.city + " " + poiInfo.name + " "
                + poiInfo.address);
        button_popuowindow.setTextColor(Color.BLUE);
        button_popuowindow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        getApplicationContext(),
                        "欢迎使用扬宏豕慧官网修改版本地图，更多请访问扬宏豕慧官方网站http://www.iyhsh.faisco.cn",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(
        // R.drawable.toum));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景颜色为透明色
        // getResources().getDrawable(R.drawable.toum).setAlpha(0);// 设置图片透明度为0
        // 设置好参数之后再show
        // int[] location = new int [2];
        // view.getLocationInWindow(location);
        // int x = location[0];
        // int y = location[1];
        // popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, x, y);
        popupWindow.showAsDropDown(view);// 此方法也可以
        // 缩放动画
        ScaleAnimation scanim = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
        scanim.setDuration(4000);// 四秒钟

        // 渐变动画
        AlphaAnimation apap = new AlphaAnimation(0.1f, 1.0f);
        apap.setDuration(4000);

        // 旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF,
                0.1f);
        ra.setDuration(4000);
        ra.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        // 组合动画
        AnimationSet ams = new AnimationSet(true);// 共享动画插入器
        // 添加组合动画
        ams.addAnimation(ra);
        ams.addAnimation(scanim);
        ams.addAnimation(apap);
        // 开启动画效果
        contentView.startAnimation(ams);
    }

    private void dialogpic() {
        dialog_tv = View.inflate(getApplicationContext(),
                R.layout.activity_dialogpic, null);
        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                .position(point).width(MapViewLayoutParams.WRAP_CONTENT)
                .height(MapViewLayoutParams.WRAP_CONTENT).build();
        mv_maptest.addView(dialog_tv, params);
        dialog_tv.setVisibility(View.INVISIBLE);// 首先隐藏
        tv_dialog = (TextView) dialog_tv.findViewById(R.id.tv_dialog);
        map.setOnMarkerClickListener(this);// 设置点击事件
    }

    // 打开之前先检验key的正确与否
    private void initsdk() {
        // 注册广播
        filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);// 网络连接错误
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);// 校验失败
        registerReceiver(new Mybraodcast(), filter);
        SDKInitializer.initialize(getApplicationContext());
    }

    class Mybraodcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 判断是否成功

            if (isNetworkAvailable(MainActivity.this)) {
                if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                    Toast.makeText(getApplicationContext(), "校验错误。请检查校验码是否正确。",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (action
                            .equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                        Toast.makeText(getApplicationContext(),
                                "网络错误。请检查网络是否在正常。", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    // 设置地图图层
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                map.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 底图
                map.setTrafficEnabled(false);// 关闭交通图
                break;
            case KeyEvent.KEYCODE_2:
                map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// 卫星地图
                map.setTrafficEnabled(false);// 关闭交通图
                break;
            case KeyEvent.KEYCODE_3:
                map.setTrafficEnabled(true);// 打开交通图
                break;
            case KeyEvent.KEYCODE_4:
                MapStatusUpdate zoomin = MapStatusUpdateFactory.zoomIn();// 一级一级缩放
                // 放大
                map.setMapStatus(zoomin);
                break;
            case KeyEvent.KEYCODE_5:
                MapStatusUpdate zoomout = MapStatusUpdateFactory.zoomOut();// 一级一级缩放
                // 缩小
                map.setMapStatus(zoomout);
                break;
            case KeyEvent.KEYCODE_6:
                float jiaodu = map.getMapStatus().rotate;
                MapStatus roates = new MapStatus.Builder().rotate(jiaodu + 15)
                        .build();
                MapStatusUpdate roate = MapStatusUpdateFactory.newMapStatus(roates);
                // 旋转
                map.setMapStatus(roate);
                break;
            case KeyEvent.KEYCODE_7:
                float jiaodus = map.getMapStatus().overlook;
                MapStatus roatelines = new MapStatus.Builder()
                        .overlook(jiaodus - 5).build();
                MapStatusUpdate roateline = MapStatusUpdateFactory
                        .newMapStatus(roatelines);
                // 一条线旋转
                map.setMapStatus(roateline);
                break;
            case KeyEvent.KEYCODE_8:
                MapStatusUpdate move = MapStatusUpdateFactory.newLatLng(new LatLng(
                        39.9899560000, 116.3230660000));// 中关村地铁经纬度坐标
                // 移动
                map.animateMapStatus(move);
                break;
            case KeyEvent.KEYCODE_9:
                MapStatusUpdate center = MapStatusUpdateFactory.newLatLng(point);
                // 设置默认中心店
                map.setMapStatus(center);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    // public boolean isNetworkConnected() {
    // ConnectivityManager cm = (ConnectivityManager)
    // getSystemService(Context.CONNECTIVITY_SERVICE);
    // NetworkInfo ni = cm.getActiveNetworkInfo();
    // return ni != null && ni.isConnectedOrConnecting();
    // }
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 在中心点地图位置写文字标注信息
    private void drawtext() {
        TextOptions text = new TextOptions();
        text.position(point).// 需要标注的点的经纬度
                bgColor(0x01ffffff).// 文字背景色
                fontColor(0x99ff0000).// 文字颜色
                fontSize(24).// 字体大小
                text("北京创和世纪通讯技术股份有限公司").// 文字信息
                typeface(Typeface.MONOSPACE).// 安卓字体
                rotate(0);// .visible(false);//旋转45°,隐藏文字信息
        map.addOverlay(text);// 将文字信息添加到地图上面
    }

    /**
     * 画图片在地图上面
     */
    private void drawpictuer() {
        MarkerOptions pictuer = new MarkerOptions();
        // 设置坐标位置
        pictuer.position(new LatLng(weidu + 0.005, jindu - 0.0005))
                .title("扬宏豕慧官方店铺(长按此处可以搜索全局\n点击此处可隐藏搜索框)")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.caiguan));
        map.addOverlay(pictuer);

        pictuer.position(new LatLng(weidu + 0.02, jindu + 0.005))
                .title("扬宏豕慧店铺(长按此处可以搜索全局\n点击此处可隐藏搜索框)")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.caiguan))
                .draggable(true);// 可以被拖拽
        map.addOverlay(pictuer);

        ArrayList<BitmapDescriptor> pic = new ArrayList<BitmapDescriptor>();
        pic.add(BitmapDescriptorFactory.fromResource(R.drawable.caiguan));
        pic.add(BitmapDescriptorFactory.fromResource(R.drawable.ya));
        pic.add(BitmapDescriptorFactory.fromResource(R.drawable.wifi));
        pic.add(BitmapDescriptorFactory.fromResource(R.drawable.jia));
        pictuer.position(new LatLng(weidu + 0.002, jindu - 0.0085))
                .title("扬宏豕慧周黑鸭(长按此处可以搜索全局\n点击此处可隐藏搜索框)").icons(pic)
                .draggable(true);// 可以被拖拽
        map.addOverlay(pictuer);

    }

    /**
     * 设置覆盖物的点击事件
     */

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                .position(marker.getPosition())
                .width(MapViewLayoutParams.WRAP_CONTENT)
                .height(MapViewLayoutParams.WRAP_CONTENT).yOffset(-40).// 标示在点中的物体上面10距离
                build();
        mv_maptest.updateViewLayout(dialog_tv, params);
        tv_dialog.setText(marker.getTitle());
        dialog_tv.setVisibility(View.VISIBLE);// 点击后显示对话框
        return true;// 标示消费
    }

    // 卫星图切换功能
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_putong:
                map.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 底图
                map.setTrafficEnabled(false);// 关闭交通图
                break;
            case R.id.bt_weixing:
                map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// 卫星地图
                map.setTrafficEnabled(false);// 关闭交通图
                break;
            case R.id.bt_jiaotong:
                map.setTrafficEnabled(true);// 打开交通图
                break;
            case R.id.ib_da:
                MapStatusUpdate zoomin = MapStatusUpdateFactory.zoomIn();// 一级一级缩放
                // 放大
                map.setMapStatus(zoomin);// 放大地图
                break;
            case R.id.ib_xiao:
                MapStatusUpdate zoomout = MapStatusUpdateFactory.zoomOut();// 一级一级缩放
                // 缩小
                map.setMapStatus(zoomout);// 缩小地图
                break;
            case R.id.tv_dialog:
                // ll_search.setVisibility(View.GONE);// 隐藏搜索框
                // 弹出附近搜索框
                showDialog_NearSearch();
                break;
            case R.id.bt_nearsearch:
                // 附近查找功能
                nearSearchOk();
                break;
            case R.id.bt_xiayiye:
                // 显示下一页数据
                // Toast.makeText(getApplicationContext(), "下一页数据",
                // Toast.LENGTH_LONG).show();
                index++;
                dialog_edittext_city_search();
                break;
            case R.id.bt_shangyiye:
                if (index >= 1) {
                    index--;
                } else {
                    index = 0;
                    Toast.makeText(getApplicationContext(), "已经是首页了。",
                            Toast.LENGTH_SHORT).show();
                }
                dialog_edittext_city_search();
                break;
            case R.id.ib_location:
                //点击后调头定位方法
                initLocation();
//			BitmapDescriptor location_icon = BitmapDescriptorFactory.fromResource(R.drawable.dingwei_icon);//自定义定位图标
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);//null表示默认图标
                //点击后让定位点回到地图中心
                map.setMyLocationConfigeration(config);
                //恢复默认缩放
                MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(16);// 设置地图默认缩放比例为10(默认为12)
                map.setMapStatus(zoom);
                open = true;
                if (open) {
                    mv_maptest.showScaleControl(true);//打开标尺
                }
        }
    }

    /**
     * 区分对话框附近搜索。城市搜索，范围内搜索方法
     */
    private void dialog_edittext_city_search() {
        if (isShow) {
            // 当点击附近搜索按钮的时候拿到数据进行查找
            near_result = et_nearsearch.getText().toString().trim();
            if (!near_result.equals("") && !near_result.equals(null)) {
                // 将输入框设置为空
                et_serach.setText(near_result);
                nearSearch();
                Toast.makeText(MainActivity.this, "搜索情况如下：", Toast.LENGTH_LONG)
                        .show();
                // 自动关闭对话框
                alertDialog.dismiss();
                isShow = false;
            }
        }
        // 拿到输入框的内容
        et_result = et_serach.getText().toString().trim();
        // 搜索之前判断数据是否可用
        if (et_result.contains("_")) {
            // 如果是城市搜索格式。走城市搜索方法
            // 切割数据
            String[] split = et_result.split("_");
            str_city = split[0];
            str_search = split[1];
            citySearch();
        } else {
            // 不是城市搜索格式，走范围内搜索方法
            // 搜索资源
            serachrescours();
        }
        // citySearch();//再次调用城市搜索方法
        // break;
    }

    /**
     * 弹出附近搜索对话框
     */
    private void showDialog_NearSearch() {
        Builder alert = new Builder(this);
        if (poiInfo != null) {
            dialog = alert.setTitle("附近查找").setMessage(poiInfo.address);
        } else {
            dialog = alert.setTitle("附近查找").setMessage(point.toString());
        }
        View nearserach_view = View.inflate(getApplicationContext(),
                R.layout.activity_nearsearch, null);
        Button bt_nearsearch = (Button) nearserach_view
                .findViewById(R.id.bt_nearsearch);
        et_nearsearch = (EditText) nearserach_view
                .findViewById(R.id.et_nearserach);
        bt_nearsearch.setOnClickListener(this);
        alert.setView(nearserach_view);

        // 缩放动画
        ScaleAnimation scanim = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
        scanim.setDuration(4000);// 四秒钟
        nearserach_view.startAnimation(scanim);
        alertDialog = dialog.create();
        alertDialog.setView(nearserach_view);
        alertDialog.show();
        // 当对话框出来后做标示
        isShow = true;
    }

    private void nearSearchOk() {
        // 当点击搜索按钮的时候拿到数据进行查找
        near_result = et_nearsearch.getText().toString().trim();
        // 判断是否为空
        if (!near_result.equals("") && !near_result.equals(null)) {
            nearSearch();
            Toast.makeText(MainActivity.this, "搜索情况如下：", Toast.LENGTH_LONG)
                    .show();
            // 自动关闭对话框
            alertDialog.dismiss();
        } else {
            Toast.makeText(MainActivity.this, "请输入数据再点击查询按钮", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void nearSearch() {
        PoiSearch poiS = PoiSearch.newInstance();// 创建对象
        poiS.setOnGetPoiSearchResultListener(new Mypoisearch());
        PoiNearbySearchOption pso = new PoiNearbySearchOption();
        pso.keyword(near_result)
                .location(new LatLng(latitude_result, longitude_result))
                .radius(10000);
        // 设置下一页数据
        pso.pageNum(index);
        // 设置清除上一页显示的数据
        map.clear();
        // 发起范围内搜索
        poiS.searchNearby(pso);
    }

    /**
     * 城市内搜索
     */
    private void citySearch() {
        PoiSearch citys = PoiSearch.newInstance();
        citys.setOnGetPoiSearchResultListener(new Mypoisearch());
        PoiCitySearchOption pcso = new PoiCitySearchOption();
        // 设置搜索范围城市
        pcso.city(str_city).keyword(str_search);
        pcso.pageNum(index);// 设置当前第几页
        citys.searchInCity(pcso);// 发起城市搜索
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.ib_da:
                ll_suofang.setVisibility(View.GONE);// 隐藏缩放按钮
                break;
            case R.id.bt_putong:
                llweixing.setVisibility(View.GONE);// 隐藏卫星地图切换
                break;
            case R.id.tv_dialog:
                ll_search.setVisibility(View.VISIBLE);// 显示搜索框
                llweixing.setVisibility(View.VISIBLE);// 显示维修地图按钮
                ll_suofang.setVisibility(View.VISIBLE);// 显示缩放按钮
                break;
            case R.id.bt_search:
                // 设置标示驾车还是路线换乘
                jiache_huancheng = jiache;
                // ll_search.setVisibility(View.GONE);// 隐藏搜索框
                // 跳到路线搜索页面
                View inflate_road_search = View.inflate(getApplicationContext(),
                        R.layout.activity_road_search, null);
                et_search_start = (EditText) inflate_road_search
                        .findViewById(R.id.et_search_start);
                et_search_end = (EditText) inflate_road_search
                        .findViewById(R.id.et_search_end);
                et_search_middle = (EditText) inflate_road_search
                        .findViewById(R.id.et_search_middle);
                Button et_search_ok = (Button) inflate_road_search
                        .findViewById(R.id.et_search_ok);
                rg_jiache_huancheng = (RadioGroup) inflate_road_search
                        .findViewById(R.id.rg_jiache_huancheng);
                // 隐藏换乘布局
                rg_jiache_huancheng.setVisibility(View.GONE);
                // 初始化换乘控件
                rg_group = (RadioGroup) inflate_road_search
                        .findViewById(R.id.rg_group);
                rb_transtrate_road1 = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_transtrate_road1);
                rb_transtrate_road2 = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_transtrate_road2);
                rb_nosubway = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_nosubway);
                rb_time_first = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_time_first);
                rb_little_transtrate = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_little_transtrate);
                rb_little_walking = (RadioButton) inflate_road_search
                        .findViewById(R.id.rb_little_walking);
                rb_transtrate_road1.setBackgroundColor(Color.rgb(238, 169, 184));
                Builder road_dialog = new Builder(this);
                road_dialog.setView(inflate_road_search);
                create_road = road_dialog.create();
                // 设置换乘点击事件
                // create_road.show();// 显示对话框
                rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // 设置两个背景选中色
                        switch (checkedId) {
                            case R.id.rb_transtrate_road1:
                                et_search_middle.setVisibility(View.VISIBLE);// 打开中途必须经过地
                                // 设置标示驾车还是路线换乘
                                jiache_huancheng = jiache;
                                rb_transtrate_road1.setBackgroundColor(Color.rgb(238,
                                        169, 184));
                                rb_transtrate_road2.setBackgroundColor(0x99ffffff);
                                rg_jiache_huancheng.setVisibility(View.GONE);
                                break;
                            case R.id.rb_transtrate_road2:
                                et_search_middle.setVisibility(View.GONE);// 隐藏中途必须经过地
                                // 设置标示驾车还是路线换乘
                                jiache_huancheng = huancheng;
                                // 当选择了换乘，那就默认换乘方式为无地铁
                                transfer_mode = 0;
                                // 切换fragment
                                // getSupportFragmentManager().beginTransaction().replace(R.id.ll_jiache,
                                // new TranstrateFragment()).commit();
                                rg_jiache_huancheng.setVisibility(View.VISIBLE);
                                rb_transtrate_road1.setBackgroundColor(0x99ffffff);
                                rb_transtrate_road2.setBackgroundColor(Color.rgb(238,
                                        169, 184));
                                // 第一个默认绿色
                                rb_nosubway.setBackgroundColor(Color.rgb(144, 238, 144));

                                // 设置换乘方式标示
                                rg_jiache_huancheng
                                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                                            @Override
                                            public void onCheckedChanged(
                                                    RadioGroup group, int checkedId) {
                                                // 其他三个恢复初始化白色
                                                rb_nosubway
                                                        .setBackgroundColor(0x99ffffff);
                                                rb_time_first
                                                        .setBackgroundColor(0x99ffffff);
                                                rb_little_transtrate
                                                        .setBackgroundColor(0x99ffffff);
                                                rb_little_walking
                                                        .setBackgroundColor(0x99ffffff);
                                                switch (checkedId) {
                                                    case R.id.rb_nosubway:
                                                        transfer_mode = 0;
                                                        rb_nosubway.setBackgroundColor(Color
                                                                .rgb(144, 238, 144));
                                                        break;
                                                    case R.id.rb_time_first:
                                                        transfer_mode = 1;
                                                        rb_time_first.setBackgroundColor(Color
                                                                .rgb(144, 238, 144));
                                                        break;
                                                    case R.id.rb_little_transtrate:
                                                        transfer_mode = 2;
                                                        rb_little_transtrate.setBackgroundColor(Color
                                                                .rgb(144, 238, 144));
                                                        break;
                                                    case R.id.rb_little_walking:
                                                        transfer_mode = 3;
                                                        rb_little_walking.setBackgroundColor(Color
                                                                .rgb(144, 238, 144));
                                                        break;
                                                }
                                            }
                                        });
                                break;
                        }
                        // 设置默认选中驾车按钮
                        // rg_group.check(R.id.rb_transtrate_road1);
                    }
                });
                // 设置点击事件
                et_search_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 拿到搜索框数据判断搜索
                        trim_start = et_search_start.getText().toString().trim();
                        trim_end = et_search_end.getText().toString().trim();
                        trim_middle = et_search_middle.getText().toString().trim();
                        // 判断中途经过的地方是否为空
                        if (!trim_middle.equals("") && trim_middle != null) {
                            // 判断格式是否正确
                            if (trim_middle.contains("_")) {
                                // 切割
                                String[] split_result = trim_middle.split("_");
                                // 判断输入长度
                                if (split_result.length == 1) {
                                    split_city = split_result[0];
                                    split_splans = "";
                                } else if (split_result.length > 1) {
                                    split_city = split_result[0];
                                    split_splans = split_result[1];
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "中途必过地点格式输入有误！\n例如:北京_西直门",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        // 判断两个数据
                        if (!trim_start.equals("") && !trim_start.equals(null)
                                && !trim_end.equals("") && !trim_end.equals(null)) {
                            // 将输入框的两个数据传到路线查找框中
                            map.clear();// 搜索之前清除之前的搜索路线
                            // 判断是驾车还是换乘
                            if (jiache_huancheng == jiache) {
                                // 走驾车方法
                                driverPlan(split_city, split_splans, trim_start,
                                        trim_end);
                            } else if (jiache_huancheng == huancheng) {
                                // 走换乘方法transfer_mode标示
                                translateroad("北京", trim_start, trim_end,
                                        transfer_mode);
                            }
                            // 隐藏对话框
                            create_road.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "请输入起始终点在进行查询。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                create_road.show();// 显示对话框

                // WindowManager.LayoutParams params =
                // create_road.getWindow().getAttributes();
                // params.width = LayoutParams.MATCH_PARENT;
                // params.height = 400 ;
                // create_road.getWindow().setAttributes(params);
                /*
                 * 将对话框的大小按屏幕大小的百分比设置
                 */
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                WindowManager.LayoutParams p = create_road.getWindow()
                        .getAttributes(); // 获取对话框当前的参数值
                p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
                p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的0.65
                create_road.getWindow().setAttributes(p);

                // Window window = create_road.getWindow();
                // WindowManager.LayoutParams lp = window.getAttributes();
                // lp.gravity = Gravity.CENTER;
                // lp.width = LayoutParams.MATCH_PARENT;//宽高可设置具体大小
                // lp.height = LayoutParams.MATCH_PARENT;
                // create_road.getWindow().setAttributes(lp);
                break;
            case R.id.bt_xiayiye:
                bt_xiayiye.setVisibility(View.GONE);
                bt_shangyiye.setVisibility(View.GONE);
                llweixing.setVisibility(View.VISIBLE);// 隐藏下一页，显示卫星按钮
                break;
            // case R.id.ib_xiao:
            // // 初始化驾车路线
            // driverPlan("北京", "", "生命科学园", "军事博物馆");
        }
        return true;
    }

    /**
     * 定位的方法
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onStart() {
        //开启定位
        mLocationClient.start();
        super.onStart();
    }

    @Override
    protected void onPause() {
        //失去焦点的时候，停止定位，省电，省流量
        mLocationClient.stop();
        mv_maptest.onPause();
        mv_maptest.showScaleControl(false);//关闭标尺
        super.onPause();
    }

    @Override
    protected void onResume() {
        mv_maptest.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mv_maptest.onDestroy();
        super.onDestroy();
    }

    public class MyLocationListeners implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location_result) {
            //拿到定位的经纬度
            location_latitude = location_result.getLatitude();
            location_longitude = location_result.getLongitude();
            MyLocationData location_data = new MyLocationData.Builder().latitude(location_latitude).longitude(location_longitude).build();
            //在定位生效之前调用下面一句。地图层生效
            map.setMyLocationEnabled(true);//打开图层
            map.setMyLocationData(location_data);
        }

    }
}
