package leon.android.translatevoice;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import leon.android.translatevoice.adapter.TranslateLanguageAdapter;
import leon.android.translatevoice.database.LanguageContract;
import leon.android.translatevoice.database.LanguageDBHelper;
import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.translate.JNIEX;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    //private ImageView mImageViewExpandMoreLanguage;
    private ImageView imageViewLanguageOne;
    private ImageView imageViewLanguageTwo;

    private RadioButton rbMicrophone;
    private RadioButton rbText;
    private RadioGroup rbGroup;

    private TextView textViewFlags;

    private ProgressBar progressBar;

    private RecyclerView mRecyclerView;
    private List<Language> mLanguageData;
    private TranslateLanguageAdapter mAdapter;

    private SQLiteDatabase mDatabase;

    /* Карточка при нажатие на флаг */
    private CardView cardViewInputText;
    private TextView textViewOfChoosenLanguage;
    private EditText editTextViewOfChoosenLanguage;
    private ImageView imageViewApply;

    /*Класс перевода */
    JNIEX jniex;

    Language language = new Language();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);

        /* Привязываем наши View от CardView */
        cardViewInputText = rootView.findViewById(R.id.cardViewInputText);
        textViewOfChoosenLanguage = rootView.findViewById(R.id.textViewOfChoosenLanguage);
        editTextViewOfChoosenLanguage = rootView.findViewById(R.id.editTextViewOfChoosenLanguage);
        imageViewApply = rootView.findViewById(R.id.imageViewApply);

        //mImageViewExpandMoreLanguage = rootView.findViewById(R.id.imageViewExpandMoreLanguage);
        LanguageDBHelper db = new LanguageDBHelper(getActivity());
        mDatabase = db.getWritableDatabase();

        imageViewLanguageOne = rootView.findViewById(R.id.imageViewLanguageOne);
        imageViewLanguageTwo = rootView.findViewById(R.id.imageViewLanguageTwo);

        rbMicrophone = rootView.findViewById(R.id.rbMicrophone);
        rbText = rootView.findViewById(R.id.rbText);
        rbGroup = rootView.findViewById(R.id.rbGroup);

        textViewFlags = rootView.findViewById(R.id.textViewFlags);

        imageViewLanguageOne.setOnClickListener(this);
        imageViewLanguageTwo.setOnClickListener(this);
        rbGroup.setOnCheckedChangeListener(this);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewText);
        initRecyclerView();

        return rootView;
    }


    private void initRecyclerView() {

        /* Reverse RecyclerView */
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setSmoothScrollbarEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List mLanguageData = getElements();
        // List mLanguageData = new ArrayList();
        mAdapter = new TranslateLanguageAdapter(getActivity(), mLanguageData);
        mRecyclerView.setAdapter(mAdapter);

    }

//        public void onExpandMoreLanguage(View view) {
//        LanguageBottomSheetFragment languageBottomSheetFragment = new LanguageBottomSheetFragment();
//        languageBottomSheetFragment.show(getSupportFragmentManager(), languageBottomSheetFragment.getTag());
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageViewLanguageOne:
                if (rbText.isChecked()) {
                    jniex = new JNIEX();
                    textViewOfChoosenLanguage.setText("RUS");
                    initText();

                    imageViewApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                startAsyckTask("RUS", "ENG", "ru", "en");
                            } else {
                                Toast.makeText(getActivity(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else if (rbMicrophone.isChecked()) {
                    Toast.makeText(getActivity(), "RUS TEST", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imageViewLanguageTwo:
                if (rbText.isChecked()) {
                    jniex = new JNIEX();
                    textViewOfChoosenLanguage.setText("ENG");
                    initText();
                    imageViewApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                startAsyckTask("ENG", "RUS", "en", "ru");
                            } else {
                                Toast.makeText(getActivity(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else if (rbMicrophone.isChecked()) {
                    Toast.makeText(getActivity(), "ENG TEST", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void initText() {
        cardViewInputText.setVisibility(View.VISIBLE);

        editTextViewOfChoosenLanguage.requestFocus();
        editTextViewOfChoosenLanguage.setText("");

        /* Открывается клавиатура */
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        editTextViewOfChoosenLanguage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextViewOfChoosenLanguage.hasFocus() && !TextUtils.isEmpty(editTextViewOfChoosenLanguage.getText().toString())) {
                    imageViewApply.setImageResource(R.drawable.ic_check_circle);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Fragment fragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public List<Language> getElements() {
        List<Language> languages = new ArrayList<Language>();

        Cursor c = mDatabase.query(LanguageContract.LanguageEntry.TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE);
            int idCol1 = c.getColumnIndex(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT);
            int idCol2 = c.getColumnIndex(LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE);
            int idCol3 = c.getColumnIndex(LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT);

            do {
                Language obj = new Language();
                obj.setTitleOfFirstLanguage(c.getString(idColIndex));
                obj.setTitleOfSecondLanguage(c.getString(idCol2));
                obj.setTextOfFirstLanguage(c.getString(idCol1));
                obj.setTextOfSecondLanguage(c.getString(idCol3));
                languages.add(obj);
            } while (c.moveToNext());
        }
        return languages;
    }

    /*Смена текста в textView при нажатие на radionButton-ны */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rbText) {
            textViewFlags.setText("Tap a flag to start typing");
        } else if (checkedId == R.id.rbMicrophone) {
            textViewFlags.setText("Tap a flag to start speeking");
        }
    }


    private void startAsyckTask(String titleOfFirstLanguage, String titleOfSecondLanguage, String languageTranslateOne, String languageTranslateTwo) {
        Observable.just(doInBackground(titleOfFirstLanguage, titleOfSecondLanguage, languageTranslateOne, languageTranslateTwo))
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        return doInBackground(titleOfFirstLanguage, titleOfSecondLanguage, languageTranslateOne, languageTranslateTwo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        onPreExecute();
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        onPostExecute();
                    }
                });

    }

    private void onPreExecute() {
        textViewFlags.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        /*Закрытие клавиатуры */
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textViewOfChoosenLanguage.getWindowToken(), 0);

        /*Карточка уходит */
        cardViewInputText.setVisibility(View.GONE);
    }

    private int doInBackground(String titleOfFirstLanguage, String titleOfSecondLanguage, String languageTranslateOne, String languageTranslateTwo) {
        String translate = jniex.translate(getActivity(), languageTranslateOne, languageTranslateTwo, editTextViewOfChoosenLanguage.getText().toString());
        language.setTitleOfFirstLanguage(titleOfFirstLanguage);
        language.setTitleOfSecondLanguage(titleOfSecondLanguage);
        language.setTextOfFirstLanguage(editTextViewOfChoosenLanguage.getText().toString());
        language.setTextOfSecondLanguage(translate);

        ContentValues contentValues = new ContentValues();
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE, titleOfFirstLanguage);
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT, editTextViewOfChoosenLanguage.getText().toString());
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE, titleOfSecondLanguage);
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT, translate);
        mDatabase.insert(LanguageContract.LanguageEntry.TABLE_NAME, null, contentValues);

        return 0;
    }

    private void onPostExecute() {
        progressBar.setVisibility(View.GONE);
        textViewFlags.setVisibility(View.VISIBLE);
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        mAdapter.addItems(language);
    }
}

