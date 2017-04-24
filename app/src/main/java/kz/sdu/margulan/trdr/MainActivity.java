package kz.sdu.margulan.trdr;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TrPage trPage;
    TopMenu topMenu;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trPage = new TrPage();
        topMenu = new TopMenu();

        Bundle args = new Bundle();
        args.putString("firstText", "");
        args.putString("secondText", "");
        args.putString("firstLang", "Английский");
        args.putString("secondLang", "Русский");
        trPage.setArguments(args);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.setCustomAnimations(R.animator.left_anim, R.animator.right_anim);
        fTrans.add(R.id.bottomFrame, topMenu);
        fTrans.add(R.id.mainFrame, trPage);
        fTrans.commit();

    }

}
