/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 VMSA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.phomc.tensai.fabric.test.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.phomc.tensai.fabric.clients.FabricClientHandle;

public class AnimationCommand {
	public static final String COMMAND = "animation";
	public static final String COMMAND_PLAY = "play";

	public static LiteralArgumentBuilder<ServerCommandSource> animation() {
		LiteralArgumentBuilder<ServerCommandSource> ret = literal(COMMAND);
		ret.then(argument("target", EntityArgumentType.players())
				.then(playCommand()));
		return ret;
	}

	private static LiteralArgumentBuilder<ServerCommandSource> playCommand() {
		LiteralArgumentBuilder<ServerCommandSource> ret = literal("play");
		ret.then(argument("type", IdentifierArgumentType.identifier())
				.then(argument("start", DoubleArgumentType.doubleArg(0))
						.then(argument("duration", DoubleArgumentType.doubleArg(0))
								.executes(ctx -> playOnce(ctx, DoubleArgumentType.getDouble(ctx, "start"), DoubleArgumentType.getDouble(ctx, "duration"))))
						.executes(ctx -> playOnce(ctx,  DoubleArgumentType.getDouble(ctx, "start"), Double.POSITIVE_INFINITY)))
				.executes(ctx -> playOnce(ctx, 0, Double.POSITIVE_INFINITY)));
		return ret;
	}

	private static int playOnce(CommandContext<ServerCommandSource> ctx, double startSec, double durationSec) throws CommandSyntaxException {
		Identifier type = IdentifierArgumentType.getIdentifier(ctx, "type");
		Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "target");
		String command = "/" + TensaiTestCommand.COMMAND + " " + COMMAND + " @s " + COMMAND_PLAY + " " + type.toString();
		Text clickableText = Text.literal(type.toString()).styled(s -> s
				.withUnderline(true)
				.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(command))));

		for (ServerPlayerEntity e : players) {
			((FabricClientHandle) e).getVfx().playAnimationOnce(type.toString(), startSec, durationSec);
			ctx.getSource().sendFeedback(Text.literal("Sent animation play request (").append(clickableText).append(") to ").append(e.getDisplayName()), true);
		}
		return 1;
	}
}
