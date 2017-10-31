package net.uprin.mayiuseit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import net.uprin.mayiuseit.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    //TextView user_nickname,user_email;
    //CircleImageView user_img;
    //LinearLayout success_layout;
    //Button logout_btn;
    //AQuery aQuery;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViewPagerAndTabs();


        //aQuery = new AQuery(this);

        /** 로그인 성공 시 사용할 뷰
         success_layout = (LinearLayout)findViewById(R.id.success_layout);
         user_nickname =(TextView)findViewById(R.id.user_nickname);
         user_img =(CircleImageView) findViewById(R.id.user_img);
         user_email =(TextView)findViewById(R.id.user_email);
         logout_btn = (Button)findViewById(R.id.logout);
         logout_btn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        if(Session.getCurrentSession().isOpened()) {
        requestLogout();
        }
        }
        });
         Intent intent = getIntent();
         user_nickname.setText(intent.getExtras().getString("user_nickname"));
         user_email.setText(intent.getExtras().getString("user_email"));
         aQuery.id(user_img).image(intent.getExtras().getString("user_img")); // <- 프로필 작은 이미지 , userProfile.getProfileImagePath() <- 큰 이미지
         **/

    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(MainActivityFragment.createInstance(20), getString(R.string.tab_1));
        pagerAdapter.addFragment(MainActivityFragment.createInstance(4), getString(R.string.tab_2));
        pagerAdapter.addFragment(MainActivityFragment.createInstance(10), getString(R.string.tab_3));
        pagerAdapter.addFragment(MainActivityFragment.createInstance(10), getString(R.string.tab_4));
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }


    private void requestLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.item1_id){

            Toast.makeText(getApplicationContext(),"item1 is selected",Toast.LENGTH_SHORT).show();

        }else if (id==R.id.item2_id){

            Toast.makeText(getApplicationContext(),"item2 is selected",Toast.LENGTH_SHORT).show();


        }else if (id==R.id.item3_id){

            Toast.makeText(getApplicationContext(),"item3 is selected",Toast.LENGTH_SHORT).show();

        }else if (id==R.id.logout_menu_item){

            requestLogout();

        }else if (id==R.id.search_id){

            Toast.makeText(getApplicationContext(),"search",Toast.LENGTH_SHORT).show();

        }else if (id==R.id.cart_id){

            Toast.makeText(getApplicationContext(),"cart",Toast.LENGTH_SHORT).show();

        }else if (id==android.R.id.home){

            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
