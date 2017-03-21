package com.thunder.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.thunder.nytsearch.R;
import com.thunder.nytsearch.models.Filter;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anlinsquall on 19/3/17.
 */

public class FilterDialogFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener {
    private TextView tvBeginDate;
    private Spinner spSortOrder;
    private CheckBox cbArts;
    private CheckBox cbFashionStyles;
    private CheckBox cbSports;
    private Button btnSave;

    private Filter selectedFilter;

    public FilterDialogFragment() {
    }

    public static FilterDialogFragment newInstance(String title){
        FilterDialogFragment fragment = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(selectedFilter ==null){
            selectedFilter = new Filter();
        }

        String title = getArguments().getString("title");
        getDialog().setTitle(title);
        tvBeginDate = (TextView) view.findViewById(R.id.tvBeginDate);
        tvBeginDate.setText("Click to select a date.");
        tvBeginDate.setOnClickListener(v -> {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTargetFragment(FilterDialogFragment.this, 0);
                datePickerFragment.show(getFragmentManager(), "datePickerDialog");
            });

        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashionStyles = (CheckBox) view.findViewById(R.id.cbFashionStyles);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
                selectedFilter.isSports = cbSports.isChecked();
                selectedFilter.sortOrderBy =spSortOrder.getSelectedItem().toString();
                selectedFilter.isFashionSytles = cbFashionStyles.isChecked();
                selectedFilter.isArts = cbArts.isChecked();

                // Dismiss and send datat to search activity
                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onSaveFilterDialog(selectedFilter);
                dismiss();
            });
    }

    @Override
    public void onFinishDatePickerDialog(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String selectedDate = simpleDateFormat.format(date);
        tvBeginDate.setText(selectedDate);
        selectedFilter.beginDate = date;
    }

    public interface FilterDialogListener{
        void onSaveFilterDialog(Filter filter);
    }
}
