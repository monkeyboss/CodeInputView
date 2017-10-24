package pub.monkeyboss.codeinputviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pub.monkeyboss.widget.CodeInputView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CodeInputView codeInputView = (CodeInputView) findViewById(R.id.codeInputView);
        codeInputView.setOnInputListener(new CodeInputView.OnInputListener() {
            @Override
            public void onInput(int position, char c, String content) {

            }

            @Override
            public void onDelete(String content) {

            }

            @Override
            public void onComplete(String content) {

            }
        });
    }
}
