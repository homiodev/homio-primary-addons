package org.homio.addon.telegram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.homio.api.EntityContext;
import org.homio.api.model.OptionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
