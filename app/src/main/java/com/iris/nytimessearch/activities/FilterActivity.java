package com.iris.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iris.nytimessearch.R;


import org.w3c.dom.Text;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by iris on 7/31/16.
 */
public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Spinner sortOrderSpinner;
    String sortOrder;
    Calendar beginDate;
    List<String> newsDeskValues;
    TextView tvItemDueDate;
    CheckBox checkArts;
    CheckBox checkOpinion;
    CheckBox checkWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sortOrder = getIntent().getStringExtra(SearchActivity.QUERY_PARAM_SORT);
        newsDeskValues = (List<String>) getIntent().getSerializableExtra(
            SearchActivity.NEWS_DESK_ARG);
        beginDate = (Calendar) getIntent().getSerializableExtra(
            SearchActivity.QUERY_PARAM_BEGIN_DATE);
        createPrioritySpinner(sortOrder);
        setUpViews();
    }

    private void setUpViews() {
        tvItemDueDate = (TextView) findViewById(R.id.tvItemDueDate);
        if (beginDate != null) {
            SimpleDateFormat beginDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = beginDate.getTime();
            String beginDateStr = beginDateFormat.format(date);
            tvItemDueDate.setText(beginDateStr);
        } else {
            tvItemDueDate.setText("Click here to set a date!");
        }
        checkArts = (CheckBox) findViewById(R.id.checkbox_arts);
        checkOpinion = (CheckBox) findViewById(R.id.checkbox_opinion);
        checkWorld = (CheckBox) findViewById(R.id.checkbox_world);
        checkArts.setOnCheckedChangeListener(checkListener);
        checkOpinion.setOnCheckedChangeListener(checkListener);
        checkWorld.setOnCheckedChangeListener(checkListener);
        if (newsDeskValues.contains(SearchActivity.NEWS_DESK_ART)) {
           checkArts.setChecked(true);
        }
        if (newsDeskValues.contains(SearchActivity.NEWS_DESK_OPINION)) {
            checkOpinion.setChecked(true);
        }
        if (newsDeskValues.contains(SearchActivity.NEWS_DESK_WORLD)) {
            checkWorld.setChecked(true);
        }
    }

    public void createPrioritySpinner(String originalSortOrder) {
        sortOrderSpinner = (Spinner) findViewById(R.id.sortOrderSpinner);

        ArrayAdapter<String> sortOrderAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, SearchActivity.SORT_ORDERS);

        sortOrderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortOrderSpinner.setAdapter(sortOrderAdapter);

        sortOrderSpinner.setSelection(SearchActivity.SORT_ORDERS.indexOf(originalSortOrder));
        sortOrderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortOrder = SearchActivity.SORT_ORDERS.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void saveSettings(View view) {
        Intent data = new Intent();
        data.putExtra(SearchActivity.QUERY_PARAM_SORT, sortOrder);
        data.putExtra(SearchActivity.QUERY_PARAM_BEGIN_DATE, beginDate);
        data.putExtra(SearchActivity.NEWS_DESK_ARG, (Serializable) newsDeskValues);
        setResult(RESULT_OK, data);
        finish();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        beginDate = c;
        SimpleDateFormat beginDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = beginDate.getTime();
        String beginDateStr = beginDateFormat.format(date);
        tvItemDueDate = (TextView) findViewById(R.id.tvItemDueDate);
        tvItemDueDate.setText(beginDateStr);
    }

    private void addNewsDesk(String newsDeskValue) {
        if (! newsDeskValues.contains(newsDeskValue)) {
            newsDeskValues.add(newsDeskValue);
        }
    }

    private void removeNewsDeskValue(String newsDeskValue) {
        if (newsDeskValues.contains(newsDeskValue)) {
            newsDeskValues.remove(newsDeskValue);
        }
    }

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean checked) {
            switch(view.getId()) {
                case R.id.checkbox_arts:
                    if (checked) {
                        addNewsDesk(SearchActivity.NEWS_DESK_ART);
                    } else {
                        removeNewsDeskValue(SearchActivity.NEWS_DESK_ART);
                    }
                    break;
                case R.id.checkbox_opinion:
                    if (checked) {
                        addNewsDesk(SearchActivity.NEWS_DESK_OPINION);
                    } else {
                        removeNewsDeskValue(SearchActivity.NEWS_DESK_OPINION);
                    }
                    break;
                case R.id.checkbox_world:
                    if (checked) {
                        addNewsDesk(SearchActivity.NEWS_DESK_WORLD);
                    } else {
                        removeNewsDeskValue(SearchActivity.NEWS_DESK_WORLD);
                    }
                    break;
            }
        }
    };


}
