package org.desp.itemSelecter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RewardItemDto {
    private String rewardItemId;
    private int quantity;
}
