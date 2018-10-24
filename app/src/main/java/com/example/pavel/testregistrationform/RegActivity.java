package com.example.pavel.testregistrationform;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class RegActivity extends AppCompatActivity {

    private AppCompatEditText textPassportNum;
    private AppCompatEditText textDriverLicenseNum;

    private AppCompatEditText textDriverLicenseDate;
    private AppCompatEditText textPassportDate;
    private AppCompatEditText textBirthDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        {
            ActionBar actionBar = getSupportActionBar();
            //Configure action bar
            // enable back button

            actionBar.setDisplayHomeAsUpEnabled(true);
            // set button image
            actionBar.setHomeAsUpIndicator(R.drawable.exit_button);
            //set title
            actionBar.setTitle(R.string.reg_title);
        }

        //find elements
        textPassportNum = findViewById(R.id.text_passport_num);

        textDriverLicenseNum = findViewById(R.id.text_driverlicense_num);
        textDriverLicenseDate = findViewById(R.id.text_driverlicense_date);
        textPassportDate = findViewById(R.id.text_passport_date);
        textBirthDate = findViewById(R.id.text_birthday);
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
