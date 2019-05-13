package leon.android.translatevoice.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import leon.android.translatevoice.database.LanguageContract;
import leon.android.translatevoice.database.LanguageDBHelper;
import leon.android.translatevoice.model.Language;
import leon.android.translatevoice.R;

public class TranslateLanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Language> mLanguageData = new ArrayList<>();
    private Context mContext;
    private OnRecyclerListener onRecyclerListener;

    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;
    private static final int AD_DELTA = 5;

    SQLiteDatabase mDatabase;

    private TextToSpeech textToSpeech;
    private int result;


    public TranslateLanguageAdapter(Context context, List<Language> mLanguageData, OnRecyclerListener onRecyclerListener, TextToSpeech textToSpeech, int result) {
        this.mLanguageData = mLanguageData;
        this.mContext = context;
        this.onRecyclerListener = onRecyclerListener;
        this.textToSpeech = textToSpeech;
        this.result = result;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.banner_ad, parent, false);
            return new AdViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_translated_item, parent, false);
            return new MainViewHolder(itemView, onRecyclerListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == CONTENT_TYPE) {
            MainViewHolder baseholder = (MainViewHolder) holder;
            Language language = mLanguageData.get(getRealPosition(position));

            baseholder.textViewFirstChoosenLanguage.setText(language.getTitleOfFirstLanguage());
            baseholder.textViewFirstChoosenTranslateLanguage.setText(language.getTextOfFirstLanguage());
            baseholder.textViewSecondChoosenLanguage.setText(language.getTitleOfSecondLanguage());
            baseholder.textViewSecondChoosenTranslateLanguage.setText(language.getTextOfSecondLanguage());

            baseholder.imageViewShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Исходный текст: " + baseholder.textViewFirstChoosenTranslateLanguage.getText().toString() + "\n"
                            + "Переведённый текст: " + baseholder.textViewSecondChoosenTranslateLanguage.getText().toString());
                    intent.setType("text/plain");
                    mContext.startActivity(Intent.createChooser(intent, "Send To"));
                }
            });

            baseholder.imageViewCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Translated text", baseholder.textViewSecondChoosenTranslateLanguage.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, "Hey you copy " + clip.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            baseholder.imageViewMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("smsto:0800000123");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra("sms_body", baseholder.textViewSecondChoosenTranslateLanguage.getText().toString());
                    mContext.startActivity(intent);
                }
            });

            //TODO Сделать удаление элемента из бд(удаляется только если была кликнута сначала 1 позиция)
            baseholder.imageViewTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LanguageDBHelper db = new LanguageDBHelper(v.getContext());
                    mDatabase = db.getWritableDatabase();
                    mDatabase.execSQL("DELETE FROM " + LanguageContract.LanguageEntry.TABLE_NAME + " WHERE " + position + ";");
                    mDatabase.delete(LanguageContract.LanguageEntry.TABLE_NAME, LanguageContract.LanguageEntry._ID + " =? ", new String[]{String.valueOf(position)});
                    mDatabase.close();
                    mLanguageData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mLanguageData.size());

                }
            });


            baseholder.onExp();

        } else if (getItemViewType(position) == AD_TYPE) {
            ((AdViewHolder) holder).imageViewAdBanner.setImageResource(R.drawable.example_ad);
            holder.getAdapterPosition();
        }
    }


    @Override
    public int getItemCount() {
        int content = 0;
        if (mLanguageData.size() > 0 && AD_DELTA > 0 && mLanguageData.size() > AD_DELTA) {
            content = (mLanguageData.size() + (mLanguageData.size() / AD_DELTA)) / AD_DELTA;
        }
        return mLanguageData.size() + content;
    }

    private int getRealPosition(int position) {
        if (AD_DELTA == 0) {
            return position;
        } else {
            return position - position / AD_DELTA;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 0 && position % AD_DELTA == 0) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }

    public void addItems(Language language) {
        mLanguageData.add(language);
        notifyItemInserted(mLanguageData.size() + 1);
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAdBanner;

        public AdViewHolder(View items) {
            super(items);
            // this.imageViewAdBanner = (ImageView) itemView.findViewById(R.id.imageViewAdBanner);
            imageViewAdBanner = items.findViewById(R.id.imageViewAdBanner);

        }
    }


    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewFirstChoosenLanguage;
        private TextView textViewFirstChoosenTranslateLanguage;
        private TextView textViewSecondChoosenLanguage;
        private TextView textViewSecondChoosenTranslateLanguage;

        private ImageView imageViewFirstVolume;
        private ImageView imageViewSecondVolume;

        private LinearLayout linearLayout;
        private ConstraintLayout constraintLayout;

        private ImageView imageViewShare;
        private ImageView imageViewCopy;
        private ImageView imageViewMessage;
        private ImageView imageViewTrash;

        OnRecyclerListener onRecyclerListener;

        public MainViewHolder(View itemView, OnRecyclerListener onRecyclerListener) {
            super(itemView);
            this.onRecyclerListener = onRecyclerListener;

            textViewFirstChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenLanguage);
            textViewSecondChoosenLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenLanguage);
            textViewSecondChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewSecondChoosenTranslateLanguage);
            textViewFirstChoosenTranslateLanguage = (TextView) itemView.findViewById(R.id.textViewFirstChoosenTranslateLanguage);

            imageViewFirstVolume = (ImageView) itemView.findViewById(R.id.imageViewFirstVolume);
            imageViewSecondVolume = (ImageView) itemView.findViewById(R.id.imageViewSecondVolume);

            imageViewShare = itemView.findViewById(R.id.imageViewShare);
            imageViewCopy = itemView.findViewById(R.id.imageViewCopy);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage);
            imageViewTrash = itemView.findViewById(R.id.imageViewTrash);

            imageViewFirstVolume.setOnClickListener(this);
            imageViewSecondVolume.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageViewFirstVolume:
                    onRecyclerListener.firstVolumeClick(getAdapterPosition());
                    break;
                case R.id.imageViewSecondVolume:
                    onRecyclerListener.secondVolumeClick(getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnRecyclerListener {

        void firstVolumeClick(int position);

        void secondVolumeClick(int position);

    }


}
