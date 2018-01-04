package baidu.com.blockmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jumpFormMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent= new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }
}
