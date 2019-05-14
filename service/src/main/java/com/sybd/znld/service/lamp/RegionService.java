package com.sybd.znld.service.lamp;

import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.model.lamp.RegionModel;
import com.sybd.znld.model.lamp.dto.RegionIdAndName;
import com.sybd.znld.model.lamp.dto.RegionWithLocation;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RegionService implements IRegionService {
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
            Supplier<Stream<RegionWithLocation>> streamSupplier = ret::stream;
            Supplier<Stream<RegionModel>> regionsSupplier = () -> streamSupplier.get().map(r -> RegionModel.builder().
                            id(r.id).
                            name(r.name).
                            organizationId(r.organizationId).
                            status(r.status).
                            type(r.type).
                            build()).distinct();
            var list = new ArrayList<RegionIdAndName>();
            regionsSupplier.get().forEach(r -> {
                var locations = streamSupplier.get().filter(t -> t.id.equals(r.id));
                locations.findFirst().ifPresent(first -> list.add(RegionIdAndName.builder()
                        .id(first.id).name(first.name).longitude(first.longitude).latitude(first.latitude).build()));
            });
            return list;
        }
        return null;
    }
}
