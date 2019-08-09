package com.example.calc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    public TextView currentDateTime;
    TextView eurText;
    TextView usdText;
    TextView jpyText;
    Calendar dateAndTime=Calendar.getInstance();
    DBHelper dbHelper;
    Courses courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courses = new Courses();
        dbHelper = new DBHelper(this);
        currentDateTime=(TextView)findViewById(R.id.currentDate);
        setInitialDateTime();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        NumberPicker np1 = (NumberPicker)findViewById(R.id.numberPicker1);
        NumberPicker np2 = (NumberPicker)findViewById(R.id.numberPicker2);
        final String[] arrayString= new String[]{"EUR", "USD", "RUB", "JPY"};
        np1.setMinValue(0);
        np1.setMaxValue(arrayString.length-1);
        np2.setMinValue(0);
        np2.setMaxValue(arrayString.length-1);

        np1.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return arrayString[value];
            }
        });

        np2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return arrayString[value];
            }
        });
        eurText = findViewById(R.id.eurTextView);
        usdText = findViewById(R.id.usdTextView);
        jpyText = findViewById(R.id.jpyTextView);
        setTextInTextView();
        setHistoryConverts();






    }

    public String getOutput(String valute, double course) {
        Log.d("check ", String.format("%s : %s", valute, course));
        return String.format("%s : %s", valute, course);
    }

    public String getUrl() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return String.format("http://www.cbr.ru/scripts/XML_daily.asp?date_req=%s", sdf.format(dateAndTime.getTime()));
    }

    public void setTextInTextView(){
        eurText.setText(getOutput("Евро", courses.getCourses(getUrl())[0]));
        usdText.setText(getOutput("Доллар", courses.getCourses(getUrl())[1]));
        jpyText.setText(getOutput("Японские йены", courses.getCourses(getUrl())[2]));


    }


    public void setDate(View v) {
        new DatePickerDialog(MainActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setInitialDateTime() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        currentDateTime.setText(sdf.format(dateAndTime.getTime()));


    }

    private void setHistoryConverts() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ArrayList listHistoryOfConverts = dbHelper.getFromDB(database);
        Collections.reverse(listHistoryOfConverts);
        final TextView[] arrayTextView= new TextView[]{findViewById(R.id.textView5),
                findViewById(R.id.textView6), findViewById(R.id.textView7), findViewById(R.id.textView8),
                findViewById(R.id.textView9), findViewById(R.id.textView10), findViewById(R.id.textView11),
                findViewById(R.id.textView12), findViewById(R.id.textView13), findViewById(R.id.textView14)};
        for (int i = 0; i < arrayTextView.length; i++) {
            if ( i  >= listHistoryOfConverts.size()) {
                arrayTextView[i].setText(" ");
            } else {
                arrayTextView[i].setText(listHistoryOfConverts.get(i).toString());
            }

        }
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
            setTextInTextView();
        }
    };


    public void onButtonClick(View v) throws JSONException {
        EditText el1;
        el1 = findViewById(R.id.addValue);
        TextView resText;
        resText = findViewById(R.id.result);
        NumberPicker np1;
        np1 = findViewById(R.id.numberPicker1);
        NumberPicker np2;
        np2 = findViewById(R.id.numberPicker2);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        double value = 0;
        try {
            value = Double.parseDouble(el1.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("NumberFormatException",e.toString());
        }

        double result;
        double eur = courses.getCourses(getUrl())[0];
        Log.d("result",Double.toString(eur));
        double usd = courses.getCourses(getUrl())[1];
        double jpy = courses.getCourses(getUrl())[2];

        if (np1.getValue() == 0) {
            switch (np2.getValue()) {
                case 0: result = value; // eur to eur
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "EUR", "EUR", currentDateTime.getText().toString());
                    break;
                case 1: result = value * eur /  usd; // eur to usd
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "EUR", "USD", currentDateTime.getText().toString());
                    break;
                case 2: result = value * eur; // eur to rub
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "EUR", "RUB", currentDateTime.getText().toString());
                    break;
                case 3: result = value * eur / jpy * 100; // eur to jpy
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "EUR", "JPY", currentDateTime.getText().toString());
                    break;
            }
        } else if (np1.getValue() == 1) {
            switch (np2.getValue()) {
                case 0: result = value * usd / eur; // usd to eur
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "USD", "EUR", currentDateTime.getText().toString());
                    break;
                case 1: result = value; // usd to usd
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "USD", "USD", currentDateTime.getText().toString());
                    break;
                case 2: result = value * usd; // usd to rub
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "USD", "RUB", currentDateTime.getText().toString());
                    break;
                case 3: result = value * usd / jpy * 100; // usd to jpy
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "USD", "JPY", currentDateTime.getText().toString());
                    break;
            }
        } else if (np1.getValue() == 2) {
            switch (np2.getValue()) {
                case 0: result = value / eur; // rub to eur
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "RUB", "EUR", currentDateTime.getText().toString());
                    break;
                case 1: result = value / usd; // rub to usd
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "RUB", "USD", currentDateTime.getText().toString());
                    break;
                case 2: result = value; // rub to rub
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "RUB", "RUB", currentDateTime.getText().toString());
                    break;
                case 3: result = value / jpy * 100; // rub to jpy
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "RUB", "JPY", currentDateTime.getText().toString());
                    break;
            }
        } else if (np1.getValue() == 3) {
            switch (np2.getValue()) {
                case 0: result = value * jpy / 100 / eur; // jpy to eur
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "JPY", "EUR", currentDateTime.getText().toString());
                    break;
                case 1: result = value * jpy / 100 / usd; // jpy to usd
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "JPY", "USD", currentDateTime.getText().toString());
                    break;
                case 2: result = value * jpy / 100; // jpy to rub
                    resText.setText(String.format("%s", result));
                    dbHelper.putInDB(database, value, result, "JPY", "RUB", currentDateTime.getText().toString());
                    break;
                case 3: result = value; // jpy to jpy
                    dbHelper.putInDB(database, value, result, "JPY", "JPY", currentDateTime.getText().toString());
                    break;
            }
        }
        setHistoryConverts();
    }
}



