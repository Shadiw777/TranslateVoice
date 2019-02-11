package leon.android.translatevoice;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import leon.android.translatevoice.database.LanguageContract;
import leon.android.translatevoice.database.LanguageDBHelper;

public class AlertDialogFragment extends DialogFragment {

    SQLiteDatabase mDatabase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder
                .setView(inflater.inflate(R.layout.dialog_delete_conversation, null))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LanguageDBHelper db = new LanguageDBHelper(getActivity());
                        mDatabase = db.getWritableDatabase();
                        mDatabase.delete(LanguageContract.LanguageEntry.TABLE_NAME, null, null);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Dismiss", Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }
}
