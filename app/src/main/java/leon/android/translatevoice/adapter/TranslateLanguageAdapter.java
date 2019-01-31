package leon.android.translatevoice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.R;
import leon.android.translatevoice.translate.JNIEX;

public class TranslateLanguageAdapter extends RecyclerView.Adapter<TranslateLanguageAdapter.ViewHolder> {

    private ArrayList<Language> mLanguageData;
    private Context mContext;
    JNIEX jniex;


    public TranslateLanguageAdapter(Context context, ArrayList<Language> languageTextData) {
        this.mLanguageData = languageTextData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public TranslateLanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_translated_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Language language = mLanguageData.get(position);
        holder.bindTo(language);
        holder.onExp();
        holder.editTextOpenKeyboard();
        holder.editTextButtonsChanges();
        holder.keyboardInputMethod();
        if (holder.editTextViewFirstChoosenTranslateLanguage.requestFocus()) {
            holder.imageViewFirstVolume.setImageResource(R.drawable.ic_check_circle_opacity);
        }

    }


    @Override
    public int getItemCount() {
        return mLanguageData.size();
    }

    public void addItems(Language language) {
        if (mLanguageData == null)
            mLanguageData = new ArrayList();
        mLanguageData.add(language);
        notifyItemInserted(mLanguageData.size() + 1);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFirstChoosenLanguage;
        private TextView textViewSecondChoosenLanguage;
        private TextView textViewSecondChoosenTranslateLanguage;

        private EditText editTextViewFirstChoosenTranslateLanguage;

        private ImageView imageViewFirstVolume;
        private ImageView imageViewSecondVolume;

        private LinearLayout linearLayout;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewFirstChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenLanguage);
            textViewSecondChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenLanguage);
            textViewSecondChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenTranslateLanguage);

            editTextViewFirstChoosenTranslateLanguage = (EditText) itemView.findViewById(R.id.editTextViewFirstChoosenTranslateLanguage);

            imageViewFirstVolume = (ImageView) itemView.findViewById(R.id.imageViewFirstVolume);
            imageViewSecondVolume = (ImageView) itemView.findViewById(R.id.imageViewSecondVolume);

        }

        private void editTextOpenKeyboard() {
            editTextViewFirstChoosenTranslateLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    editTextViewFirstChoosenTranslateLanguage.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editTextViewFirstChoosenTranslateLanguage, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                }
            });
        }

        private void editTextButtonsChanges() {
            editTextViewFirstChoosenTranslateLanguage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editTextViewFirstChoosenTranslateLanguage.hasFocus()) {
                        imageViewFirstVolume.setImageResource(R.drawable.ic_check_circle);
                        imageViewFirstVolume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imageViewChangeListener();
                            }
                        });
                    } else {
                        imageViewFirstVolume.setImageResource(R.drawable.ic_volume);
                        imageViewFirstVolume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext, "NOT OK", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        private void imageViewChangeListener() {
            String str = editTextViewFirstChoosenTranslateLanguage.getText().toString();
            textViewSecondChoosenTranslateLanguage.setText(str);

            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextViewFirstChoosenTranslateLanguage.getWindowToken(), 0);
            imageViewFirstVolume.setImageResource(R.drawable.ic_volume);
        }

        private void keyboardInputMethod() {
            editTextViewFirstChoosenTranslateLanguage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        imageViewChangeListener();
                        handled = true;
                    }
                    return handled;
                }
            });
        }

        private void bindTo(Language language) {
            textViewFirstChoosenLanguage.setText(language.getTitleOfFirstLanguage());
            textViewSecondChoosenLanguage.setText(language.getTitleOfSecondLanguage());
        }


        private void onExp() {
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayoutExp);
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


}
