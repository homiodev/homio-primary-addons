package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.touchhome.bundle.api.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@RequestMapping("/rest/raspberry")
public class RaspberryController {

    private final RaspberryGPIOService raspberryGPIOService;

    @GetMapping("DS18B20")
    public List<BaseEntity> getRaspberryDS18B20() {
        return raspberryGPIOService.getDS18B20()
                .stream().map(s -> BaseEntity.of(s, s)).collect(Collectors.toList());
    }
}
