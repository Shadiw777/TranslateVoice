package leon.android.translatevoice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import leon.android.translatevoice.Adapter.TranslateLanguageAdapter;
import leon.android.translatevoice.Model.Language;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageViewExpandMoreLanguage;
    private ImageView imageViewLanguageOne, imageViewLanguageTwo, imageViewMicrophone, imageViewText, imageViewExpandMoreLanguage;

    private RecyclerView mRecyclerView;
    private ArrayList<Language> mLanguageData;
    private TranslateLanguageAdapter mAdapter;

    private EditText editTextViewFirstChoosenTranslateLanguage;
    private TextView textViewFirstChoosenLanguage, textViewSecondChoosenLanguage, textViewSecondChoosenTranslateLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageViewExpandMoreLanguage = findViewById(R.id.imageViewExpandMoreLanguage);
        mRecyclerView = findViewById(R.id.recyclerViewText);


        editTextViewFirstChoosenTranslateLanguage = findViewById(R.id.editTextViewFirstChoosenTranslateLanguage);
        textViewSecondChoosenTranslateLanguage = findViewById(R.id.textViewSecondChoosenTranslateLanguage);

        textViewFirstChoosenLanguage = findViewById(R.id.textViewFirstChoosenLanguage);
        textViewSecondChoosenLanguage = findViewById(R.id.textViewSecondChoosenLanguage);



        /* Reverse RecyclerView */
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLanguageData = new ArrayList<>();
        mAdapter = new TranslateLanguageAdapter(this, mLanguageData);
        mRecyclerView.setAdapter(mAdapter);


        imageViewLanguageOne = findViewById(R.id.imageViewLanguageOne);
        imageViewLanguageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = 0;
                for (i = 0; i < 10; i++) {
                    mLanguageData.add(new Language("TEST " + i, "TEST " + i));
                }
                mAdapter.notifyDataSetChanged();

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void onExpandMoreLanguage(View view) {
        LanguageBottomSheetFragment languageBottomSheetFragment = new LanguageBottomSheetFragment();
        languageBottomSheetFragment.show(getSupportFragmentManager(), languageBottomSheetFragment.getTag());
    }


    public void onExp(View view) {
        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutExp);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}
