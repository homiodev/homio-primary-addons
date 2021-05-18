package org.touchhome.bundle.mail;

import com.pivovarit.function.ThrowingBiConsumer;
import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.ThrowingPredicate;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
@Component
public class Scratch3MailBlocks extends Scratch3ExtensionBlocks {

    private final MenuBlock.ServerMenuBlock mailMenu;
    private final Scratch3Block sendMailCommand;
    private final Scratch3Block attachFileCommand;
    private final Scratch3Block whenMailFromUserReceived;

    private final Map<String, POPHandler> popHandlers = new ConcurrentHashMap<>();
    private final Scratch3Block totalMailsReporter;
    private final Scratch3Block unreadMailsReporter;
    private final Scratch3Block whenMailSubjectHasValueReceived;

    public Scratch3MailBlocks(EntityContext entityContext, MailEntryPoint mailEntryPoint) {
        super("#8F4D77", entityContext, mailEntryPoint, null);
        setParent("communication");

        // Menu
        this.mailMenu = MenuBlock.ofServerItems("mailEntity", MailEntity.class);

        // Hats
        this.whenMailFromUserReceived = withMail(Scratch3Block.ofHandler(20, "when_get_from",
                BlockType.hat, "When got mail from [FROM] of [MAIL]", this::whenGotMailFromUserHat));
        this.whenMailFromUserReceived.addArgument("FROM", "receiver@mail.com");

        this.whenMailSubjectHasValueReceived = withMail(Scratch3Block.ofHandler(20, "when_has_subject",
                BlockType.hat, "When got mail with subject [SUBJECT] of [MAIL]", this::whenGotMailHasSubjectHat));
        this.whenMailSubjectHasValueReceived.addArgument("SUBJECT", "subject");
        this.whenMailSubjectHasValueReceived.appendSpace();

        // reporters
        this.totalMailsReporter = withMail(Scratch3Block.ofReporter(40, "total_mails",
                "Get total mails of [MAIL] in folder [FOLDER]", this::getTotalMailsReporter));
        this.totalMailsReporter.addArgument("FOLDER", "INBOX");

        this.unreadMailsReporter = withMail(Scratch3Block.ofReporter(50, "unread_mails",
                "Get unread mails of [MAIL] in folder [FOLDER]", this::getUnreadMailsReporter));
        this.unreadMailsReporter.addArgument("FOLDER", "INBOX");
        this.unreadMailsReporter.appendSpace();

        // commands
        this.sendMailCommand = withMail(Scratch3Block.ofHandler(100, "send_mail",
                BlockType.command, "Send mail [TITLE] to [RECIPIENTS] of [MAIL] with body [BODY]", this::sendMailCommand));
        this.sendMailCommand.addArgument("TITLE", "title");
        this.sendMailCommand.addArgument("RECIPIENTS", "receiver@mail.com");
        this.sendMailCommand.addArgument("BODY", "<b>body</b>");

        this.attachFileCommand = Scratch3Block.ofHandler(130, MailApplyHandler.update_add_file.name(), BlockType.command,
                "Attach file[VALUE]", this::skipExpression);
        this.attachFileCommand.addArgument(VALUE, "file");
    }

