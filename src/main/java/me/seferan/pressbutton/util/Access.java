package me.seferan.pressbutton.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Throwables;
import com.mumfrey.liteloader.util.ObfuscationUtilities;

import net.eq2online.console.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.ContainerEnchantment;

public final class Access
{
  private static Field GuiScreen_buttonsList;
  private static Method GuiScreen_actionPerformed;
  
  static
  {
    String btnsFieldName = ObfuscationUtilities.getObfuscatedFieldName("buttonList", "n", "field_146292_n");
    try
    {
      GuiScreen_buttonsList = GuiScreen.class.getDeclaredField(btnsFieldName);
    }
    catch (NoSuchFieldException e)
    {
      e.printStackTrace();
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    if (GuiScreen_buttonsList == null)
    {
      Log.info("Missing field for " + btnsFieldName);
      Log.info("Avalible: " + Arrays.toString(GuiScreen.class.getDeclaredFields()));
    }
    else
    {
      GuiScreen_buttonsList.setAccessible(true);
    }
    String actMethodName = ObfuscationUtilities.getObfuscatedFieldName("actionPerformed", "a", "func_146284_a");
    try
    {
      GuiScreen_actionPerformed = GuiScreen.class.getDeclaredMethod(actMethodName, new Class[] { GuiButton.class });
    }
    catch (NoSuchMethodException e)
    {
      e.printStackTrace();
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    if (GuiScreen_actionPerformed == null)
    {
      Log.info("Missing method for " + actMethodName);
      Log.info("Avalible: " + Arrays.toString(GuiScreen.class.getDeclaredMethods()));
    }
    else
    {
      GuiScreen_actionPerformed.setAccessible(true);
    }
  }
  
  public static int doButtonClick(GuiScreen open, int id, int button)
  {
    if ((open instanceof GuiEnchantment)) {
      return doEnchantPacket((GuiEnchantment)open, id);
    }
    List<GuiButton> buttonList = getButtons(open);
    if (id >= buttonList.size()) {
      return Integer.MAX_VALUE;
    }
    GuiButton btn = (GuiButton)buttonList.get(id);
    if (!btn.mousePressed(Minecraft.getMinecraft(), btn.x, btn.y)) {
      return -1;
    }
    return callAction(open, btn);
  }
  
  private static int doEnchantPacket(GuiEnchantment open, int id)
  {
    if (id > 2) {
      return Integer.MAX_VALUE;
    }
    ContainerEnchantment ench = (ContainerEnchantment)open.inventorySlots;
    boolean okay = ench.enchantItem(Minecraft.getMinecraft().player, id);    
    if (okay)
    {
      Minecraft.getMinecraft().playerController.sendEnchantPacket(ench.windowId, id);
      
      return 0;
    }
    return -1;
  }
  
  private static int callAction(GuiScreen open, GuiButton btn)
  {
    try
    {
      if (GuiScreen_actionPerformed != null) {
        GuiScreen_actionPerformed.invoke(open, new Object[] { btn });
      }
      return 0;
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      Throwables.propagate(e);
    }
    return -2;
  }
  
  private static List<GuiButton> getButtons(GuiScreen open)
  {
    try
    {
      return (List)GuiScreen_buttonsList.get(open);
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    return new ArrayList();
  }
}