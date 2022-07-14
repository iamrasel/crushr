package rasel.neo.crushr;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static NotificationManager notifyManager;
    protected static final int NOTIFY_ID = 0;

    protected static void createNotification(Context ctx, String text, int appWidgetId) {

        Intent openIntent = new Intent(ctx, SingleTaskDialog.class);
        openIntent.putExtra("rasel.neo.crushr.OPEN", true);
        openIntent.putExtra("text", text);
        openIntent.putExtra("id", appWidgetId);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent openPending =
                PendingIntent.getActivity(ctx, 1, openIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent addIntent = new Intent(ctx, NewTaskDialog.class);
        addIntent.putExtra("rasel.neo.crushr.ADD", true);
        addIntent.putExtra("id", appWidgetId);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent addPending =
                PendingIntent.getActivity(ctx, 1, addIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent removeIntent = new Intent(ctx, NotificationReceiver.class);
        removeIntent.putExtra("rasel.neo.crushr.REMOVE", true);
        removeIntent.putExtra("text", text);
        removeIntent.putExtra("id", appWidgetId);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent removePending =
                PendingIntent.getBroadcast(ctx, 1, removeIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent unpinIntent = new Intent(ctx, NotificationReceiver.class);
        unpinIntent.putExtra("rasel.neo.crushr.UNPIN", true);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent unpinPending =
                PendingIntent.getBroadcast(ctx, 1, unpinIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String channelId = ctx.getString(R.string.pinned_task);
        String channelTitle = ctx.getString(R.string.pinned_task);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelId);
        int importance;

        if (notifyManager == null) {
            notifyManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = notifyManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(channelId, channelTitle, importance);
                notifyManager.createNotificationChannel(notificationChannel);
            }
        } else {
            importance = Notification.PRIORITY_DEFAULT;
        }

        builder.setContentTitle(ctx.getString(R.string.pinned_task))
                .setSmallIcon(R.drawable.icon_notify)
                .setContentText(text)
                .setPriority(importance)
                .setShowWhen(false)
                .setOngoing(true)
                .setContentIntent(openPending)
                .addAction(R.drawable.icon_add, ctx.getString(R.string.add), addPending)
                .addAction(R.drawable.icon_remove, ctx.getString(R.string.remove), removePending)
                .addAction(R.drawable.icon_unpin, ctx.getString(R.string.unpin), unpinPending);

        Notification notification = builder.build();
        notifyManager.notify(NOTIFY_ID, notification);
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra("rasel.neo.crushr.UNPIN")) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NotificationReceiver.NOTIFY_ID);
        } else if(intent.hasExtra("rasel.neo.crushr.REMOVE")) {
            String text = intent.getExtras().getString("text");
            int id = intent.getExtras().getInt("id");
            BaseUtils.removeItem(context, text, id);
            ExtraUtils.refreshListView(context, id);
        }
    }
}
