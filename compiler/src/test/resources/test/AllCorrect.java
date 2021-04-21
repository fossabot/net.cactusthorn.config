package test;

import net.cactusthorn.config.core.Config;
import net.cactusthorn.config.core.Prefix;
import net.cactusthorn.config.core.Disable;
import net.cactusthorn.config.core.Split;
import net.cactusthorn.config.core.Key;
import net.cactusthorn.config.core.Default;

import java.util.*;

@Config @Prefix("prefix") @Split(";") interface AllCorrect {

    enum FromStringEnum {
        AAA, BBB;

        public static FromStringEnum fromString(String string) {
            return AAA;
        }
    }

    enum SimpleEnum {
        AAA, BBB;
    }

    FromStringEnum fromStringEnum();

    SimpleEnum simpleEnum();

    String value();

    @Default("100") int intValue();

    Optional<UUID> uuid();

    @Split(",") Optional<List<Integer>> list();

    @Default("46400000-8cc0-11bd-b43e-10d46e4ef14d") Set<UUID> set();

    @Default("10.5,11.5") SortedSet<Float> sorted();

    StringBuilder buf();

    @Key("ddd") @Disable(Disable.Feature.PREFIX) Double ddd();

    @Default("A,B,C") List<String> listNoOptional();

    Optional<SortedSet<Float>> sortedOptional();

    Optional<Set<Float>> setOptional();

    Float boxedPrimitive();
}
