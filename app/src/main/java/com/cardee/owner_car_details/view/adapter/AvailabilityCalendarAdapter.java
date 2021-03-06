package com.cardee.owner_car_details.view.adapter;

import com.cardee.custom.calendar.model.Day;
import com.cardee.custom.calendar.view.selection.MultipleSelectionAdapter;
import com.cardee.custom.calendar.view.selection.RangeSelectionAdapter;
import com.cardee.owner_car_details.view.listener.AvailabilityCalendarListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AvailabilityCalendarAdapter extends MultipleSelectionAdapter<Date> {

    private List<Date> dates;
    private List<Date> selected;

    private AvailabilityCalendarListener listener;

    public AvailabilityCalendarAdapter() {

    }

    @Override
    protected void onSelectionChanged(List<Day> dayz) {
        if (listener != null && dayz != null) {
            List<Date> dates = new ArrayList<>();
            for (Day day : dayz) {
                dates.add(day.getCalendarTime());
            }
            selected = dates;
            listener.onSelectedDatesChange(dates);
        }
    }

    @Override
    protected Date onNext(Date item) {
        return item;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
        if (dates != null && !dates.isEmpty()) {
            setSelection(dates);
        }
    }

    public void setListener(AvailabilityCalendarListener listener) {
        this.listener = listener;
    }

    public List<Date> getSelected() {
        return selected;
    }
}
