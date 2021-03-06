package com.cardee.custom.time_picker.view.selection;

import android.support.annotation.NonNull;
import com.cardee.custom.time_picker.view.TimePicker;

import java.util.Date;

public abstract class RangeSelectionAdapter<T> extends SelectionAdapter<T> {

    private T start;
    private T end;

    public final void setRange(@NonNull T start, @NonNull T end) {
        this.start = start;
        this.end = end;
        notifyDataSetChanged();
    }

    @Override
    public final Date onNext(int position) {
        if (position == 0) {
            return onNext(start);
        } else if (position == 1) {
            return onNext(end);
        } else {
            throw new IllegalArgumentException("Range position: " + position);
        }
    }

    protected abstract Date onNext(T item);

    @Override
    protected final int getMode() {
        return TimePicker.MODE_RANGE;
    }

    @Override
    public final int getItemCount() {
        return start == null || end == null ? 0 : 2;
    }
}
