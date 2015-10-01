package com.tapewormsinc.giggles.hitboxsystem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends ActionBarActivity {

    private Spinner charSpinner, moveSpinner, variantSpinner;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDropdowns();
        setSubmitButton();

    }

    protected void setDropdowns() {
         /*
            CHARACTER DROPDOWN MENU
         */
        charSpinner = (Spinner) findViewById(R.id.charSpinner);
        // Creates an arrayadapter using string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chars, android.R.layout.simple_spinner_item);
        // specify the layout to use when the list appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // apply adapter to the spinner
        charSpinner.setAdapter(adapter);

        /*
            MOVELIST DROPDOWN MENU
         */
        int moveFile = R.array.testMoves;
        //regular file: R.array.moves
        moveSpinner = (Spinner) findViewById(R.id.moveSpinner);
        // Creates an arrayadapter using string array and default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                moveFile, android.R.layout.simple_spinner_item);
        // specify the layout to use when the list appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // apply adapter to the spinner
        moveSpinner.setAdapter(adapter2);
    }

    /*
        VARIANT DROPDOWN MENU -- different from the others.
        TODO:
        make it create spinner and set text, rather than just set.
        this may require a lot of changes. tentative.
    */
    protected void setThirdDropdown (int variantCode) {


        int variantFile = R.array.blank;            // logic to make text / menus appear
    /*
        variant codes:
        0       blank (default)
        1       grounded/aerial
        2       side angle
        3       Charged / uncharged
        4       Jab2
        5       Jab2 Rapid
        6       Jab3
        7       Jab3 Rapid
    */
        switch (variantCode) {
            case 1:
                variantFile = R.array.variantAerial;
                break;
            case 2:
                variantFile = R.array.variantAngle;
                break;
            case 3:
                variantFile = R.array.variantCharged;
                break;
            case 4:
                variantFile = R.array.variantJab2;
                break;
            case 5:
                variantFile = R.array.variantJab2rapid;
                break;
            case 6:
                variantFile = R.array.variantJab3;
                break;
            case 7:
                variantFile = R.array.variantJab3rapid;
                break;
        }

        variantSpinner = (Spinner)findViewById(R.id.variantSpinner);
        // Creates an arrayadapter using string array and default spinner layout
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                variantFile, android.R.layout.simple_spinner_item);
        // specify the layout to use when the list appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // apply adapter to the spinner
        variantSpinner.setAdapter(adapter3);

    }

    // adds behavior to submit button
    protected void setSubmitButton() {
        // addling logic to button
        goButton = (Button) findViewById(R.id.submit);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequestHelper();
            }
        });
    }

    // because the preceding onclick is out of scope of the spinners
    protected void submitRequestHelper() {
        String sendChar = String.valueOf(charSpinner.getSelectedItem());
        String sendMove = String.valueOf(moveSpinner.getSelectedItem());
        submitRequest(sendChar, sendMove);
    }

    protected void submitRequest(String character, String move)  {

        int variantCode = 0;
        String moveName, damage, total, active, IASA, auto, notes1, notes2, notes3;
        moveName = damage = total = active = IASA = auto = notes1 = notes2 = notes3 = "";
        JSONObject parent = parseJSONData(character);
        try {
            variantCode = parent.getJSONObject(move).getInt("variantCode");
            moveName = parent.getJSONObject(move).getString("name");
            damage = parent.getJSONObject(move).getString("damage");
            total = parent.getJSONObject(move).getString("total");
            active = parent.getJSONObject(move).getString("active");
            IASA = parent.getJSONObject(move).getString("IASA");
            auto = parent.getJSONObject(move).getString("auto");
            notes1 = parent.getJSONObject(move).getString("notes1");
            notes2 = parent.getJSONObject(move).getString("notes2");
            notes3 = parent.getJSONObject(move).getString("notes3");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // also returns to blank if required
        // only make it change if variantCode is different ?
        setThirdDropdown(variantCode);

        //writes labels
        String labels = "Move: \nMove name: \nDamage: \nTotal: \nActive: " +
                "\nIASA: \nAuto-cancel: \nNotes: ";
        String output = "" + move + "\n" + moveName + "\n" + damage + "\n" + total + "\n" +
                active + "\n" + IASA + "\n" + auto + "\n" + notes1 + "\n" + notes2 + "\n" + notes3;

        TextView r = (TextView) findViewById(R.id.infoLabels);
        TextView t = (TextView) findViewById(R.id.frameInfo);
        r.setText(labels);
        t.setText(output);
    }

    //reads the json files.
    //special care must be taken to match all names and strings
    protected JSONObject parseJSONData(String character) {
        String jss;
        JSONObject jso;
        String moveFile = character + ".json";
        try {

            InputStream is = getAssets().open(moveFile);

            int sizeofFile = is.available();
            byte[] bytes = new byte[sizeofFile];

            is.read(bytes);
            is.close();

            jss = new String(bytes, "UTF-8");
            jso = new JSONObject(jss);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException x){
            x.printStackTrace();
            return null;
        }
        return jso;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
