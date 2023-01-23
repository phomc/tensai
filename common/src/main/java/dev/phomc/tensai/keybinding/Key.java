/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 PhoMC
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

package dev.phomc.tensai.keybinding;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a key.<br>
 * This enumerable class combines a subset of GLFW-supported "keyboard" keys and mouse keys.<br>
 * <ul>
 *     <li>For "keyboard" keys, {@link #getCode()} is identical to {@link #getGLFWCode()}.</li>
 *     <li>For mouse keys, {@link #getCode()} equals to {@link #getGLFWCode()} plus an offset of {@code 1 << 9}.</li>
 * </ul>
 */
public enum Key {
	// Printable keys
	KEY_SPACE(32),
	KEY_APOSTROPHE(39),
	KEY_COMMA(44),
	KEY_MINUS(45),
	KEY_PERIOD(46),
	KEY_SLASH(47),
	KEY_0(48),
	KEY_1(49),
	KEY_2(50),
	KEY_3(51),
	KEY_4(52),
	KEY_5(53),
	KEY_6(54),
	KEY_7(55),
	KEY_8(56),
	KEY_9(57),
	KEY_SEMICOLON(59),
	KEY_EQUAL(61),
	KEY_A(65),
	KEY_B(66),
	KEY_C(67),
	KEY_D(68),
	KEY_E(69),
	KEY_F(70),
	KEY_G(71),
	KEY_H(72),
	KEY_I(73),
	KEY_J(74),
	KEY_K(75),
	KEY_L(76),
	KEY_M(77),
	KEY_N(78),
	KEY_O(79),
	KEY_P(80),
	KEY_Q(81),
	KEY_R(82),
	KEY_S(83),
	KEY_T(84),
	KEY_U(85),
	KEY_V(86),
	KEY_W(87),
	KEY_X(88),
	KEY_Y(89),
	KEY_Z(90),
	KEY_LEFT_BRACKET(91),
	KEY_BACKSLASH(92),
	KEY_RIGHT_BRACKET(93),
	KEY_GRAVE_ACCENT(96),
	KEY_WORLD_1(161),
	KEY_WORLD_2(162),

	// Functional key
	KEY_ESCAPE(256),
	KEY_ENTER(257),
	KEY_TAB(258),
	KEY_BACKSPACE(259),
	KEY_INSERT(260),
	KEY_DELETE(261),
	KEY_RIGHT(262),
	KEY_LEFT(263),
	KEY_DOWN(264),
	KEY_UP(265),
	KEY_PAGE_UP(266),
	KEY_PAGE_DOWN(267),
	KEY_HOME(268),
	KEY_END(269),
	KEY_CAPS_LOCK(280),
	KEY_SCROLL_LOCK(281),
	KEY_NUM_LOCK(282),
	KEY_PRINT_SCREEN(283),
	KEY_PAUSE(284),
	KEY_F1(290),
	KEY_F2(291),
	KEY_F3(292),
	KEY_F4(293),
	KEY_F5(294),
	KEY_F6(295),
	KEY_F7(296),
	KEY_F8(297),
	KEY_F9(298),
	KEY_F10(299),
	KEY_F11(300),
	KEY_F12(301),
	KEY_F13(302),
	KEY_F14(303),
	KEY_F15(304),
	KEY_F16(305),
	KEY_F17(306),
	KEY_F18(307),
	KEY_F19(308),
	KEY_F20(309),
	KEY_F21(310),
	KEY_F22(311),
	KEY_F23(312),
	KEY_F24(313),
	KEY_F25(314),
	KEY_KP_0(320),
	KEY_KP_1(321),
	KEY_KP_2(322),
	KEY_KP_3(323),
	KEY_KP_4(324),
	KEY_KP_5(325),
	KEY_KP_6(326),
	KEY_KP_7(327),
	KEY_KP_8(328),
	KEY_KP_9(329),
	KEY_KP_DECIMAL(330),
	KEY_KP_DIVIDE(331),
	KEY_KP_MULTIPLY(332),
	KEY_KP_SUBTRACT(333),
	KEY_KP_ADD(334),
	KEY_KP_ENTER(335),
	KEY_KP_EQUAL(336),
	KEY_LEFT_SHIFT(340),
	KEY_LEFT_CONTROL(341),
	KEY_LEFT_ALT(342),
	KEY_LEFT_SUPER(343),
	KEY_RIGHT_SHIFT(344),
	KEY_RIGHT_CONTROL(345),
	KEY_RIGHT_ALT(346),
	KEY_RIGHT_SUPER(347),
	KEY_MENU(348),

	// Mouse key
	MOUSE_BUTTON_1(1 << 9),
	MOUSE_BUTTON_2(MOUSE_BUTTON_1.code + 1),
	MOUSE_BUTTON_3(MOUSE_BUTTON_1.code + 2),
	MOUSE_BUTTON_4(MOUSE_BUTTON_1.code + 3),
	MOUSE_BUTTON_5(MOUSE_BUTTON_1.code + 4),
	MOUSE_BUTTON_6(MOUSE_BUTTON_1.code + 5),
	MOUSE_BUTTON_7(MOUSE_BUTTON_1.code + 6),
	MOUSE_BUTTON_8(MOUSE_BUTTON_1.code + 7),
	MOUSE_BUTTON_LAST(MOUSE_BUTTON_8.code),
	MOUSE_BUTTON_LEFT(MOUSE_BUTTON_1.code),
	MOUSE_BUTTON_RIGHT(MOUSE_BUTTON_2.code),
	MOUSE_BUTTON_MIDDLE(MOUSE_BUTTON_3.code);

	private static final Map<Integer, Key> lookupTable = new HashMap<>();

	static {
		for (Key key : Key.values()) {
			lookupTable.put(key.code, key);
		}
	}

	private final int code;

	Key(int code) {
		this.code = code;
	}

	/**
	 * Given a key code, finds out its corresponding {@link Key}.
	 *
	 * @param code key code
	 * @return {@link Key} if exists, {@code null} otherwise
	 */
	@Nullable
	public static Key lookup(int code) {
		return lookupTable.get(code);
	}

	/**
	 * Given a GLFW key code, finds out its corresponding {@link Key}.
	 *
	 * @param glfwCode GLFW key code
	 * @param mouse    whether it is a mouse key
	 * @return {@link Key} if exists, {@code null} otherwise
	 */
	@Nullable
	public static Key lookupGLFW(int glfwCode, boolean mouse) {
		return lookup(mouse ? (MOUSE_BUTTON_1.code + glfwCode) : glfwCode);
	}

	/**
	 * Gets the key code.<br>
	 * <b>This method is different from {@link #getGLFWCode()}</b>
	 *
	 * @return key code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets the GLFW key code.<br>
	 * <b>This method is different from {@link #getCode()}</b>
	 *
	 * @return GLFW key code
	 */
	public int getGLFWCode() {
		if (code >= MOUSE_BUTTON_1.code) {
			return code - MOUSE_BUTTON_1.code;
		}

		return code;
	}

	/**
	 * Checks if the current key is used for mouse.
	 *
	 * @return {@code true} or {@code false}
	 */
	public boolean isMouse() {
		return code >= MOUSE_BUTTON_1.code;
	}
}
