package leon.android.translatevoice;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import leon.android.translatevoice.adapter.TranslateLanguageAdapter;
import leon.android.translatevoice.model.Language;

public class MainFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    //private ImageView mImageViewExpandMoreLanguage;
    private ImageView imageViewLanguageOne;
    private ImageView imageViewLanguageTwo;

    private RadioButton rbMicrophone;
    private RadioButton rbText;
    private RadioGroup rbGroup;

    private TextView textViewFlags;

    private RecyclerView mRecyclerView;
    private ArrayList<Language> mLanguageData;
    private TranslateLanguageAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //mImageViewExpandMoreLanguage = rootView.findViewById(R.id.imageViewExpandMoreLanguage);

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
        mLanguageData = new ArrayList<>();
        mAdapter = new TranslateLanguageAdapter(getActivity(), mLanguageData);
        mRecyclerView.setAdapter(mAdapter);

    }

//        public void onExpandMoreLanguage(View view) {
//        LanguageBottomSheetFragment languageBottomSheetFragment = new LanguageBottomSheetFragment();
//        languageBottomSheetFragment.show(getSupportFragmentManager(), languageBottomSheetFragment.getTag());
//    }


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewLanguageOne:
                if (rbText.isChecked()) {
                    mAdapter.addItems(new Language("RUS", "ENG"));
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                } else if (rbMicrophone.isChecked()) {
                    Toast.makeText(getActivity(), "RUS TEST", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imageViewLanguageTwo:
                if (rbText.isChecked()) {
                    mAdapter.addItems(new Language("ENG", "RUS"));
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                } else if (rbMicrophone.isChecked()) {
                    Toast.makeText(getActivity(), "ENG TEST", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rbText) {
            textViewFlags.setText("Tap a flag to start typing");
        } else if (checkedId == R.id.rbMicrophone) {
            textViewFlags.setText("Tap a flag to start speeking");
        }
    }
}
