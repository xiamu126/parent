package com.sybd.znld.service.znld;

import com.sybd.znld.model.RegionModel;
import com.sybd.znld.service.znld.dto.RegionIdAndName;
import com.sybd.znld.service.znld.mapper.RegionMapper;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService implements IRegionService {
    private final Logger log = LoggerFactory.getLogger(RegionService.class);
    private final RegionMapper regionMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RegionService(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    @Override
    public RegionModel getOneRegion() {
        return this.regionMapper.selectOne();
    }

    @Override
    public RegionModel addRegion(RegionModel model) {
        if(model == null || !model.isValid()) {
            log.debug("错误的参数");
            return null;
        }
        //区域不能重复定义
        if(this.regionMapper.selectByName(model.name) != null){
            log.debug("区域名字不能重复");
            return null;
        }
        if(!RegionModel.Status.isValid(model.status)){
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
        if(model == null) return null;
        if(!MyString.isUuid(model.id)){
            log.debug("错误的id"); return null;
        }
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("错误的区域名称"); return null;
        }
        if(this.regionMapper.selectByName(model.name) != null){
            log.debug("重复的区域名称"); return null;
        }
        if(!RegionModel.Status.isValid(model.status)){
            log.debug("错误的状态"); return null;
        }
        if(this.regionMapper.updateById(model) > 0) return model;
        return null;
    }

    @Override
    public List<RegionModel> getAllRegion() {
        return this.regionMapper.selectAll();
    }

    @Override
    public List<RegionModel> getRegion(int count) {
        if(count <= 0) return null;
        return this.regionMapper.select(count);
    }

    @Override
    public List<RegionIdAndName> getAllRegionWithValidLamp(String organId) {
        var ret = this.regionMapper.selectAllRegionWithValidLamp(organId);
        if(ret != null){
            return ret.stream().map( t -> new RegionIdAndName(t.id, t.name)).collect(Collectors.toList());
        }
        return null;
    }
}