    private Object getUnreadMailsReporter(WorkspaceBlock workspaceBlock) {
        MailEntity mailEntity = getMailEntity(workspaceBlock);
        return connectToMailServerAndHandle(mailEntity, store -> {
            try (Folder mailbox = store.getFolder(StringUtils.defaultString(workspaceBlock.getInputString("FOLDER"),
                    store.getDefaultFolder().getName()))) {
                mailbox.open(Folder.READ_ONLY);
                return mailbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false)).length;
            }
        }, -1);
    }

    private int getTotalMailsReporter(WorkspaceBlock workspaceBlock) {
        MailEntity mailEntity = getMailEntity(workspaceBlock);
        return connectToMailServerAndHandle(mailEntity, store -> {
            try (Folder mailbox = store.getFolder(StringUtils.defaultString(workspaceBlock.getInputString("FOLDER"),
                    store.getDefaultFolder().getName()))) {
                mailbox.open(Folder.READ_ONLY);
                return mailbox.getMessageCount();
            }
        }, -1);
    }

    private void whenGotMailHasSubjectHat(WorkspaceBlock workspaceBlock) {
        String subject = workspaceBlock.getInputStringRequired("SUBJECT");
        handleNextWhenGotNewMessages(workspaceBlock, message -> StringUtils.defaultString(message.getSubject(), "").contains(subject));
    }

    private void whenGotMailFromUserHat(WorkspaceBlock workspaceBlock) {
        String expectedFrom = workspaceBlock.getInputStringRequired("FROM");
        handleNextWhenGotNewMessages(workspaceBlock, message -> {
            for (Address fromAddress : message.getFrom()) {
                if (fromAddress.toString().startsWith(expectedFrom)) {
                    return true;
                }
            }
            return false;
        });
    }

    private void handleNextWhenGotNewMessages(WorkspaceBlock workspaceBlock, ThrowingPredicate<Message, Exception> acceptMessage) {
        handleHat(workspaceBlock, store -> {
            try (Folder mailbox = store.getDefaultFolder()) {
                mailbox.open(Folder.READ_ONLY);
                // search for new messages
                Message[] messages = mailbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (Message message : messages) {
                    if (acceptMessage.test(message)) {
                        return true;
                    }
                }
            } catch (Exception ex) {
                log.error("Error when fetching mails from server", ex);
            }
            return false;
        });
    }

    private void handleHat(WorkspaceBlock workspaceBlock, Function<Store, Boolean> handler) {
        workspaceBlock.handleNext(next -> {
            MailEntity mailEntity = getMailEntity(workspaceBlock);

            popHandlers.computeIfAbsent(mailEntity.getEntityID(), s -> new POPHandler(mailEntity));
            popHandlers.get(mailEntity.getEntityID()).registeredHandlers
                    .put(workspaceBlock.getBlockId(), store -> {
                        if (handler.apply(store)) {
                            next.handle();
                        }
                    });

            workspaceBlock.onRelease(() -> {
                POPHandler popHandler = popHandlers.get(mailEntity.getEntityID());
                popHandler.registeredHandlers.remove(workspaceBlock.getBlockId());

                // stop whole fetching
                if (popHandler.registeredHandlers.isEmpty()) {
                    popHandler.refreshTask.cancel();
                }
                popHandlers.remove(mailEntity.getEntityID());
            });
        });
    }

    private MailEntity getMailEntity(WorkspaceBlock workspaceBlock) {
        MailEntity mailEntity = workspaceBlock.getMenuValueEntity("MAIL", this.mailMenu);
        if (mailEntity == null) {
            workspaceBlock.logErrorAndThrow("Unable to find mail entity");
        }
        return mailEntity;
    }

    private void skipExpression(WorkspaceBlock ignore) {
        // skip expression
    }

    @SneakyThrows
    private void sendMailCommand(WorkspaceBlock workspaceBlock) {
        MailEntity mailEntity = getMailEntity(workspaceBlock);
        MailBuilder mailBuilder = new MailBuilder(mailEntity,
                workspaceBlock.getInputString("TITLE"),
                workspaceBlock.getInputString("BODY"),
                workspaceBlock.getInputString("RECIPIENTS"));
        applyParentBlocks(mailBuilder, workspaceBlock.getParent());

        mailBuilder.sendMail();
    }

    @SneakyThrows
    private void applyParentBlocks(MailBuilder mailBuilder, WorkspaceBlock parent) {
        if (parent == null || !parent.getBlockId().startsWith("mail_update_")) {
            return;
        }
        applyParentBlocks(mailBuilder, parent.getParent());
        MailApplyHandler.valueOf(parent.getOpcode()).applyFn.accept(parent, mailBuilder);
    }

    private Scratch3Block withMail(Scratch3Block scratch3Block) {
        scratch3Block.addArgument("MAIL", this.mailMenu);
        return scratch3Block;
    }

    @AllArgsConstructor
    private enum MailApplyHandler {
        update_add_file((workspaceBlock, mailBuilder) -> {
            Object input = workspaceBlock.getInput(VALUE, true);
            if (input instanceof String) {
                String mediaURL = (String) input;
                if (mediaURL.startsWith("http")) {
                    mailBuilder.withURLAttachment(mediaURL);
                } else {
                    Path path = Paths.get(mediaURL);
                    if (Files.isRegularFile(path)) {
                        mailBuilder.withFileAttachment(mediaURL);
                    } else {
                        writeAsByteArray(workspaceBlock, mailBuilder);
                    }
                }
            } else {
                writeAsByteArray(workspaceBlock, mailBuilder);
            }
        });

        private static void writeAsByteArray(WorkspaceBlock workspaceBlock, MailBuilder mailBuilder) throws IOException {
            Path attachment = Files.createTempFile("mail_attachment_" + workspaceBlock.hashCode(), "tmp");
            Files.write(attachment, workspaceBlock.getInputByteArray(VALUE));
            mailBuilder.withFileAttachment(attachment.toString());
        }

        private final ThrowingBiConsumer<WorkspaceBlock, MailBuilder, Exception> applyFn;
    }

    private class POPHandler {
        private Map<String, Consumer<Store>> registeredHandlers = new HashMap<>();
        private EntityContextBGP.ThreadContext<Void> refreshTask;
        private MailEntity mailEntity;

        public POPHandler(MailEntity mailEntity) {
            this.refreshTask = entityContext.bgp().schedule(mailEntity.getEntityID() + "-mail-fetch-task",
                    mailEntity.getPop3RefreshTime(), TimeUnit.SECONDS, this::refresh, true);
            this.mailEntity = mailEntity;

        }

        private void refresh() {
            connectToMailServerAndHandle(mailEntity, store -> {
                for (Consumer<Store> handler : registeredHandlers.values()) {
                    handler.accept(store);
                }
                return null;
            }, null);
        }
    }

    private <T> T connectToMailServerAndHandle(MailEntity mailEntity, ThrowingFunction<Store, T, Exception> handler, T onErrorValue) {
        String baseProtocol = mailEntity.getMailFetchProtocolType().name().toLowerCase();
        String protocol = mailEntity.getPop3Security() == MailEntity.Security.SSL ? baseProtocol.concat("s") : baseProtocol;

        Properties props = new Properties();
        props.setProperty("mail." + baseProtocol + ".starttls.enable", "true");
        props.setProperty("mail.store.protocol", protocol);
        Session session = Session.getInstance(props);

        try (Store store = session.getStore()) {
            store.connect(mailEntity.getPop3Hostname(), mailEntity.getPop3Port(),
                    mailEntity.getPop3User(), mailEntity.getPop3Password().asString());
            return handler.apply(store);
        } catch (Exception e) {
            log.error("error when trying to refresh IMAP: {}", e.getMessage());
        }
        return onErrorValue;
    }
}
