package com.finnmglas.launcher;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontAwesomeBrand extends androidx.appcompat.widget.AppCompatTextView {

    public FontAwesomeBrand(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontAwesomeBrand(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeBrand(Context context) {
        super(context);
        init();
    }

    private void init() {

        //Font name should not contain "/".
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/fa-brand-400.ttf");
        setTypeface(tf);
    }

}