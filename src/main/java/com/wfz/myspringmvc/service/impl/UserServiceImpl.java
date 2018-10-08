package com.wfz.myspringmvc.service.impl;

import com.wfz.myspringmvc.annotation.Qualifier;
import com.wfz.myspringmvc.annotation.Service;
import com.wfz.myspringmvc.dao.UserDao;
import com.wfz.myspringmvc.service.UserService;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Qualifier("userDaoImpl")
    private UserDao userDao;

    public void insert() {
        System.out.println("UserServiceImpl.insert() start。。。");

        userDao.insert();

        System.out.println("UserServiceImpl.insert() end。。。");

    }
}
