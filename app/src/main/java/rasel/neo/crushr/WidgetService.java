package rasel.neo.crushr;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new RemoteViewFactory(this.getApplicationContext(), intent));
    }

}
