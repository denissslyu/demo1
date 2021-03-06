package com.yzt.zhmp.service.impl;

import com.yzt.zhmp.beans.Cbuilding;
import com.yzt.zhmp.beans.Cdistrict;
import com.yzt.zhmp.beans.DisUserAddDisName;
import com.yzt.zhmp.dao.CollectionSystemDao;
import com.yzt.zhmp.service.CollectionSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author .
 */
@Service
public class CollectionSystemServiceImpl implements CollectionSystemService {

    /**
     * 这里的单引号不能少，否则会报错，被识别是一个对象
     */
    private static final String CACHE_KEY = "'cbuilding'";
    private static final String DEMO_CACHE_NAME = "cbuilding";

    @Autowired
    private CollectionSystemDao collectionSystemDao;

    @Override
    public int addFarmerInfo(Cbuilding cbuilding) {
        return collectionSystemDao.addFarmerInfo(cbuilding);
    }

    @Override
    public int addCountyInfo(Cdistrict cdistrict) {
        return collectionSystemDao.addCountyInfo(cdistrict);
    }

    @Override
    public Cdistrict selectCdistrict(String disCode) {
        return collectionSystemDao.selectCdistrict(disCode);
    }

    @Override
    public int updateCdistrict(Cdistrict cdistrict) {
        return collectionSystemDao.updateCdistrict(cdistrict);
    }

    @Override
    public Cbuilding selectBuildingByid(int buID) {
        return collectionSystemDao.selectCbuildingByid(buID);
    }


    @Override
    @Cacheable(value = DEMO_CACHE_NAME,key = "'cbuilding_'+#disCode")
    public List<Cbuilding> selectBuildingBycode(String disCode) {
        return collectionSystemDao.selectBuildingBycode(disCode);
    }

    @Override
    @CacheEvict(value = DEMO_CACHE_NAME,key = "'cbuilding_'+#buID")
    public int deleteCbuilidingByid(int buID) {
        return collectionSystemDao.deleteCbuilidingByid(buID);
    }

    @Override
    @CachePut(value = DEMO_CACHE_NAME,key = "'cbuilding_'+#cbuilding")
    public int updateCbuidingByfamilyType(Cbuilding cbuilding) {
        return collectionSystemDao.updateCbuidingByfamilyType(cbuilding);
    }

    @Override
    public List<DisUserAddDisName> selectUserByuserid(int usrID) {
        return collectionSystemDao.selectUserByuserid(usrID);
    }

    @Override
    public String selectDisName(int usrID) {
        return collectionSystemDao.selectDisName(usrID);
    }




}
