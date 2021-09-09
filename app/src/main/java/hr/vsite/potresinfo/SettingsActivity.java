package hr.vsite.potresinfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private int magnitude;
    private int num;
    private String current_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getSharedPreferences("postavke", 0);
        magnitude = settings.getInt("minmag", 1);
        num = settings.getInt("limit", 10);
        current_lang = settings.getString("lang", "hr");

        setTitle(R.string.settings);

        TextView tv_num = findViewById(R.id.tb_num);
        TextView tv_mag = findViewById(R.id.tb_mag);

        tv_num.setText("" + num);
        tv_mag.setText("" + magnitude);

        Spinner mySpinner = (Spinner) findViewById(R.id.lang_sw);

        if (current_lang.equals("hr"))
            mySpinner.setSelection(1);
        else
            mySpinner.setSelection(0);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        current_lang = "en";
                        break;
                    case 1:
                        current_lang = "hr";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


    public void btnBack(View V)
    {
        finish();
    }

    public void btnSave(View V) {
        //Spremanje korisničkih postavki
        TextView tv_num = (TextView) findViewById(R.id.tb_num);
        TextView tv_mag = (TextView) findViewById(R.id.tb_mag);

        num = Integer.parseInt(tv_num.getText().toString());
        magnitude = Integer.parseInt(tv_mag.getText().toString());

        SharedPreferences settings = getSharedPreferences("postavke", 0);
        settings.edit().putString("lang", current_lang).apply();
        settings.edit().putInt("minmag", magnitude).apply();;
        settings.edit().putInt("limit", num).apply();;

        settings.edit().putBoolean("reload", true).apply();
        finish();
    }
}