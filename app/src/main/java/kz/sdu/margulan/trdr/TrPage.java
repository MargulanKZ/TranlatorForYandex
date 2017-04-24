package kz.sdu.margulan.trdr;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

import static kz.sdu.margulan.trdr.TopMenu.btnFav;
import static kz.sdu.margulan.trdr.TopMenu.btnStr;
import static kz.sdu.margulan.trdr.TopMenu.btnTr;


public class TrPage extends Fragment implements View.OnClickListener {


    public TrPage() {
    }

    Intent intent;
    String str = "";
    EditText ed;
    TextView translatedTV;
    static Button lang1, lang2;
    String strT = "";
    Button clearET;
    String firstLang = "Английский", secondLang = "Русский";
    DBHelper dbHelper;
    static HashMap<String, String> langMap = new HashMap<>();
    static HashMap<String, String> shortToLong = new HashMap<>();
    static String langsShort[] = new String[]{"en", "ru", "kk", "es", "fr", "tr"};
    static String langs[] = new String[]{"Английский", "Русский", "Казахский", "Испанский", "Французский", "Турецкий"};


    static String langtolang = "en-ru";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            str = getArguments().getString("firstText");
            strT = getArguments().getString("secondText");
            firstLang = getArguments().getString("firstLang");
            secondLang = getArguments().getString("secondLang");
        }


        for (int i = 0; i < langs.length; i++) {
            langMap.put(langs[i], langsShort[i]);
        }

        for (int i = 0; i < langs.length; i++) {
            shortToLong.put(langsShort[i], langs[i]);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tr_page, container, false);

        btnTr.setActivated(true);
        btnFav.setActivated(false);
        btnStr.setActivated(false);
        lang1 = (Button) (v.findViewById(R.id.lang1));
        lang2 = (Button) (v.findViewById(R.id.lang2));
        lang1.setText(firstLang);
        lang2.setText(secondLang);
        refreshLanguage();
        System.out.println(langtolang);
        clearET = (Button) v.findViewById(R.id.clearET);
        clearET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.setText("");
            }
        });
        translatedTV = (TextView) (v.findViewById(R.id.textView2));
        ed = (EditText) (v.findViewById(R.id.TrWord));

        ed.setText(str);
        translatedTV.setText(strT);
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                str = ed.getText().toString();
                TranslateOperation translateOperation = new TranslateOperation();
                translateOperation.execute("");
                System.out.println(langtolang);
            }
        });

        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !strT.equals("")) {
                    ContentValues cv = new ContentValues();
                    // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put("original", str);
                    cv.put("afterTr", strT);
                    cv.put("favourite", 0);
                    cv.put("language", langtolang);
                    db.insert("mytable", null, cv);

                    dbHelper.close();
                }
            }
        });


        lang1.setOnClickListener(this);
        lang2.setOnClickListener(this);

        ImageButton switchLang = (ImageButton) (v.findViewById(R.id.switchLang));
        switchLang.setOnClickListener(this);

        dbHelper = new DBHelper(getActivity().getApplicationContext());

        return v;
    }

    public static String getTranslateFromJSON(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) parser.parse(str);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        JSONArray array = null;
        array = (JSONArray) object.get("text");
        for (Object s : array) {
            sb.append(s.toString() + "\n");
        }
        return sb.toString();
    }

    public static String getJsonStringYandex(String trans, String text) throws IOException, ParseException {
        String apiKey = "trnsl.1.1.20170401T195912Z.5d662f73e0afd2ff.d50591af0ee375d28ef2086a548848a1a62a11c6";
        text = text.replaceAll(" ", "+");
        String requestUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key="
                + apiKey + "&lang=" + trans + "&text=" + text;
        URL url = new URL(requestUrl);

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        int rc = httpConnection.getResponseCode();

        if (rc == 200) {
            String line = null;
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                strBuilder.append(line + '\n');
            }

            return getTranslateFromJSON(strBuilder.toString());
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchLang:
                switchLanguage();
                break;
            case R.id.lang1:
                intent = new Intent(getActivity().getApplicationContext(), ChooseLanguageActivity.class);
                intent.putExtra("btnNumber", "1");
                startActivity(intent);
                break;
            case R.id.lang2:
                intent = new Intent(getActivity().getApplicationContext(), ChooseLanguageActivity.class);
                intent.putExtra("btnNumber", "2");
                startActivity(intent);
                break;
        }


    }

    public static void switchLanguage() {
        String tmp = lang1.getText().toString();
        lang1.setText(lang2.getText());
        lang2.setText(tmp);
        refreshLanguage();
    }

    public static void refreshLanguage() {
        langtolang = langMap.get(lang1.getText().toString()) + "-" + langMap.get(lang2.getText().toString());
    }

    private class TranslateOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                System.out.println(langtolang);
                strT = getJsonStringYandex(langtolang, str);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            translatedTV.setText(strT);


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
