package org.touchhome.bundle.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/rest/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final EntityContext entityContext;

    @GetMapping("entityUser")
    public Collection<OptionModel> getEntityAndUsers() {
        List<OptionModel> models = new ArrayList<>();
        for (TelegramEntity telegramEntity : entityContext.findAll(TelegramEntity.class)) {
            models.add(OptionModel.of(telegramEntity.getEntityID(), telegramEntity.getTitle() + "/All"));
            telegramEntity.getUsers().stream().forEach(user ->
                    models.add(OptionModel.of(telegramEntity.getEntityID() + "/" + user.getId(),
                            telegramEntity.getTitle() + "/" + user.getName())));
        }
        return models;
    }
}
