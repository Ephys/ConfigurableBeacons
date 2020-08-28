

function forEachMethodCalls(containerMethod, searchedMethodType, searchedMethodOwner, searchedMethodName, searchedMethodDescriptor, callback) {
  var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
  var method = ASM.findFirstMethodCall(containerMethod, searchedMethodType, searchedMethodOwner, searchedMethodName, searchedMethodDescriptor);

  callback(method);
}

function initializeCoreMod() {
  var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
  var MethodType = ASM.MethodType;

  return {
    'replace-beacon-add-effects-to-players': {
      target: {
        type: 'METHOD',
        class: 'net.minecraft.tileentity.BeaconTileEntity',
        methodName: 'func_73660_a', // tick()
        methodDesc: '()V',
      },
      transformer: function (method) {
        print('RUNNING - CB')

        var addEffectsToPlayer = ASM.mapMethod('func_146000_x');

        forEachMethodCalls(method, MethodType.SPECIAL, 'net/minecraft/tileentity/BeaconTileEntity', addEffectsToPlayer, '()V', function (foundMethod) {
          var redirectedMethodCall = ASM.buildMethodCall(
            "be/ephys/configurablebeacons/BeaconHook",
            "addEffectsToPlayers",
            "(Lnet/minecraft/tileentity/BeaconTileEntity;)V",
            MethodType.STATIC
          );

          method.instructions.insert(foundMethod, redirectedMethodCall);
          method.instructions.remove(foundMethod);
        });

        return method;
      },
    },
  };
}
