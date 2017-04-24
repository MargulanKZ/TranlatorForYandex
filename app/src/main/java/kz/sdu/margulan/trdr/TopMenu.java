package kz.sdu.margulan.trdr;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class TopMenu extends Fragment implements View.OnClickListener {
    HistoryFragment stFr;
    TrPage TrFragment;
    FragmentTransaction fTrans;
    FavoriteFragment favFr;
    static ImageButton btnFav, btnStr, btnTr;

    public TopMenu() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_menu, container, false);

        btnFav = (ImageButton) (v.findViewById(R.id.btnFav));
        btnFav.setOnClickListener(this);

        btnStr = (ImageButton) (v.findViewById(R.id.btnStory));
        btnStr.setOnClickListener(this);

        btnTr = (ImageButton) (v.findViewById(R.id.btnTr));
        btnTr.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFav:
                fTrans = getFragmentManager().beginTransaction();
                favFr = new FavoriteFragment();
                fTrans.setCustomAnimations(R.animator.left_anim, R.animator.right_anim);
                fTrans.replace(R.id.mainFrame, favFr);
                fTrans.commit();
                break;
            case R.id.btnStory:
                fTrans = getFragmentManager().beginTransaction();
                stFr = new HistoryFragment();
                fTrans.setCustomAnimations(R.animator.left_anim, R.animator.right_anim);
                fTrans.replace(R.id.mainFrame, stFr);
                fTrans.commit();
                break;
            case R.id.btnTr:
                fTrans = getFragmentManager().beginTransaction();
                TrFragment = new TrPage();
                fTrans.setCustomAnimations(R.animator.left_anim, R.animator.right_anim);
                fTrans.replace(R.id.mainFrame, TrFragment);
                fTrans.commit();
                break;
        }
    }
}
