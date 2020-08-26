package org.touchhome.bundle.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.telegram.service.TelegramService;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/rest/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;

    @GetMapping("user/options")
    public List<Option> getRegisteredUsers() {
        return telegramService.getUsers().stream()
                .map(u -> Option.of(u.getEntityID(), u.getName()))
                .collect(Collectors.toList());
    }
}
