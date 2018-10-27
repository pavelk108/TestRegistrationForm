package com.example.pavel.testregistrationform;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;
import com.example.pavel.testregistrationform.MyAdapter.ListItem;
import com.example.pavel.testregistrationform.MyAdapter.ItemsTypes;

public class RegActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final String PHOTO_SELFIE_FILE_NAME = "photoSelfie.jpg";
    public static final String PHOTO_PAS1_FILE_NAME = "photoPas1.jpg";
    public static final String PHOTO_PAS2_FILE_NAME = "photoPas2.jpg";
    public static final String PHOTO_DL1_FILE_NAME = "photoDL1.jpg";
    public static final String PHOTO_DL2_FILE_NAME = "photoDL2.jpg";

    private static final int ITEM_ID_SURNAME = 1;
    private static final int ITEM_ID_NAME = 2;
    private static final int ITEM_ID_EXNAME = 3;
    private static final int ITEM_ID_BIRTHDATE = 4;
    private static final int ITEM_ID_EMAIL = 5;
    private static final int ITEM_ID_PAS_NUM = 6;
    private static final int ITEM_ID_PAS_DATE = 7;
    private static final int ITEM_ID_PAS_WHO = 8;
    private static final int ITEM_ID_PAS_SELFIE = 9;
    private static final int ITEM_ID_PAS_PHOTO1 = 10;
    private static final int ITEM_ID_PAS_PHOTO2 = 11;
    private static final int ITEM_ID_DL_NUM = 12;
    private static final int ITEM_ID_DL_DATE = 13;
    private static final int ITEM_ID_DL_PHOTO1 = 14;
    private static final int ITEM_ID_DL_PHOTO2 = 15;
    private static final int ITEM_ID_SHOW_CONTRACT = 16;
    private static final int ITEM_ID_AGREE_CONTRACT = 17;
    private static final int ITEM_ID_SHOW_AGREEMENT = 18;
    private static final int ITEM_ID_AGREE_AGREEMENT = 19;
    private static final int ITEM_ID_NEXT = 20;

    private static final String MASK_DATE = "__.__.__";
    private static final String MASK_PAS_NUM = "__ __ ______";
    private static final String MASK_DL_NUM = "__ __ ______";


    private ListView listView;
    private MyAdapter adapter;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.US);
    private Date dateBirth = new Date();
    private Date datePassport = new Date();
    private Date dateDriverLicense = new Date();

    private File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        final Resources resources = getResources();
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

        {
            listView = findViewById(R.id.list_pers_data);
            listView.setHeaderDividersEnabled(false);
            TextView header = (TextView) getLayoutInflater().inflate(R.layout.list_view_header, null);
            header.setText(R.string.personal_data_section_header);

            Slot[] slotsDate = new UnderscoreDigitSlotsParser().parseSlots(MASK_DATE);
            Slot[] slotsDL = new UnderscoreDigitSlotsParser().parseSlots(MASK_DL_NUM);
            Slot[] slotsPassport = new UnderscoreDigitSlotsParser().parseSlots(MASK_PAS_NUM);

            adapter = new MyAdapter(
                    getLayoutInflater(),
                    new ListItem[] {
                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.personal_data_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_surname))
                                    .setID(ITEM_ID_SURNAME),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_name))
                                    .setID(ITEM_ID_NAME),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_exname))
                                    .setID(ITEM_ID_EXNAME),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_birthday))
                                    .setID(ITEM_ID_BIRTHDATE)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_email))
                                    .setID(ITEM_ID_EMAIL),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.passport_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_passport_num))
                                    .setID(ITEM_ID_PAS_NUM)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsPassport))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_passport_date))
                                    .setID(ITEM_ID_PAS_DATE)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_passport_who))
                                    .setID(ITEM_ID_PAS_WHO)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_selfie))
                                    .setImg(resources.getDrawable(R.drawable.ic_photo))
                                    .setID(ITEM_ID_PAS_SELFIE),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_first))
                                    .setID(ITEM_ID_PAS_PHOTO1)
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_second))
                                    .setID(ITEM_ID_PAS_PHOTO2)
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.driverlicense_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_driverlicense_num))
                                    .setID(ITEM_ID_DL_NUM)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDL))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_driverlicense_date))
                                    .setID(ITEM_ID_DL_DATE)
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_driverlicense_photo_first))
                                    .setID(ITEM_ID_DL_PHOTO1)
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_driverlicense_photo_second))
                                    .setID(ITEM_ID_DL_PHOTO2)
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.law_section_header)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_NARROW,
                                    resources.getString(R.string.reg_law_contract))
                                    .setID(ITEM_ID_SHOW_CONTRACT),
                            new ListItem(ItemsTypes.SWITCH,
                                    resources.getString(R.string.reg_law_contract_agree))
                                    .setID(ITEM_ID_AGREE_CONTRACT),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_NARROW,
                                    resources.getString(R.string.reg_law_pers))
                                    .setID(ITEM_ID_SHOW_AGREEMENT),
                            new ListItem(ItemsTypes.SWITCH,
                                    resources.getString(R.string.reg_law_pers_agree))
                                    .setID(ITEM_ID_AGREE_AGREEMENT),
                            new ListItem(ItemsTypes.BUTTON,
                                    resources.getString(R.string.reg_next))
                                    .setID(ITEM_ID_NEXT)
                    }
            );

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

        }

    }

    private void markRed(ListItem item) {
        item.is_red = true;
        listView.invalidateViews();
    }

    private boolean checkLength(int id, int length) {
        ListItem item = adapter.getItemByID(id);
            if (item.usertext.length() == length) {
            return true;
        } else {
            markRed(item);
            return false;
        }
    }

    private boolean checkNotEmpty(int id) {
        ListItem item = adapter.getItemByID(id);
        if (item.usertext.length() > 0) {
            return true;
        } else {
            markRed(item);
            return false;
        }
    }

    // Date date - obj to save parsed date
    private boolean checkDate(int id, Date date) {
        ListItem item = adapter.getItemByID(id);
        try {
            // save Date
            date.setTime(sdf.parse(item.usertext).getTime());
            if (!sdf.format(date).equals(item.usertext)) {
                markRed(item);
                return false;
            }
        } catch (Exception e) {
            markRed(item);
            return false;
        }
        return true;
    }

    private boolean checkEmail(int id) {
        ListItem item = adapter.getItemByID(id);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(item.usertext);
        if (matcher.find()) {
            return true;
        } else  {
            markRed(item);
            return false;
        }
    }

    private boolean checkSwitch(int id) {
        ListItem item = adapter.getItemByID(id);
        if (item.checked)
            return true;
        else {
            markRed(item);
            return false;
        }
    }

    private boolean check() {
        boolean flag = true;
        /// pers data fields must be not empty
        if (!checkNotEmpty(ITEM_ID_SURNAME)) flag = false;
        if (!checkNotEmpty(ITEM_ID_NAME)) flag = false;
        if (!checkNotEmpty(ITEM_ID_EXNAME)) flag = false;
        if (!checkNotEmpty(ITEM_ID_PAS_WHO)) flag = false;

        // easy check email
        if (!checkEmail(ITEM_ID_EMAIL)) flag = false;

        // check num passport and num driver license
        // only check length
        // other checked by tinkoff
        if (!checkLength(ITEM_ID_PAS_NUM, MASK_PAS_NUM.length())) flag = false;
        if (!checkLength(ITEM_ID_DL_NUM, MASK_DL_NUM.length())) flag = false;

        //check date fields
        if (!checkDate(ITEM_ID_BIRTHDATE, dateBirth)) flag = false;
        if (!checkDate(ITEM_ID_PAS_DATE, datePassport)) flag = false;
        if (!checkDate(ITEM_ID_DL_DATE, dateDriverLicense)) flag = false;

        // check that switches are checked
        if (!checkSwitch(ITEM_ID_AGREE_CONTRACT)) flag = false;
        if (!checkSwitch(ITEM_ID_AGREE_AGREEMENT)) flag = false;

        return flag;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // TO DO change to ids
        switch (adapter.getItem(i).id) {
            case ITEM_ID_DL_PHOTO1:
            case ITEM_ID_DL_PHOTO2:
            case ITEM_ID_PAS_PHOTO1:
            case ITEM_ID_PAS_PHOTO2:
            case ITEM_ID_PAS_SELFIE:
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    // create file for save photo
                    // filename defined by button ID
                    photo = new File(storageDir, getPhotoFileName(i));
                    photo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pavel.testregistrationform.fileprovider",
                        photo);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //request code is item number
                this.startActivityForResult(intent, i);
            }
            break;
            case ITEM_ID_SHOW_CONTRACT:
            {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(
                        WebViewActivity.extraTitle,
                        getResources().getString(R.string.сontract_activity_title)
                );
                intent.putExtra(
                        WebViewActivity.extraURL,
                        //"https://lifcar.ru/"
                        "https://drive.google.com/viewerng/viewer?embedded=true&url=https://lifcar.ru/lifcar_agreement.pdf"
                );
                startActivity(intent);
            }
            break;
            case ITEM_ID_SHOW_AGREEMENT:
            {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(
                        WebViewActivity.extraTitle,
                        getResources().getString(R.string.pers_activity_title)
                );
                intent.putExtra(
                        WebViewActivity.extraURL,
                        //"https://ya.ru"
                        "https://drive.google.com/viewerng/viewer?embedded=true&url=https://lifcar.ru/lifcar_agreement.pdf"
                );
                startActivity(intent);
            }
            break;
            case ITEM_ID_NEXT:
                if (check()) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    // successful registration
                } else break;
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) return;

        // get and resize photo
        int size = spToPx(20, this);
        Bitmap b = BitmapFactory.decodeFile(photo.getAbsolutePath());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
            b, size, size, false);
        b.recycle();

        // show small photo
        Drawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
        ((ListItem)listView.getAdapter().getItem(requestCode)).setImg(drawable);
        listView.invalidateViews();

    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static String getPhotoFileName(int resId) {
        switch (resId) {
            case 9: return PHOTO_SELFIE_FILE_NAME;
            case 10: return PHOTO_PAS1_FILE_NAME;
            case 11: return PHOTO_PAS2_FILE_NAME;
            case 15: return PHOTO_DL1_FILE_NAME;
            case 16: return PHOTO_DL2_FILE_NAME;

            default: return null;
        }
    }
}
