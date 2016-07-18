package info.androidhive.webgroupchat;

import android.app.Activity;
import android.os.Bundle;

public class welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getActionBar().hide();
    }
}
