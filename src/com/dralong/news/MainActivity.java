package com.dralong.news;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.dralong.fragment.*;
import com.dralong.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity {
    private RadioGroup mRadioGroup;

    private FragmentTabHost mTabHost;
    //按两次返回退出程序时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setContentView(R.layout.main);
//        Bundle args = getArguments();
//        String text = args != null ? args.getString("text") : "";
//        String Type = args != null ? args.getString("type") : "";
//        int id = args != null ? args.getInt("id") : 15;
        initView();
        initSlidingMenu();
    }

    void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.raidoGroupTab);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("news").setIndicator("新闻"), TabNewsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("read").setIndicator("阅读"), TabReadFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("va").setIndicator("视听"), TabVaFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("discovery").setIndicator("发现"), TabDiscoveryFragment.class, null);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.bottom_tab_news:
                        mTabHost.setCurrentTabByTag("news");
                        break;
                    case R.id.bottom_tab_read:
                        mTabHost.setCurrentTabByTag("read");
                        break;
                    case R.id.bottom_tab_va:
                        mTabHost.setCurrentTabByTag("va");
                        break;
                    case R.id.bottom_tab_discovery:
                        mTabHost.setCurrentTabByTag("discovery");
                        break;
                }

            }
        });
        ((RadioButton) mRadioGroup.getChildAt(0)).toggle(); //默认选中第一项

    }

    void initSlidingMenu() {
        SlidingMenu menu = new SlidingMenu(this);
        // 设置左滑菜单
        menu.setMode(SlidingMenu.LEFT);
        // 设置滑动的屏幕范围，该设置为全屏区域都可以滑动
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        // SlidingMenu划出时主页面显示的剩余宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置menu的布局文件
        menu.setMenu(R.layout.slidingmenu);
        // 设置SlidingMenu菜单的宽度
        menu.setBehindWidth(500);
        // SlidingMenu滑动时的渐变程度
        menu.setFadeDegree(0.35f);
        // 使SlidingMenu附加在Activity上
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    }

    /**
     * 按两次返回键推出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
