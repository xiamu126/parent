package com.sybd.znld.ministar.task;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.sybd.znld.mapper.lamp.MiniStarTaskMapper;
import com.sybd.znld.ministar.Service.IMiniStarService;
import com.sybd.znld.ministar.model.Subtitle;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.lamp.MiniStarTaskModel;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@Component
public class MyScheduledTask {
    private final RedissonClient redissonClient;
    private static Map<String, RLock> lockers;
    private static final String CHECK_TASK = "CHECK_TASK";
    private final MiniStarTaskMapper miniStarTaskMapper;
    private final IMiniStarService miniStarService;

    @PreDestroy
    public void preDestroy() {
        for (var item : lockers.entrySet()) {
            var locker = item.getValue();
            locker.forceUnlock();
            log.debug("释放锁：" + locker.getName());
        }
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MyScheduledTask(RedissonClient redissonClient, MiniStarTaskMapper miniStarTaskMapper, IMiniStarService miniStarService) {
        this.redissonClient = redissonClient;
        this.miniStarTaskMapper = miniStarTaskMapper;
        this.miniStarService = miniStarService;

        lockers = Map.of(CHECK_TASK, this.redissonClient.getLock(CHECK_TASK));
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 3)
    public void checkMiniStarTask() {
        var locker = lockers.get(CHECK_TASK);
        if (locker != null && locker.tryLock()) {
            try {
                locker.lock();
                var tasks = this.miniStarTaskMapper.selectByStatusWaiting();
                if (tasks != null && !tasks.isEmpty()) {
                    for (var task : tasks) {
                        var tmp = Duration.between(LocalDateTime.now(), task.beginTime);
                        if (tmp.toMinutes() < 3 && task.status.equals(MiniStarTaskModel.TaskStatus.WAITING)) {
                            log.debug("发现可执行的灯带任务");
                            var model = new SubtitleForRegion();
                            model.title = task.title;
                            model.type = Subtitle.getTypeCode(task.effectType);
                            model.userId = task.userId;
                            model.regionId = task.areaId;
                            model.speed = task.speed;
                            model.brightness = task.brightness;
                            model.colors = Subtitle.Rgb.getRgbs(task.colors);
                            model.beginTimestamp = MyDateTime.toTimestamp(task.beginTime);
                            model.endTimestamp = MyDateTime.toTimestamp(task.endTime);
                            this.miniStarService.newMiniStar(model);
                            task.status = MiniStarTaskModel.TaskStatus.FINISHED;
                            this.miniStarTaskMapper.updateStatus(task);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            } finally {
                locker.forceUnlock();
            }
        } else {
            log.debug("获取锁失败");
        }
    }
}
