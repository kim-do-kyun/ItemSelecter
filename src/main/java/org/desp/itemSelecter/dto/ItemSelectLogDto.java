package org.desp.itemSelecter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ItemSelectLogDto {
    private String user_id;
    private String uuid;
    private String selectedItem;
    private int quantity;
}
