package org.touchhome.bundle.telegram.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TelegramAnswer {
    private final Integer messageId;
    private final String data;
    private final Long userId;
}
