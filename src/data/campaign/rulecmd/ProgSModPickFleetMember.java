package data.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import com.fs.starfarer.api.util.Misc.Token;

/** ProgSModPickFleetMember [selected ship key] [trigger] [menuId]
 * -- afterwards sets [selected ship key] to the picked ship
 * -- fires [trigger] and changes $menuState to [menuId] 
 *    upon successful selection of a ship */
public class ProgSModPickFleetMember extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Token> params, final Map<String, MemoryAPI> memoryMap)  {
        if (dialog == null) return false;
        if (params.size() < 3) return false;

        final String selectedShipKey = params.get(0).string;
        // This function excludes fighters
        List<FleetMemberAPI> selectFrom =  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        dialog.showFleetMemberPickerDialog("Select a ship", "Ok", "Cancel", 3, 7, 58f, true, false, selectFrom, 
            new FleetMemberPickerListener() {

                @Override
                public void cancelledFleetMemberPicking() {}

                @Override
                public void pickedFleetMembers(List<FleetMemberAPI> fleetMembers) {
                    if (fleetMembers == null || fleetMembers.size() == 0) {
                        return;
                    }

                    FleetMemberAPI picked = fleetMembers.get(0);
                    memoryMap.get(MemKeys.LOCAL).set(selectedShipKey, picked, 0f);
                    
                    // Wait for player to finish picking a ship before messing with
                    // the menu states.
                    dialog.getVisualPanel().showFleetMemberInfo(picked, false);
                    memoryMap.get(MemKeys.LOCAL).set("$menuState", params.get(2).getString(memoryMap), 0f);
                    FireAll.fire(ruleId, dialog, memoryMap, params.get(1).getString(memoryMap));
                }

            }
        );

        return true;
    }
    
}
