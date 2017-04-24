package kz.sdu.margulan.trdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static kz.sdu.margulan.trdr.TrPage.lang1;
import static kz.sdu.margulan.trdr.TrPage.lang2;
import static kz.sdu.margulan.trdr.TrPage.langs;
import static kz.sdu.margulan.trdr.TrPage.refreshLanguage;
import static kz.sdu.margulan.trdr.TrPage.switchLanguage;

public class ChooseLanguageActivity extends AppCompatActivity{
    Intent i;
    String chooseLangButton;
    private ListView lv;

    @Override


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_choose_language);
        i = getIntent();
        chooseLangButton = i.getStringExtra("btnNumber");
        lv = (ListView) findViewById(R.id.lv);

        List<String> arrayList = new ArrayList<String>();
        for(int i = 0; i < langs.length; i++){
            arrayList.add(langs[i]);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList );

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(chooseLangButton.equals("1")){
                    if(langs[position].equals(lang2.getText().toString())){
                        switchLanguage();
                    }
                    else{
                        lang1.setText(langs[position]);
                        refreshLanguage();
                    }
                }
                else{
                    if(langs[position].equals(lang1.getText().toString())){
                        switchLanguage();
                    }
                    else{
                        lang2.setText(langs[position]);
                        refreshLanguage();
                    }
                }
                finish();
            }
        });


    }
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }



}
