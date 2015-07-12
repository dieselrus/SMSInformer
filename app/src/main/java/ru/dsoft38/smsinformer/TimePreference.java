package ru.dsoft38.smsinformer;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by diesel on 12.07.15.
 */
public class TimePreference extends DialogPreference {

    TimePicker tp;
    int selectedTime;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setPersistent(true);
    }

    @Override
    protected View onCreateDialogView() {
        this.tp = new TimePicker(getContext());
        this.tp.setIs24HourView(true);
        return this.tp;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            final String result = this.tp.getCurrentHour() + ":" + this.tp.getCurrentMinute();
            setTitle(getTitle());
            setSummary(result);
            persistString(result);
        }
    }
}

