package com.wfz.myspringmvc.dao.impl;

import com.wfz.myspringmvc.annotation.Repository;
import com.wfz.myspringmvc.dao.UserDao;

@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {

    public void insert() {
        System.out.println("execute UserDaoImpl.insert().");
    }
}
