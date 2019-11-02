package com.sybd.znld.environment.controller;

import com.sybd.znld.environment.service.AQI;
import com.sybd.znld.environment.service.dto.AQIResult;
import com.sybd.znld.environment.service.dto.AVGResult;
import com.sybd.znld.mapper.lamp.DataLocationMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.model.lamp.dto.ElementAvgResult;
import com.sybd.znld.model.lamp.dto.LampWithLocation;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MySignature;
import com.sybd.znld.util.MyString;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "区域接口")
@RestController
@RequestMapping("/api/v1/environment/region")
public class RegionController implements IRegionController {
    private final RegionMapper regionMapper;
    private final DataLocationMapper dataLocationMapper;
    private final LampMapper lampMapper;
    private final RedissonClient accountRedis;

    //@Reference(url = "dubbo://localhost:18085")
    //private ISigService sigService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RegionController(RegionMapper regionMapper,
                            DataLocationMapper dataLocationMapper, LampMapper lampMapper,
                            @Qualifier("account-redis") RedissonClient accountRedis) {
        this.regionMapper = regionMapper;
        this.dataLocationMapper = dataLocationMapper;
        this.lampMapper = lampMapper;
        this.accountRedis = accountRedis;
    }

    @Override
    public List<LampWithLocation> getRegionOfEnvironmentList(String organId, HttpServletRequest request) {
        //var ret = this.sigService.checkSig(null,null,null,null,null);
        //log.debug(ret.toString());
        var lamps = this.regionMapper.selectLampsOfEnvironment(organId);
        var result = new ArrayList<LampWithLocation>();
        var lampWithLocation = new LampWithLocation();
        for(var l : lamps){
            lampWithLocation.deviceId = l.deviceId;
            lampWithLocation.deviceName = l.deviceName;
            var jingdu = this.dataLocationMapper.selectByDeviceIdAndResourceName(l.deviceId, "北斗经度");
            var weidu = this.dataLocationMapper.selectByDeviceIdAndResourceName(l.deviceId, "北斗纬度");
            lampWithLocation.longitude = jingdu == null ? "" : jingdu.value.toString();
            lampWithLocation.latitude = weidu == null ? "" : weidu.value.toString();

            result.add(lampWithLocation);
        }
        return result;
    }

    @GetMapping(value="{organId:^[0-9a-f]{32}$}/2", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public List<LampWithLocation> getRegionOfEnvironmentList2(@PathVariable(name = "organId") String organId,
                                                              @RequestHeader("now") Long now,
                                                              @RequestHeader("nonce") String nonce,
                                                              @RequestHeader("sig") String sig,
                                                              @RequestHeader("key") String secretKey) {
        try{
            var data = new HashMap<String, String>();
            data.put("organId", organId);
            MySignature.generate(data, "secretKey");
        }catch (Exception ex){
        }
        finally {
        }
        return null;
    }

    @Override
    public AQIResult getAQILastHourOfOrgan(String organId) {
        if(!MyString.isUuid(organId)) return null;
        var lamps = this.lampMapper.selectEnvironmentLampByOrganId(organId);
        if(lamps == null || lamps.isEmpty()) return null;
        List<AQIResult> values = new ArrayList<>();
        for(var l : lamps){
            var ret = this.getAQILastHourOfDevice(l.deviceId);
            if(ret != null){
                values.add(ret);
            }
        }
        if(values.isEmpty()) return null;
        var value = values.stream().mapToDouble(v -> v.value).average().orElse(0);
        var result = new AQIResult();
        var nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        result.value = Float.parseFloat(nf.format(value));
        result.describe = null;
        result.primary = null;
        result.at = values.get(0).at;
        return result;
    }

    @Override
    public AQIResult getAQILastHourOfDevice(Integer deviceId) {
        var avgs = this.regionMapper.selectAvgOfEnvironmentElementLastHourByDeviceId(deviceId);
        if(avgs == null || avgs.isEmpty()) return null;
        Map<String, Double> map = new HashMap<>();
        avgs.forEach(a -> map.put(a.name, a.value));
        var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
        result.at = MyDateTime.toTimestamp(avgs.get(0).at);
        return result;
    }

    @Override
    public AQIResult getAQILastDayOfDevice(Integer deviceId) {
        var avgs = this.regionMapper.selectAvgOfEnvironmentElementLastDayByDeviceId(deviceId);
        if(avgs == null || avgs.isEmpty()) return null;
        Map<String, Double> map = new HashMap<>();
        avgs.forEach(a -> map.put(a.name, a.value));
        var result = AQI.of24Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"), map.get("PM10"), map.get("PM2.5"));
        result.at = MyDateTime.toTimestamp(avgs.get(0).at);
        return result;
    }

    @Override
    public List<AQIResult> getAQIHourlyOfDevice(Integer deviceId) {
        var now = LocalDateTime.now().minusHours(1);
        var end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        var begin = end.minusHours(1);
        var list = new ArrayList<AQIResult>();
        for(var i = 1; i <= 24; i++){ // 过去二十四个小时
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, begin, end);
            end = begin;
            begin = end.minusHours(1);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public List<AQIResult> getAQIDailyOfDevice(Integer deviceId) {
        var now = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        var begin = end.minusDays(1);
        var list = new ArrayList<AQIResult>();
        for(var i = 1; i <= 30; i++){ // 过去30天
            //log.debug(begin.toString() + "," + end.toString());
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, begin, end);
            end = begin;
            begin = end.minusDays(1);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public List<AQIResult> getAQIMonthlyOfDevice(Integer deviceId) {
        var now = LocalDateTime.now().minusMonths(1); // 从上个月开始
        var yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
        var daysInMonth = yearMonthObject.lengthOfMonth();
        var end = LocalDateTime.of(now.getYear(), now.getMonth(), daysInMonth, 0, 0, 0);
        var begin = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
        var list = new ArrayList<AQIResult>();
        for(var i = 1; i <= 12; i++){ // 过去12个月
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, begin, end);
            now = end.minusMonths(1);
            yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
            daysInMonth = yearMonthObject.lengthOfMonth();
            end = LocalDateTime.of(now.getYear(), now.getMonth(), daysInMonth, 0, 0, 0);
            begin = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public List<AQIResult> getAQIHourlyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toHours() < 1){
            log.debug("参数错误，开始结束时间小于1小时");
            return null;
        }
        if(sub.toDays() > 3) {
            log.debug("参数错误，开始结束时间大于3天");
            return null;
        }
        var now = MyDateTime.toLocalDateTime(end);
        var thisHour = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                LocalDateTime.now().getHour(), 0, 0);
        var minBegin = MyDateTime.toLocalDateTime(begin);
        var theEnd = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        var theBegin = theEnd.minusHours(1);
        var list = new ArrayList<AQIResult>();
        while(theBegin.isAfter(minBegin) || theBegin.isEqual(minBegin)){
            // 如果开始结束时间包含本小时，则跳过，因为这个小时还没结束
            sub = Duration.between(theEnd, thisHour);
            if(sub.toHours() < 1){
                log.debug("结束时间包含本小时（" + theEnd.toString() + "," + thisHour.toString()+ "），跳过，这个小时还没结束");
                theEnd = theBegin;
                theBegin = theEnd.minusHours(1);
                continue;
            }
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, theBegin, theEnd);
            theEnd = theBegin;
            theBegin = theEnd.minusHours(1);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public List<AQIResult> getAQIDailyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toDays() < 1){
            log.debug("参数错误，开始结束时间小于1天");
            return null;
        }
        if(sub.toDays() > 30*3) {
            log.debug("参数错误，开始结束时间大于3个月");
            return null;
        }
        var now = MyDateTime.toLocalDateTime(end);
        var thisDay = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                0, 0, 0);
        var minBegin = MyDateTime.toLocalDateTime(begin);
        var theEnd = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        var theBegin = theEnd.minusDays(1);
        var list = new ArrayList<AQIResult>();
        while(theBegin.isAfter(minBegin) || theBegin.isEqual(minBegin)){
            // 如果开始结束时间包含今天，则跳过，因为今天还没结束
            sub = Duration.between(theEnd, thisDay);
            if(sub.toDays() < 1){
                log.debug("结束时间包含今天（" + theEnd.toString() + "," + thisDay.toString()+ "），跳过，今天还没结束");
                theEnd = theBegin;
                theBegin = theEnd.minusDays(1);
                continue;
            }
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, theBegin, theEnd);
            theEnd = theBegin;
            theBegin = theEnd.minusDays(1);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of24Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"), map.get("PM2.5"), map.get("PM10"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public List<AQIResult> getAQIMonthlyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toDays() < 28){
            log.debug("参数错误，开始结束时间小于1一个月最小天数");
            return null;
        }
        if(sub.toDays() > 365*3) {
            log.debug("参数错误，开始结束时间大于3年");
            return null;
        }
        var now = MyDateTime.toLocalDateTime(end);
        var minBegin = MyDateTime.toLocalDateTime(begin);
        var yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
        var daysInMonth = yearMonthObject.lengthOfMonth();
        var theEnd = LocalDateTime.of(now.getYear(), now.getMonth(), daysInMonth, 0, 0, 0);
        var theBegin = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
        var list = new ArrayList<AQIResult>();
        while(theBegin.isAfter(minBegin) || theBegin.isEqual(minBegin)){
            if(theEnd.isAfter(LocalDateTime.now())){
                log.debug("结束时间包含本月（" + theEnd.toString() + "," + LocalDateTime.now().toString()+ "），跳过，本月还没结束");
                now = theEnd.minusMonths(1);
                yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
                daysInMonth = yearMonthObject.lengthOfMonth();
                theEnd = LocalDateTime.of(now.getYear(), now.getMonth(), daysInMonth, 0, 0, 0);
                theBegin = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
                continue;
            }
            var avgs = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(deviceId, theBegin, theEnd);
            now = theEnd.minusMonths(1);
            yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
            daysInMonth = yearMonthObject.lengthOfMonth();
            theEnd = LocalDateTime.of(now.getYear(), now.getMonth(), daysInMonth, 0, 0, 0);
            theBegin = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
            if(avgs == null || avgs.isEmpty()) continue;
            Map<String, Double> map = new HashMap<>();
            avgs.forEach(a -> map.put(a.name, a.value));
            var result = AQI.of24Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"), map.get("PM2.5"), map.get("PM10"));
            result.at = MyDateTime.toTimestamp(avgs.get(0).at);
            list.add(result);
        }
        return list;
    }

    @Override
    public Map<String, List<AVGResult>> getAvgHourlyOfDevice(Integer deviceId) {
        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "CO");
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "NO2");
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "SO2");
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "O3");
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "PM2.5");
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "PM10");
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "温度");
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceId(deviceId, "湿度");

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "hourly");
    }

    private Map<String, List<AVGResult>> getAvg(List<ElementAvgResult> avgsOfCo,
                                                List<ElementAvgResult> avgsOfNo2,
                                                List<ElementAvgResult> avgsOfSo2,
                                                List<ElementAvgResult> avgsOfO3,
                                                List<ElementAvgResult> avgsOfPm25,
                                                List<ElementAvgResult> avgsOfPm10,
                                                List<ElementAvgResult> avgsOfWendu,
                                                List<ElementAvgResult> avgsOfShidu, String type){
        var listOfCo = new ArrayList<AVGResult>();
        var listOfNo2 = new ArrayList<AVGResult>();
        var listOfSo2 = new ArrayList<AVGResult>();
        var listOfO3 = new ArrayList<AVGResult>();
        var listOfPm25 = new ArrayList<AVGResult>();
        var listOfPm10 = new ArrayList<AVGResult>();
        var listOfWendu = new ArrayList<AVGResult>();
        var listOfShidu = new ArrayList<AVGResult>();
        var result = new HashMap<String, List<AVGResult>>();
        var nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        for(var a : avgsOfCo){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfCo.add(tmp);
        }
        result.put("CO", listOfCo);

        for(var a : avgsOfNo2){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfNo2.add(tmp);
        }
        result.put("NO2", listOfNo2);

        for(var a : avgsOfSo2){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfSo2.add(tmp);
        }
        result.put("SO2", listOfSo2);

        for(var a : avgsOfO3){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfO3.add(tmp);
        }
        result.put("O3", listOfO3);

        for(var a : avgsOfPm25){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfPm25.add(tmp);
        }
        result.put("PM2.5", listOfPm25);

        for(var a : avgsOfPm10){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfPm10.add(tmp);
        }
        result.put("PM10", listOfPm10);

        for(var a : avgsOfWendu){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfWendu.add(tmp);
        }
        result.put("温度", listOfWendu);

        for(var a : avgsOfShidu){
            var tmp = new AVGResult();
            tmp.value = Float.parseFloat(nf.format(a.value));
            tmp.describe = a.name;
            var at = a.at;
            switch (type) {
                case "hourly":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), at.getHour(), 0, 0);
                    break;
                case "daily":
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), at.getDayOfMonth(), 0, 0, 0);
                    break;
                case "monthly":
                    var yearMonthObject = YearMonth.of(at.getYear(), at.getMonth());
                    var daysInMonth = yearMonthObject.lengthOfMonth();
                    at = LocalDateTime.of(at.getYear(), at.getMonth(), daysInMonth, 0, 0, 0);
                    break;
            }
            tmp.at = MyDateTime.toTimestamp(at);
            listOfShidu.add(tmp);
        }
        result.put("湿度", listOfShidu);

        return result;
    }

    @Override
    public Map<String, List<AVGResult>> getAvgDailyOfDevice(Integer deviceId) {
        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "CO");
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "NO2");
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "SO2");
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "O3");
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "PM2.5");
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "PM10");
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "温度");
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceId(deviceId, "湿度");

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "daily");
    }

    @Override
    public Map<String, List<AVGResult>> getAvgMonthlyOfDevice(Integer deviceId) {
        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "CO");
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "NO2");
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "SO2");
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "O3");
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "PM2.5");
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "PM10");
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "温度");
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceId(deviceId, "湿度");

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "monthly");
    }

    @Override
    public Map<String, List<AVGResult>> getAvgHourlyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toHours() < 1){
            log.debug("参数错误，开始结束时间小于1小时");
            return null;
        }
        if(sub.toDays() > 3) {
            log.debug("参数错误，开始结束时间大于3天");
            return null;
        }
        var theBegin = MyDateTime.toLocalDateTime(begin);
        var theEnd = MyDateTime.toLocalDateTime(end);
        var now = LocalDateTime.now();
        while(Duration.between(theEnd, now).toHours() < 1){
            // 如果结束时间包含了本小时，需要修正
            theEnd = theEnd.minusHours(1);
        }
        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "CO", theBegin, theEnd);
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "NO2", theBegin, theEnd);
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "SO2", theBegin, theEnd);
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "O3", theBegin, theEnd);
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "PM2.5", theBegin, theEnd);
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "PM10", theBegin, theEnd);
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "温度", theBegin, theEnd);
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(deviceId, "湿度", theBegin, theEnd);

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "hourly");
    }

    @Override
    public Map<String, List<AVGResult>> getAvgDailyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toDays() < 1){
            log.debug("参数错误，开始结束时间小于1天");
            return null;
        }
        if(sub.toDays() > 30*3) {
            log.debug("参数错误，开始结束时间大于3个月");
            return null;
        }
        var theBegin = MyDateTime.toLocalDateTime(begin);
        var theEnd = MyDateTime.toLocalDateTime(end);
        var now = LocalDateTime.now();
        while(Duration.between(theEnd, now).toDays() < 1){
            // 如果结束时间包含了今天，需要修正
            theEnd = theEnd.minusDays(1);
        }
        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "CO", theBegin, theEnd);
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "NO2", theBegin, theEnd);
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "SO2", theBegin, theEnd);
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "O3", theBegin, theEnd);
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "PM2.5", theBegin, theEnd);
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "PM10", theBegin, theEnd);
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "温度", theBegin, theEnd);
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementDailyByDeviceIdBetween(deviceId, "湿度", theBegin, theEnd);

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "daily");
    }

    @Override
    public Map<String, List<AVGResult>> getAvgMonthlyOfDeviceBetween(Integer deviceId, Long begin, Long end) {
        if(!MyDateTime.isAllPastAndStrict(begin, end)){
            log.debug("参数错误，开始结束时间并非按照严格的过去时间排列");
            return null;
        }
        var sub = Duration.between(MyDateTime.toLocalDateTime(begin), MyDateTime.toLocalDateTime(end));
        if(sub.toDays() < 28){
            log.debug("参数错误，开始结束时间小于1一个月最小天数");
            return null;
        }
        if(sub.toDays() > 365*3) {
            log.debug("参数错误，开始结束时间大于3年");
            return null;
        }

        var theBegin = MyDateTime.toLocalDateTime(begin);
        var theEnd = MyDateTime.toLocalDateTime(end);
        var now = LocalDateTime.now();
        var yearMonthObject = YearMonth.of(now.getYear(), now.getMonth());
        var daysInMonth = yearMonthObject.lengthOfMonth();
        while(Duration.between(theEnd, now).toDays() < daysInMonth){
            // 如果结束时间包含了本月，需要修正
            theEnd = theEnd.minusMonths(1);
        }

        var avgsOfCo = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "CO",
                theBegin, theEnd);
        var avgsOfNo2 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "NO2",
                theBegin, theEnd);
        var avgsOfSo2 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "SO2",
                theBegin, theEnd);
        var avgsOfO3 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "O3",
                theBegin, theEnd);
        var avgsOfPm25 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "PM2.5",
                theBegin, theEnd);
        var avgsOfPm10 = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "PM10",
                theBegin, theEnd);
        var avgsOfWendu = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "温度",
                theBegin, theEnd);
        var avgsOfShidu = this.regionMapper.selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(deviceId, "湿度",
                theBegin, theEnd);

        return getAvg(avgsOfCo, avgsOfNo2, avgsOfSo2, avgsOfO3, avgsOfPm25, avgsOfPm10, avgsOfWendu, avgsOfShidu, "monthly");
    }
}
