package be.ephys.configurablebeacons.mixins;

import be.ephys.configurablebeacons.BeaconHook;
import net.minecraft.tileentity.BeaconTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconTileEntity.class)
public class BeaconTileEntityMixin {

  @Inject(method = "addEffectsToPlayers", at = @At("HEAD"), cancellable = true)
  public void addEffectsToPlayers$override(CallbackInfo ci) {
    BeaconHook.addEffectsToPlayers((BeaconTileEntity) (Object) this);
  }
}
