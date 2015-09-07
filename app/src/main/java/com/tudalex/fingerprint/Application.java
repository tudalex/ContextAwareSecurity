package com.tudalex.fingerprint;

import android.content.Intent;

import com.tudalex.fingerprint.db.Context;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.InitializeListener;
import co.uk.rushorm.core.RushCore;

/**
 * Created by tudalex on 21/08/15.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        config.setInitializeListener(new InitializeListener() {
            @Override
            public void initialized(boolean b) {
                startService(new Intent(getApplicationContext(), ContextAwareService.class));
            }
        });
        RushCore.initialize(config);

        super.onCreate();
    }
}
