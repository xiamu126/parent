package com.sybd.znld.position.controller;

import com.sybd.znld.mapper.lamp.GpggaMapper;
import com.sybd.znld.position.controller.dto.HistoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/positioning/history")
public class HistoryController {
    @Autowired
    private GpggaMapper gpggaMapper;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<HistoryItem> getList(){
        var models = this.gpggaMapper.list();
        var items = models.stream().map(m -> {
           var item = new HistoryItem();
            item.name = m.filename;
            item.beginTime = m.beginTime;
            item.endTime = m.endTime;
            return item;
        });
        return items.collect(Collectors.toList());
    }

    @DeleteMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public int delete(@RequestHeader("name") String filename){
        return this.gpggaMapper.delete(filename);
    }
}
