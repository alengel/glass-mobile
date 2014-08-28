package com.brandwatch.glassmobile.data;

import android.util.Log;

import com.glass.brandwatch_shared.utils.PropertiesManager;
import com.semantics3.api.Products;

public class Semantics3Data {
	private static final String TAG = Semantics3Data.class.getSimpleName();

	public static String getData(String query) {

		Log.i(TAG, "Measurement " + String.format("Requesting features data for query '%s'", query));

		Products products = new Products(PropertiesManager.getProperty("semantics3_key"),
				PropertiesManager.getProperty("semantics3_secret"));

		products.productsField("search", query);

		try {
			String data = products.getProducts().toString();

			Log.i(TAG, "Measurement " + "Receiving features");

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}