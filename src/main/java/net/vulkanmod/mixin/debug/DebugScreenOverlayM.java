package net.vulkanmod.mixin.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.vulkanmod.render.chunk.WorldRenderer;
import net.vulkanmod.vulkan.SystemInfo;
import net.vulkanmod.vulkan.Vulkan;
import net.vulkanmod.vulkan.device.Device;
//import net.vulkanmod.vulkan.device.DeviceRAMInfo;
import net.vulkanmod.vulkan.memory.MemoryManager;
//import net.vulkanmod.vulkan.queue.Queue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import static net.vulkanmod.Initializer.CONFIG;
import static net.vulkanmod.Initializer.getVersion;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayM {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private static long bytesToMegabytes(long bytes) {
        return 0;
    }

    @Shadow
    @Final
    private Font font;

    @Shadow
    protected abstract List<String> getGameInformation();

    @Shadow
    protected abstract List<String> getSystemInformation();

    @Redirect(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private ArrayList<String> redirectList(Object[] elements) {
        ArrayList<String> strings = new ArrayList<>();

        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Device device = Vulkan.getDevice();

        strings.add(String.format("Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32));
        strings.add(String.format("Mem: % 2d%% %03d/%03dMB", usedMemory * 100L / maxMemory, bytesToMegabytes(usedMemory), bytesToMegabytes(maxMemory)));
        strings.add(String.format("Allocated: % 2d%% %03dMB", totalMemory * 100L / maxMemory, bytesToMegabytes(totalMemory)));
        strings.add(String.format("Off-heap: " + getOffHeapMemory() + "MB"));
        strings.add("NativeMemory: %dMB".formatted(MemoryManager.getInstance().getNativeMemoryMB()));
        strings.add("DeviceMemory: %dMB".formatted(MemoryManager.getInstance().getAllocatedDeviceMemoryMB()));
        strings.add("");
        strings.add("VulkanMod " + getVersion());
        strings.add("CPU: " + SystemInfo.cpuInfo);
        strings.add("GPU: " + device.deviceName);
        strings.add("Driver: " + device.driverVersion);
        strings.add("Instance: " + device.vkInstanceLoaderVersion);
        strings.add("Vulkan: " + device.vkVersion);
        strings.add("");
        Collections.addAll(strings, WorldRenderer.getInstance().getChunkAreaManager().getStats());

        //if (CONFIG.showQueueFamily) {
        //    strings.add("");
        //    strings.add("Device Queue Families:");
        //    strings.add("Graphics Queue: " + (Queue.graphicsSupported ? "§aSupported§r" : "§cUnsupported§r"));
        //    strings.add("Present Queue: " + (Queue.presentFallback ? "§eFallback§r" : "§aSupported§r"));
        //    strings.add("Transfer Queue: " + (Queue.transferFallback ? "§eFallback§r" : "§aSupported§r"));
        //    strings.add("Compute Queue: " + (Queue.computeFallback ? "§eFallback§r" : "§aSupported§r"));
        //}
    
        //if (isRunningOnCompatDevice() && CONFIG.showDeviceRAM) {
        //    strings.add("");
        //    strings.add("Device RAM Info:");
        //    strings.add(DeviceRAMInfo.getMemoryInfo());
        //    strings.add(DeviceRAMInfo.getAvailableMemoryInfo());
        //    strings.add(DeviceRAMInfo.getCurrentUsage());
        //    strings.add(DeviceRAMInfo.getHighestMemoryUsedRecord());
        //    strings.add(DeviceRAMInfo.getBuffersInfo());
        //    if (CONFIG.showlowRAM) {
        //        strings.add(DeviceRAMInfo.getAvailableRAMWarn());
        //    }
        //}
        
        return strings;
    }

    //private static boolean isRunningOnCompatDevice() {
    //    String osName = System.getProperty("os.name").toLowerCase();
    //    return osName.contains("linux") || osName.contains("android");
    //}

    private long getOffHeapMemory() {
        return bytesToMegabytes(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed());
    }
}
