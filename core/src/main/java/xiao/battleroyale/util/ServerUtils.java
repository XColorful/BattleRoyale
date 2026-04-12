package xiao.battleroyale.util;

import net.minecraft.util.profiling.ProfilerFiller;

import java.util.function.Supplier;

public class ServerUtils {

    public static class ProfileSection implements AutoCloseable {
        private final ProfilerFiller profiler;

        public ProfileSection(ProfilerFiller profiler, Supplier<String> nameSupplier) {
            this.profiler = profiler;
            if (this.profiler != null) {
                this.profiler.push(nameSupplier);
            }
        }
        public ProfileSection(ProfilerFiller profiler, String name) {
            this.profiler = profiler;
            if (this.profiler != null) {
                this.profiler.push(name);
            }
        }

        @Override
        public void close() {
            if (this.profiler != null) {
                this.profiler.pop();
            }
        }
    }
}
