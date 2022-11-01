package org.touchhome.bundle.zigbee;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;

@Log4j2
public class ZigBeeIsAliveTracker {

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final Map<ZigBeeDeviceService, Integer> handlerIntervalMapping = new ConcurrentHashMap<>();
  private final Map<ZigBeeDeviceService, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  public void addHandler(ZigBeeDeviceService zigBeeDeviceService, int expectedUpdateInterval) {
    zigBeeDeviceService.setExpectedUpdateInterval(expectedUpdateInterval);
    log.debug("{}: Add IsAlive Tracker", zigBeeDeviceService.getNodeIeeeAddress());
    handlerIntervalMapping.put(zigBeeDeviceService, expectedUpdateInterval);
    resetTimer(zigBeeDeviceService);
  }

  public void removeHandler(ZigBeeDeviceService zigBeeDeviceService) {
    log.debug("{}: Remove IsAlive Tracker", zigBeeDeviceService.getNodeIeeeAddress());
    cancelTask(zigBeeDeviceService);
    handlerIntervalMapping.remove(zigBeeDeviceService);
  }

  public synchronized void resetTimer(ZigBeeDeviceService zigBeeDeviceService) {
    if (handlerIntervalMapping.containsKey(zigBeeDeviceService)) {
      zigBeeDeviceService.setExpectedUpdateIntervalTimer(System.currentTimeMillis());
      log.debug("{}: Reset timeout for handler with zigBeeDevice", zigBeeDeviceService.getNodeIeeeAddress());
      cancelTask(zigBeeDeviceService);
      scheduleTask(zigBeeDeviceService);
    }
  }

  private void scheduleTask(ZigBeeDeviceService zigBeeDeviceService) {
    ScheduledFuture<?> existingTask = scheduledTasks.get(zigBeeDeviceService);
    if (existingTask == null && handlerIntervalMapping.containsKey(zigBeeDeviceService)) {
      int interval = handlerIntervalMapping.get(zigBeeDeviceService);
      log.debug("{}: Scheduling timeout task for zigBeeDevice in {} seconds", zigBeeDeviceService.getNodeIeeeAddress(), interval);
      ScheduledFuture<?> task = scheduler.schedule(() -> {
        log.debug("{}: Timeout has been reached for zigBeeDevice", zigBeeDeviceService.getNodeIeeeAddress());
        zigBeeDeviceService.aliveTimeoutReached();
        scheduledTasks.remove(zigBeeDeviceService);
      }, interval, TimeUnit.SECONDS);

      scheduledTasks.put(zigBeeDeviceService, task);
    }
  }

  private void cancelTask(ZigBeeDeviceService zigBeeDeviceService) {
    ScheduledFuture<?> task = scheduledTasks.get(zigBeeDeviceService);
    if (task != null) {
      log.debug("{}: Canceling timeout task for zigBeeDevice", zigBeeDeviceService.getNodeIeeeAddress());
      task.cancel(true);
      scheduledTasks.remove(zigBeeDeviceService);
    }
  }
}
