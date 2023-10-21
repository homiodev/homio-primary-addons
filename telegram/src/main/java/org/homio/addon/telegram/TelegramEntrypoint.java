package org.homio.addon.telegram;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.telegram.service.TelegramService;
import org.homio.api.AddonEntrypoint;
import org.homio.api.Context;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramEntrypoint implements AddonEntrypoint {

    private final Context context;
    private final TelegramService telegramService;

    public void init() {
        context.event().runOnceOnInternetUp("telegram-start", () -> {
            for (TelegramEntity telegramEntity : context.db().findAll(TelegramEntity.class)) {
                telegramService.restart(telegramEntity);
            }
        });
        //listen for bot name/token changes and fire restart
        context.event().addEntityUpdateListener(TelegramEntity.class, "listen-telegram-to-start", (newValue, oldValue) -> {
            if (oldValue == null || !Objects.equals(newValue.getBotName(), oldValue.getBotName()) ||
                    !Objects.equals(newValue.getBotToken(), oldValue.getBotToken())) {
                if (StringUtils.isNotEmpty(newValue.getBotName()) && StringUtils.isNotEmpty(newValue.getBotToken().asString())) {
                    newValue.reboot(context);
                }
            }
        });
    }

    @Override
    public AddonImageColorIndex getAddonImageColorIndex() {
        return AddonImageColorIndex.ONE;
    }
}
