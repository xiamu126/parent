package com.sybd.znld.light.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.lamp.LampAlarmModel;
import com.sybd.znld.model.lamp.dto.LampAlarm;
import com.sybd.znld.model.lamp.dto.Report;
import com.sybd.znld.mapper.lamp.LampAlarmMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService implements IReportService {
    private final LampStatisticsMapper lampStatisticsMapper;
    private final LampAlarmMapper lampAlarmMapper;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private final ObjectMapper objectMapper;
    private final LampMapper lampMapper;

    @Autowired
    public ReportService(LampStatisticsMapper lampStatisticsMapper,
                         LampAlarmMapper lampAlarmMapper,
                         RedissonClient redissonClient,
                         IOneNetService oneNetService,
                         ObjectMapper objectMapper,
                         LampMapper lampMapper) {
        this.lampStatisticsMapper = lampStatisticsMapper;
        this.lampAlarmMapper = lampAlarmMapper;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
        this.objectMapper = objectMapper;
        this.lampMapper = lampMapper;
    }

    @Override
    public Report getReport(String organId, Report.TimeType type) {
        var obj = new Report();
        obj.organId = organId;
        if(type == Report.TimeType.WEEK) {
            // 查看本周的统计
            var data = this.lampStatisticsMapper.selectThisWeekGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum(); // 实际用电
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum(); // 理论用电
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.MONTH) {
            var data = this.lampStatisticsMapper.selectThisMonthGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                var tmp = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    var dateTime = MyDateTime.toLocalDate(d.id);
                    var weekFields = WeekFields.of(Locale.getDefault());
                    detail.key = String.valueOf(dateTime.get(weekFields.weekOfMonth()));
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                var map = new HashMap<String, Report.Detail>();
                var keys = tmp.stream().map(d -> d.key).distinct();
                keys.forEach(k -> {
                    tmp.forEach(d -> {
                        var detail = map.get(k);
                        if(d.key.equals(k)) {
                            if(detail != null) {
                                detail.electricity = detail.electricity + d.electricity;
                                detail.fullElectricity = detail.fullElectricity + d.fullElectricity;
                            } else {
                                map.put(k, d);
                            }
                        }
                    });
                });
                obj.details = new ArrayList<>(map.values());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.YEAR) {
            var data = this.lampStatisticsMapper.selectThisYearGroupMonthByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.WEEK) {
           /* Calendar nowCal = Calendar.getInstance();
            nowCal.add(Calendar.DAY_OF_YEAR,-7);
            Date startTime=nowCal.getTime();
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(startTime)+"00:00:00";
            Date startDateTime = null;
            try {
                startDateTime = sdf2.parse(format);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            var data = this.lampStatisticsMapper.selectThisSevenDayGroupDayByOrganId(organId);
            List<String> dateList = this.getLastDate(7);
            dateList.forEach(dateStr -> {
                long count = data.stream().filter(d -> dateStr.equals(d.id)).count();
                if(count <= 0) {
                    var detail = new Report.Detail();
                    detail.key = dateStr;
                    // detail.electricity = d.energy;
                    // detail.fullElectricity = d.energy;
                    data.add(detail);
                }
            });
            if (data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d -> d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d -> d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d -> d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d -> d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d -> d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.MONTH) {
           /* Calendar nowCal = Calendar.getInstance();
            nowCal.add(Calendar.MONTH,-6);
            Date startTime=nowCal.getTime();
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(startTime)+"00:00:00";
            Date startDateTime = null;
            try {
                startDateTime = sdf2.parse(format);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            /*try{

                SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM");
                String nowdate=formats.format(new Date());//当前月份
                Date d1 = new SimpleDateFormat("yyyy-MM").parse("begin");//定义起始日期
                Date d2 = new SimpleDateFormat("yyyy-MM").parse(nowdate);//定义结束日期  可以去当前月也可以手动写日期。
                Calendar dd = Calendar.getInstance();//定义日期实例
                dd.setTime(d1);//设置日期起始时间
                while (dd.getTime().before(d2)) {//判断是否到结束日期
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
                    String str = sdf1.format(dd.getTime());
                    //System.out.println(str);//输出日期结果
                    dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
                }
                //System.out.println(nowdate);//输出日期结果
            }catch (Exception e){
                e.printStackTrace();
            }*/
            /*
            Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -5);
        String before_six = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH);//六个月前
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(before_six));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(sdf.parse(sdf.format(new Date())));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

            */


            var data = this.lampStatisticsMapper.selectThisSixMonthGroupMonthByOrganId(organId);
            List<String> dateList = this.getLastMonth(6);
            dateList.forEach(dateStr -> {
                long count = data.stream().filter(d -> dateStr.equals(d.id)).count();
                if(count <= 0) {
                    var detail = new Report.Detail();
                    detail.key = dateStr;
                    // detail.electricity = d.energy;
                    // detail.fullElectricity = d.energy;
                    data.add(detail);
                }
            });
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.YEAR) {
           /* Calendar nowCal = Calendar.getInstance();
            nowCal.add(Calendar.YEAR,0);
            Date startTime=nowCal.getTime();
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(startTime)+"00:00:00";
            Date startDateTime = null;
            try {
                startDateTime = sdf2.parse(format);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            var data = this.lampStatisticsMapper.selectThisYearGroupYearByOrganId(organId);
            List<String> dateList = this.getLastYear(1);
            dateList.forEach(dateStr -> {
                long count = data.stream().filter(d -> dateStr.equals(d.id)).count();
                if(count <= 0) {
                    var detail = new Report.Detail();
                    detail.key = dateStr;
                    // detail.electricity = d.energy;
                    // detail.fullElectricity = d.energy;
                    data.add(detail);
                }
            });
            if (data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d -> d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d -> d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d -> d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d -> d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d -> d.avgLight).average().orElse(0.0);
            }
            return obj;
        }
        return null;
    }
    private List<String> getLastDate(int lastDays) {
        List<String> existsDateList = new ArrayList<>();
        Calendar currCalendar = Calendar.getInstance();
        for (int i = 0; i < lastDays; ++i) {
            existsDateList.add(new SimpleDateFormat("yyyy-MM-dd").format(currCalendar.getTime()));
            currCalendar.add(Calendar.DATE, -1);
        }
        return existsDateList;
    }
    private List<String> getLastMonth(int lastMonths) {
        List<String> existsMonthList = new ArrayList<>();
        Calendar currCalendar = Calendar.getInstance();
        for (int i = 0; i < lastMonths; ++i) {
            existsMonthList.add(new SimpleDateFormat("yyyy-MM").format(currCalendar.getTime()));
            currCalendar.add(Calendar.MONTH, -1);
        }
        return existsMonthList;
    }
    private List<String> getLastYear(int lastYears) {
        List<String> existsYearList = new ArrayList<>();
        Calendar currCalendar = Calendar.getInstance();
        for (int i = 0; i < lastYears; ++i) {
            existsYearList.add(new SimpleDateFormat("yyyy").format(currCalendar.getTime()));
            currCalendar.add(Calendar.YEAR, -1);
        }
        return existsYearList;
    }

    @Override
    public Report getReport(String organId, Report.TimeType type, LocalDateTime begin, LocalDateTime end) {
        var obj = new Report();
        obj.organId = organId;
        if(type == Report.TimeType.WEEK) {
            // 查看本周的统计
            var data = this.lampStatisticsMapper.selectThisWeekGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.MONTH) {
            var data = this.lampStatisticsMapper.selectThisMonthGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                var tmp = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    var dateTime = MyDateTime.toLocalDate(d.id);
                    var weekFields = WeekFields.of(Locale.getDefault());
                    detail.key = String.valueOf(dateTime.get(weekFields.weekOfMonth()));
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                var map = new HashMap<String, Report.Detail>();
                var keys = tmp.stream().map(d -> d.key).distinct();
                keys.forEach(k -> {
                    tmp.forEach(d -> {
                        var detail = map.get(k);
                        if(d.key.equals(k)) {
                            if(detail != null) {
                                detail.electricity = detail.electricity + d.electricity;
                                detail.fullElectricity = detail.fullElectricity + d.fullElectricity;
                            } else {
                                map.put(k, d);
                            }
                        }
                    });
                });
                obj.details = new ArrayList<>(map.values());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.YEAR) {
            var data = this.lampStatisticsMapper.selectThisYearGroupMonthByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.WEEK) {
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(begin)+"00:00:00";
            String format2 = sdf.format(end)+"00:00:00";
            Date beginDateTime = null;
            Date endDateTime = null;
            try {
                beginDateTime = sdf2.parse(format);
                endDateTime = sdf2.parse(format2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            var data = this.lampStatisticsMapper.selectDayByOrganIdBetween(organId,begin,end);

            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.MONTH) {
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(begin)+"00:00:00";
            String format2 = sdf.format(end)+"00:00:00";
            Date beginDateTime = null;
            Date endDateTime = null;
            try {
                beginDateTime = sdf2.parse(format);
                endDateTime = sdf2.parse(format2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            var data = this.lampStatisticsMapper.selectMonthByOrganIdBetween(organId,begin,end);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }else if(type == Report.TimeType.YEAR) {
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy");
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(begin)+"00:00:00";
            String format2 = sdf.format(end)+"00:00:00";
            Date beginDateTime = null;
            Date endDateTime = null;
            try {
                beginDateTime = sdf2.parse(format);
                endDateTime = sdf2.parse(format2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            var data = this.lampStatisticsMapper.selectYearByOrganIdBetween(organId,begin,end);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.energy;
                    detail.fullElectricity = d.energy;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.energy).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }
        return null;
    }
   /*public static List<String> findDates(String begin, String end) throws ParseException {
        List<String> allDate = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date dBegin = sdf.parse(begin);
        Date dEnd = sdf.parse(end);
        allDate.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            allDate.add(sdf.format(calBegin.getTime()));
        }
       // System.out.println("时间==" + allDate);
        return allDate;
    }
*/
    //集合中包含2019-05-01/2019-05-05，不需要可去除
    /*List<String> list = new ArrayList<>();
try {
        //只有结束时间大于开始时间时才进行查询===改完
        list = findDates(begin, end);
        //System.out.println("时间啊啊啊==" + list);
    } catch (ParseException e) {
        e.printStackTrace();
    }*/


    @Override
    public List<LampAlarm.Message> getAlarmList(String organId) {
        if(!MyString.isUuid(organId)) return null;
        var models = this.lampAlarmMapper.selectByOrganId(organId);
        if(models == null || models.isEmpty()) return null;
        return models.stream().map(m -> {
            var tmp = new LampAlarm.Message();
            tmp.id = m.id;
            tmp.at = MyDateTime.toTimestamp(m.at);
            tmp.content = m.content;
            tmp.lampId = m.lampId;
            tmp.lampName = m.lampName;
            tmp.regionName = m.regionName;
            tmp.status = m.status.getDescribe();
            tmp.type = m.type.getDescribe();
            return tmp;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer ignoreAlarm(List<String> ids) {
        if(ids == null || ids.isEmpty()) return null;
        var count = 0;
        for(var id : ids) {
            var alarm = this.lampAlarmMapper.selectById(id);
            if(alarm != null) {
                alarm.status = LampAlarmModel.Status.IGNORED;
                if(this.lampAlarmMapper.update(alarm) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void statistics(RawData rawData, String name) {
        try {
            rawData.value = rawData.value.toString().replaceAll("'","\"");
            // 首先把数据存入redis
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
            var lastData = (RealTimeData)map.get(name); // 上一次的数据
            var realTimeData = new RealTimeData();
            realTimeData.describe = name;
            realTimeData.value = rawData.value;
            realTimeData.at = MyDateTime.toTimestamp(rawData.at);
            map.put(name, realTimeData); // 更新实时缓存
            var obj = this.objectMapper.readValue(rawData.value.toString(), LampStatistics.class);
            var electricity = MyNumber.getDouble(map.get(Config.REDIS_MAP_KEY_ENERGY)); // 上一次累计的电量
            var ep = obj.EP.get(1);
            if(electricity == null) {
                if(ep == null || ep <= 0) {
                    electricity = 0.0;
                } else {
                    electricity = ep; // 把这一次的数据累计上去
                }
            }else {
                if(ep != null && ep > 0) {
                    electricity = electricity + ep; // 把这一次的数据累计上去
                }
            }

            map.put(Config.REDIS_MAP_KEY_IS_LIGHT, obj.B > 0); // 当前灯的亮度状态
            map.put(Config.REDIS_MAP_KEY_IS_FAULT, false); // 当前灯的故障状态
            map.put(Config.REDIS_MAP_KEY_ENERGY, electricity);
            var isOnline = map.get(Config.REDIS_MAP_KEY_IS_ONLINE);
            if(isOnline == null) { // 如果设备的在线状态未知，则手动刷新下
                isOnline = this.oneNetService.isDeviceOnline(rawData.imei);
                map.put(Config.REDIS_MAP_KEY_IS_ONLINE, isOnline); // 如果设备的在线状态为空，则更新设备的在线状态
            }
            var ids = this.lampMapper.selectLampRegionOrganIdByImei(rawData.imei);
            var lastUpdateStatisticsTime = (Long) map.get(Config.REDIS_MAP_KEY_LAST_STATISTICS_UPDATE_TIME); // 上次更新数据库的时间
            if(lastUpdateStatisticsTime == null) {
                // 更新数据库
                var model = new LampStatisticsModel();
                model.lampId = ids.lampId;
                model.regionId = ids.regionId;
                model.organId = ids.organId;
                model.isOnline = this.oneNetService.isDeviceOnline(rawData.imei);
                model.isLight =  obj.B > 0;
                model.isFault = false; // 故障暂时不做判断
                model.energy = electricity; // 到目前为止累计电能
                model.updateTime = rawData.at;
                this.lampStatisticsMapper.insert(model);
                map.put(Config.REDIS_MAP_KEY_LAST_STATISTICS_UPDATE_TIME, MyDateTime.toTimestamp(LocalDateTime.now()));
                map.put(Config.REDIS_MAP_KEY_ENERGY, 0); // 清空累计，也就是我只保存这一个小时的电量，下个周期从0开始重新计算
            } else {
                // 存在上一次的更新时间，则看上一次的更新时间，到现在有没有达到一个小时（至少）
                var lastTime = MyDateTime.toLocalDateTime(lastUpdateStatisticsTime);
                if(lastTime != null) {
                    var now = LocalDateTime.now();
                    var hours = Duration.between(lastTime, now).abs().toHours();
                    if(hours >= 1) {
                        // 更新数据库
                        var model = new LampStatisticsModel();
                        model.lampId = ids.lampId;
                        model.regionId = ids.regionId;
                        model.organId = ids.organId;
                        model.isOnline = this.oneNetService.isDeviceOnline(rawData.imei);
                        model.isLight =  obj.B > 0;
                        model.isFault = false; // 故障暂时不做判断
                        model.energy = electricity; // 到目前为止累计电能
                        model.updateTime = rawData.at;
                        this.lampStatisticsMapper.insert(model);
                        map.put(Config.REDIS_MAP_KEY_LAST_STATISTICS_UPDATE_TIME, MyDateTime.toTimestamp(LocalDateTime.now()));
                        map.put(Config.REDIS_MAP_KEY_ENERGY, 0); // 清空累计，也就是我只保存这一个小时的点亮，下个周期从0开始重新计算
                    }
                }
            }
            // 最后将收到的数据推送到页面
            var statistics = new LampStatistic();
            var msg = new LampStatistic.Message();
            msg.id = ids.lampId;
            msg.voltage =  new LampStatistic.Message.ValueError<>(obj.V, obj.V != null && obj.V <= 0.1);
            msg.brightness = new LampStatistic.Message.ValueError<>(obj.B, obj.B != null && obj.B >= 0 && obj.B <= 100);
            msg.electricity = new LampStatistic.Message.ValueError<>(obj.I.get(1), obj.I.get(1) != null && obj.I.get(1) <= 0.1);
            msg.energy = new LampStatistic.Message.ValueError<>(obj.EP.get(1), obj.EP.get(1) != null && obj.EP.get(1) <= 1.5);
            msg.power = new LampStatistic.Message.ValueError<>(obj.PP.get(1), obj.PP.get(1) != null && obj.EP.get(1) <= 1.5);
            var pp = obj.PP.get(1);
            var ps = obj.PS.get(1);
            if(ps == null || ps <= 0) {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(0.0, true);
            } else {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(pp / ps, false);
            }
            msg.rate = new LampStatistic.Message.ValueError<>(obj.HZ, obj.HZ != null && obj.HZ <= 60);
            msg.updateTime = MyDateTime.toTimestamp(rawData.at);
            statistics.message = msg;
            var json = this.objectMapper.writeValueAsString(statistics);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }
}
