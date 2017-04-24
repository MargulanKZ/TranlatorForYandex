package kz.sdu.margulan.trdr;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static kz.sdu.margulan.trdr.TopMenu.btnFav;
import static kz.sdu.margulan.trdr.TopMenu.btnStr;
import static kz.sdu.margulan.trdr.TopMenu.btnTr;
import static kz.sdu.margulan.trdr.TrPage.shortToLong;


public class FavoriteFragment extends Fragment implements View.OnClickListener {

    final int MENU_DELETE = 1;
    int changableId = 0;
    DBHelper dbh;
    SQLiteDatabase db;
    ImageButton btnClear;
    ListView lv;
    HistoryAdapter adapter;
    ArrayList<TrHistory> data = new ArrayList();
    ArrayList<TrHistory> forsearch = new ArrayList<>();
    AlertDialog.Builder ad;
    TextView emptyV;

    public FavoriteFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_story, container, false);
        btnFav.setActivated(true);
        btnStr.setActivated(false);
        btnTr.setActivated(false);
        lv = (ListView) v.findViewById(R.id.lv);
        emptyV = (TextView) (v.findViewById(R.id.emptyV));
        btnClear = (ImageButton) v.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        final EditText edSearch = (EditText) v.findViewById(R.id.edSearch);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = edSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
                if (adapter.isEmpty()) {
                    emptyV.setText("Ничего не найдено");
                    lv.setEmptyView(emptyV);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dbh = new DBHelper(getActivity().getApplicationContext());
        db = dbh.getWritableDatabase();

        refresh(null);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                // Указываем Title
                alertDialog.setTitle("ИЗБРАННОЕ");

                alertDialog.setMessage("Вы уверены, что хотите очистить избранное?");

                // Обработчик на нажатие ДА
                alertDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("update mytable set favourite=0");
                        refresh(null);
                    }
                });
                // Обработчик на нажатие НЕТ
                alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // показываем Alert
                alertDialog.show();

        }
    }

    @Override
    public void onStart() {
        refresh(null);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbh.close();
    }


    public void refresh(View v) {
        registerForContextMenu(lv);
        data.clear();
        Cursor c = db.query("mytable", null, "favourite=1", null, null, null, null);
        if (c.moveToFirst()) {
            do {
                int idH = c.getInt(c.getColumnIndex("id"));
                int fav = c.getInt(c.getColumnIndex("favourite"));
                String originalText = c.getString(c.getColumnIndex("original"));
                String afterTr = c.getString(c.getColumnIndex("afterTr"));
                String langtolang = c.getString(c.getColumnIndex("language"));

                TrHistory trHistory = new TrHistory();
                trHistory.favStatus = fav + "";
                trHistory.originalText = originalText;
                trHistory.afterTranslateText = afterTr;
                trHistory.langToLang = langtolang;
                trHistory.id = idH;
                data.add(trHistory);
            } while (c.moveToNext());
        }
        forsearch.clear();
        forsearch.addAll(data);
        adapter = new HistoryAdapter(getActivity().getApplicationContext(), data);
        if (adapter.getCount() == 0) {
            emptyV.setText("Нет переводов в избранном");
            lv.setEmptyView(emptyV);
        } else lv.setAdapter(adapter);

    }


    public class HistoryAdapter extends ArrayAdapter<TrHistory> {
        public HistoryAdapter(Context context, ArrayList<TrHistory> data) {
            super(context, 0, data);
        }

        ImageButton btnFavStatus;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final TrHistory trHistory = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.format_item, parent, false);
            }
            TextView tvOriginalText = (TextView) convertView.findViewById(R.id.firstLang);
            TextView tvAfterTranslateText = (TextView) convertView.findViewById(R.id.secondLang);
            TextView tvLangtoLang = (TextView) convertView.findViewById(R.id.tvLangToLang);
            btnFavStatus = (ImageButton) convertView.findViewById(R.id.btnAddtoFav);

            tvOriginalText.setText(trHistory.originalText);
            tvAfterTranslateText.setText(trHistory.afterTranslateText);
            tvLangtoLang.setText(trHistory.langToLang);
            if (trHistory.getFavStatus().equals("0")) {
                btnFavStatus.setActivated(false);

            } else {
                btnFavStatus.setActivated(true);
            }
            btnFavStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isActivated()) {

                        SQLiteDatabase db = dbh.getWritableDatabase();
                        db.execSQL("update mytable set favourite=0 where id=" + trHistory.getId());
                        v.setActivated(false);
                    } else {
                        SQLiteDatabase db = dbh.getWritableDatabase();
                        db.execSQL("update mytable set favourite=1 where id=" + trHistory.getId());
                        v.setActivated(true);

                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(trHistory.getId() + "");
                    TrPage trPage = new TrPage();
                    Bundle args = new Bundle();
                    args.putString("firstText", trHistory.getOriginalText());
                    args.putString("secondText", trHistory.getAfterTranslateText());
                    args.putString("firstLang", shortToLong.get(trHistory.langToLang.split("-")[0]));
                    args.putString("secondLang", shortToLong.get(trHistory.langToLang.split("-")[1]));


                    trPage.setArguments(args);
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.left_anim, R.animator.right_anim).replace(R.id.mainFrame, trPage).commit();


                }
            });
            // Return the completed view to render on screen
            return convertView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            data.clear();
            if (charText.length() == 0) {
                data.addAll(forsearch);
            } else {
                for (TrHistory tH : forsearch) {
                    if (tH.getOriginalText().toLowerCase(Locale.getDefault())
                            .contains(charText) || tH.getAfterTranslateText().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        data.add(tH);
                    }
                }
            }
            notifyDataSetChanged();
        }


    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        changableId = data.get(acmi.position).getId();
        menu.add(0, MENU_DELETE, 0, "Удалить");
        super.onCreateContextMenu(menu, v, menuInfo);


    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DELETE) {
            db.execSQL("update mytable SET favourite=0 where id=" + changableId);
            refresh(null);
        }
        return super.onContextItemSelected(item);
    }
}
