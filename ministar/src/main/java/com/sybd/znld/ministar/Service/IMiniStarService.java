package com.sybd.znld.ministar.Service;

import com.sybd.znld.ministar.model.SubtitleForDevice;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.BaseApiResult;

public interface IMiniStarService {
    BaseApiResult newMiniStar(SubtitleForRegion subtitle);
    BaseApiResult newMiniStar(SubtitleForDevice subtitle);
}
