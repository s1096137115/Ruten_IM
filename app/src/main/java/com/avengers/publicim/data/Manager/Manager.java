package com.avengers.publicim.data.Manager;


import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public interface Manager<T> {

	List<T> getList();

	T getItem(String value);

	void setList(List<T> list);

	boolean contains(T item);

	boolean contains(String value);

	void sort();

	void reload();
}
