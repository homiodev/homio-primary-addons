package org.touchhome.bundle.camera.handler.impl;

import de.onvif.soap.OnvifDeviceState;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.*;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.api.util.Curl;
import org.touchhome.bundle.camera.CameraCoordinator;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.handler.BaseCameraStreamServerHandler;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.onvif.BrandCameraHasAudioAlarm;
import org.touchhome.bundle.camera.onvif.BrandCameraHasMotionAlarm;
import org.touchhome.bundle.camera.onvif.impl.InstarBrandHandler;
import org.touchhome.bundle.camera.onvif.util.ChannelTracking;
import org.touchhome.bundle.camera.onvif.util.MyNettyAuthHandler;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionConditional;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;
import org.touchhome.bundle.camera.ui.UICameraDimmerButton;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.touchhome.bundle.api.util.TouchHomeUtils.MACHINE_IP_ADDRESS;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@Log4j2
public class OnvifCameraHandler extends BaseFFmpegCameraHandler<OnvifCameraEntity> {

    // ChannelGroup is thread safe
    public final ChannelGroup mjpegChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // private GroupTracker groupTracker;
    private final ChannelGroup snapshotMjpegChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final ChannelGroup autoSnapshotMjpegChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final ChannelGroup openChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public Map<String, ChannelTracking> channelTrackingMap = new ConcurrentHashMap<>();
    public boolean useDigestAuth = false;
    @Setter
    @Getter
    private String snapshotUri;
    @Setter
    @Getter
    public String mjpegUri = "";
    public boolean audioAlarmUpdateSnapshot = false;
    public boolean snapshotPolling = false;
    private boolean streamingAutoFps = false;
    private EntityContextBGP.ThreadContext<Void> snapshotJob = null;
    private Bootstrap mainBootstrap;
    private EventLoopGroup mainEventLoopGroup = new NioEventLoopGroup();
    private FullHttpRequest putRequestWithBody = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod("PUT"), "");
    private FullHttpRequest postRequestWithBody = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod("POST"), "");
    // basicAuth MUST remain private as it holds the cameraEntity.getPassword()
    private String basicAuth = "";
    private Object firstStreamedMsg = new Object();
    private boolean motionAlarmUpdateSnapshot = false;
    private boolean firstAudioAlarm = false;
    private boolean firstMotionAlarm = false;
    private boolean streamingSnapshotMjpeg = false;
    private boolean updateAutoFps = false;
    @Getter
    private OnvifDeviceState onvifDeviceState;
    private EntityContextBGP.ThreadContext<Void> pullConfigSchedule;

    public OnvifCameraHandler(OnvifCameraEntity cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
        this.onvifDeviceState = new OnvifDeviceState(cameraEntity.getIp(), cameraEntity.getOnvifPort(),
                cameraEntity.getServerPort(),
                cameraEntity.getUser(), cameraEntity.getPassword().asString());
        this.onvifDeviceState.setUnreachableHandler(message -> this.disposeAndSetStatus(Status.OFFLINE, message));

        onvifDeviceState.getEventDevices().subscribe("RuleEngine/CellMotionDetector/Motion",
                (dataName, dataValue) -> motionDetected(dataValue.equals("true"), CHANNEL_CELL_MOTION_ALARM));
        onvifDeviceState.getEventDevices().subscribe("VideoSource/MotionAlarm",
                (dataName, dataValue) -> motionDetected(dataValue.equals("true"), CHANNEL_MOTION_ALARM));
        onvifDeviceState.getEventDevices().subscribe("AudioAnalytics/Audio/DetectedSound",
                (dataName, dataValue) -> audioDetected(dataValue.equals("true")));
        onvifDeviceState.getEventDevices().subscribe("RuleEngine/FieldDetector/ObjectsInside",
                (dataName, dataValue) -> motionDetected(dataValue.equals("true"), CHANNEL_FIELD_DETECTION_ALARM));
        onvifDeviceState.getEventDevices().subscribe("RuleEngine/LineDetector/Crossed",
                (dataName, dataValue) -> motionDetected(dataName.equals("ObjectId"), CHANNEL_LINE_CROSSING_ALARM));
        onvifDeviceState.getEventDevices().subscribe("RuleEngine/TamperDetector/Tamper",
                (dataName, dataValue) -> setAttribute(CHANNEL_TAMPER_ALARM, OnOffType.valueOf(dataValue.equals("true"))));
        onvifDeviceState.getEventDevices().subscribe("Device/HardwareFailure/StorageFailure",
                (dataName, dataValue) -> setAttribute(CHANNEL_STORAGE_ALARM, OnOffType.valueOf(dataValue.equals("true"))));

        onvifDeviceState.getEventDevices().subscribe(
                "VideoSource/ImageTooDark/AnalyticsService",
                "VideoSource/ImageTooDark/ImagingService",
                "VideoSource/ImageTooDark/RecordingService",
                (dataName, dataValue) -> setAttribute(CHANNEL_TOO_DARK_ALARM, OnOffType.valueOf(dataValue.equals("true"))));

        onvifDeviceState.getEventDevices().subscribe(
                "VideoSource/GlobalSceneChange/AnalyticsService",
                "VideoSource/GlobalSceneChange/ImagingService",
                "VideoSource/GlobalSceneChange/RecordingService",
                (dataName, dataValue) -> setAttribute(CHANNEL_SCENE_CHANGE_ALARM, OnOffType.valueOf(dataValue.equals("true"))));

        onvifDeviceState.getEventDevices().subscribe(
                "VideoSource/ImageTooBright/AnalyticsService",
                "VideoSource/ImageTooBright/ImagingService",
                "VideoSource/ImageTooBright/RecordingService",
                (dataName, dataValue) -> setAttribute(CHANNEL_TOO_BRIGHT_ALARM, OnOffType.valueOf(dataValue.equals("true"))));

        onvifDeviceState.getEventDevices().subscribe(
                "VideoSource/ImageTooBlurry/AnalyticsService",
                "VideoSource/ImageTooBlurry/ImagingService",
                "VideoSource/ImageTooBlurry/RecordingService",
                (dataName, dataValue) -> setAttribute(CHANNEL_TOO_BLURRY_ALARM, OnOffType.valueOf(dataValue.equals("true"))));
    }

    // false clears the stored user/pass hash, true creates the hash
    public boolean setBasicAuth(boolean useBasic) {
        if (!useBasic) {
            log.debug("Clearing out the stored BASIC auth now.");
            basicAuth = "";
            return false;
        } else if (!basicAuth.isEmpty()) {
            // due to camera may have been sent multiple requests before the auth was set, this may trigger falsely.
            log.warn("Camera is reporting your username and/or password is wrong.");
            return false;
        }
        if (!cameraEntity.getUser().isEmpty() && !cameraEntity.getPassword().isEmpty()) {
            String authString = cameraEntity.getUser() + ":" + cameraEntity.getPassword().asString();
            ByteBuf byteBuf = null;
            try {
                byteBuf = Base64.encode(Unpooled.wrappedBuffer(authString.getBytes(CharsetUtil.UTF_8)));
                basicAuth = byteBuf.getCharSequence(0, byteBuf.capacity(), CharsetUtil.UTF_8).toString();
            } finally {
                if (byteBuf != null) {
                    byteBuf.release();
                }
            }
            return true;
        } else {
            disposeAndSetStatus(Status.ERROR, "Camera is asking for Basic Auth when you have not provided a username and/or password.");
        }
        return false;
    }

    private String getCorrectUrlFormat(String longUrl) {
        String temp = longUrl;
        URL url;

        if (longUrl.isEmpty() || longUrl.equals("ffmpeg")) {
            return longUrl;
        }

        try {
            url = new URL(longUrl);
            int port = url.getPort();
            if (port == -1) {
                if (url.getQuery() == null) {
                    temp = url.getPath();
                } else {
                    temp = url.getPath() + "?" + url.getQuery();
                }
            } else {
                if (url.getQuery() == null) {
                    temp = ":" + url.getPort() + url.getPath();
                } else {
                    temp = ":" + url.getPort() + url.getPath() + "?" + url.getQuery();
                }
            }
        } catch (MalformedURLException e) {
            disposeAndSetStatus(Status.ERROR, "A non valid URL has been given to the binding, check they work in a browser.");
        }
        return temp;
    }

    public void sendHttpPUT(String httpRequestURL, FullHttpRequest request) {
        putRequestWithBody = request; // use Global so the authhandler can use it when resent with DIGEST.
        sendHttpRequest("PUT", httpRequestURL, null);
    }

    public void sendHttpGET(String httpRequestURL) {
        sendHttpRequest("GET", httpRequestURL, null);
    }

    public void sendHttpPOST(String httpRequestURL, FullHttpRequest request) {
        postRequestWithBody = request; // use Global so the authhandler can use it when resent with DIGEST.
        sendHttpRequest("POST", httpRequestURL, null);
    }

    public int getPortFromShortenedUrl(String httpRequestURL) {
        if (httpRequestURL.startsWith(":")) {
            int end = httpRequestURL.indexOf("/");
            return Integer.parseInt(httpRequestURL.substring(1, end));
        }
        return cameraEntity.getRestPort();
    }

    public String getTinyUrl(String httpRequestURL) {
        if (httpRequestURL.startsWith(":")) {
            int beginIndex = httpRequestURL.indexOf("/");
            return httpRequestURL.substring(beginIndex);
        }
        return httpRequestURL;
    }

    // Always use this as sendHttpGET(GET/POST/PUT/DELETE, "/foo/bar",null)//
    // The authHandler will generate a digest string and re-send using this same function when needed.
    @SuppressWarnings("null")
    public void sendHttpRequest(String httpMethod, String httpRequestURLFull, String digestString) {
        int port = getPortFromShortenedUrl(httpRequestURLFull);
        String httpRequestURL = getTinyUrl(httpRequestURLFull);

        if (mainBootstrap == null) {
            mainBootstrap = new Bootstrap();
            mainBootstrap.group(mainEventLoopGroup);
            mainBootstrap.channel(NioSocketChannel.class);
            mainBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            mainBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4500);
            mainBootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 8);
            mainBootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 1024);
            mainBootstrap.option(ChannelOption.TCP_NODELAY, true);
            mainBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @SneakyThrows
                @Override
                public void initChannel(SocketChannel socketChannel) {
                    // HIK Alarm stream needs > 9sec idle to stop stream closing
                    socketChannel.pipeline().addLast(new IdleStateHandler(18, 0, 0));
                    socketChannel.pipeline().addLast(new HttpClientCodec());
                    socketChannel.pipeline().addLast(AUTH_HANDLER,
                            new MyNettyAuthHandler(cameraEntity.getUser(), cameraEntity.getPassword().asString(), OnvifCameraHandler.this));
                    socketChannel.pipeline().addLast(COMMON_HANDLER, new CommonCameraHandler());

                    String handlerName = CameraCoordinator.cameraBrands.get(cameraEntity.getCameraType()).getHandlerName();
                    socketChannel.pipeline().addLast(handlerName, cameraEntity.getBaseOnvifCameraBrandHandler());
                }
            });
        }

        FullHttpRequest request;
        if ("GET".equals(httpMethod) || (useDigestAuth && digestString == null)) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod(httpMethod), httpRequestURL);
            request.headers().set(HttpHeaderNames.HOST, cameraEntity.getIp() + ":" + port);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else if ("PUT".equals(httpMethod)) {
            request = putRequestWithBody;
        } else {
            request = postRequestWithBody;
        }

        if (!basicAuth.isEmpty()) {
            if (useDigestAuth) {
                log.warn("Camera at IP:{} had both Basic and Digest set to be used", cameraEntity.getIp());
                setBasicAuth(false);
            } else {
                request.headers().set(HttpHeaderNames.AUTHORIZATION, "Basic " + basicAuth);
            }
        }

        if (useDigestAuth) {
            if (digestString != null) {
                request.headers().set(HttpHeaderNames.AUTHORIZATION, "Digest " + digestString);
            }
        }

        mainBootstrap.connect(new InetSocketAddress(cameraEntity.getIp(), port))
                .addListener((ChannelFutureListener) future -> {
                    if (future == null) {
                        return;
                    }
                    if (future.isDone() && future.isSuccess()) {
                        Channel ch = future.channel();
                        openChannels.add(ch);
                        log.debug("Sending camera: {}: http://{}:{}{}", httpMethod, cameraEntity.getIp(), port,
                                httpRequestURL);
                        channelTrackingMap.put(httpRequestURL, new ChannelTracking(ch, httpRequestURL));

                        CommonCameraHandler commonHandler = (CommonCameraHandler) ch.pipeline().get(COMMON_HANDLER);
                        commonHandler.setURL(httpRequestURLFull);
                        MyNettyAuthHandler authHandler = (MyNettyAuthHandler) ch.pipeline().get(AUTH_HANDLER);
                        authHandler.setURL(httpMethod, httpRequestURL);

                        cameraEntity.getBaseOnvifCameraBrandHandler().handleSetURL(ch.pipeline(), httpRequestURL);
                        ch.writeAndFlush(request);
                    } else { // an error occurred
                        restart("Connection Timeout: Check your IP and PORT are correct and the camera can be reached.", null, false);
                    }
                });
    }

    @Override
    protected void streamServerStarted() {
        if (cameraEntity.getCameraType().equals("instar")) {
            log.info("Setting up the Alarm Server settings in the camera now");
            sendHttpGET("/param.cgi?cmd=setmdalarm&-aname=server2&-switch=on&-interval=1&cmd=setalarmserverattr&-as_index=3&-as_server="
                    + MACHINE_IP_ADDRESS + "&-as_port=" + serverPort + "&-as_path=/instar&-as_queryattr1=&-as_queryval1=&-as_queryattr2=&-as_queryval2=&-as_queryattr3=&-as_queryval3=&-as_activequery=1&-as_auth=0&-as_query1=0&-as_query2=0&-as_query3=0");
        }
    }

    public void setupSnapshotStreaming(boolean stream, ChannelHandlerContext ctx, boolean auto) {
        if (stream) {
            sendMjpegFirstPacket(ctx);
            if (auto) {
                autoSnapshotMjpegChannelGroup.add(ctx.channel());
                lockCurrentSnapshot.lock();
                try {
                    sendMjpegFrame(getLatestSnapshot(), autoSnapshotMjpegChannelGroup);
                    // iOS uses a FIFO? and needs two frames to display a pic
                    sendMjpegFrame(getLatestSnapshot(), autoSnapshotMjpegChannelGroup);
                } finally {
                    lockCurrentSnapshot.unlock();
                }
                streamingAutoFps = true;
            } else {
                snapshotMjpegChannelGroup.add(ctx.channel());
                lockCurrentSnapshot.lock();
                try {
                    sendMjpegFrame(getLatestSnapshot(), snapshotMjpegChannelGroup);
                } finally {
                    lockCurrentSnapshot.unlock();
                }
                streamingSnapshotMjpeg = true;
                startSnapshotPolling();
            }
        } else {
            snapshotMjpegChannelGroup.remove(ctx.channel());
            autoSnapshotMjpegChannelGroup.remove(ctx.channel());
            if (streamingSnapshotMjpeg && snapshotMjpegChannelGroup.isEmpty()) {
                streamingSnapshotMjpeg = false;
                stopSnapshotPolling();
                log.info("All snapshots.mjpeg streams have stopped.");
            } else if (streamingAutoFps && autoSnapshotMjpegChannelGroup.isEmpty()) {
                streamingAutoFps = false;
                stopSnapshotPolling();
                log.info("All autofps.mjpeg streams have stopped.");
            }
        }
    }

    // If start is true the CTX is added to the list to stream video to, false stops
    // the stream.
    public void setupMjpegStreaming(boolean start, ChannelHandlerContext ctx) {
        if (start) {
            if (mjpegChannelGroup.isEmpty()) {// first stream being requested.
                mjpegChannelGroup.add(ctx.channel());
                if (mjpegUri.isEmpty() || mjpegUri.equals("ffmpeg")) {
                    sendMjpegFirstPacket(ctx);
                    startMJPEGRecord();
                } else {
                    try {
                        // fix Dahua reboots when refreshing a mjpeg stream.
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                    sendHttpGET(mjpegUri);
                }
            } else if (ffmpegMjpeg != null) {// not first stream and we will use ffmpeg
                sendMjpegFirstPacket(ctx);
                mjpegChannelGroup.add(ctx.channel());
            } else {// not first stream and camera supplies the mjpeg source.
                ctx.channel().writeAndFlush(firstStreamedMsg);
                mjpegChannelGroup.add(ctx.channel());
            }
        } else {
            mjpegChannelGroup.remove(ctx.channel());
            if (mjpegChannelGroup.isEmpty()) {
                log.info("All ipcamera.mjpeg streams have stopped.");
                if (mjpegUri.equals("ffmpeg") || mjpegUri.isEmpty()) {
                    Ffmpeg localMjpeg = ffmpegMjpeg;
                    if (localMjpeg != null) {
                        localMjpeg.stopConverting();
                    }
                } else {
                    closeChannel(getTinyUrl(mjpegUri));
                }
            }
        }
    }

    void closeChannel(String url) {
        ChannelTracking channelTracking = channelTrackingMap.get(url);
        if (channelTracking != null) {
            if (channelTracking.getChannel().isOpen()) {
                channelTracking.getChannel().close();
            }
        }
    }

    /**
     * This method should never run under normal use, if there is a bug in a camera or binding it may be possible to
     * open large amounts of channels. This may help to keep it under control and WARN the user every 8 seconds this is
     * still occurring.
     */
    void cleanChannels() {
        for (Channel channel : openChannels) {
            boolean oldChannel = true;
            for (ChannelTracking channelTracking : channelTrackingMap.values()) {
                if (!channelTracking.getChannel().isOpen() && channelTracking.getReply().isEmpty()) {
                    channelTrackingMap.remove(channelTracking.getRequestUrl());
                }
                if (channelTracking.getChannel() == channel) {
                    log.trace("Open channel to camera is used for URL:{}", channelTracking.getRequestUrl());
                    oldChannel = false;
                }
            }
            if (oldChannel) {
                channel.close();
            }
        }
    }

    public void storeHttpReply(String url, String content) {
        ChannelTracking channelTracking = channelTrackingMap.get(url);
        if (channelTracking != null) {
            channelTracking.setReply(content);
        }
    }

    // sends direct to ctx so can be either snapshots.mjpeg or normal mjpeg stream
    public void sendMjpegFirstPacket(ChannelHandlerContext ctx) {
        final String boundary = "thisMjpegStream";
        String contentType = "multipart/x-mixed-replace; boundary=" + boundary;
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        ctx.channel().writeAndFlush(response);
    }

    public void sendMjpegFrame(byte[] jpg, ChannelGroup channelGroup) {
        final String boundary = "thisMjpegStream";
        ByteBuf imageByteBuf = Unpooled.copiedBuffer(jpg);
        int length = imageByteBuf.readableBytes();
        String header = "--" + boundary + "\r\n" + "content-type: image/jpeg" + "\r\n" + "content-length: " + length
                + "\r\n\r\n";
        ByteBuf headerBbuf = Unpooled.copiedBuffer(header, 0, header.length(), StandardCharsets.UTF_8);
        ByteBuf footerBbuf = Unpooled.copiedBuffer("\r\n", 0, 2, StandardCharsets.UTF_8);
        streamToGroup(headerBbuf, channelGroup, false);
        streamToGroup(imageByteBuf, channelGroup, false);
        streamToGroup(footerBbuf, channelGroup, true);
    }

    public void streamToGroup(Object msg, ChannelGroup channelGroup, boolean flush) {
        channelGroup.write(msg);
        if (flush) {
            channelGroup.flush();
        }
    }

    @Override
    public String getFFMPEGInputOptions() {
        String inputOptions = cameraEntity.getFfmpegInputOptions();
        if (rtspUri.isEmpty()) {
            log.warn("The camera tried to use a FFmpeg feature when no valid input for FFmpeg is provided.");
            return null;
        }
        if (rtspUri.toLowerCase().contains("rtsp")) {
            if (inputOptions.isEmpty()) {
                inputOptions = "-rtsp_transport tcp";
            }
        }
        if (!inputOptions.contains("asd")) {
            inputOptions += " -stimeout " + TimeUnit.SECONDS.toMicros(10);
        }
        return inputOptions;
    }

    @Override
    public void motionDetected(boolean on, String key) {
        super.motionDetected(on, key);
        if (on) {
            if (streamingAutoFps) {
                startSnapshotPolling();
            }
            if (cameraEntity.getUpdateImageWhen().contains("2")) {
                if (!firstMotionAlarm) {
                    if (!snapshotUri.isEmpty()) {
                        sendHttpGET(snapshotUri);
                    }
                    firstMotionAlarm = true;// reset back to false when the jpg arrives.
                }
            } else if (cameraEntity.getUpdateImageWhen().contains("4")) { // During Motion Alarms
                if (!snapshotPolling) {
                    startSnapshotPolling();
                }
                firstMotionAlarm = true;
                motionAlarmUpdateSnapshot = true;
            }
        } else {
            firstMotionAlarm = false;
            motionAlarmUpdateSnapshot = false;
            if (streamingAutoFps) {
                stopSnapshotPolling();
            } else if (cameraEntity.getUpdateImageWhen().contains("4")) { // During Motion Alarms
                stopSnapshotPolling();
            }
        }
    }

    @Override
    protected BaseCameraStreamServerHandler createCameraStreamServerHandler() {
        return new OnvifCameraStreamHandler(this);
    }

    @Override
    public void audioDetected(boolean on) {
        super.audioDetected(on);
        if (on) {
            if (cameraEntity.getUpdateImageWhen().contains("3")) {
                if (!firstAudioAlarm) {
                    if (!snapshotUri.isEmpty()) {
                        sendHttpGET(snapshotUri);
                    }
                    firstAudioAlarm = true;// reset back to false when the jpg arrives.
                }
            } else if (cameraEntity.getUpdateImageWhen().contains("5")) {// During audio alarms
                firstAudioAlarm = true;
                audioAlarmUpdateSnapshot = true;
            }
        } else {
            firstAudioAlarm = false;
            audioAlarmUpdateSnapshot = false;
        }
    }

    @Override
    public void processSnapshot(byte[] incomingSnapshot) {
        super.processSnapshot(incomingSnapshot);

        if (streamingSnapshotMjpeg) {
            sendMjpegFrame(incomingSnapshot, snapshotMjpegChannelGroup);
        }
        if (streamingAutoFps) {
            if (motionDetected) {
                sendMjpegFrame(incomingSnapshot, autoSnapshotMjpegChannelGroup);
            } else if (updateAutoFps) {
                // only happens every 8 seconds as some browsers need a frame that often to keep stream alive.
                sendMjpegFrame(incomingSnapshot, autoSnapshotMjpegChannelGroup);
                updateAutoFps = false;
            }
        }

        /*if (updateImageChannel) {
            setAttribute(CHANNEL_IMAGE, new RawType(incomingSnapshot, "image/jpeg"));
        } else */
        if (firstMotionAlarm || motionAlarmUpdateSnapshot) {
            setAttribute(CHANNEL_IMAGE, new RawType(incomingSnapshot, "image/jpeg"));
            firstMotionAlarm = motionAlarmUpdateSnapshot = false;
        } else if (firstAudioAlarm || audioAlarmUpdateSnapshot) {
            setAttribute(CHANNEL_IMAGE, new RawType(incomingSnapshot, "image/jpeg"));
            firstAudioAlarm = audioAlarmUpdateSnapshot = false;
        }
    }

    public String returnValueFromString(String rawString, String searchedString) {
        String result;
        int index = rawString.indexOf(searchedString);
        if (index != -1) // -1 means "not found"
        {
            result = rawString.substring(index + searchedString.length());
            index = result.indexOf("\r\n"); // find a carriage return to find the end of the value.
            if (index == -1) {
                return result; // Did not find a carriage return.
            } else {
                return result.substring(0, index);
            }
        }
        return ""; // Did not find the String we were searching for
    }

    @Override
    public void startSnapshot() {
        if (!isEmpty(snapshotUri)) {
            sendHttpGET(snapshotUri);// Allows this to change Image FPS on demand
        } else {
            super.startSnapshot();
        }
    }

    @Override
    public void testOnline() {
        getOnvifDeviceState().checkForErrors();
    }

    @UICameraActionGetter(CHANNEL_PAN)
    public DecimalType getPan() {
        return new DecimalType(Math.round(onvifDeviceState.getPtzDevices().getCurrentPanPercentage()));
    }

    @UICameraAction(name = CHANNEL_PAN, order = 3, icon = "fas fa-expand-arrows-alt", type = UICameraAction.ActionType.Dimmer)
    @UICameraActionConditional(SupportPTZ.class)
    @UICameraDimmerButton(name = "LEFT", icon = "fas fa-caret-left")
    @UICameraDimmerButton(name = "OFF", icon = "fas fa-power-off")
    @UICameraDimmerButton(name = "RIGHT", icon = "fas fa-caret-right")
    public void setPan(String command) {
        if ("LEFT".equals(command) || "RIGHT".equals(command)) {
            if ("LEFT".equals(command)) {
                onvifDeviceState.getPtzDevices().moveLeft(cameraEntity.isPtzContinuous());
            } else {
                onvifDeviceState.getPtzDevices().moveRight(cameraEntity.isPtzContinuous());
            }
        } else if ("OFF".equals(command)) {
            onvifDeviceState.getPtzDevices().stopMove();
        } else {
            onvifDeviceState.getPtzDevices().setAbsolutePan(Float.parseFloat(command));
        }
    }

    @UICameraActionGetter(CHANNEL_TILT)
    public DecimalType getTilt() {
        return new DecimalType(Math.round(onvifDeviceState.getPtzDevices().getCurrentTiltPercentage()));
    }

    @UICameraAction(name = CHANNEL_TILT, order = 5, icon = "fas fa-sort", type = UICameraAction.ActionType.Dimmer)
    @UICameraActionConditional(SupportPTZ.class)
    @UICameraDimmerButton(name = "UP", icon = "fas fa-caret-up")
    @UICameraDimmerButton(name = "OFF", icon = "fas fa-power-off")
    @UICameraDimmerButton(name = "DOWN", icon = "fas fa-caret-down")
    public void setTilt(String command) {
        if ("UP".equals(command) || "DOWN".equals(command)) {
            if ("UP".equals(command)) {
                onvifDeviceState.getPtzDevices().moveUp(cameraEntity.isPtzContinuous());
            } else {
                onvifDeviceState.getPtzDevices().moveDown(cameraEntity.isPtzContinuous());
            }
        } else if ("OFF".equals(command)) {
            onvifDeviceState.getPtzDevices().stopMove();
        } else {
            onvifDeviceState.getPtzDevices().setAbsoluteTilt(Float.parseFloat(command));
        }
    }

    @UICameraActionGetter(CHANNEL_ZOOM)
    public DecimalType getZoom() {
        return new DecimalType(Math.round(onvifDeviceState.getPtzDevices().getCurrentZoomPercentage()));
    }

    @UICameraAction(name = CHANNEL_ZOOM, order = 7, icon = "fas fa-search-plus", type = UICameraAction.ActionType.Dimmer)
    @UICameraActionConditional(SupportPTZ.class)
    @UICameraDimmerButton(name = "IN", icon = "fas fa-search-plus")
    @UICameraDimmerButton(name = "OFF", icon = "fas fa-power-off")
    @UICameraDimmerButton(name = "OUT", icon = "fas fa-search-minus")
    public void setZoom(String command) {
        if ("IN".equals(command) || "OUT".equals(command)) {
            if ("IN".equals(command)) {
                onvifDeviceState.getPtzDevices().moveIn(cameraEntity.isPtzContinuous());
            } else {
                onvifDeviceState.getPtzDevices().moveOut(cameraEntity.isPtzContinuous());
            }
        } else if ("OFF".equals(command)) {
            onvifDeviceState.getPtzDevices().stopMove();
        }
        onvifDeviceState.getPtzDevices().setAbsoluteZoom(Float.valueOf(command));
    }

    @UICameraActionGetter(CHANNEL_GOTO_PRESET)
    public DecimalType getGotoPreset() {
        return new DecimalType(0);
    }

    @UICameraAction(name = CHANNEL_GOTO_PRESET, order = 30, icon = "fas fa-location-arrow", min = 1, max = 25, selectReplacer = "Preset %0  ")
    @UICameraActionConditional(SupportPTZ.class)
    public void gotoPreset(int preset) {
        onvifDeviceState.getPtzDevices().gotoPreset(preset);
    }

    protected void bringCameraOnline0() {
        //     groupTracker.onlineCameraMap.put(cameraEntity.getEntityID(), this);

        if (cameraEntity.getGifPreroll() > 0 || cameraEntity.getUpdateImageWhen().contains("1")) {
            snapshotPolling = true;
            snapshotJob = entityContext.bgp().schedule("snapshotJob", 1000, cameraEntity.getJpegPollTime(), TimeUnit.MILLISECONDS,
                    this::snapshotRunnable, true, true);
        }
//        startSnapshot();
     /*   if (!groupTracker.listOfGroupHandlers.isEmpty()) {
            for (IpCameraGroupHandler handle : groupTracker.listOfGroupHandlers) {
                handle.cameraOnline(cameraEntity);
            }
        }*/
    }

    @Override
    protected List<StatefulContextMenuAction> getAdditionalCameraActions() {
        return cameraEntity.getBaseOnvifCameraBrandHandler().getCameraActions();
    }

    @Override
    protected void pollingCameraConnection() {
        startSnapshot();
    }

    public boolean streamIsStopped(String url) {
        ChannelTracking channelTracking = channelTrackingMap.get(url);
        if (channelTracking != null) {
            return !channelTracking.getChannel().isActive(); // stream is running.
        }
        return true; // Stream stopped or never started.
    }

    void snapshotRunnable() {
        // Snapshot should be first to keep consistent time between shots
        sendHttpGET(snapshotUri);
        if (snapCount > 0) {
            if (--snapCount == 0) {
                startGifRecord();
            }
        }
    }

    private void stopSnapshotPolling() {
        if ((!streamingSnapshotMjpeg && cameraEntity.getGifPreroll() == 0
                && !cameraEntity.getUpdateImageWhen().contains("1")) ||
                cameraEntity.getUpdateImageWhen().contains("4")) { // only during Motion Alarms
            snapshotPolling = false;
            if (snapshotJob != null) {
                snapshotJob.cancel();
            }
        }
    }

    private void startSnapshotPolling() {
        if (snapshotPolling || !ffmpegSnapshotGeneration) {
            return; // Already polling or creating with FFmpeg from RTSP
        }
        if (streamingSnapshotMjpeg || streamingAutoFps || cameraEntity.getUpdateImageWhen().contains("4")) {
            snapshotPolling = true;
            snapshotJob = entityContext.bgp().schedule("snapshotJob", 200, cameraEntity.getJpegPollTime(), TimeUnit.MILLISECONDS,
                    this::snapshotRunnable, true, true);
        }
    }

    @Override
    protected void pollCameraRunnable() {
        fireFfmpeg(ffmpegHLS, Ffmpeg::stopProcessIfNoKeepAlive);

        // Snapshot should be first to keep consistent time between shots
        if (streamingAutoFps) {
            updateAutoFps = true;
            if (!snapshotPolling && !ffmpegSnapshotGeneration) {
                // Dont need to poll if creating from RTSP stream with FFmpeg or we are polling at full rate already.
                sendHttpGET(snapshotUri);
            }
        } else if (!isEmpty(snapshotUri) && !snapshotPolling) {// we need to check camera is still online.
            sendHttpGET(snapshotUri);
        }
        // what needs to be done every poll//
        cameraEntity.getBaseOnvifCameraBrandHandler().pollCameraRunnable(this);

        if (openChannels.size() > 18) {
            log.debug("There are {} open Channels being tracked.", openChannels.size());
            cleanChannels();
        }
    }

    @Override
    protected void initialize0() {
        if (!onvifDeviceState.isOnline()) {
            throw new RuntimeException("Unable connect to offline camera");
        }
        this.onvifDeviceState.initFully(cameraEntity);
        super.initialize0();

        setAttribute("PROFILES", new ObjectType(onvifDeviceState.getProfiles()));
        snapshotUri = getCorrectUrlFormat(cameraEntity.getSnapshotUrl());
        mjpegUri = getCorrectUrlFormat(cameraEntity.getMjpegUrl());

        cameraEntity.getBaseOnvifCameraBrandHandler().initialize(entityContext);

        pullConfigSchedule = entityContext.bgp().schedule("pull-configs-" + cameraEntity.getEntityID(), 30000,
                60, TimeUnit.SECONDS, () -> cameraEntity.getBaseOnvifCameraBrandHandler()
                        .runOncePerMinute(entityContext), false, false);

        // for poll times above 9 seconds don't display a warning about the Image channel.
        if (cameraEntity.getJpegPollTime() <= 9000 && cameraEntity.getUpdateImageWhen().contains("1")) {
            log.warn("The Image channel is set to update more often than 8 seconds. This is not recommended. The Image channel is best used only for higher poll times. See the readme file on how to display the cameras picture for best results or use a higher poll time.");
        }

        if (("ffmpeg".equals(snapshotUri) || isEmpty(snapshotUri)) && isEmpty(rtspUri)) {
            throw new RuntimeException("Camera unable to find valid Snapshot and/or RTSP URL.");
        }

        if (snapshotUri.equals("ffmpeg")) {
            log.warn("Camera <{}> has no snapshot url. Will use your CPU and FFmpeg to create snapshots from the cameras RTSP.", cameraEntity.getTitle());
            snapshotUri = "";
        }
        setAttribute("SNAPSHOT_URI", new StringType(snapshotUri));
    }

    @Override
    protected String createRtspUri() {
        return cameraEntity.getFfmpegInput().isEmpty() ? onvifDeviceState.getMediaDevices().getRTSPStreamUri() : cameraEntity.getFfmpegInput();
    }

    @Override
    protected void dispose0() {
        super.dispose0();
        snapshotPolling = false;
        onvifDeviceState.dispose();

        if (pullConfigSchedule != null) {
            pullConfigSchedule.cancel();
        }

        if (snapshotJob != null) {
            snapshotJob.cancel();
        }
        /*if (this.onvifPollCameraEach8Sec != null) {
            this.onvifPollCameraEach8Sec.cancel();
        }*/
//        groupTracker.onlineCameraMap.remove(cameraEntity.getEntityID());
        // inform all group handlers that this camera has gone offline
  /*      for (IpCameraGroupHandler handle : groupTracker.listOfGroupHandlers) {
            handle.cameraOffline(this);
        }*/
        basicAuth = ""; // clear out stored Password hash
        useDigestAuth = false;
        openChannels.close();
        channelTrackingMap.clear();
    }

    public static class SupportPTZ implements Predicate<Object> {

        @Override
        public boolean test(Object o) {
            return ((OnvifCameraHandler) o).onvifDeviceState.getPtzDevices().supportPTZ();
        }
    }

    // These methods handle the response from all camera brands, nothing specific to 1 brand.
    private class CommonCameraHandler extends ChannelDuplexHandler {
        private int bytesToReceive = 0;
        private int bytesAlreadyReceived = 0;
        private byte[] incomingJpeg = new byte[0];
        private String incomingMessage = "";
        private String contentType = "empty";
        private Object reply = new Object();
        private String requestUrl = "";
        private boolean closeConnection = true;
        private boolean isChunked = false;

        public void setURL(String url) {
            requestUrl = url;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg == null || ctx == null) {
                return;
            }
            try {
                if (msg instanceof HttpResponse) {
                    HttpResponse response = (HttpResponse) msg;
                    if (response.status().code() != 401) {
                        if (!response.headers().isEmpty()) {
                            for (String name : response.headers().names()) {
                                // Some cameras use first letter uppercase and others dont.
                                switch (name.toLowerCase()) { // Possible localization issues doing this
                                    case "content-type":
                                        contentType = response.headers().getAsString(name);
                                        break;
                                    case "content-length":
                                        bytesToReceive = Integer.parseInt(response.headers().getAsString(name));
                                        break;
                                    case "connection":
                                        if (response.headers().getAsString(name).contains("keep-alive")) {
                                            closeConnection = false;
                                        }
                                        break;
                                    case "transfer-encoding":
                                        if (response.headers().getAsString(name).contains("chunked")) {
                                            isChunked = true;
                                        }
                                        break;
                                }
                            }
                            if (contentType.contains("multipart")) {
                                closeConnection = false;
                                if (mjpegUri.equals(requestUrl)) {
                                    if (msg instanceof HttpMessage) {
                                        // very start of stream only
                                        ReferenceCountUtil.retain(msg, 1);
                                        firstStreamedMsg = msg;
                                        streamToGroup(firstStreamedMsg, mjpegChannelGroup, true);
                                    }
                                }
                            } else if (contentType.contains("image/jp")) {
                                if (bytesToReceive == 0) {
                                    bytesToReceive = 768000; // 0.768 Mbyte when no Content-Length is sent
                                    log.debug("Camera has no Content-Length header, we have to guess how much RAM.");
                                }
                                incomingJpeg = new byte[bytesToReceive];
                            }
                        }
                    }
                }
                if (msg instanceof HttpContent) {
                    if (mjpegUri.equals(requestUrl)) {
                        // multiple MJPEG stream packets come back as this.
                        ReferenceCountUtil.retain(msg, 1);
                        streamToGroup(msg, mjpegChannelGroup, true);
                    } else {
                        HttpContent content = (HttpContent) msg;
                        // Found some cameras use Content-Type: image/jpg instead of image/jpeg
                        if (contentType.contains("image/jp")) {
                            for (int i = 0; i < content.content().capacity(); i++) {
                                incomingJpeg[bytesAlreadyReceived++] = content.content().getByte(i);
                            }
                            if (content instanceof LastHttpContent) {
                                processSnapshot(incomingJpeg);
                                // testing next line and if works need to do a full cleanup of this function.
                                closeConnection = true;
                                if (closeConnection) {
                                    ctx.close();
                                } else {
                                    bytesToReceive = 0;
                                    bytesAlreadyReceived = 0;
                                }
                            }
                        } else { // incomingMessage that is not an IMAGE
                            if (incomingMessage.isEmpty()) {
                                incomingMessage = content.content().toString(CharsetUtil.UTF_8);
                            } else {
                                incomingMessage += content.content().toString(CharsetUtil.UTF_8);
                            }
                            bytesAlreadyReceived = incomingMessage.length();
                            if (content instanceof LastHttpContent) {
                                // If it is not an image send it on to the next handler//
                                if (bytesAlreadyReceived != 0) {
                                    reply = incomingMessage;
                                    super.channelRead(ctx, reply);
                                }
                            }
                            // Alarm Streams never have a LastHttpContent as they always stay open//
                            else if (contentType.contains("multipart")) {
                                if (bytesAlreadyReceived != 0) {
                                    reply = incomingMessage;
                                    incomingMessage = "";
                                    bytesToReceive = 0;
                                    bytesAlreadyReceived = 0;
                                    super.channelRead(ctx, reply);
                                }
                            }
                            // Foscam needs this as will other cameras with chunks//
                            if (isChunked && bytesAlreadyReceived != 0) {
                                log.debug("Reply is chunked.");
                                reply = incomingMessage;
                                super.channelRead(ctx, reply);
                            }
                        }
                    }
                } else { // msg is not HttpContent
                    // Foscam cameras need this
                    if (!contentType.contains("image/jp") && bytesAlreadyReceived != 0) {
                        reply = incomingMessage;
                        log.debug("Packet back from camera is {}", incomingMessage);
                        super.channelRead(ctx, reply);
                    }
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (cause == null || ctx == null) {
                return;
            }
            if (cause instanceof ArrayIndexOutOfBoundsException) {
                log.debug("Camera sent {} bytes when the content-length header was {}.", bytesAlreadyReceived,
                        bytesToReceive);
            } else {
                log.warn("!!!! Camera possibly closed the channel on the binding, cause reported is: {}",
                        cause.getMessage());
            }
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            if (ctx == null) {
                return;
            }
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                // If camera does not use the channel for X amount of time it will close.
                if (e.state() == IdleState.READER_IDLE) {
                    String urlToKeepOpen = cameraEntity.getBaseOnvifCameraBrandHandler().getUrlToKeepOpenForIdleStateEvent();
                    ChannelTracking channelTracking = channelTrackingMap.get(urlToKeepOpen);
                    if (channelTracking != null) {
                        if (channelTracking.getChannel() == ctx.channel()) {
                            return; // don't auto close this as it is for the alarms.
                        }
                    }
                    ctx.close();
                }
            }
        }
    }

    private class OnvifCameraStreamHandler extends BaseCameraStreamServerHandler<OnvifCameraHandler> {
        private boolean onvifEvent;
        private boolean handlingMjpeg = false; // used to remove ctx from group when handler is removed.
        private boolean handlingSnapshotStream = false; // used to remove ctx from group when handler is removed.

        public OnvifCameraStreamHandler(OnvifCameraHandler onvifCameraHandler) {
            super(onvifCameraHandler);
        }

        @Override
        protected void handleLastHttpContent(byte[] bytes) {
            if (onvifEvent) {
                onvifDeviceState.getEventDevices().fireEvent(new String(bytes, StandardCharsets.UTF_8));
            } else { // handles the snapshots that make up mjpeg from rtsp to ffmpeg conversions.
                if (bytes.length > 1000) {
                    sendMjpegFrame(bytes, mjpegChannelGroup);
                }
            }
        }

        @Override
        protected boolean handleHttpRequest(QueryStringDecoder queryStringDecoder, ChannelHandlerContext ctx) {
            switch (queryStringDecoder.path()) {
                case "/ipcamera.jpg":
                    if (!cameraHandler.snapshotPolling && !cameraHandler.snapshotUri.equals("")) {
                        cameraHandler.sendHttpGET(cameraHandler.snapshotUri);
                    }
                    if (cameraHandler.latestSnapshot.length == 1) {
                        log.warn("ipcamera.jpg was requested but there is no jpg in ram to send.");
                        return true;
                    }
                    return true;
                case "/snapshots.mjpeg":
                    handlingSnapshotStream = true;
                    cameraHandler.startSnapshotPolling();
                    cameraHandler.setupSnapshotStreaming(true, ctx, false);
                    return true;
                case "/ipcamera.mjpeg":
                    cameraHandler.setupMjpegStreaming(true, ctx);
                    handlingMjpeg = true;
                    return true;
                case "/autofps.mjpeg":
                    handlingSnapshotStream = true;
                    cameraHandler.setupSnapshotStreaming(true, ctx, true);
                    return true;
                case "/instar":
                    InstarBrandHandler instar = new InstarBrandHandler(cameraHandler);
                    instar.alarmTriggered(queryStringDecoder.uri());
                    ctx.close();
                    return true;
            }
            return false;
        }

        @Override
        protected boolean streamServerReceivedPostHandler(HttpRequest httpRequest) {
            if ("/OnvifEvent".equals(httpRequest.uri())) {
                onvifEvent = true;
                return true;
            }
            return false;
        }

        @Override
        protected void handlerChildRemoved(ChannelHandlerContext ctx) {
            if (handlingMjpeg) {
                cameraHandler.setupMjpegStreaming(false, ctx);
            } else if (handlingSnapshotStream) {
                handlingSnapshotStream = false;
                cameraHandler.setupSnapshotStreaming(false, ctx, false);
            }
        }
    }

    @Override
    protected void setAudioAlarmThreshold(int audioThreshold) {
        ((BrandCameraHasAudioAlarm) cameraEntity.getBaseOnvifCameraBrandHandler()).setAudioAlarmThreshold(audioThreshold);
    }

    @Override
    protected void setMotionAlarmThreshold(int motionThreshold) {
        ((BrandCameraHasMotionAlarm) cameraEntity.getBaseOnvifCameraBrandHandler()).setMotionAlarmThreshold(motionThreshold);
    }

    @Override
    protected boolean isAudioAlarmHandlesByCamera() {
        return cameraEntity.getBaseOnvifCameraBrandHandler() instanceof BrandCameraHasAudioAlarm;
    }

    @Override
    protected boolean isMotionAlarmHandlesByCamera() {
        return cameraEntity.getBaseOnvifCameraBrandHandler() instanceof BrandCameraHasMotionAlarm;
    }

    @Override
    public RawType recordImageSync(String profile) {
        if (isEmpty(snapshotUri)) {
            return super.recordImageSync(profile);
        }
        String snapshotUri = onvifDeviceState.getMediaDevices().getSnapshotUri(profile);
        return Curl.download(snapshotUri, cameraEntity.getUser(), cameraEntity.getPassword().asString());
    }
}
