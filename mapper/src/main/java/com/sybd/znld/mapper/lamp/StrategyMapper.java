package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.Strategy;
import com.sybd.znld.model.lamp.LampStrategyModel;
import com.sybd.znld.model.lamp.Target;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
@DbSource("znld")
public interface StrategyMapper {
}