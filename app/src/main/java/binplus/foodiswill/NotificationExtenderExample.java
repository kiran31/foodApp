package binplus.foodiswill;

import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 19,July,2020
 */
public class NotificationExtenderExample extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read Properties from result
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Red on Android 5.0+ devices.

//                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
//                builder.setContentTitle(HtmlCompat.fromHtml("<font color=\"" + color + "\">" + getResources().getString(R.string.app_name) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
////                builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
////                builder.setContentTitle()
//                        .setColor(getResources().getColor(R.color.colorPrimary))
                  builder.setSmallIcon(R.drawable.ic_stat_veg_logo)
////                .setColorized(true)
                        .setColor(getResources().getColor(R.color.bell_color
                        ));
                return builder;
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

        // Return true to stop the notification from displaying
        return true;
    }
}
