package leon.android.translatevoice.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
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
import java.util.List;

import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.R;

public class TranslateLanguageAdapter extends RecyclerView.Adapter<TranslateLanguageAdapter.ViewHolder> {

    private List<Language> mLanguageData;
    private Context mContext;

    public TranslateLanguageAdapter(Context context, List<Language> mLanguageData) {
        this.mLanguageData = mLanguageData;
        this.mContext = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TranslateLanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_translated_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Language language = mLanguageData.get(position);

        holder.textViewFirstChoosenLanguage.setText(language.getTitleOfFirstLanguage());
        holder.textViewFirstChoosenTranslateLanguage.setText(language.getTextOfFirstLanguage());
        holder.textViewSecondChoosenLanguage.setText(language.getTitleOfSecondLanguage());
        holder.textViewSecondChoosenTranslateLanguage.setText(language.getTextOfSecondLanguage());

        holder.onExp();
    }


    @Override
    public int getItemCount() {
        return mLanguageData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addItems(Language language) {
        if (mLanguageData == null)
            mLanguageData = new ArrayList<>();
        mLanguageData.add(language);
        notifyItemInserted(mLanguageData.size() + 1);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFirstChoosenLanguage;
        private TextView textViewFirstChoosenTranslateLanguage;
        private TextView textViewSecondChoosenLanguage;
        private TextView textViewSecondChoosenTranslateLanguage;

        private ImageView imageViewFirstVolume;
        private ImageView imageViewSecondVolume;

        private LinearLayout linearLayout;
        private ConstraintLayout constraintLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            textViewFirstChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenLanguage);
            textViewSecondChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenLanguage);
            textViewSecondChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenTranslateLanguage);
            textViewFirstChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenTranslateLanguage);

            imageViewFirstVolume = (ImageView) itemView.findViewById(R.id.imageViewFirstVolume);
            imageViewSecondVolume = (ImageView) itemView.findViewById(R.id.imageViewSecondVolume);
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
