/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 PhoMC
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

package dev.phomc.tensai.fabric.test.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class PluginMessagingChannelListener {
	private static final int BYTES_PER_ROW = 16;
	private static final String HEADER = "____ |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f | 0   4   8   c   ";
	private static final String DISPLAYABLE_CHARS = "0123456789!@#$%^&*()-=_+`~[]{};':\"\\|,./<>?abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZ";

	public static void listen(Identifier channel) {
		Text prefix = Text.literal("[Client] ").formatted(Formatting.GOLD)
				.append(Text.literal(channel.toString()).formatted(Formatting.YELLOW))
				.append(Text.literal(" >> ").formatted(Formatting.DARK_GRAY));

		ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler, buf, responseSender) -> {
			byte[] bs = buf.array();
			String messageType = "(unknown)";

			try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bs))) {
				messageType = stream.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

			client.inGameHud.getChatHud().addMessage(Text.empty()
					.append(prefix)
					.append(Text.literal("Message type = " + messageType).formatted(Formatting.WHITE)));

			TensaiFabricTestClient.LOGGER.info("Displaying bytes for message type {}:", messageType);
			logBytes(buf.array());
		});

		TensaiFabricTestClient.LOGGER.info("Now listening for messages from {} channel", channel);
	}

	private static String createHex(int bv, int size) {
		String s = Integer.toHexString(bv);
		while (s.length() < size) s = "0" + s;
		return s;
	}

	private static String spaces(String s, int max) {
		while (s.length() < max) s += " ";
		return s;
	}

	private static void logBytes(byte[] bs) {
		TensaiFabricTestClient.LOGGER.info(HEADER);
		String left = "";
		String right = "";

		for (int i = 0; i < bs.length; i++) {
			if (i != 0 && (i % BYTES_PER_ROW) == 0) {
				TensaiFabricTestClient.LOGGER.info(createHex((i - 1) >> 4, 3) + "x | " + left + "| " + right);
				left = "";
				right = "";
			}

			int uint = Byte.toUnsignedInt(bs[i]);
			left += createHex(uint, 2) + " ";

			int chIdx = DISPLAYABLE_CHARS.indexOf(uint);
			right += chIdx != -1 ? ((char) uint) : '.';
		}

		if (left.length() != 0) {
			TensaiFabricTestClient.LOGGER.info(createHex((bs.length - 1) >> 4, 3) + "x | " + spaces(left, 48) + "| " + right);
		}
	}

	public static void main(String[] args) {
		logBytes(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19});
	}
}
