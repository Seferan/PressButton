package me.seferan.pressbutton.action;

import me.seferan.pressbutton.util.Access;
import me.seferan.pressbutton.util.ModuleInfo;
import net.eq2online.console.Log;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.api.ReturnValue;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionPressButton extends ScriptAction {

	public ScriptActionPressButton() {
		super(ScriptContext.MAIN, "pressbutton");
	}

	public void onInit() {
		this.context.getCore().registerScriptAction(this);
	}

	public IReturnValue execute(IScriptActionProvider provider, IMacro macro, IMacroAction instance, String rawParams,
			String[] params) {

		if (params.length > 0) {
			try {

				int id = ScriptCore.tryParseInt(provider.expand(macro, params[0], false), 0);

				int L = 0;
				int R = 1;
				int button = 0;
				if (params.length > 1) {
					String leftOrRight = ScriptCore.parseVars(provider, macro, params[1], false);

					button = leftOrRight.startsWith("l") ? 0 : 1;
				}
				GuiScreen open = Minecraft.getMinecraft().currentScreen;
				if (open != null) {
					int doButtonClick = Access.doButtonClick(open, id, button);
					if (doButtonClick == Integer.MAX_VALUE) {
						Log.info("Invalid button ID " + id, provider);
					} else if (doButtonClick != -1) {
						if (doButtonClick == -2) {
							Log.info("Error in creation of reflection, please report the logs to the author", provider);
						} else {
							return new ReturnValue(doButtonClick == 0);
						}
					}
				}
			} catch (NumberFormatException notInt) {
				Log.info(params[0] + " is not an int", provider);
			}
		}
		return new ReturnValue(false);
	}
}
