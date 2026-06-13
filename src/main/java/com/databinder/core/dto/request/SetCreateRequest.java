package com.databinder.core.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SetCreateRequest {
    private String name;
    private String code;
}