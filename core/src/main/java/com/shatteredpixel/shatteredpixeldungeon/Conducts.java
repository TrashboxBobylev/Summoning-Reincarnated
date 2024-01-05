package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Conducts {
    public enum Conduct {
        NULL,
//        NO_ARMOR(1.2f),
        PACIFIST(2.5f),
        CRIPPLED(1.75f, 3),
        NO_MAGIC(1.5f, 4),
        ZEN(2f, 5),
//        BERSERK(0.85f),
        WRAITH(1.4f, 7),
        SLEEPY(0.35f, 8),
        KING(1.3f, 10),
        EVERYTHING(0f, 11),
        EXPLOSIONS(1.5f, 12),
//        INVISIBLE(2f),
        REGENERATION(1.33f, 14),
//        UNKNOWN(1.7f),
//        NO_STR(3f),
//        CHAMPS(1.7f),
        NO_REGEN(1.66f, 18),
        CURSE(1.4f, 19),
        ALLSIGHT(1.4f, 20),
        NO_LOOT(2f, 21),
        LIMITED_MONSTERS(1.5f, 22),
//        HUGE(1.75f),
//        DEBUG_SCROLL(-1f){
//            @Override
//            public boolean shouldAppear() {
//                return DeviceCompat.isDebug() || !SPDSettings.oneConduct();
//            }
//        };
        ;

        public float scoreMod;
        public int icon;

        Conduct(){
            scoreMod = 1f;
            icon = ordinal();
        }

        Conduct(float scoreMod){
            this.scoreMod = scoreMod;
            this.icon = ordinal();
        }

        Conduct(int icon){
            this.scoreMod = 1f;
            this.icon = icon;
        }

        Conduct(float scoreMod, int icon){
            this.scoreMod = scoreMod;
            this.icon = icon;
        }

        public boolean shouldAppear(){
            return true;
        }

        @Override
        public String toString() {
            return Messages.get(Conducts.class, this.name());
        }

        public String desc(){
            return Messages.get(Conducts.class, name() + "_desc") + "\n\n" + Messages.get(Dungeon.class, "score", new DecimalFormat("#.##").format(scoreMod));
        }
    }

    public static class ConductStorage implements Bundlable {

        public ArrayList<Conduct> conducts;

        public ConductStorage() {
            conducts = new ArrayList<>();
        }

        public ConductStorage(Conduct... conducts) {this.conducts = new ArrayList<>(Arrays.asList(conducts));}

        public ConductStorage(ConductStorage storage) {this.conducts = new ArrayList<>(storage.conducts);}

        @Override
        public void storeInBundle(Bundle bundle) {
            ArrayList<String> conductIds = new ArrayList<>();
            for (Conduct conduct: conducts){
                conductIds.add(conduct.name());
            }
            bundle.put("conduct", conductIds.toArray(new String[0]));
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            conducts.clear();
            if (bundle.contains("conduct")) {
                String[] conductIds = bundle.getStringArray("conduct");
                for (String conduct : conductIds) {
                    conducts.add(Conduct.valueOf(conduct));
                }
            }
        }

        public String getDebugString(){
            if (conducts.isEmpty()){
                return "NULL";
            }
            StringBuilder str = new StringBuilder();
            for (Conduct conduct : conducts){
                str.append(conduct.name()).append(",");
            }
            str.delete(str.length() - 1, str.length());
            return str.toString();
        }

        public boolean isConductedAtAll(){
            return !conducts.isEmpty();
        }

        public boolean oneConduct(){
            return conducts.size() == 1;
        }

        public float scoreMod(){
            float total = 1;
            for (Conduct conduct : conducts){
                total *= conduct.scoreMod;
            }
            return total;
        }

        public boolean isConducted(Conduct mask){
            return isConductedAtAll() && conducts.contains(mask);
        }

        public Conduct getFirst(){
            if (isConductedAtAll()) return conducts.get(0);
            return null;
        }

        public int size(){
            return conducts.size();
        }
    }
}
