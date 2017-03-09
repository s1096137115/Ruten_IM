package com.avengers.publicim.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by D-IT-MAX2 on 2016/12/30.
 */

public class RetrofixUtils {
	private static Retrofit retrofit;

	public static Retrofit getInstance(String url) {
		if (retrofit == null) {

			HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
			httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.addInterceptor(httpLoggingInterceptor)
					.build();

			retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(url)
//					.baseUrl("https://api.github.com/")
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;

	}
}
