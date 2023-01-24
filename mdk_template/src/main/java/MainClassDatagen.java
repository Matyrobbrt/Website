// fileName: props.datagenSourceSet ? "src/main/datagen/${packageName.replace('.' as char, '/' as char)}/datagen/${mainClass}Datagen.java" : null
package ${packageName}.datagen;

import ${packageName};
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ${mainClass}.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ${mainClass}Datagen {

    @SubscribeEvent
    public static void onDatagen(GatherDataEvent event) {

    }
}
