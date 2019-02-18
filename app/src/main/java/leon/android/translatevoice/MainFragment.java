package leon.android.translatevoice;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import leon.android.translatevoice.adapter.TranslateLanguageAdapter;
import leon.android.translatevoice.common.Common;
import leon.android.translatevoice.database.LanguageContract;
import leon.android.translatevoice.database.LanguageDBHelper;
import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.translate.JNIEX;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TranslateLanguageAdapter.OnRecyclerListener, IOnBackPressed {

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

    private int result;
    private String translate = null;
    private String text_trans = null;

    private TextToSpeech textToSpeech;

    /*Класс перевода */
    JNIEX jniex;

    Language language = new Language();

    List<Language> listOfLanguages = new ArrayList<Language>();

    /*Speech To Text */
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private static final int REQUEST_RECORD_PERMISSION = 100;
    SpeechToText speechToText;


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

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


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
        mAdapter = new TranslateLanguageAdapter(getActivity(), mLanguageData, this, textToSpeech, result);
        mRecyclerView.setItemViewCacheSize(12);
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
                    textViewOfChoosenLanguage.setText("RUS");
                    initText();
                    imageViewApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Common.isConnectedToInternet(getActivity())) {
                                startAsyckTask("RUS", "ENG", "ru", "en");
                            } else {
                                Toast.makeText(getActivity(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else if (rbMicrophone.isChecked()) {
                    requestMicroPermission("ru", "en", "RUS", "ENG");
                }
                break;

            case R.id.imageViewLanguageTwo:
                if (rbText.isChecked()) {
                    textViewOfChoosenLanguage.setText("ENG");
                    initText();
                    imageViewApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Common.isConnectedToInternet(getActivity())) {
                                startAsyckTask("ENG", "RUS", "en", "ru");
                            } else {
                                Toast.makeText(getActivity(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else if (rbMicrophone.isChecked()) {
                    requestMicroPermission("en", "ru", "ENG", "RUS");
                }
                break;

            default:
                break;
        }
    }

    /*Запрашиваем разрешения на микрофон */
    private void requestMicroPermission(String langOne, String langTwo, String nameofFirstLang, String nameOfSecondLang) {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        initSpeechToText(langOne, langTwo, nameofFirstLang, nameOfSecondLang);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void initSpeechToText(String langOne, String langTwo, String nameofFirstLang, String nameOfSecondLang) {
        if (Common.isConnectedToInternet(getActivity())) {
            speechToText = new SpeechToText(langOne, langTwo, nameofFirstLang, nameOfSecondLang);
            speechToText.initSpeechToText();
            speech.startListening(recognizerIntent);
            textViewOfChoosenLanguage.setText(nameofFirstLang);
        } else {
            Toast.makeText(getActivity(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
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
        listOfLanguages = new ArrayList<Language>();

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
                listOfLanguages.add(obj);
            } while (c.moveToNext());
        }

        c.close();
        return listOfLanguages;
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
        Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.just(doInBackground(titleOfFirstLanguage, titleOfSecondLanguage, languageTranslateOne, languageTranslateTwo));
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
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        /*Карточка уходит */
        cardViewInputText.setVisibility(View.GONE);
    }

    private int doInBackground(String titleOfFirstLanguage, String titleOfSecondLanguage, String languageTranslateOne, String languageTranslateTwo) {
        language = new Language();
        jniex = new JNIEX();
        text_trans = editTextViewOfChoosenLanguage.getText().toString();
        translate = jniex.translate(getActivity(), languageTranslateOne, languageTranslateTwo, text_trans);
        language.setTitleOfFirstLanguage(titleOfFirstLanguage);
        language.setTitleOfSecondLanguage(titleOfSecondLanguage);
        language.setTextOfFirstLanguage(text_trans);
        language.setTextOfSecondLanguage(translate);

        ContentValues contentValues = new ContentValues();
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE, titleOfFirstLanguage);
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT, text_trans);
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE, titleOfSecondLanguage);
        contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT, translate);
        mDatabase.insert(LanguageContract.LanguageEntry.TABLE_NAME, null, contentValues);
        return 0;
    }

    private void onPostExecute() {
        progressBar.setVisibility(View.GONE);
        textViewFlags.setVisibility(View.VISIBLE);
        mAdapter.addItems(language);
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    @Override
    public void firstVolumeClick(int position) {
        if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(getActivity(), "Feature Not Support in your Device", Toast.LENGTH_LONG).show();
        } else {
            try {
                textToSpeech.speak(listOfLanguages.get(position).getTextOfFirstLanguage(), TextToSpeech.QUEUE_FLUSH, null);
            } catch (IndexOutOfBoundsException e) {
                textToSpeech.speak(listOfLanguages.get(position - 1).getTextOfFirstLanguage(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }

    }

    @Override
    public void secondVolumeClick(int position) {
        if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(getActivity(), "Feature Not Support in your Device", Toast.LENGTH_LONG).show();
        } else {
            try {
                textToSpeech.speak(listOfLanguages.get(position).getTextOfSecondLanguage(), TextToSpeech.QUEUE_FLUSH, null);
            } catch (IndexOutOfBoundsException e) {
                textToSpeech.speak(listOfLanguages.get(position - 1).getTextOfSecondLanguage(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();

        mDatabase.close();
    }

    @Override
    public boolean onBackPressed() {
        boolean myCondition = true;
        if (myCondition) {
            cardViewInputText.setVisibility(View.GONE);
            if (speech != null) {
                speech.cancel();
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            cardViewInputText.setVisibility(View.GONE);
            Log.i(LOG_TAG, "destroy");
        }
    }

    class SpeechToText implements RecognitionListener {
        String languageOne = null;
        String languageTwo = null;
        String nameOfFirstLanguage = null;
        String nameOfSecondLanguage = null;

        public SpeechToText(String languageOne, String languageTwo, String nameOfFirstLanguage, String nameOfSecondLanguage) {
            this.languageOne = languageOne;
            this.languageTwo = languageTwo;
            this.nameOfFirstLanguage = nameOfFirstLanguage;
            this.nameOfSecondLanguage = nameOfSecondLanguage;
        }

        private void initSpeechToText() {
            speech = SpeechRecognizer.createSpeechRecognizer(getActivity());
            Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(getActivity()));
            speech.setRecognitionListener(this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i(LOG_TAG, "onReadyForSpeech");
            cardViewInputText.setVisibility(View.VISIBLE);
            imageViewApply.setVisibility(View.INVISIBLE);
            editTextViewOfChoosenLanguage.setText("");
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.i(LOG_TAG, "onEndOfSpeech");
            cardViewInputText.setVisibility(View.GONE);
            textViewFlags.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            jniex = new JNIEX();
            Log.i(LOG_TAG, "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String trns_text = matches.get(0);
            translate = jniex.translate(getActivity(), languageOne, languageTwo, trns_text);

            language.setTextOfFirstLanguage(trns_text);
            language.setTextOfSecondLanguage(translate);
            language.setTitleOfFirstLanguage(nameOfFirstLanguage);
            language.setTitleOfSecondLanguage(nameOfSecondLanguage);

            ContentValues contentValues = new ContentValues();
            contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE, nameOfFirstLanguage);
            contentValues.put(LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT, trns_text);
            contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE, nameOfSecondLanguage);
            contentValues.put(LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT, translate);
            mDatabase.insert(LanguageContract.LanguageEntry.TABLE_NAME, null, contentValues);

            mAdapter.addItems(language);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            progressBar.setVisibility(View.INVISIBLE);
            textViewFlags.setVisibility(View.VISIBLE);
            imageViewApply.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null) {
                editTextViewOfChoosenLanguage.setText(matches.get(0));
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }
}

