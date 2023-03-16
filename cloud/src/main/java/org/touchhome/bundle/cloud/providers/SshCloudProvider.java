package org.touchhome.bundle.cloud.providers;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UI.Color;
import org.touchhome.bundle.api.ui.field.action.ActionInputParameter;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;
import org.touchhome.common.exception.NotFoundException;
import org.touchhome.common.util.CommonUtils;

@Log4j2
@Component
@RequiredArgsConstructor
public class SshCloudProvider implements CloudProvider {

    private final MachineHardwareRepository machineHardwareRepository;
    private final EntityContext entityContext;
    private Status status = Status.UNKNOWN;
    private String statusMessage = null;

    @Override
    public String getStatus() {
        int serviceStatus = machineHardwareRepository.getServiceStatus("touchhome-tunnel");
        return serviceStatus == 0 ? ServerConnectionStatus.CONNECTED.name() : ServerConnectionStatus.DISCONNECTED_WIDTH_ERRORS.name();
    }

    @Override
    public void start() {
        updateNotificationBlock();
        try {
            UserEntity user = entityContext.getUserRequire(false);
            String passphrase = user.getJsonData().optString("passphrase", null);
            if (passphrase == null) {
                throw new NotFoundException("Cloud not configured");
            }
            JSch j = new JSch();
            j.addIdentity(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome").toString(), passphrase.getBytes(StandardCharsets.UTF_8));
            String hostname = entityContext.getEnv("touchhome.cloud.hostname");
            if (hostname == null) {
                throw new IllegalArgumentException("Unable to find cloud hostname in configuration");
            }
            Session session = j.getSession(user.getName(), hostname, 22);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            /*JSch j = new JSch();
            Session session = null;
            j.addIdentity("/home/reporting/.ssh/id_rsa");
            j.setKnownHosts("/home/reporting/.ssh/known_hosts");
            session = j.getSession("reporting", "app", 2222);
            session.connect();
            session.setPortForwardingR("touchhome.org", 30003, "localhost", 9119);*/
            /*try (SshClient ssh = new SshClient("localhost", 22, "username", "password".toCharArray())) {
             *//**
             * First we must allow forwarding. Without this no forwarding is possible. This
             * will allow us to forward from localhost and accept remote forwarding from the
             * remote server.
             *//*
                ssh.getContext().getForwardingPolicy().allowForwarding();

                *//**
             * A local forward allows the ssh client user to connect to a resource
             * on the remote network
             *//*
                ssh.startLocalForwarding("127.0.0.1", 9119, "www.touchhome.org", 2222);

                *//**
             * A remote forward allows a user to connect from the remote computer to
             * a resource on the client's network
             *//*
                //  ssh.startRemoteForwarding("127.0.0.1", 8080, "service.local", 80);

                *//**
             * If we want to allow other local computers to connect to our forwarding we can
             * allow gateway forwarding. This allows a local forwarding to be started on a
             * wildcard or IP address of the client that can accept connections from external
             * computers. With this enabled, we have to start the forwarding so that we are
             * listening on a publicly accessible interface of the client.
             *//*

                //   ssh.getContext().getForwardingPolicy().allowGatewayForwarding();

                *//**
             * We we start a local forwarding that is accessible by any IP on the clients
             * network. This is called "Gateway Forwarding"
             *//*
                //      ssh.startLocalForwarding("::", 9443, "www.jadaptive.com", 443);

                *//**
             * Wait for the connection to be disconnected.
             *//*
                status = Status.ONLINE;
                updateNotificationBlock();
                ssh.getConnection().getDisconnectFuture().waitForever();
                updateNotificationBlock();
            }*/
        } catch (Exception ex) {
            status = Status.ERROR;
            statusMessage = CommonUtils.getErrorMessage(ex);
            updateNotificationBlock();
        }
    }

    @Override
    public void stop() {

    }

   /* @Override
    public void assembleBellNotifications(BellNotificationBuilder bellNotificationBuilder) {
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome"))) {
            bellNotificationBuilder.danger("private-key", "Cloud", "Private Key not found");
        }
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome.pub"))) {
            bellNotificationBuilder.danger("public-key", "Cloud", "Public Key not found");
        }
        int serviceStatus = machineHardwareRepository.getServiceStatus("touchhome-tunnel");
        if (serviceStatus == 0) {
            bellNotificationBuilder.info("cloud-status", "Cloud", "Connected");
        } else {
            bellNotificationBuilder.warn("cloud-status", "Cloud", "Connection status not active " + serviceStatus);
        }
    }*/

    public void updateNotificationBlock() {
        // login if no private key found
        boolean hasPrivateKey = false;
        entityContext.ui().addNotificationBlock("cloud", "Cloud", "fas fa-cloud", "#D1D1D1", builder -> {
            builder.setStatus(status);
            if (!status.isOnline()) {
                builder.addInfo(statusMessage, Color.RED, "fas fa-exclamation", null);
            }

            if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome"))) {
                builder.addButtonInfo("private-key", Color.RED, "fas fa-key", Color.RED,
                    "fas fa-right-to-bracket", "Sync", (entityContext, params) -> {
                        entityContext.ui().sendDialogRequest("", "", (responseType, pressedButton, parameters) -> {
                            RestTemplate restTemplate = new RestTemplate();
                            LoginBody loginBody = new LoginBody(
                                parameters.get("email").asText(),
                                parameters.get("pwd").asText(),
                                parameters.get("passphrase").asText(),
                                false);
                            String url = entityContext.getEnv("touchhome.cloud.login_url");
                            if (url == null) {
                                throw new IllegalArgumentException("Unable to find cloud login url in configuration");
                            }
                            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, loginBody, LoginResponse.class);
                            LoginResponse loginResponse = response.getBody();
                            if (response.getStatusCode() == HttpStatus.OK && loginResponse != null) {
                                TouchHomeUtils.writeToFile(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome"),
                                    loginResponse.privateKey, false);
                                UserEntity user = entityContext.getUserRequire(false);
                                user.getJsonData().put("passphrase", loginBody.passphrase);
                                entityContext.save(user);
                                this.start();
                            } else {
                                log.error("Wrong status response from cloud server: {}", response.getStatusCode());
                            }
                        }, dialogModel -> {
                            List<ActionInputParameter> inputs = new ArrayList<>();
                            inputs.add(ActionInputParameter.text("email", ""));
                            inputs.add(ActionInputParameter.text("pwd", ""));
                            inputs.add(ActionInputParameter.text("passphrase", ""));
                            dialogModel.submitButton("Login", button -> {
                            }).group("General", inputs);
                        });
                        return null;
                    });
            }
        });
    }

    @Getter
    @RequiredArgsConstructor
    private static class LoginBody {

        private final String user;
        private final String password;
        private final String passphrase;
        private final boolean recreate;
    }

    @Getter
    @RequiredArgsConstructor
    private static class LoginResponse {

        private byte[] privateKey;
    }
}
