package leon.android.translatevoice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.R;

public class TranslateLanguageAdapter extends RecyclerView.Adapter<TranslateLanguageAdapter.ViewHolder> {

    private ArrayList<Language> mLanguageTextData;
    private Context mContext;


    public TranslateLanguageAdapter(Context context, ArrayList<Language> languageTextData) {
        this.mLanguageTextData = languageTextData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public TranslateLanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_translated_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Language language = mLanguageTextData.get(position);
        holder.bindTo(language);
        holder.onExp();
    }


    @Override
    public int getItemCount() {
        return mLanguageTextData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFirstChoosenLanguage;
        private TextView textViewSecondChoosenLanguage;
        private TextView textViewSecondChoosenTranslateLanguage;

        private EditText editTextViewFirstChoosenTranslateLanguage;

        private ImageView imageViewFirstVolume, imageViewSecondVolume;
        private ImageView imageViewLanguageOne;
        private ImageView imageViewLanguageTwo;

        private LinearLayout linearLayout;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewFirstChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenLanguage);
            textViewSecondChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenLanguage);
            editTextViewFirstChoosenTranslateLanguage = (EditText) itemView.findViewById(R.id.editTextViewFirstChoosenTranslateLanguage);
            textViewSecondChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenTranslateLanguage);
            imageViewFirstVolume = (ImageView) itemView.findViewById(R.id.imageViewFirstVolume);
            imageViewSecondVolume = (ImageView) itemView.findViewById(R.id.imageViewSecondVolume);


        }

        private void bindTo(Language language) {
            textViewFirstChoosenLanguage.setText(language.getTitleOfLanguage());
            textViewSecondChoosenLanguage.setText(language.getTextOfLanguage());
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
