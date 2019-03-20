package com.sybd.znld.service.rbac;

import com.sybd.znld.service.model.rbac.OrganizationModel;

import java.util.List;

public interface IOrganizationService {
    OrganizationModel addOrganization(OrganizationModel organization);
    OrganizationModel getOrganizationByName(String name);
    OrganizationModel getOrganizationById(String id);
    OrganizationModel modifyOrganization(OrganizationModel organization);
    List<OrganizationModel> getOrganizationByParenIdAndPosition(String parentId, Integer position);
    OrganizationModel removeOrganizationById(String id);
    List<OrganizationModel> getOrganizationByParenId(String parentId);
}
