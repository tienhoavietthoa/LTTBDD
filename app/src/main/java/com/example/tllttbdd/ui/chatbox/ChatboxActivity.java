package com.example.tllttbdd.ui.chatbox;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tllttbdd.R;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChatboxActivity extends AppCompatActivity {

    private LinearLayout chatMessages;
    private EditText inputMessage;
    private Button sendButton;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);

        chatMessages = findViewById(R.id.chat_messages);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scroll_view);

        sendButton.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                addMessage(msg, true);
                inputMessage.setText("");
                sendToServer(msg);
            }
        });
    }

    private void addMessage(String msg, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(msg);
        textView.setBackgroundResource(isUser ? R.drawable.bg_user_msg : R.drawable.bg_bot_msg);
        textView.setPadding(20, 10, 20, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        params.gravity = isUser ? View.TEXT_ALIGNMENT_VIEW_END : View.TEXT_ALIGNMENT_VIEW_START;
        textView.setLayoutParams(params);
        chatMessages.addView(textView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void sendToServer(String msg) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:3000/api/chat"); // Dùng 10.0.2.2 cho emulator
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("message", msg);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                Scanner in = new Scanner(conn.getInputStream(), "UTF-8");
                StringBuilder sb = new StringBuilder();
                while (in.hasNextLine()) {
                    sb.append(in.nextLine());
                }
                in.close();
                conn.disconnect();

                runOnUiThread(() -> addMessage(sb.toString(), false));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> addMessage("Lỗi kết nối server", false));
            }
        }).start();
    }
}
