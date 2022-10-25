package org.touchhome.bundle.raspberry;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.touchhome.bundle.api.entity.BaseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/raspberry")
public class RaspberryController {

  private final RaspberryGPIOService raspberryGPIOService;

  @GetMapping("DS18B20")
  public List<BaseEntity> getRaspberryDS18B20() {
    return raspberryGPIOService.getDS18B20()
        .stream().map(s -> BaseEntity.fakeEntity(s).setName(s)).collect(Collectors.toList());
  }
}
