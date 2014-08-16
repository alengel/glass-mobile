package com.brandwatch.glassmobile.application;

import android.app.Application;

import com.brandwatch.glassmobile.utils.PropertiesManager;
import com.glass.brandwatch.R;

public class GlassMobile extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the configuration file once, so that the properties are
		// accessible throughout the lifetime of the application
		PropertiesManager.init(getApplicationContext(), R.raw.config);
	}
}