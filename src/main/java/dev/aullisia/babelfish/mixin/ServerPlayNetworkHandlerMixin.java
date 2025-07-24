package dev.aullisia.babelfish.mixin;

import dev.aullisia.babelfish.TranslateService;
import dev.aullisia.babelfish.TranslationPreferences;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    private Optional<LastSeenMessageList> validateAcknowledgment(LastSeenMessageList.Acknowledgment acknowledgment) {
        return Optional.empty();
    }

    @Shadow
    private void validateMessage(String message, Runnable callback) {
    }

    @Shadow
    private SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException {
        return null;
    }

    @Shadow
    private void handleMessageChainException(MessageChain.MessageChainException exception) {
    }

    @Shadow
    private <T, R> CompletableFuture<R> filterText(T text, BiFunction<TextStream, T, CompletableFuture<R>> filterer) {
        return null;
    }

    @Shadow
    private CompletableFuture<FilteredMessage> filterText(String text) {
        return null;
    }

    @Shadow
    private CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
        return null;
    }

    @Shadow
    private void checkForSpam() {
    }

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private MessageChainTaskQueue messageChainTaskQueue;

    @Unique
    private MinecraftServer myServer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void captureServer(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        this.myServer = server;
    }

    private boolean verifyMessage(SignedMessage message) {
        return message.hasSignature() && !message.isExpiredOnServer(Instant.now());
    }

    private void handleDecoratedMessage(SignedMessage message, String language) {
        Set<UUID> playerUuids = TranslationPreferences.getPlayersByLanguage(language);

        for (ServerPlayerEntity player : this.myServer.getPlayerManager().getPlayerList()) {
            if (playerUuids.contains(player.getUuid())) {
                SentMessage sentMessage = SentMessage.of(message);
                boolean filtered = true;
                player.sendChatMessage(sentMessage, filtered, MessageType.params(MessageType.CHAT, this.player));
            }
        }
        this.checkForSpam();
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        ci.cancel();
        Optional<LastSeenMessageList> optional = this.validateAcknowledgment(packet.acknowledgment());
        optional.ifPresent(lastSeenMessageList -> this.validateMessage(packet.chatMessage(), () -> {
            SignedMessage signedMessage;
            try {
                signedMessage = this.getSignedMessage(packet, (LastSeenMessageList) lastSeenMessageList);
            } catch (MessageChain.MessageChainException var6) {
                this.handleMessageChainException(var6);
                return;
            }


            String originalLanguage = TranslationPreferences.getLanguage(this.player.getUuid());

            TranslationPreferences.getAllLanguages().forEach(lang -> {
                TranslateService.translateMessage(new TranslateService.TranslateParams(
                        packet.chatMessage(),
                        originalLanguage,
                        lang
                )).thenAccept(translated -> {
                    CompletableFuture<FilteredMessage> completableFuture = this.filterText(signedMessage.getSignedContent());

                    Text displayText;
                    if (translated == null) {
                        displayText = Text.literal(packet.chatMessage())
                                .setStyle(Style.EMPTY.withHoverEvent(
                                        new HoverEvent.ShowText(
                                                Text.literal("Translation failed from " + originalLanguage + " to " + lang)
                                        )
                                ));
                    } else {
                        displayText = Text.literal(translated)
                                .setStyle(Style.EMPTY.withHoverEvent(
                                        new HoverEvent.ShowText(
                                                Text.literal("Translated from " + originalLanguage + ": " + packet.chatMessage())
                                        )
                                ));
                    }

                    Text decoratedText = this.myServer.getMessageDecorator().decorate(this.player, displayText);

                    this.messageChainTaskQueue.append(completableFuture, filtered -> {
                        SignedMessage signedMessage2 = signedMessage.withUnsignedContent(decoratedText).withFilterMask(filtered.mask());
                        this.handleDecoratedMessage(signedMessage2, lang);
                    });
                });
            });
        }));
    }
}
