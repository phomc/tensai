package dev.phomc.tensai.keybinding;

/**
 * Represents a key binding.
 */
public class KeyBinding {
	private final String id, category;
	private final Type type;
	private final int keyCode;

	public KeyBinding(String id, Type type, int keyCode, String category) {
		this.id = id;
		this.type = type;
		this.keyCode = keyCode;
		this.category = category;
	}

	public String id() {
		return id;
	}

	public Type type() {
		return type;
	}

	public int keyCode() {
		return keyCode;
	}

	public String category() {
		return category;
	}

	public enum Type {
		KEYBOARD,
		MOUSE
	}
}
