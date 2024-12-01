package com.prove.domain.Prove.Dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveWithF_CNTDTO {
    List<ProveDtoV2> proveDtoV2;
    Long friendCnt;
}
