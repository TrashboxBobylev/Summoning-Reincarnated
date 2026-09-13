/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;

import java.util.Collections;

public interface TypedItem {
    default String getTypeMessage(int type) {
        return Messages.get(this, "type" + type);
    }

    default String getTypeBasedString(String message, int type, Object... args){
        String str = Messages.get(this, message + type, args);
        if (str.startsWith("!!!"))
            return Messages.get(this, message, args);
        else
            return str;
    }

    int type();

    void type(int type);

    boolean isReinforced();
    default int reinforcementCooldown(){
        return 225;
    };
    int currentCooldown();
    default boolean canSwitchTypes(){
        return isReinforced() && currentCooldown() == 0;
    }

    default String reinforceCooldownDescription(){
        if (isReinforced() && !canSwitchTypes())
            return "\n\n" + Messages.get(TypedItem.class, "reinforcement_cooldown_desc", currentCooldown());
        return "";
    }

    static int getTypeColor(int type, boolean isGolden){
        if (isGolden)
            return ItemSlot.TYPE_GOLD;
        switch (type){
            default: return 0xFFFFFF;

            case 1: return ItemSlot.TYPE1;
            case 2: return ItemSlot.TYPE2;
            case 3: return ItemSlot.TYPE3;
        }
    }

    default int getTypeIcon(){
        return getTypeIcon(type(), isReinforced(), false);
    }

    static int getTypeIcon(int type, boolean isGolden, boolean isLarge){
        int icon;
        if (!isLarge) {
            switch (type) {
                case 1:
                default:
                    icon = ItemSpriteSheet.Icons.TYPE_1;
                    break;
                case 2:
                    icon = ItemSpriteSheet.Icons.TYPE_2;
                    break;
                case 3:
                    icon = ItemSpriteSheet.Icons.TYPE_3;
                    break;
            }
            if (isGolden) {
                icon += 1;
            }
        } else {
            switch (type) {
                case 1:
                default:
                    icon = ItemSpriteSheet.TYPE_1;
                    break;
                case 2:
                    icon = ItemSpriteSheet.TYPE_2;
                    break;
                case 3:
                    icon = ItemSpriteSheet.TYPE_3;
                    break;
            }
            if (isGolden) {
                icon += 3;
            }
        }

        return icon;
    }

    static String getTypeString(int type){
        return String.join("", Collections.nCopies(type, "I"));
    }

    default boolean canHaveLevels(){
        return false;
    }

    default boolean canBeCurseInfused(){
        return true;
    }

    public interface Managing {
        void onDetach(Item item);

        int uiIcon();

        void reShowSelector();

        String managingDescription(Item itemChanged);
    }
}
