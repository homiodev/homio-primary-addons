package org.homio.addon.telegram;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.api.AddonEntrypoint;
import org.homio.api.EntityContext;
import org.homio.addon.telegram.service.TelegramService;
import org.homio.api.model.Icon;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramEntrypoint implements AddonEntrypoint {

    private final EntityContext entityContext;
    private final TelegramService telegramService;

    public void init() {
        entityContext.ui().addNotificationBlockOptional("telegram", "Telegram", new Icon("fab fa-telegram", "#0088CC"));
        entityContext.event().runOnceOnInternetUp("telegram-start", () -> {
            for (TelegramEntity telegramEntity : entityContext.findAll(TelegramEntity.class)) {
                telegramService.restart(telegramEntity);
            }
        });
        //listen for bot name/token changes and fire restart
        entityContext.event().addEntityUpdateListener(TelegramEntity.class, "listen-telegram-to-start", (newValue, oldValue) -> {
            if (oldValue == null || !Objects.equals(newValue.getBotName(), oldValue.getBotName()) ||
                !Objects.equals(newValue.getBotToken(), oldValue.getBotToken())) {
                if (StringUtils.isNotEmpty(newValue.getBotName()) && StringUtils.isNotEmpty(newValue.getBotToken().asString())) {
                    newValue.reboot(entityContext);
                }
            }
        });
    }

    @Override
    public AddonImageColorIndex getAddonImageColorIndex() {
        return AddonImageColorIndex.ONE;
    }
}
