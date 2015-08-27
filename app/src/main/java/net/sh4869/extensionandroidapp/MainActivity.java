package net.sh4869.extensionandroidapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public  void sendLoginRequest(View v) {
        EditText userNameInput = (EditText)findViewById(R.id.usernameInput);
        EditText passwordInput = (EditText)findViewById(R.id.passwordInput);

        String username = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        Log.d("password",password);
        Log.d("username",username);
        if (username == " " && password == " ") {
            //TODO
        }else{
            TextView messageTextView = (TextView)findViewById(R.id.messageTextView);
            messageTextView.setText(R.string.dontEnterStringMessage);
        }
    }
}
