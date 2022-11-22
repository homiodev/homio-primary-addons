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

  public void addHandler(ZigBeeDeviceService service, int expectedUpdateInterval) {
    service.setExpectedUpdateInterval(expectedUpdateInterval);
    log.debug("[{}]: Add IsAlive Tracker {}", service.getEntityID(), service.getNodeIeeeAddress());
    handlerIntervalMapping.put(service, expectedUpdateInterval);
    resetTimer(service);
  }

  public void removeHandler(ZigBeeDeviceService service) {
    log.debug("[{}]: Remove IsAlive Tracker {}", service.getEntityID(), service.getNodeIeeeAddress());
    cancelTask(service);
    handlerIntervalMapping.remove(service);
  }

  public synchronized void resetTimer(ZigBeeDeviceService service) {
    if (handlerIntervalMapping.containsKey(service)) {
      service.setExpectedUpdateIntervalTimer(System.currentTimeMillis());
      log.debug("[{}]: Reset timeout for handler with zigBeeDevice {}", service.getEntityID(), service.getNodeIeeeAddress());
      cancelTask(service);
      scheduleTask(service);
    }
  }

  private void scheduleTask(ZigBeeDeviceService service) {
    ScheduledFuture<?> existingTask = scheduledTasks.get(service);
    if (existingTask == null && handlerIntervalMapping.containsKey(service)) {
      int interval = handlerIntervalMapping.get(service);
      log.debug("[{}]: Scheduling timeout task {} for zigBeeDevice in {} seconds", service.getEntityID(), service.getNodeIeeeAddress(), interval);
      ScheduledFuture<?> task = scheduler.schedule(() -> {
        log.info("[{}]: Timeout has been reached for zigBeeDevice {}", service.getEntityID(), service.getNodeIeeeAddress());
        service.aliveTimeoutReached();
        scheduledTasks.remove(service);
      }, interval, TimeUnit.SECONDS);

      scheduledTasks.put(service, task);
    }
  }

  private void cancelTask(ZigBeeDeviceService service) {
    ScheduledFuture<?> task = scheduledTasks.get(service);
    if (task != null) {
      log.debug("[{}]: Canceling timeout task for zigBeeDevice {}", service.getEntityID(), service.getNodeIeeeAddress());
      task.cancel(true);
      scheduledTasks.remove(service);
    }
  }
}
