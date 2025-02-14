package org.desp.itemSelecter.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ItemListDto {
    private String consumeItemID;
    private Map<String, Integer> rewardItems;
}
