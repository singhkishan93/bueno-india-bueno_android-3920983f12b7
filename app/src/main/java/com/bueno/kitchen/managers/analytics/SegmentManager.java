package com.bueno.kitchen.managers.analytics;

import android.content.Context;
import android.content.pm.PackageManager;

import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.PreferenceManager;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by bedi on 03/04/16.
 */
public class SegmentManager {

    public enum EventType {
        TRACK, SCREEN
    }

    private Context context;
    private Properties properties;
    private String name;

    private SegmentManager(Context context) {
        this.context = context;
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        properties = new Properties();
    }

    public void track(EventType eventType) {
        switch (eventType) {
            case SCREEN:
                Analytics.with(context).screen(null, name, properties);
                break;
            case TRACK:
                Analytics.with(context).track(name, properties);
                break;
        }
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private SegmentManager segmentManager;

        public Builder(Context context) {
            segmentManager = new SegmentManager(context);
        }

        public Builder setProperties(HashMap<String, String> properties) {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                segmentManager.properties.put(property.getKey(), property.getValue());
            }
            return this;
        }

        public Builder setName(String name) {
            segmentManager.name = name;
            return this;
        }

        public SegmentManager build(EventType eventType) {
            segmentManager.track(eventType);
            return segmentManager;
        }
    }
}
