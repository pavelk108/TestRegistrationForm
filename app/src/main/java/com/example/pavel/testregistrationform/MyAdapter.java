package com.example.pavel.testregistrationform;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class MyAdapter extends BaseAdapter implements View.OnFocusChangeListener {
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
    static class ListItem {
        int id = 0;
        ItemsTypes type;
        String text;
        Drawable img;
        MaskFormatWatcher mfw;
        boolean is_red;
        boolean checked;
        TextWatcher textWatcher;
        String usertext = ""; // text in edittext, "text" use as hint

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
        ListItem setID(int id) {
            this.id = id;
            return this;
        }
    }

    private ArrayList<ListItem> items;
    private LayoutInflater layoutInflater;
    MyAdapter(LayoutInflater layoutInflater, ListItem[] items) {
        this.layoutInflater = layoutInflater;
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
    public ListItem getItem(int i) {
        return items.get(i);
    }

    public ListItem getItemByID(int id) {
        for (ListItem item : items) {
            if (item.id == id)
                return item;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Log.d("11121", i + "");
        // switch by enum
        int itemType = getItemViewType(i);
        if (convertView == null) {
            convertView = layoutInflater.inflate(ItemsTypes.getLayoutID(itemType), viewGroup, false);
        }
        convertView.setOnFocusChangeListener(this);
        TextView textView = (TextView) convertView;
        final ListItem item = items.get(i);
        final ListItem oldItem = (ListItem)convertView.getTag();

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
                break;
            }
            case TEXTVIEW_WITH_IMG: {
                textView.setText(item.text);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, item.img, null);
                break;
            }
            case EDITTEXT: {
                if (oldItem != null) {
                    // remove our old listener
                    textView.removeTextChangedListener(oldItem.textWatcher);
                    if (oldItem.mfw != null) {// try to remove tinkoff listener
                        // tinkoff know only about last textView
                        // and it doesn't remove its listener, when install on new textView
                        if (oldItem.mfw.isAttachedTo(textView)) {
                            oldItem.mfw.removeFromTextView();
                        } else {
                            textView.removeTextChangedListener(oldItem.mfw);
                        }
                    }
                }
                if (item.textWatcher == null) {
                    // first query for this item
                    // create textWatcher
                    item.textWatcher = new TextWatcher() {
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
                    };
                }
                if (item.mfw != null) item.mfw.installOn(textView);
                textView.setHint(item.text);
                textView.setText(item.usertext);
                textView.addTextChangedListener(item.textWatcher);
                if (item.is_red) textView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                break;
            }
        }
        convertView.setTag(item);
        return convertView;
    }
    @Override
    public void onFocusChange(View view, boolean b) {
        view.getBackground().clearColorFilter();
        ((ListItem)view.getTag()).is_red = false;
    }
}