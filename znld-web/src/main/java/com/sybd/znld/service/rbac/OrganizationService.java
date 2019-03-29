package com.sybd.znld.service.rbac;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.OrganizationModel;
import com.sybd.znld.service.rbac.mapper.OrganizationMapper;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
public class OrganizationService implements IOrganizationService {
    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);
    private final OrganizationMapper organizationMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OrganizationService(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    @Override
    public OrganizationModel addOrganization(OrganizationModel model) {
        if(model == null) {
            log.debug("输入参数为空");
            return null;
        }
        if(this.organizationMapper.selectByName(model.name) != null){
            log.debug("已经存在同名的组织");
            return null;
        }
        //如果设置了父节点，则检测此父节点是否存在，若没设置意味着顶级节点
        if(!MyString.isEmptyOrNull(model.parentId) && this.organizationMapper.selectById(model.parentId) == null) {
            log.debug("设置了父节点（不为空），但此父节点是无效的");
            return null;
        }
        //监测position节点，同一个parentId下，不能重复
        var ret = this.organizationMapper.selectByParentIdAndPosition(model.parentId, model.position);
        if(ret != null && !ret.isEmpty()) {
            log.debug("已经存在相同的父节点与位置");
            return null;
        }
        if(this.organizationMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public OrganizationModel getOrganizationByName(String name) {
        if(MyString.isEmptyOrNull(name)) return null;
        return this.organizationMapper.selectByName(name);
    }

    @Override
    public OrganizationModel getOrganizationById(String id) {
        if(MyString.isEmptyOrNull(id)) return null;
        return this.organizationMapper.selectById(id);
    }

    @Override
    public OrganizationModel modifyOrganization(OrganizationModel organization) {
        if(organization == null || !MyString.isUuid(organization.id)) return null;
        if(this.organizationMapper.updateById(organization) > 0) return organization;
        return null;
    }

    @Override
    public List<OrganizationModel> getOrganizationByParenIdAndPosition(String parentId, Integer position) {
        if(!parentId.equals("") && !MyString.isUuid(parentId)) return null;
        if(MyNumber.isNegative(position)) return null;
        return this.organizationMapper.selectByParentIdAndPosition(parentId, position);
    }

    @Override
    public OrganizationModel removeOrganizationById(String id) {
        if(MyString.isEmptyOrNull(id) || !MyString.isUuid(id)) return null;
        //查看此parentId是否被其他人引用过，如果存在有效引用，则不能删除
        var ret = this.organizationMapper.selectByParentId(id);
        if(ret != null && !ret.isEmpty()){
            log.debug("当前id正在被引用");
            return null;
        }
        var tmp = this.organizationMapper.selectById(id);
        if(this.organizationMapper.deleteById(id) > 0) return tmp;
        return null;
    }

    @Override
    public List<OrganizationModel> getOrganizationByParenId(String parentId) {
        if(!MyString.isUuid(parentId)) return null;
        return this.organizationMapper.selectByParentId(parentId);
    }
}
