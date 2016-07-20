package com.example.guru.pa;

import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class Bill extends AppCompatActivity {

    private FileOperate fileOperate;
    private String fileContent = null;
    private String[] lineContent;

    /* 必备的三个量：一个List（也可以为数组）,一个Adapter,一个ListView */
    private ArrayList<String> strs;
    private ArrayAdapter<String> arrayAdapter;
    private SwipeMenuListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        /**
        * 强烈不建议在onCreate里面进行以下操作
        * 数据量大时会让人感觉界面卡顿
        * 建议在另一个线程里加载，然后更新UI
        */

        fileOperate = new FileOperate(this);
        try {
            fileContent = fileOperate.read(MainActivity.FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        strs = new ArrayList<String>();

        if(fileContent == null){
            Toast.makeText(Bill.this, "打开文件失败", Toast.LENGTH_SHORT).show();
            strs.add("木有内容");
        }
        else {
            lineContent = fileContent.split("\n");

            int index = 0;
            for (String s : lineContent){
                strs.add(index, s);
                index++;
            }
        }

        /* 实例化ArrayAdapter */
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs);

        /* 实例化SwipeMenuListView */
        mListView = (SwipeMenuListView) findViewById(R.id.bill_list);

        /* 设置mListView的View最小高度 */
        mListView.setMinimumHeight(180);

        /* 实例化SwipeMenuCreator */
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                /* create "open" item */
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setWidth(180);
                openItem.setTitle("Open");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.rgb(0x00, 0x00, 0x00));
                // 添加到SwipeMenu
                menu.addMenuItem(openItem);

                /* create "delete" item */
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(180);
                deleteItem.setTitle("X"); /* 未来会换成icon */
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // 添加到SwipeMenu
                menu.addMenuItem(deleteItem);
            }
        };

        /* 给mListView设置Adapter,MenuCreator,设置滑动方向 */
        mListView.setAdapter(arrayAdapter);
        mListView.setMenuCreator(creator);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        /* 给mListView添加按钮点击监听 */
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        strs.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_bill,menu);

       /* SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        SearchableInfo info = searchManager.getSearchableInfo(cn);

        searchView.setSearchableInfo(info);*/

        /* 设置菜单项的搜索项 */
        MenuItem searchItem = menu.findItem(R.id.bill_search);

        /* 给搜索项添加展开和缩起监听器 */
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.bill_plus:
                Intent intent = new Intent(Bill.this, AddBill.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
