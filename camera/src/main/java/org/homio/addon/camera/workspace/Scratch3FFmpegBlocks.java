package org.homio.addon.camera.workspace;

import com.pivovarit.function.ThrowingBiConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.homio.addon.camera.CameraEntrypoint;
import org.homio.api.Context;
import org.homio.api.ContextMedia.FFMPEG;
import org.homio.api.ContextMedia.FFMPEGHandler;
import org.homio.api.workspace.WorkspaceBlock;
import org.homio.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.homio.api.ContextMedia.FFMPEGFormat.RTSP_ALARMS;
import static org.homio.api.util.CommonUtils.addToListSafe;

@Log4j2
@Getter
@Component
public class Scratch3FFmpegBlocks extends Scratch3ExtensionBlocks {

  private static final int FFMPEG_WAIT_TO_START_TIMEOUT = 1000;

  public Scratch3FFmpegBlocks(Context context, CameraEntrypoint cameraEntrypoint) {
    super("#87B023", context, cameraEntrypoint, "ffmpeg");
    setParent(ScratchParent.media);

    blockCommand(10, FFmpegApplyHandler.argsInput.name(), "Input arg [VALUE]", this::skipHandler, block ->
      block.addArgument(VALUE, ""));
    blockCommand(20, FFmpegApplyHandler.argsOutput.name(), "Output arg [VALUE]", this::skipHandler, block ->
      block.addArgument(VALUE, ""));

    blockCommand(30, "fire_ffmpeg", "Run FFmpeg input [INPUT] output [OUTPUT]", this::fireFFmpegCommand, block -> {
      block.addArgument("INPUT", "");
      block.addArgument("OUTPUT", "");
    });
  }

  private void skipHandler(WorkspaceBlock workspaceBlock) {
    // skip execution
  }

  private void fireFFmpegCommand(WorkspaceBlock workspaceBlock) throws InterruptedException {
    String input = workspaceBlock.getInputString("INPUT");
    String output = workspaceBlock.getInputString("OUTPUT");
    FfmpegBuilder ffmpegBuilder = new FfmpegBuilder();
    applyParentBlocks(ffmpegBuilder, workspaceBlock.getParent());

    FFMPEG ffmpeg = context.media().buildFFMPEG(workspaceBlock.getId(),
      "FFMPEG workspace", new FFMPEGHandler() {


        @Override
        public void ffmpegError(@NotNull String error) {
          log.error("FFmpeg error: <{}>", error);
        }

        @Override
        public void ffmpegLog(@NotNull Level level, @NotNull String message) {
          log.log(level, "{}", message);
        }

      }, RTSP_ALARMS, String.join(" ", ffmpegBuilder.inputArgs), input,
      String.join(" ", ffmpegBuilder.outputArgs),
      output, "", "");
    try {
      ffmpeg.startConverting();
      // wait to able process start
      Thread.sleep(FFMPEG_WAIT_TO_START_TIMEOUT);
      while (!Thread.currentThread().isInterrupted() && ffmpeg.getIsAlive()) {
        Thread.sleep(1000);
      }
    } catch (InterruptedException ex) { // thread was interrupted
      log.error("Error while wait finish for ffmpeg: <{}>", ex.getMessage());
      // if someone else interrupted thread and ffmpeg not started yet - wait 1 sec to allow process to be started
      if (!ffmpeg.getIsAlive()) {
        Thread.sleep(FFMPEG_WAIT_TO_START_TIMEOUT);
      }
    }

    ffmpeg.stopConverting();
    log.info("FFmpeg has been stopped");

       /* Path ffmpegPath = context.setting().getValue(CameraFFMPEGInstallPathOptions.class);
        FfmpegInputDeviceHardwareRepository repository = context.getBean(FfmpegInputDeviceHardwareRepository.class);
        repository.fireFfmpeg(ffmpegPath.toString(), input, source, output, -1);*/
  }

  @SneakyThrows
  private void applyParentBlocks(FfmpegBuilder ffmpegBuilder, WorkspaceBlock parent) {
    if (parent == null || !parent.getBlockId().startsWith("ffmpeg_args")) {
      return;
    }
    applyParentBlocks(ffmpegBuilder, parent.getParent());
    Scratch3FFmpegBlocks.FFmpegApplyHandler.valueOf(parent.getOpcode()).applyFn.accept(parent, ffmpegBuilder);
  }

  @AllArgsConstructor
  private enum FFmpegApplyHandler {
    argsInput((workspaceBlock, builder) ->
      addToListSafe(builder.inputArgs, workspaceBlock.getInputString(VALUE).trim())),
    argsOutput((workspaceBlock, builder) ->
      addToListSafe(builder.outputArgs, workspaceBlock.getInputString(VALUE).trim()));

    private final ThrowingBiConsumer<WorkspaceBlock, FfmpegBuilder, Exception> applyFn;
  }

  private static class FfmpegBuilder {

    private final List<String> inputArgs = new ArrayList<>();
    private final List<String> outputArgs = new ArrayList<>();
  }
}
