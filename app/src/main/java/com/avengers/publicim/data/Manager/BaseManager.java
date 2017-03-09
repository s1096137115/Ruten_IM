package com.avengers.publicim.data.manager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.avengers.publicim.component.DbHelper;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.Listener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public abstract class BaseManager<T,L extends Listener>{
	protected Context mContext;
	protected DbHelper mDB;
	protected Handler mHandler;
	protected Set<L> mListeners = new CopyOnWriteArraySet<>();

	public BaseManager(Context context) {
		mContext = context;
		mHandler = new Handler();
	}

	public void addListener(L listener){
		mListeners.add(listener);
	}

	public void removeListener(L listener){
		mListeners.remove(listener);
	}

	public abstract List<T> getList(@NonNull String type);

	public abstract T getItem(@NonNull String type, @NonNull String value);

	public abstract void notify(@NonNull ServiceEvent event);
}
