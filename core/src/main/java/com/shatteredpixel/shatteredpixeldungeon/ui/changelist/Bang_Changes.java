package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

//bang's \uD53D\uC140 \uB358\uC804 changelog. Add new entries at the top of addAllChanges.
public class Bang_Changes {

	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

		ChangeInfo changes = new ChangeInfo("bang's \uD53D\uC140 \uB358\uC804", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		//example entry:
		//changes.addButton( new ChangeButton( Icons.get(Icons.CHANGES), "Title", "Description" ));
	}

}
