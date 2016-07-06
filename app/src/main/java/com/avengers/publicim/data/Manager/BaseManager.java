package com.avengers.publicim.data.Manager;

import android.content.Context;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.data.callback.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public abstract class BaseManager<T,E extends Listener> implements Manager<T>{
	protected DbHelper mDB;

	protected List<T> mList = new ArrayList<>();

	protected Set<E> mListeners = new CopyOnWriteArraySet<>();

	public BaseManager(Context context) {
		mDB = DbHelper.getInstance(context);
	}

	public void addListener(E listener){
		mListeners.add(listener);
	}

	public void removeListener(E listener){
		mListeners.remove(listener);
	}
}
