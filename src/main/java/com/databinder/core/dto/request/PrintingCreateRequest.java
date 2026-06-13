package com.databinder.core.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PrintingCreateRequest {

    private Long cardId;
    private Long setId;
    private String collectorNumber;
    private String imageUrl;
}
