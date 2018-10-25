package com.example.pavel.testregistrationform;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class RegActivity extends AppCompatActivity {

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
    }

}
