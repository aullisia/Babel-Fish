package dev.aullisia.babelfish.network.packet;

import dev.aullisia.babelfish.BabelFish;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetLanguagePayload(String language) implements CustomPayload {
    public static final Identifier SET_LANGUAGE_PAYLOAD_ID = Identifier.of(BabelFish.MOD_ID, "set_language");
    public static final CustomPayload.Id<SetLanguagePayload> ID = new CustomPayload.Id<>(SET_LANGUAGE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SetLanguagePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SetLanguagePayload::language, SetLanguagePayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
