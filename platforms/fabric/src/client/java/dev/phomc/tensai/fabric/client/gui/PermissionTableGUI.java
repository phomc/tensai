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

package dev.phomc.tensai.fabric.client.gui;

import java.util.Map;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

import net.minecraft.text.Text;

import dev.phomc.tensai.fabric.client.iam.Permission;

public class PermissionTableGUI extends LightweightGuiDescription {
	public PermissionTableGUI(Map<Permission, Boolean> map, boolean showFalseOnly, boolean defaultVal, Runnable callback) {
		WBox container = new WBox(Axis.VERTICAL);

		for (Permission perm : map.keySet()) {
			if (showFalseOnly && map.get(perm)) {
				continue;
			}
			map.put(perm, defaultVal);

			WToggleButton toggleButton = new WToggleButton(perm.getDescription());
			toggleButton.setOnToggle(on -> {
				map.put(perm, on);
			});
			toggleButton.setToggle(defaultVal);
			container.add(toggleButton);
		}

		WButton button = new WButton(Text.translatable("gui.permissionTable.confirm"));
		button.setOnClick(callback);

		WGridPanel root = (WGridPanel) rootPanel;
		root.add(new WLabel(Text.translatable("gui.permissionTable.title")), 0, 0, 12, 1);
		root.add(new WScrollPanel(container), 0, 1, 12, 8);
		root.add(button, 0, 10, 12, 3);
		root.validate(this);
	}
}
