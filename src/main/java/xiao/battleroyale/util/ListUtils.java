package xiao.battleroyale.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    @NotNull
    public static  <T> List<T> getSubListSafely(List<T> list, int min, int max) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        int size = list.size();
        int fromIndex = Math.max(0, min);
        int toIndex = Math.min(size, max + 1);

        if (fromIndex >= toIndex) {
            return new ArrayList<>();
        }

        return list.subList(fromIndex, toIndex);
    }
}
