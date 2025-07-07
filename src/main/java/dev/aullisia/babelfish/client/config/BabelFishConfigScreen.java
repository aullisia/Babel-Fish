package dev.aullisia.babelfish.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BabelFishConfigScreen {
    public static Screen get(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("BabelFish Config"))
                .setSavingRunnable(BabelFishClientConfig::save);

        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder
                .startStrField(Text.literal("Preferred Language"), BabelFishClientConfig.get().preferredLang)
                .setSaveConsumer(val -> BabelFishClientConfig.get().preferredLang = val)
                .setDefaultValue("en")
                .build());

        return builder.build();
    }
}
