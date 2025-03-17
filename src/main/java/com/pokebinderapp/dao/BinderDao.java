package com.pokebinderapp.dao;

import com.pokebinderapp.model.Binder;

import java.util.List;

public interface BinderDao {

    Binder createBinder(Binder binder);

    List<Binder> getAllBinders();

    List<Binder> getBindersByUserId(int userId);

    Binder getBinderById(int binderId);

    boolean updateBinderName(int binderId, String newName);

    boolean deleteBinder(int binderId);}
