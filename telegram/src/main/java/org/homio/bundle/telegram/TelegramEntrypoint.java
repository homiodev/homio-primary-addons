package org.homio.bundle.telegram;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.bundle.api.BundleEntrypoint;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.telegram.service.TelegramService;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;
    private final TelegramService telegramService;

    public void init() {
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
    public int order() {
        return 200;
    }

    @Override
    public BundleImageColorIndex getBundleImageColorIndex() {
        return BundleImageColorIndex.ONE;
    }
}
