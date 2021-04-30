package pub.monkeyboss.codeinputviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import pub.monkeyboss.widget.CodeInputView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CodeInputView codeInputView = (CodeInputView) findViewById(R.id.codeInputView);
        final CodeInputView codeInputView2 = (CodeInputView) findViewById(R.id.codeInputView2);
        final CodeInputView codeInputView3 = (CodeInputView) findViewById(R.id.codeInputView3);
        codeInputView.setOnInputListener(new CodeInputView.OnInputListener() {
            @Override
            public void onInput(int position, char c, String content) {
                Log.d("MainActivity", "onInput---" + content);
            }

            @Override
            public void onDelete(String content) {
                Log.d("MainActivity", "onDelete---" + content);
            }

            @Override
            public void onComplete(String content) {
                Log.d("MainActivity", "onComplete---" + content);
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeInputView.clear();
                codeInputView2.clear();
                codeInputView3.clear();
            }
        });
    }

}
