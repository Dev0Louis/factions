package io.icker.factions.core;

import io.icker.factions.FactionsMod;
import io.icker.factions.api.persistents.Faction;
import io.icker.factions.api.persistents.User;
import io.icker.factions.text.FillerText;
import io.icker.factions.text.Message;
import io.icker.factions.text.PlainText;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatManager {
    public static void register() {
        ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
            if (sender != null && FactionsMod.CONFIG.DISPLAY.MODIFY_CHAT) {
                return CompletableFuture.completedFuture(ChatManager.handleMessage(sender, message.getString()));
            }
            return CompletableFuture.completedFuture(message);
        });
    }

    public static Text handleMessage(ServerPlayerEntity sender, String message) {
        UUID id = sender.getUuid();
        User member = User.get(id);

        if (member.chat == User.ChatMode.GLOBAL) {
            if (member.isInFaction()) {
                return ChatManager.inFactionGlobal(sender, member.getFaction(), message);
            } else {
                return ChatManager.global(sender, message);
            }
        } else {
            if (member.isInFaction()) {
                return ChatManager.faction(sender, member.getFaction(), message);
            } else {
                return ChatManager.global(sender, message);
            }
        }
    }

    private static Text global(ServerPlayerEntity sender, String message) {
        return new PlainText(message).format(Formatting.GRAY)
                .build(sender.getUuid());
    }

    private static Text inFactionGlobal(ServerPlayerEntity sender, Faction faction, String message) {
        return new Message()
                .append(new PlainText(faction.getName()).format(Formatting.BOLD, faction.getColor()))
                .append(new FillerText("»"))
                .append(new PlainText(message).format(Formatting.GRAY))
                .build(sender.getUuid());
    }

    private static Text faction(ServerPlayerEntity sender, Faction faction, String message) {
        return new Message()
                .append(new PlainText("F").format(Formatting.BOLD, faction.getColor()))
                .append(new FillerText("»"))
                .append(new PlainText(message).format(Formatting.GRAY))
                .build(sender.getUuid());
    }
}
