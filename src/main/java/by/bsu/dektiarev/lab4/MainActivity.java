package by.bsu.dektiarev.lab4;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureOverlayView gestures;
    private GestureLibrary glib;
    private EditText editText;
    private Button button;
    private Button clearButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.glib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if(!glib.load()) {
            finish();
        }

        this.gestures = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
        gestures.addOnGesturePerformedListener(this);

        this.resultText = (TextView) findViewById(R.id.textView);
        this.editText = (EditText) findViewById(R.id.editText);
        this.button = (Button) findViewById(R.id.button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = parseExpression(editText.getText().toString());
                resultText.setText(result);
            }
        });

        this.clearButton = (Button) findViewById(R.id.clearButton);
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.getText().clear();
            }
        });
    }

    public String parseExpression(String message) {
        try {
            for(int i = 0; i < message.length(); i++) {
                switch(message.charAt(i)) {
                    case '+':
                        return String.valueOf(Integer.parseInt(message.substring(0, i)) +
                                Integer.parseInt(message.substring(i+1, message.length())));
                    case '-':
                        return String.valueOf(Integer.parseInt(message.substring(0, i)) -
                                Integer.parseInt(message.substring(i+1, message.length())));
                    case '*':
                        return String.valueOf(Integer.parseInt(message.substring(0, i)) *
                                Integer.parseInt(message.substring(i+1, message.length())));
                    case '/':
                        if(Integer.parseInt(message.substring(i+1, message.length())) == 0) {
                            return getResources().getString(R.string.divByZeroString);
                        }
                        else return String.valueOf(Double.parseDouble(message.substring(0, i)) /
                                Double.parseDouble(message.substring(i+1, message.length())));
                    default:
                        break;
                }
            }
        }
        catch (NumberFormatException ex) {
            resultText.setText(getResources().getString(R.string.wrongFormatString));
        }
        return message;
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

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        List<Prediction> predictions = glib.recognize(gesture);
        if(predictions.size() > 0) {
            if(predictions.get(0).score > 1.0) {
                if(predictions.get(0).name.equals("div")) {
                    this.editText.append("/");
                }
                else {
                    this.editText.append(predictions.get(0).name);
                }
            }
        }
    }
}
