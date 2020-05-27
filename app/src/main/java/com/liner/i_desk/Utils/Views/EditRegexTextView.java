package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

import java.util.Objects;
import java.util.regex.Pattern;

@SuppressLint("AppCompatCustomView")
public class EditRegexTextView extends EditText {
    private IEditTextListener textListener;
    private String regexString = "";
    private int leftDrawableIconResource;
    private Drawable leftDrawableIcon;


    public EditRegexTextView(Context context) {
        super(context);
        init(context, null);
    }

    public EditRegexTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditRegexTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EditRegexTextView, 0, 0);
        leftDrawableIconResource = typedArray.getResourceId(R.styleable.EditRegexTextView_ertv_left_icon, R.drawable.email_icon);
        regexString = typedArray.getString(R.styleable.EditRegexTextView_ertv_regex);
        typedArray.recycle();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String result = editable.toString().trim();
                if (Pattern.compile(regexString).matcher(result).matches()) {
                    leftDrawableIcon = getContext().getDrawable(leftDrawableIconResource);
                    Objects.requireNonNull(leftDrawableIcon).setColorFilter(ColorUtils.getThemeColor(getContext(), R.attr.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawableIcon, null, null, null);
                    if (textListener != null) {
                        textListener.onValid(result);
                    }
                } else {
                    leftDrawableIcon = getContext().getDrawable(leftDrawableIconResource);
                    Objects.requireNonNull(leftDrawableIcon).setColorFilter(ColorUtils.getThemeColor(getContext(), R.attr.disabledColor), PorterDuff.Mode.SRC_IN);
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawableIcon, null, null, null);
                    if (textListener != null) {
                        textListener.onNotValid();
                    }
                }
            }
        };
        addTextChangedListener(textWatcher);
        leftDrawableIcon = context.getDrawable(leftDrawableIconResource);
        Objects.requireNonNull(leftDrawableIcon).setColorFilter(ColorUtils.getThemeColor(context, R.attr.disabledColor), PorterDuff.Mode.SRC_IN);
        setCompoundDrawablesWithIntrinsicBounds(leftDrawableIcon, null, null, null);
    }


    public void setTextListener(IEditTextListener textListener) {
        this.textListener = textListener;
    }

    public void setRegexString(String regexString) {
        this.regexString = regexString;
    }

    public interface IEditTextListener {
        void onValid(String result);

        void onNotValid();
    }

}
