package leon.android.translatevoice;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import leon.android.translatevoice.database.LanguageContract;
import leon.android.translatevoice.database.LanguageDBHelper;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Button btnDelete;
    private Button btnSendEmail;
    AlertDialogFragment alertDialogFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        btnDelete = rootView.findViewById(R.id.delete);
        btnSendEmail = rootView.findViewById(R.id.sendEmail);
        btnDelete.setOnClickListener(this);
        btnSendEmail.setOnClickListener(this);

        alertDialogFragment = new AlertDialogFragment();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                alertDialogFragment.show(getFragmentManager(), "alertDialog");
                break;

            case R.id.sendEmail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Application TranslateVoice");
                i.putExtra(Intent.EXTRA_TEXT, "Это работает");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
