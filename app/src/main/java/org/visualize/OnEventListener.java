package org.visualize;

/**
 * Created by cesarferreira on 30/05/14.
 */
public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}