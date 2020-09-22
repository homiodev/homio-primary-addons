package org.touchhome.bundle.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.http.service.TelegramService;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramEntrypoint implements BundleEntrypoint {

    private final TelegramService telegramService;

    public void init() {
        telegramService.postConstruct();
    }

    @Override
    public String getBundleId() {
        return "telegram";
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public String getSettingDescription() {
        return "telegram.setting.description";
    }
}
