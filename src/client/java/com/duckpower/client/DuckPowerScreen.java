package com.duckpower.client;

import com.duckpower.network.DuckPowerConfigPayload;
import com.duckpower.power.FuryPowers;
import com.duckpower.registry.ModComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

/** Configuração visual: cada clique avança nível 0 -> 1 -> 2 -> 3 -> 0. */
public final class DuckPowerScreen extends Screen {
    private final ItemStack cap;
    private static final int BUTTON_WIDTH = 190;
    private static final int BUTTON_HEIGHT = 20;

    public DuckPowerScreen(ItemStack cap) {
        super(Text.literal("Duck Power"));
        this.cap = cap;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = Math.max(55, this.height / 2 - 90);
        int index = 0;

        for (Map.Entry<String, FuryPowers.Power> entry : FuryPowers.all().entrySet()) {
            String id = entry.getKey();
            FuryPowers.Power power = entry.getValue();
            int column = index / 4;
            int row = index % 4;
            int x = centerX + (column == 0 ? -BUTTON_WIDTH - 6 : 6);
            int y = startY + row * 25;

            addDrawableChild(ButtonWidget.builder(label(id, power.name()), b -> {
                int next = (FuryPowers.getLevel(cap, id) + 1) % 4;
                setLevel(id, next);
                b.setMessage(label(id, power.name()));
            }).dimensions(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            index++;
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Salvar e fechar"), b -> {
            String config = FuryPowers.encode(cap);
            cap.set(ModComponents.POWER_CONFIG, config);
            ClientPlayNetworking.send(new DuckPowerConfigPayload(config));
            close();
        }).dimensions(centerX - 75, startY + 112, 150, 22).build());
    }

    private void setLevel(String id, int level) {
        StringBuilder updated = new StringBuilder();
        for (FuryPowers.Power power : FuryPowers.all().values()) {
            if (updated.length() > 0) updated.append(';');
            int value = power.id().equals(id) ? level : FuryPowers.getLevel(cap, power.id());
            updated.append(power.id()).append('=').append(value);
        }
        cap.set(ModComponents.POWER_CONFIG, updated.toString());
    }

    private Text label(String id, String name) {
        int level = FuryPowers.getLevel(cap, id);
        String state = level == 0 ? "§cOFF" : "§aNÍVEL " + level;
        return Text.literal("[" + state + "] " + name);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        int centerX = this.width / 2;
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("DUCK POWER").formatted(Formatting.GOLD, Formatting.BOLD),
                centerX, 22, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Clique para alternar: OFF, nível 1, 2 ou 3"),
                centerX, 38, 0xD0D0D0);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("As alterações são salvas no boné ao confirmar"),
                centerX, this.height - 24, 0xAAAAAA);
    }

    @Override public boolean shouldPause() { return false; }
}
