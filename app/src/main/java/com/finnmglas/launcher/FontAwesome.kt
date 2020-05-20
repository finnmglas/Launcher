package com.finnmglas.launcher // replace with your package

// On GitHub: https://github.com/finnmglas/fontawesome-android

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class FontAwesomeSolid : AppCompatTextView {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        typeface = Typeface.createFromAsset(
            context.assets,
            "fontawesome/fa-solid-900.ttf"
        )
    }
}

class FontAwesomeRegular : AppCompatTextView {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        typeface = Typeface.createFromAsset(
            context.assets,
            "fontawesome/fa-regular-400.ttf"
        )
    }
}

class FontAwesomeBrand : AppCompatTextView {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        typeface = Typeface.createFromAsset(
            context.assets,
            "fontawesome/fa-brands-400.ttf"
        )
    }
}
