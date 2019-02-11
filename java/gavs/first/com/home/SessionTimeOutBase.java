package gavs.first.com.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class SessionTimeOutBase extends Activity {
    public static final long DISCONNECT_TIMEOUT = 30000;


    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            //finish();
            Intent i = new Intent(getApplicationContext(), Home_Activity.class);
            startActivity(i);
            finish();
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
        }


    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}
