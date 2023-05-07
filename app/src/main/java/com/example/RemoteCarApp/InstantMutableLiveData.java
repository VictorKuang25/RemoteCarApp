package com.example.RemoteCarApp;

import androidx.lifecycle.MutableLiveData;

public class InstantMutableLiveData<T> extends MutableLiveData<T> {
    private T instantValue;

    public InstantMutableLiveData() {
        super();
    }

    public InstantMutableLiveData(T value) {
        super(value);
        this.instantValue = value;
    }

    @Override
    public void postValue(T value) {
        instantValue = value;
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        instantValue = value;
        super.setValue(value);
    }

    /**  get set/post value form same thread immediate **/
    public T getInstantValue() {
        return instantValue;
    }
}
