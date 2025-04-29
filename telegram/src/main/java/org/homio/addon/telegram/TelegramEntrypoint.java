package org.homio.addon.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.homio.api.AddonEntrypoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramEntrypoint implements AddonEntrypoint {

    public void init() {

    }

    @Override
    public @NotNull AddonImageColorIndex getAddonImageColorIndex() {
        return AddonImageColorIndex.ONE;
    }
}
