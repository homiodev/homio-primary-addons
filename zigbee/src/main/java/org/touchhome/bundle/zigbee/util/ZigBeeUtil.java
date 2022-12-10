package org.touchhome.bundle.zigbee.util;

import com.zsmartsystems.zigbee.CommandResult;
import java.util.concurrent.Future;
import org.touchhome.bundle.api.model.ActionResponseModel;

public final class ZigBeeUtil {

  public static ActionResponseModel toResponseModel(Future<CommandResult> command) {
    try {
      CommandResult commandResult = command.get();
      if (commandResult.isSuccess()) {
        return ActionResponseModel.success();
      } else {
        return ActionResponseModel.showError("Unable to execute command. Response: [code '" +
            commandResult.getStatusCode() + "', data: '" + commandResult + "']");
      }
    } catch (Exception ex) {
      return ActionResponseModel.showError(ex);
    }
  }
}
