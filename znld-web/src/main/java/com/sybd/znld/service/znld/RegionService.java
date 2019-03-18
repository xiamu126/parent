package com.sybd.znld.service.znld;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.RegionModel;
import com.sybd.znld.service.mapper.znld.RegionMapper;
import com.whatever.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DbSource("znld")
public class RegionService implements IRegionService {
    public enum Status{
        Ok((short)0), Frozen((short)1), Deleted((short)2);
        private final Short value;
        Status(Short v) {
            this.value = v;
        }
        public Short getValue(){
            return this.value;
        }
        public static boolean isValid(short v){
            switch (v){
                case 0:
                case 1:
                case 2:
                    return true;
                default:
                    return false;
            }
        }
    }
    private final Logger log = LoggerFactory.getLogger(RegionService.class);
    private final RegionMapper regionMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RegionService(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    @Override
    public RegionModel addRegion(RegionModel model) {
        if(model == null) {
            log.debug("错误的参数");
            return null;
        }
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("区域名称为空");
            return null;
        }
        //区域不能重复定义
        if(this.regionMapper.selectByName(model.name) != null){
            log.debug("区域名字不能重复");
            return null;
        }
        if(!Status.isValid(model.status)){
            log.debug("错误的状态");
            return null;
        }
        if(this.regionMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public RegionModel getRegionById(String id) {
        if(MyString.isEmptyOrNull(id)){
            log.debug("错误的参数");
            return null;
        }
        return this.regionMapper.selectById(id);
    }

    @Override
    public RegionModel getRegionByName(String name) {
        if(MyString.isEmptyOrNull(name)){
            log.debug("错误的参数");
            return null;
        }
        return this.regionMapper.selectByName(name);
    }

    @Override
    public RegionModel modifyRegionById(RegionModel model) {
        if(model == null){
            log.debug("错误的参数");
            return null;
        }
        if(!MyString.isUuid(model.id)){
            log.debug("错误的id");
            return null;
        }
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("错误的区域名称");
            return null;
        }
        if(this.regionMapper.selectByName(model.name) != null){
            log.debug("重复的区域名称");
            return null;
        }
        if(!Status.isValid(model.status)){
            log.debug("错误的状态");
            return null;
        }
        if(this.regionMapper.updateById(model) > 0) return model;
        return null;
    }
}
