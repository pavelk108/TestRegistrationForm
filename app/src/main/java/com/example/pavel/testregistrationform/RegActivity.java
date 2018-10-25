package com.example.pavel.testregistrationform;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class RegActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private AppCompatEditText textName;
    private AppCompatEditText textSurame;
    private AppCompatEditText textExname;
    private AppCompatEditText textBirthDate;
    private AppCompatEditText textEMail;


    private AppCompatEditText textPassportNum;
    private AppCompatEditText textPassportWho;
    private AppCompatEditText textPassportDate;
    private AppCompatTextView butPassportSelfie;
    private AppCompatTextView butPassportPhoto1;
    private AppCompatTextView butPassportPhoto2;

    private AppCompatEditText textDriverLicenseNum;
    private AppCompatEditText textDriverLicenseDate;
    private AppCompatTextView butDriverLicensePhoto1;
    private AppCompatTextView butDriverLicensePhoto2;

    private AppCompatTextView butShowContact;
    private AppCompatTextView butShowPersAgreement;
    private SwitchCompat swicthContract;
    private SwitchCompat swicthPers;

    private AppCompatButton butNext;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.US);
    private Date dateBirth;
    private Date datePassport;
    private Date dateDriverLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                //Configure action bar
                // enable back button
                actionBar.setDisplayHomeAsUpEnabled(true);
                // set button image
                actionBar.setHomeAsUpIndicator(R.drawable.exit_button);
                //set title
                actionBar.setTitle(R.string.reg_title);
            }
        }

        //find elements
        textName = findViewById(R.id.text_name);
        textSurame = findViewById(R.id.text_surname);
        textExname = findViewById(R.id.text_exname);
        textBirthDate = findViewById(R.id.text_birthday);
        textEMail = findViewById(R.id.text_email);

        textPassportNum = findViewById(R.id.text_passport_num);
        textPassportWho = findViewById(R.id.text_passport_who);
        textPassportDate = findViewById(R.id.text_passport_date);
        butPassportSelfie = findViewById(R.id.button_passport_selfie);
        butPassportPhoto1 = findViewById(R.id.button_passport_photo1);
        butPassportPhoto2 = findViewById(R.id.button_passport_photo2);

        textDriverLicenseNum = findViewById(R.id.text_driverlicense_num);
        textDriverLicenseDate = findViewById(R.id.text_driverlicense_date);
        butDriverLicensePhoto1 = findViewById(R.id.button_driverlicense_photo1);
        butDriverLicensePhoto2 = findViewById(R.id.button_driverlicense_photo2);

        butShowContact = findViewById(R.id.button_show_contract);
        butShowPersAgreement = findViewById(R.id.button_show_pers_agreement);
        swicthContract = findViewById(R.id.switch_agree_contract);
        swicthPers = findViewById(R.id.switch_agree_pers);

        butNext = findViewById(R.id.but_next);

        // set masks
        { //passport
            MaskFormatWatcher formatWatcher = new MaskFormatWatcher(
                    MaskImpl.createTerminated(PredefinedSlots.RUS_PASSPORT)
            );
            formatWatcher.installOn(textPassportNum);
        }
        { // driver license
            Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("__ __ ______");
            MaskFormatWatcher formatWatcher = new MaskFormatWatcher(
                    MaskImpl.createTerminated(slots)
            );
            formatWatcher.installOn(textDriverLicenseNum);
        }
        { // dates
            Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("__.__.__");
            {
                MaskFormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
                formatWatcher.installOn(textBirthDate);
            }
            {
                MaskFormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
                formatWatcher.installOn(textPassportDate);
            }
            {
                MaskFormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
                formatWatcher.installOn(textDriverLicenseDate);
            }
        }

        butNext.setOnClickListener(this);
        butShowPersAgreement.setOnClickListener(this);
        butShowContact.setOnClickListener(this);
        butPassportSelfie.setOnClickListener(this);
        butPassportPhoto1.setOnClickListener(this);
        butPassportPhoto2.setOnClickListener(this);
        butDriverLicensePhoto1.setOnClickListener(this);
        butDriverLicensePhoto2.setOnClickListener(this);

        textName.setOnFocusChangeListener(this);
        textSurame.setOnFocusChangeListener(this);
        textExname.setOnFocusChangeListener(this);
        textBirthDate.setOnFocusChangeListener(this);
        textEMail.setOnFocusChangeListener(this);

        textPassportNum.setOnFocusChangeListener(this);
        textPassportWho.setOnFocusChangeListener(this);
        textPassportDate.setOnFocusChangeListener(this);

        textDriverLicenseNum.setOnFocusChangeListener(this);
        textDriverLicenseDate.setOnFocusChangeListener(this);
    }

    private void markRed(View view) {
        view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
    }

    private boolean checkLength(AppCompatEditText editText, int length) {
        if (editText.getText().length() == length) {
            return true;
        } else {
            markRed(editText);
            return false;
        }
    }

    private boolean checkNotEmpty(AppCompatEditText editText) {
        if (editText.getText().length() != 0)
            return true;
        else {
            markRed(editText);
            return false;
        }
    }

    // Date date - obj to save parsed date
    private boolean checkDate(AppCompatEditText editText, Date date) {
        try {
            date = sdf.parse(editText.getText().toString());
        } catch (ParseException e) {
            markRed(editText);
            return false;
        }
        if (date != null) {
            return true;
        } else {
            markRed(editText);
            return false;
        }
    }

    private boolean checkEmail(AppCompatEditText editText) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(editText.getText());
        if (matcher.find()) {
            return true;
        } else  {
            markRed(editText);
            return false;
        }
    }

    private boolean checkSwitch(SwitchCompat switchCompat) {
        if (switchCompat.isChecked())
            return true;
        else {
            markRed(switchCompat);
            return false;
        }
    }

    private boolean check() {
        boolean flag = true;
        // pers data fields must be not empty
        if (!checkNotEmpty(textName)) flag = false;
        if (!checkNotEmpty(textSurame)) flag = false;
        if (!checkNotEmpty(textExname)) flag = false;
        if (!checkNotEmpty(textPassportWho)) flag = false;

        // easy check email
        if (!checkEmail(textEMail)) flag = false;

        // check num passport and num driver license
        // only check length
        // other checked by tinkoff
        if (!checkLength(textPassportNum, 11)) flag = false; // хххх хххххх
        if (!checkLength(textDriverLicenseNum, 12)) flag = false; // хх хх хххххх

        //check date fields
        if (!checkDate(textBirthDate, dateBirth)) flag = false;
        if (!checkDate(textPassportDate, datePassport)) flag = false;
        if (!checkDate(textDriverLicenseDate, dateDriverLicense)) flag = false;

        // check that switches are checked
        if (!checkSwitch(swicthContract)) flag = false;
        if (!checkSwitch(swicthPers)) flag = false;

        return flag;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but_next:
                if (check()) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    // successful registration
                } else break;
            case R.id.button_show_contract:
                {
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra(
                            WebViewActivity.extraTitle,
                            getResources().getString(R.string.сontract_activity_title)
                    );
                    intent.putExtra(
                            WebViewActivity.extraURL,
                            "https://ya.ru"
                            //"https://drive.google.com/viewerng/viewer?embedded=true&url=https://lifcar.ru/lifcar_agreement.pdf"
                    );
                    startActivity(intent);
                }
                break;
            case R.id.button_show_pers_agreement:
                {
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra(
                            WebViewActivity.extraTitle,
                            getResources().getString(R.string.pers_activity_title)
                    );
                    intent.putExtra(
                            WebViewActivity.extraURL,
                            "https://ya.ru"
                            //"https://drive.google.com/viewerng/viewer?embedded=true&url=https://lifcar.ru/lifcar_agreement.pdf"
                    );
                    startActivity(intent);
                }
                break;
            default: break;
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        view.getBackground().clearColorFilter();
    }
}
