package com.example.project_android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_android.R;
import com.example.project_android.activity.login.LoginActivity;
import com.example.project_android.fragment.main.InfoFragment;
import com.example.project_android.fragment.main.CourseListFragment;
import com.example.project_android.fragment.main.ModifyPasswordFragment;
import com.example.project_android.util.ProjectStatic;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.ui.companent.CircleImageView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.action_bar_title)
    TextView titleText;
    @BindView(R.id.drawer_layout_teacher)
    DrawerLayout drawerLayout;
    @BindView(R.id.action_bar_avatar)
    CircleImageView imageView;


    private NavController controller;
    private SharedPreferences preferences;

    private FragmentManager fragmentManager;
    private InfoFragment infoFragment;
    private CourseListFragment courseListFragment;
    private ModifyPasswordFragment modifyPasswordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        titleText.setText("课程列表");

        preferences = getSharedPreferences("localRecord",MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();
        initNavigation();

       showCourseList();

        Picasso.with(this)
                .load(ProjectStatic.SERVICE_PATH + preferences.getString("avatar",null))
                .fit()
                .error(R.drawable.ic_net_error)
                .into(imageView);
    }

    public void showCourseList(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        courseListFragment = new CourseListFragment();
        transaction.add(R.id.fragment_main,courseListFragment);
        transaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        controller = Navigation.findNavController(this,R.id.fragment_main);
        return controller.navigateUp();
    }

    @OnClick({R.id.action_bar_avatar})
    public void onClicked(View view){
        switch (view.getId()){
            case R.id.action_bar_avatar:
               //设置标题栏左部小头像的点击事件
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    private void initNavigation(){
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView userName = view.findViewById(R.id.drawer_header_userName);
        CircleImageView headerImage = view.findViewById(R.id.drawer_header_avatar);
        Picasso.with(this)
                .load(ProjectStatic.SERVICE_PATH + preferences.getString("avatar",null))
                .fit()
                .error(R.drawable.ic_net_error)
                .into(headerImage);
        userName.setText("欢迎您！" + preferences.getString("name",null));
        //设置导航抽屉菜单的默认选中项为“课程列表”
        navigationView.setCheckedItem(R.id.main_menu_course);
        //导航抽屉菜单设置了一个监听器
        navigationView.setNavigationItemSelectedListener(item -> {
            //关闭导航抽屉
            drawerLayout.closeDrawers();
            //执行具体跳转fragment的操作
            //为了进行Fragment的切换，需要开始一个Fragment事务
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //隐藏当前显示的Fragment
            hideAllFragment();
            switch (item.getItemId()){
                //查看课程列表
                case R.id.main_menu_course:
                    titleText.setText("课程列表");
                    if (courseListFragment == null){
                        //创建一个新的CourseListFragment实例
                        courseListFragment = new CourseListFragment();
                        //添加到R.id.fragment_main容器中
                        transaction.add(R.id.fragment_main,courseListFragment);
                    } else {
                        transaction.show(courseListFragment);
                    }
                    transaction.commit();
                    break;
                    //查看我的信息
                case R.id.main_menu_info:
                    titleText.setText("我的信息");
                    Toast.makeText(this, "查看我的信息", Toast.LENGTH_SHORT).show();
                    if (infoFragment == null){
                        infoFragment = new InfoFragment();
                        transaction.add(R.id.fragment_main,infoFragment);
                    } else {
                        transaction.show(infoFragment);
                    }
                    transaction.commit();
                    break;
                    //修改密码
                case R.id.main_menu_password:
                    titleText.setText("修改密码");
                    Toast.makeText(this, "修改密码", Toast.LENGTH_SHORT).show();
                    if (modifyPasswordFragment == null){
                        modifyPasswordFragment = new ModifyPasswordFragment();
                        modifyPasswordFragment.setSuccessListener(() -> {
                            hideAllFragment();
                            showCourseList();
                            navigationView.setCheckedItem(R.id.main_menu_course);
                            titleText.setText("课程列表");
                        });
                        transaction.add(R.id.fragment_main,modifyPasswordFragment);
                    } else {
                        transaction.show(modifyPasswordFragment);
                    }
                    transaction.commit();
                    break;
                    //退出登陆
                case R.id.main_menu_unregister:
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    public void hideAllFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (infoFragment != null){
            fragmentTransaction.hide(infoFragment);
        }
        if (courseListFragment != null){
            fragmentTransaction.hide(courseListFragment);
        }
        if (modifyPasswordFragment != null){
            fragmentTransaction.hide(modifyPasswordFragment);
        }
        fragmentTransaction.commit();
    }

}