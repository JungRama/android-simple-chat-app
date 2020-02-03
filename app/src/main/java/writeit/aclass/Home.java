package writeit.aclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import writeit.aclass.Adapter.SectionPageAdapter;

public class Home extends AppCompatActivity {

    Button btnLogout, btnAkun, btnFind;
    ViewPager viewPager;
    SectionPageAdapter PageAdapter;
    TabLayout mainTab;

    //       icon untuk tab
    private int[] tabIcons = {
            R.drawable.mail,
            R.drawable.user,
            R.drawable.lock
    };

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mainTab = (TabLayout) findViewById(R.id.mainTab);

        PageAdapter = new SectionPageAdapter(getSupportFragmentManager());
//      set page Adapter
        viewPager.setAdapter(PageAdapter);
        mainTab.setupWithViewPager(viewPager);



    }
}