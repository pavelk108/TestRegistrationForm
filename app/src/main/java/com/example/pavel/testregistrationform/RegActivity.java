package com.example.pavel.testregistrationform;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class RegActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final String PHOTO_SELFIE_FILE_NAME = "photoSelfie.jpg";
    public static final String PHOTO_PAS1_FILE_NAME = "photoPas1.jpg";
    public static final String PHOTO_PAS2_FILE_NAME = "photoPas2.jpg";
    public static final String PHOTO_DL1_FILE_NAME = "photoDL1.jpg";
    public static final String PHOTO_DL2_FILE_NAME = "photoDL2.jpg";

    private ListView listView;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.US);
    private Date dateBirth = new Date();
    private Date datePassport = new Date();
    private Date dateDriverLicense = new Date();

    private File photo;



    enum ItemsTypes {
        HEADER, TEXTVIEW_WITH_IMG, EDITTEXT, SWITCH, TEXTVIEW_WITH_NARROW, BUTTON;
        private static int[] ids = new int[]{
                R.layout.list_view_header,
                R.layout.list_text_with_img,
                R.layout.list_edit_text_item,
                R.layout.list_switch,
                R.layout.list_text_with_img,
                R.layout.list_button
        };
        int getLayoutID() {
            return ids[this.ordinal()];
        }
        static int getLayoutID(int item) {
            return ids[item];
        }
    }
    private class ListItem {
        ItemsTypes type;
        String text;
        Drawable img;
        MaskFormatWatcher mfw;
        boolean is_red;
        boolean checked;
        String usertext = "";

        ListItem(ItemsTypes type, String text) {
            this.type = type;
            this.text = text;
        }
        ListItem setImg(Drawable img) {
            this.img = img;
            return this;
        }
        ListItem setMFW(MaskFormatWatcher mfw) {
            this.mfw = mfw;
            return this;
        }
    }
    class MyAdapter extends BaseAdapter implements View.OnFocusChangeListener {
        private ArrayList<ListItem> items;
        MyAdapter() {
            items = new ArrayList<>();
        }
        MyAdapter(ListItem[] items) {
            this.items = new ArrayList<>(Arrays.asList(items));
        }
        public void addItem(ListItem item) {
            items.add(item);
        }
        @Override
        public int getViewTypeCount() {
            return ItemsTypes.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type.ordinal();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            // switch by enum
            int itemType = getItemViewType(i);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(ItemsTypes.getLayoutID(itemType), viewGroup, false);
            }
            convertView.setOnFocusChangeListener(this);
            TextView textView = (TextView) convertView;
            final ListItem item = items.get(i);
            convertView.setTag(item);

            switch(ItemsTypes.values()[itemType]) {
                case SWITCH:
                    ((SwitchCompat) textView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            item.checked = b;
                        }
                    });
                case BUTTON:
                case HEADER:
                case TEXTVIEW_WITH_NARROW: {
                    textView.setText(item.text);
                    return convertView;
                }
                case TEXTVIEW_WITH_IMG: {
                    textView.setText(item.text);
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, item.img, null);
                    return convertView;
                }
                case EDITTEXT: {
                    textView.setHint(item.text);
                    textView.setText(item.usertext);
                    textView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            item.usertext = charSequence.toString();
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    if (item.mfw != null) item.mfw.installOn(textView);
                    if (item.is_red) textView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    return convertView;
                }
            }
            return null;
        }
        @Override
        public void onFocusChange(View view, boolean b) {
            view.getBackground().clearColorFilter();
            ((ListItem)view.getTag()).is_red = false;
        }
    }

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
            listView = (ListView)findViewById(R.id.list_pers_data);
            listView.setHeaderDividersEnabled(false);
            TextView header = (TextView) getLayoutInflater().inflate(R.layout.list_view_header, null);
            header.setText(R.string.personal_data_section_header);
            Log.d("11212","23erre");

            Slot[] slotsDate = new UnderscoreDigitSlotsParser().parseSlots("__.__.__");
            Slot[] slotsDL = new UnderscoreDigitSlotsParser().parseSlots("__ __ ______");
            Slot[] slotsPassport = new UnderscoreDigitSlotsParser().parseSlots("__ __ ______");

            MyAdapter adapter = new MyAdapter(
                    new ListItem[] {
                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.personal_data_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_surname)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_name)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_exname)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_birthday))
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_email)),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.passport_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_passport_num))
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsPassport))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_passport_date))
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_selfie))
                                            .setImg(resources.getDrawable(R.drawable.ic_photo)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_first))
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_second))
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.driverlicense_section_header)),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_driverlicense_num))
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDL))),
                            new ListItem(ItemsTypes.EDITTEXT,
                                    resources.getString(R.string.reg_driverlicense_date))
                                    .setMFW(new MaskFormatWatcher(MaskImpl.createTerminated(slotsDate))),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_selfie))
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_IMG,
                                    resources.getString(R.string.reg_passport_photo_first))
                                    .setImg(resources.getDrawable(R.drawable.ic_photo)),

                            new ListItem(ItemsTypes.HEADER,
                                    resources.getString(R.string.law_section_header)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_NARROW,
                                    resources.getString(R.string.reg_law_contract)),
                            new ListItem(ItemsTypes.SWITCH,
                                    resources.getString(R.string.reg_law_contract_agree)),
                            new ListItem(ItemsTypes.TEXTVIEW_WITH_NARROW,
                                    resources.getString(R.string.reg_law_pers)),
                            new ListItem(ItemsTypes.SWITCH,
                                    resources.getString(R.string.reg_law_pers_agree)),

                            new ListItem(ItemsTypes.BUTTON,
                                    resources.getString(R.string.reg_next))
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

    private boolean checkLength(int i, int length) {
        ListItem item = ((ListItem)listView.getAdapter().getItem(i));
            if (item.usertext.length() == length) {
            return true;
        } else {
            markRed(item);
            return false;
        }
    }

    private boolean checkNotEmpty(int i) {
        ListItem item = ((ListItem)listView.getAdapter().getItem(i));
        if (item.usertext.length() > 0) {
            return true;
        } else {
            markRed(item);
            return false;
        }
    }

    // Date date - obj to save parsed date
    private boolean checkDate(int i, Date date) {
        ListItem item = ((ListItem)listView.getAdapter().getItem(i));
        try {
            // save Date
            date.setTime(sdf.parse(item.usertext).getTime());
        } catch (ParseException e) {
            item.is_red = true;
            listView.invalidateViews();
            return false;
        }
        if (date != null) {
            return true;
        } else {
            item.is_red = true;
            listView.invalidateViews();
            return false;
        }
    }

    private boolean checkEmail(int i) {
        ListItem item = ((ListItem)listView.getAdapter().getItem(i));
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(item.usertext);
        if (matcher.find()) {
            return true;
        } else  {
            markRed(item);
            return false;
        }
    }

    private boolean checkSwitch(int i) {
        ListItem item = ((ListItem)listView.getAdapter().getItem(i));
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
        if (!checkNotEmpty(1)) flag = false;
        if (!checkNotEmpty(2)) flag = false;
        if (!checkNotEmpty(3)) flag = false;
        //if (!checkNotEmpty(7)) flag = false;

        // easy check email
        if (!checkEmail(5)) flag = false;

        // check num passport and num driver license
        // only check length
        // other checked by tinkoff
        if (!checkLength(7, 11)) flag = false; // хххх хххххх
        if (!checkLength(13, 12)) flag = false; // хх хх хххххх

        //check date fields
        if (!checkDate(4, dateBirth)) flag = false;
        if (!checkDate(8, datePassport)) flag = false;
        if (!checkDate(14, dateDriverLicense)) flag = false;

        // check that switches are checked
        if (!checkSwitch(19)) flag = false;
        if (!checkSwitch(21)) flag = false;

        return flag;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // TO DO change to ids
        switch (i) {
            case 9:
            case 10:
            case 11:
            case 15:
            case 16:
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
            case 18:
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
            case 20:
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
            case 22:
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
