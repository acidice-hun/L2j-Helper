package handlers.voicedcommandhandlers;
/*
 * L2Helper HighFive
 * Copyright (C) 2013 Pater http://pater.perfect.sk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ExperienceTable;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2Macro;
import com.l2jserver.gameserver.model.L2Macro.L2MacroCmd;
import com.l2jserver.gameserver.model.L2ShortCut;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.model.actor.stat.PcStat;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.ExVoteSystemInfo;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.model.ShortCuts;
import com.l2jserver.gameserver.network.serverpackets.ShortCutRegister;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;
import com.l2jserver.util.L2Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Pater
 */
public class Helper implements IVoicedCommandHandler {


    private static final String[] _voicedCommands = {
        "helper"
    };


    private String  html;
    private String  param;
    private String  view;
    private L2PcInstance activeChar;

    // Cache
    private static boolean Properties = false;

    private static HashMap<String, String>    Html         = new HashMap<>();
    private static HashMap<String, String>    Consumable   = new HashMap<>();
    private static HashMap<Integer, String[]> SkillBuffs   = new HashMap<>();
    private static HashMap<Integer, String[]> SkillSongs   = new HashMap<>();
    private static HashMap<Integer, String[]> SkillDances  = new HashMap<>();
    private static HashMap<Integer, String[]> SkillSpecial = new HashMap<>();

    private static boolean L2Helper;
    private static boolean L2HelperIsCombat;
    private static boolean L2HelperIsPeace;
    private static boolean L2HelperIsCraft;
    private static Integer L2HelperServer;
    private static boolean L2HelperPrice;
    private static double  L2HelperPriceRate;
    private static boolean L2HelperLevelUp;
    private static boolean L2HelperLevelDown;
    private static boolean L2HelperVitaltity;
    private static boolean L2HelperRecommend;

    private static boolean L2HelperEnchant;
    private static Integer L2HelperEnchantMax;
    private static Integer L2HelperEnchantItem;
    private static Long    L2HelperEnchantItemCount;
    private static boolean L2HelperEnchantWeapon;
    private static boolean L2HelperEnchantArmor;
    private static boolean L2HelperEnchantJewels;
    private static boolean L2HelperEnchantOther;
    //private static boolean L2HelperEnchantAttribute;

    private static boolean L2HelperNewbie;
    private static Integer L2HelperNewbieStartLevel;
    private static boolean L2HelperNewbieGetAllSkill;
    private static boolean L2HelperNewbieGetProtection;
    private static boolean L2HelperNewbieSupport;
    private static Long    L2HelperNewbieSupportAdena;
    private static boolean L2HelperNewbieSetNoble;
    private static String  L2HelperNewbieSetTitle;
    private static String  L2HelperNewbieSetNameColor;
    private static String  L2HelperNewbieSetTitleColor;

    private static boolean L2HelperBuffer;
    private static boolean L2HelperBufferRecharge;
    private static boolean L2HelperBufferBuffs;
    private static boolean L2HelperBufferDS;
    private static boolean L2HelperBufferSpecial;


    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
    {
        if(activeChar == null) {
            return true;
        }

        if(Properties == false)
        {
            getProperties();
        }

        if (L2Helper == true && command.equals("helper"))
        {
            this.html       = null;
            this.view       = "view";
            this.activeChar = activeChar;

            if(this.Check() == false)
            {
                return true;
            }

            if (params != null)
            {
                this.param = params;
                String[] prm = params.split(" ");
                if (prm[1] != null)
                {
                    this.view = prm[1];
                }
            }

            this.getCommand();

            if(this.html != null)
            {
                activeChar.sendPacket(new NpcHtmlMessage(1, this.html));
            }
        }
        else
        {
            activeChar.sendMessage("L2Helper not allowed!");
        }

        return true;
    }


    /*
     * Core
     */
    private void getCommand()
    {
        if((L2HelperNewbie == true) && (this.activeChar.getNewbie() == 1) && (this.activeChar.getClassId().level() == 0))
        {
            this.Nwb();
        }

        switch (this.view)
        {
            case "info":
                this.getInfo();
                break;

            case "level":
                this.getDefault();
                this.setLevel(1);
                break;

            case "delevel":
                this.getDefault();
                this.setLevel(0);
                break;

            case "vitality":
                this.getDefault();
                this.setVitality();
                break;

            case "rec":
                this.getDefault();
                this.setRec();
                break;

            case "ench":
                this.getEnchant();
                this.setEnchant();
                break;

            case "buffer":
                this.getBuffer();
                this.Buff();
                break;

            case "recharge":
                this.getDefault();
                this.BuffRecharge();
                break;

            default:
                this.getDefault();
                break;
        }
    }
    private void getProperties()
    {
        try
        {
            L2Properties L2HelperProperties = new L2Properties();
            //Config.DATAPACK_ROOT
            final File l2helper = new File("./config/L2Helper.properties");

            try (InputStream is = new FileInputStream(l2helper))
            {
                L2HelperProperties.load(is);
            }
            catch (Exception e)
            {
                _log.log(Level.SEVERE, "Error while loading L2Helper.properties settings file!", e);
            }

            L2Helper = Boolean.parseBoolean(L2HelperProperties.getProperty("L2Helper", "True"));
            L2HelperIsCombat = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperIsCombat", "False"));
            L2HelperIsPeace = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperIsPeace", "False"));
            L2HelperIsCombat = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperIsCraft", "False"));

            L2HelperServer = Integer.parseInt(L2HelperProperties.getProperty("L2HelperServer", "0"));
            L2HelperPrice = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperPrice", "False"));
            L2HelperPriceRate = Double.parseDouble(L2HelperProperties.getProperty("L2HelperPriceRate", "1.0"));

            L2HelperLevelDown = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperLevelDown", "True"));
            Consumable.put("L2HelperLevelDownItem", L2HelperProperties.getProperty("L2HelperLevelDownItem", "57"));
            Consumable.put("L2HelperLevelDownItemCount", L2HelperProperties.getProperty("L2HelperLevelDownItemCount", "10000"));

            L2HelperLevelUp = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperLevelDown", "True"));
            Consumable.put("L2HelperLevelUpItem", L2HelperProperties.getProperty("L2HelperLevelUpItem", "5575"));
            Consumable.put("L2HelperLevelUpItemCount", L2HelperProperties.getProperty("L2HelperLevelUpItemCount", "1000000000"));

            L2HelperVitaltity = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperLevelDown", "True"));
            Consumable.put("L2HelperVitalityItem", L2HelperProperties.getProperty("L2HelperVitalityItem", "5575"));
            Consumable.put("L2HelperVitalityItemCount", L2HelperProperties.getProperty("L2HelperVitalityItemCount", "10000"));

            L2HelperRecommend = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperLevelDown", "True"));
            Consumable.put("L2HelperRecommendItem", L2HelperProperties.getProperty("L2HelperRecommendItem", "5575"));
            Consumable.put("L2HelperRecommendItemCount", L2HelperProperties.getProperty("L2HelperRecommendItemCount", "10000"));

            L2HelperEnchant = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchant", "True"));
            L2HelperEnchantMax = Integer.parseInt(L2HelperProperties.getProperty("L2HelperEnchantMax", "65535"));
            L2HelperEnchantItem = Integer.parseInt(L2HelperProperties.getProperty("L2HelperEnchantItem", "5575"));
            L2HelperEnchantItemCount = Long.parseLong(L2HelperProperties.getProperty("L2HelperEnchantItemCount", "1000000000"));
            L2HelperEnchantWeapon = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchantWeapon", "True"));
            L2HelperEnchantArmor = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchantArmor", "True"));
            L2HelperEnchantJewels = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchantJewels", "True"));
            L2HelperEnchantOther = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchantOther", "True"));
            //L2HelperEnchantAttribute = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperEnchantAttribute", "True"));

            L2HelperNewbie = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperNewbie", "True"));
            L2HelperNewbieStartLevel = Integer.parseInt(L2HelperProperties.getProperty("L2HelperNewbieStartLevel", "0"));
            L2HelperNewbieGetAllSkill = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperNewbieGetAllSkill", "False"));
            L2HelperNewbieGetProtection = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperNewbieGetProtection", "False"));
            L2HelperNewbieSupport = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperNewbieSupport", "True"));
            L2HelperNewbieSupportAdena = Long.parseLong(L2HelperProperties.getProperty("L2HelperNewbieSupportAdena", "0"));
            L2HelperNewbieSetNoble = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperNewbieSetNoble", "False"));
            L2HelperNewbieSetTitle = L2HelperProperties.getProperty("L2HelperNewbieSetTitle", "Use voice .helper");
            L2HelperNewbieSetNameColor = L2HelperProperties.getProperty("L2HelperNewbieSetNameColor", "");
            L2HelperNewbieSetTitleColor = L2HelperProperties.getProperty("L2HelperNewbieSetTitleColor", "");

            L2HelperBuffer = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperBuffer", "True"));
            L2HelperBufferRecharge = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperBufferRecharge", "True"));
            Consumable.put("L2HelperBufferItemsById", L2HelperProperties.getProperty("L2HelperBufferItemsById", "57"));
            Consumable.put("L2HelperBufferItemPriceCount", L2HelperProperties.getProperty("L2HelperBufferItemPriceCount", "5000"));
            L2HelperBufferBuffs = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperBufferBuffs", "True"));
            Consumable.put("L2HelperBufferItemsByIdBuffs", L2HelperProperties.getProperty("L2HelperBufferItemsByIdBuffs", "57"));
            Consumable.put("L2HelperBufferItemPriceCountBuffs", L2HelperProperties.getProperty("L2HelperBufferItemPriceCountBuffs", "10000"));
            L2HelperBufferDS = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperBufferDS", "True"));
            Consumable.put("L2HelperBufferItemsByIdDS", L2HelperProperties.getProperty("L2HelperBufferItemsByIdDS", "5575"));
            Consumable.put("L2HelperBufferItemPriceCountDS", L2HelperProperties.getProperty("L2HelperBufferItemPriceCountDS", "5000"));
            L2HelperBufferSpecial = Boolean.parseBoolean(L2HelperProperties.getProperty("L2HelperBufferSpecial", "True"));
            Consumable.put("L2HelperBufferItemsByIdSpecial", L2HelperProperties.getProperty("L2HelperBufferItemsByIdSpecial", "5575"));
            Consumable.put("L2HelperBufferItemPriceCountSpecial", L2HelperProperties.getProperty("L2HelperBufferItemPriceCountSpecial", "5000"));

            this.getPropertiesRate();

            this.getPropertiesCache();

            Properties = true;

        }
        catch (Exception e)
        {
            _log.log(Level.SEVERE, "Error while loading L2Helper.properties settings complete!", e);
        }

    }
    private void getPropertiesRate()
    {
        if(L2HelperPrice == true) {

            Double Count;
            Long   Save;

            if(L2HelperServer != 0)
            {
                switch(L2HelperServer)
                {
                    case 1: // Low rate
                        break;
                    case 2: // Midle rate
                        break;
                    case 3: // High rate
                        break;
                }
            }

            if(L2HelperPriceRate != 1.0)
            {
                // Level
                Count = Double.parseDouble(Consumable.get("L2HelperLevelUpItemCount"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperLevelUpItemCount", Long.toString(Save));
                }

                // Delevel
                Count = Double.parseDouble(Consumable.get("L2HelperLevelDownItemCount"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperLevelDownItemCount", Long.toString(Save));
                }

                // Vitatlity
                Count = Double.parseDouble(Consumable.get("L2HelperVitalityItemCount"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperVitalityItemCount", Long.toString(Save));
                }

                // Recommend
                Count = Double.parseDouble(Consumable.get("L2HelperRecommendItemCount"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperRecommendItemCount", Long.toString(Save));
                }

                // Newbie Support Adena
                Count = Double.parseDouble(Long.toString(L2HelperNewbieSupportAdena));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    L2HelperNewbieSupportAdena = (long)Math.abs(Count);
                }

                // Buffer Recharge
                Count = Double.parseDouble(Consumable.get("L2HelperBufferItemPriceCount"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperBufferItemPriceCount", Long.toString(Save));
                }

                // Buffer Buffs
                Count = Double.parseDouble(Consumable.get("L2HelperBufferItemPriceCountBuffs"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperBufferItemPriceCountBuffs", Long.toString(Save));
                }

                // Buffer Song & Dance
                Count = Double.parseDouble(Consumable.get("L2HelperBufferItemPriceCountDS"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperBufferItemPriceCountDS", Long.toString(Save));
                }

                // Buffer Special
                Count = Double.parseDouble(Consumable.get("L2HelperBufferItemPriceCountSpecial"));
                if(L2HelperPriceRate > 1.0) {
                    Count = (Count * L2HelperPriceRate);
                } else {
                    Count = (Count / L2HelperPriceRate);
                }
                if(Count > 1) {
                    Save = (long)Math.abs(Count);
                    Consumable.put("L2HelperBufferItemPriceCountSpecial", Long.toString(Save));
                }
            }
        }
    }
    private void getPropertiesCache()
    {
        /*
         * [ID] = Id,Level,Name,Description,Icon
         */

        /* Songs */
        SkillSongs.put(264, new String[] {"1","title","Increases the P. Def. of all party members by 25%.","icon.skill0264"});
        SkillSongs.put(265, new String[] {"1","title","Increases HP Regeneration of all party members by 20%.","icon.skill0265"});
        SkillSongs.put(266, new String[] {"1","title","Increases the evasion of all party members by 3.","icon.skill0266"});
        SkillSongs.put(267, new String[] {"1","title","Increases the M. Def. of all party members by 30%.","icon.skill0267"});
        SkillSongs.put(268, new String[] {"1","title","Increases the movement speed of all party members by 20.","icon.skill0268"});
        SkillSongs.put(269, new String[] {"1","title","Increases the critical attack rate of all party members by 100%.","icon.skill0269"});
        SkillSongs.put(270, new String[] {"1","title","Increases the resistance of all party members to Dark attacks by 20.","icon.skill0270"});
        SkillSongs.put(304, new String[] {"1","title","Increases Max HP of all party members by 30%.","icon.skill0304"});
        SkillSongs.put(305, new String[] {"1","title","Gives a party member the ability to transfer 20% of received standard short-range damage back to the enemy.","icon.skill0305"});
        SkillSongs.put(306, new String[] {"1","title","Increases party members resistance to fire attacks by 30.","icon.skill0306"});
        SkillSongs.put(308, new String[] {"1","title","Increases party members resistance to wind attacks by 30.","icon.skill0308"});
        SkillSongs.put(349, new String[] {"1","title","Decreases all party members physical&amp;magic skill MP consumption by 5% and re-use time by 20%.","icon.skill0349"});
        SkillSongs.put(363, new String[] {"1","title","Increases all party members MP recovery bonus by 20%, and decreases magic skill use MP consumption by 10%.","icon.skill0363"});
        SkillSongs.put(364, new String[] {"1","title","Decreases all party members physical skill use and song&amp;dance skill use MP consumption by 20% and physical skill and song&amp;dance skill reuse time by 10%.","icon.skill0364"});
        SkillSongs.put(529, new String[] {"1","title","Increases all party members resistance to fire, water, wind and earth attacks by 30.","icon.skill0529"});
        SkillSongs.put(764, new String[] {"1","title","Increases a party members tolerance to bows by 30.","icon.skill0764"});

        /* Dances */
        SkillDances.put(271, new String[] {"1","title","Increases the P. Atk. of all party members by 12%.","icon.skill0271"});
        SkillDances.put(272, new String[] {"1","title","Increases the accuracy of all party members by 4.","icon.skill0272"});
        SkillDances.put(273, new String[] {"1","title","Increases the M. Atk. of all party members by 20%.","icon.skill0273"});
        SkillDances.put(274, new String[] {"1","title","Increases the critical attack power of all party members by 35%.","icon.skill0274"});
        SkillDances.put(275, new String[] {"1","title","Increases the Atk. Spd. of all party members by 15%.","icon.skill0275"});
        SkillDances.put(276, new String[] {"1","title","Decreases the magic-canceling damage of all party members by 40 and increases Casting Spd. by 30%.","icon.skill0276"});
        SkillDances.put(277, new String[] {"1","title","Increases the divine attribute P. Atk. of all party members by 20.","icon.skill0277"});
        SkillDances.put(307, new String[] {"1","title","Increases party members resistance to water attacks by 30.","icon.skill0307"});
        SkillDances.put(309, new String[] {"1","title","Increases party members resistance to earth attacks by 30.","icon.skill0309"});
        SkillDances.put(310, new String[] {"1","title","Gives all party members the ability to recover as HP 8% of any standard short-range physical damage inflicted on the enemy.","icon.skill0310"});
        SkillDances.put(311, new String[] {"1","title","Gives all party members the ability to decrease by 30 any environment-related damage received.","icon.skill0311"});
        SkillDances.put(365, new String[] {"1","title","Increases all party members damage magic critical attack rate by 100%.","icon.skill0365"});
        SkillDances.put(366, new String[] {"1","title","Decreases all party members movement speed by 50% and prevents them from being pre-emptively attacked by monsters.","icon.skill0366"});
        SkillDances.put(530, new String[] {"1","title","Increases all party members resistance to divine or Dark attacks by 30.","icon.skill0530"});
        SkillDances.put(915, new String[] {"1","title","Decreases a party members P. Def., M. Def. and evasion, and increases their P. Atk., M. Atk., Atk. Spd., Casting Spd. and movement speed.","icon.skill0915"});

        /* Buffs */
        SkillBuffs.put(1002, new String[] {"3","title","Increases Casting Spd. of all party members by 30%.","icon.skill1002"});
        SkillBuffs.put(1003, new String[] {"3","title","Increases the P. Atk. of nearby clan members by 15%.","icon.skill1003"});
        SkillBuffs.put(1004, new String[] {"3","title","Increases the Casting Spd. of nearby clan members by 30%.","icon.skill1004"});
        SkillBuffs.put(1005, new String[] {"3","title","Increases the P. Def. of nearby clan members by 15%.","icon.skill1005"});
        SkillBuffs.put(1006, new String[] {"3","title","Increases the M. Def. of all party members by 30%.","icon.skill1006"});
        SkillBuffs.put(1007, new String[] {"3","title","Increases the P. Atk. of all party members by 15%.","icon.skill1007"});
        SkillBuffs.put(1008, new String[] {"3","title","Increases the M. Def. of nearby clan members by 30%.","icon.skill1008"});
        SkillBuffs.put(1009, new String[] {"3","title","Increases the P. Def. of all party members by 15%.","icon.skill1009"});
        SkillBuffs.put(1032, new String[] {"3","title","Increases resistance to bleed attacks by 50%.","icon.skill1032"});
        SkillBuffs.put(1033, new String[] {"3","title","Increases resistance to poison attacks by 50%.","icon.skill1033"});
        SkillBuffs.put(1035, new String[] {"4","title","Increases resistance to hold, sleep and mental attacks by 50.","icon.skill1035"});
        SkillBuffs.put(1036, new String[] {"2","title","Increases M. Def. by 30%.","icon.skill1036"});
        SkillBuffs.put(1040, new String[] {"3","title","Increases P. Def. by 15%.","icon.skill1040"});
        SkillBuffs.put(1043, new String[] {"1","title","Increases a party members divine attribute P. Atk. by 20.","icon.skill1043"});
        SkillBuffs.put(1044, new String[] {"3","title","Increases HP Regeneration by 20%.","icon.skill1044"});
        SkillBuffs.put(1045, new String[] {"6","title","Increases Max HP by 35%.","icon.skill1045"});
        SkillBuffs.put(1048, new String[] {"6","title","Increases Max MP by 35%.","icon.skill1048"});
        SkillBuffs.put(1059, new String[] {"3","title","Increases M. Atk. by 75%.","icon.skill1059"});
        SkillBuffs.put(1062, new String[] {"2","title","Decreases a party members P. Def. by 8%, M. Def. by 16% and evasion by 4, and increases P. Atk. by 8%, M. Atk. by 16%, Atk. Spd. by 8%, Casting Spd. by 8%, and movement speed by 8.","icon.skill1062"});
        SkillBuffs.put(1068, new String[] {"3","title","Increases P. Atk. by 15%.","icon.skill1068"});
        SkillBuffs.put(1073, new String[] {"2","title","Increases lung capacity by 600%.","icon.skill1073"});
        SkillBuffs.put(1077, new String[] {"3","title","Increases critical attack rate by 30%.","icon.skill1077"});
        SkillBuffs.put(1078, new String[] {"6","title","Decreases magic cancel damage by 53.","icon.skill1078"});
        SkillBuffs.put(1085, new String[] {"3","title","Increases Casting Spd. by 30%.","icon.skill1085"});
        SkillBuffs.put(1086, new String[] {"2","title","Increases Atk. Spd. by 33%.","icon.skill1086"});
        SkillBuffs.put(1087, new String[] {"3","title","Increases evasion by 4.","icon.skill1087"});
        SkillBuffs.put(1182, new String[] {"3","title","Increases resistance to water attacks by 20.","icon.skill1182"});
        SkillBuffs.put(1189, new String[] {"3","title","Increases resistance to wind attacks by 20.","icon.skill1189"});
        SkillBuffs.put(1191, new String[] {"3","title","Increases resistance to fire attacks by 20.","icon.skill1191"});
        SkillBuffs.put(1204, new String[] {"2","title","Increases movement speed by 33.","icon.skill1204"});
        SkillBuffs.put(1229, new String[] {"18","title","Recovers 58 HP per second for all party members for 15 seconds.","icon.skill1229"});
        SkillBuffs.put(1240, new String[] {"3","title","Increases accuracy by 4.","icon.skill1240"});
        SkillBuffs.put(1242, new String[] {"3","title","Increases critical attack power by 35%.","icon.skill1242"});
        SkillBuffs.put(1243, new String[] {"6","title","Increases shield defense rate by 30%.","icon.skill1243"});
        SkillBuffs.put(1249, new String[] {"3","title","Increases the accuracy of nearby clan members by 4.","icon.skill1249"});
        SkillBuffs.put(1250, new String[] {"3","title","Increases the shield defense rate of nearby clan members by 50%. Effect 1.","icon.skill1250"});
        SkillBuffs.put(1251, new String[] {"2","title","Increases the Atk. Spd. of all party members by 33%.","icon.skill1251"});
        SkillBuffs.put(1252, new String[] {"3","title","Increases the evasion of all party members by 4.","icon.skill1252"});
        SkillBuffs.put(1253, new String[] {"3","title","Increases the critical attack power of all party members by 35%.","icon.skill1253"});
        SkillBuffs.put(1257, new String[] {"3","title","Increases the weight penalty interval by 9000.","icon.skill1257"});
        SkillBuffs.put(1260, new String[] {"3","title","Increases the evasion of nearby clan members by 4.","icon.skill1260"});
        SkillBuffs.put(1261, new String[] {"2","title","Decreases nearby clan members P. Def. by 8%, M. Def. by 16%, and evasion by 4, and increases P. Atk. by 8%, M. Atk. by 16%, Atk. Spd. by 8%, Casting Spd. by 8%, and moving speed by 8.","icon.skill1261"});
        SkillBuffs.put(1268, new String[] {"4","title","9% of the standard short-range physical damage inflicted on the enemy is recovered as HP.","icon.skill1268"});
        SkillBuffs.put(1282, new String[] {"2","title","Increases the movement speed of nearby clan members by 33.","icon.skill1282"});
        SkillBuffs.put(1284, new String[] {"3","title","Gives a party member the ability to transfer 20% of received standard short-range damage back to the enemy.","icon.skill1284"});
        SkillBuffs.put(1303, new String[] {"2","title","Increases by 2 the damage rate of magic.","icon.skill1303"});
        SkillBuffs.put(1304, new String[] {"3","title","Increases shield P. Def. by 50%.","icon.skill1304"});
        SkillBuffs.put(1307, new String[] {"3","title","Increases the power of HP recovery magic received by all party members by 12%.","icon.skill1307"});
        SkillBuffs.put(1308, new String[] {"3","title","Increases the critical attack rate of all party members by 30%.","icon.skill1308"});
        SkillBuffs.put(1309, new String[] {"3","title","Increases the accuracy of all party members by 4.","icon.skill1309"});
        SkillBuffs.put(1310, new String[] {"4","title","Gives all party members the ability to recover as HP 9% of any standard short-range physical damage inflicted on the enemy.","icon.skill1310"});
        SkillBuffs.put(1311, new String[] {"6","title","Restores HP of all party members by 35% and increases Max HP by 35%.","icon.skill1311"});
        SkillBuffs.put(1352, new String[] {"1","title","Increases resistance to fire, water, wind and earth attacks.","icon.skill1352"});
        SkillBuffs.put(1353, new String[] {"1","title","Increases resistance to Dark attacks by 30 and resistance to divine attacks by 20.","icon.skill1353"});
        SkillBuffs.put(1354, new String[] {"1","title","Increases resistance to buff-canceling attacks by 30 and resistance to de-buff attacks by 20.","icon.skill1354"});
        SkillBuffs.put(1362, new String[] {"1","title","Increases all party members resistance to buff-canceling attacks by 30 and resistance to de-buff attacks by 20.","icon.skill1362"});
        SkillBuffs.put(1363, new String[] {"1","title","Recovers all party members HP by 20%, receives help from a great spirit to increase Max HP by 20%, damage magic critical attack power by 2, critical attack power by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, resistance to de-buffs by 10%, and accuracy by 4. Decreases movement speed by 20%. Consumes 40 Spirit Ores.","icon.skill1363"});
        SkillBuffs.put(1364, new String[] {"1","title","Increases nearby clan members accuracy by 4 and decreases the rate of being hit by a critical attack by 30%.","icon.skill1364"});
        SkillBuffs.put(1365, new String[] {"1","title","Increases nearby clan members M. Atk. by 75% and M. Def. by 30%.","icon.skill1365"});
        SkillBuffs.put(1388, new String[] {"3","title","Increases P. Atk. by 10%. Consumes 3 Spirit Ores.","icon.skill1388"});
        SkillBuffs.put(1389, new String[] {"3","title","Increases P. Def. by 15%. Consumes 3 Spirit Ores.","icon.skill1389"});
        SkillBuffs.put(1392, new String[] {"3","title","Increases resistance to divine attacks by 30.","icon.skill1392"});
        SkillBuffs.put(1393, new String[] {"3","title","Increases resistance to Dark attacks by 30.","icon.skill1393"});
        SkillBuffs.put(1413, new String[] {"1","title","A powerful spirit acts to increase Max MP of all party members by 15%, MP recovery bonus by 1.5 when wearing light or heavy armor, MP recovery bonus by 4 when wearing a robe, M. Def. by 30%, M. Atk. by 30%, Casting Spd. by 20%, resistance to fire, water, wind and earth damage by 10, resistance to de-buff attacks by 25, and resistance to buff-canceling attacks by 40. Consumes 40 Spirit Ores.","icon.skill1413"});
        SkillBuffs.put(1415, new String[] {"1","title","Increases nearby clan members resistance to buff-canceling attacks by 30% and resistance to de-buff attacks by 20%.","icon.skill1415"});
        SkillBuffs.put(1460, new String[] {"1","title","increases the recharge power received by the target by 85.","icon.skill1460"});
        SkillBuffs.put(1461, new String[] {"1","title","Decreases the critical damage received by a party member by 30%.","icon.skill1461"});
        SkillBuffs.put(1519, new String[] {"1","title","Combines party members general attack damage absorption and Atk. Spd. increase to have a more advanced blood awakening effect. Increases Atk. Spd. by 33% and bestows the ability to recover as HP 9% of the standard short-range physical damage inflicted on the enemy.","icon.skill1519"});
        SkillBuffs.put(1536, new String[] {"1","title","Combines a party&clan members P. Atk. increase and P. Def. increase to have more advanced combat power increase effect. Increases P. Atk. by 15% and P. Def. by 15%.","icon.skill1536"});
        SkillBuffs.put(1537, new String[] {"1","title","Combines a party&clan members critical rate increase and critical power increase effects for a more advanced critical increase effect. Increases critical rate by 30% and critical power by 35%.","icon.skill1537"});
        SkillBuffs.put(1538, new String[] {"1","title","Combines a party&clan members maximum HP increase and maximum MP increase effects for more advanced mental and physical power. Increases Max HP by 35% and Max MP by 35%.","icon.skill1538"});
        SkillBuffs.put(1542, new String[] {"1","title","Increases the targets P. Def. against Critical by 30%. When the target receives an attack above a certain amount of damage, the critical damage of General Short-Range Physical Attack is increased for 8 seconds.","icon.skill1542"});

        /* Special */
        SkillSpecial.put(499, new String[] {"3","title","Increases resistance to altered state mind attacks by 100.","icon.skill0499"});
        SkillSpecial.put(825, new String[] {"1","title","Sharpens a bladed weapon to increase P. Atk. by 5% and critical rate by 20%.","icon.skill0825"});
        SkillSpecial.put(826, new String[] {"1","title","Adds a spike to a blunt weapon to increase P. Atk. by 5% and its weight for shock attack by 8%.","icon.skill0826"});
        SkillSpecial.put(827, new String[] {"1","title","Enhances the string of a bow or crossbow to increase P. Atk. by 5% and range by 100.","icon.skill0827"});
        SkillSpecial.put(828, new String[] {"1","title","Enhances the armor surface to increase P. Def. by 10%.","icon.skill0828"});
        SkillSpecial.put(829, new String[] {"1","title","Tans armor to increase P. Def. by 5% and evasion by 2. Works only on light armor users and cant be used on pets.","icon.skill0829"});
        SkillSpecial.put(830, new String[] {"1","title","Embroiders a robe to increase P. Def. by 5% and MP recovery speed by 2.","icon.skill0830"});
        SkillSpecial.put(1182, new String[] {"330","title","Increases the effectiveness by 4.<br1>Increases resistance to water attacks by 106. Enchant Power: Increases effectiveness.","icon.skill1182"});
        SkillSpecial.put(1189, new String[] {"330","title","Increases effectiveness by 4.<br1>Increases resistance to wind attacks by 106. Enchant Power: Increases effectiveness.","icon.skill1189"});
        SkillSpecial.put(1191, new String[] {"330","title","Increases effectiveness by 5.<br1>Increases resistance to fire attacks by 111. Enchant Power: Increases effectiveness.","icon.skill1191"});
        SkillSpecial.put(1232, new String[] {"330","title","For 20 minutes, transfers 20% of the target's received standard short-range damage back to the enemy. Enchant Defense: Increases P. Def. by 10%.","icon.skill1232"});
        SkillSpecial.put(1259, new String[] {"330","title","Increases Resistance to Stun attacks by 60% for 20 minutes. Enchant Power: Increases Power.","icon.skill1259"});
        SkillSpecial.put(1323, new String[] {"1","title","Maintains targets buff&de-buff condition even following death and resurrection. The Blessing of Noblesse and the Amulet of Luck disappear, however. Consumes 5 Spirit Ores.","icon.skill1323"});
        SkillSpecial.put(1355, new String[] {"315","title","For 5 minutes, a powerful spirit acts to increase the damage caused by the targeted party member's magic damage by 20%, MP Recovery Bonus by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, and Resistance to de-buffs by 10%. MP consumption for skill use is decreased by 5%. Consumes 10 Spirit Ore. Enchant Decrease Penalty: Eases the skill's restrictions.","icon.skill1355"});
        SkillSpecial.put(1356, new String[] {"315","title","For 5 minutes, a powerful spirit acts to increase a party member's Max MP by 20%, HP Recovery Bonus by 20%, magic damage by 2, Critical Damages by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, and Resistance to de-buffs by 10%. Consumes 10 Spirit Ore. Enchant Decrease Penalty: Eases the skill's restrictions.","icon.skill1356"});
        SkillSpecial.put(1357, new String[] {"315","title","For 5 minutes, a powerful spirit acts to increase a party member's Max HP by 20%, Critical Rate by 20%, magic damage by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, and Resistance to de-buff by 10%. Bestows the ability to recover as HP 5% of standard melee damage inflicted on the enemy. Consumes 10 Spirit Ore. Enchant Decrease Penalty: Eases the skill's restrictions.","icon.skill1357"});
        SkillSpecial.put(1363, new String[] {"315","title","Movement speed consumption penalty decreases by 1%.<br1>Restores 20% of all party members HP. A powerful spirit acts to increase a party members Max HP by 20%, damage magic critical attack power by 2, critical attack power by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, resistance to de-buffs by 10%, and accuracy by 4. Consumes 40 Spirit Ores. Enchant Decrease Penalty: Movement speed consumption penalty decreases.","icon.skill1363"});
        SkillSpecial.put(1374, new String[] {"1","title","For 2 minutes, increases nearby clan members' P. Atk. by 250, P. Def. by 500, and Resistance to buff-canceling attacks by 40. Consumes 80 Spirit Ore.","icon.skill1374"});
        SkillSpecial.put(1392, new String[] {"130","title","Increases divine resistance by 4.<br1>Increases resistance to divine attacks by 110. Enchant Power: Increases divine resistance.","icon.skill1392"});
        SkillSpecial.put(1393, new String[] {"130","title","Increases Dark resistance by 4.<br1>Increases resistance to Dark attacks by 110. Enchant Power: Power of the skill is increased.","icon.skill1393"});
        SkillSpecial.put(1397, new String[] {"230","title","For 20 minutes, decreases physical skill MP consumption by 20%, magic skill MP consumption by 15%, and song/dance skill MP consumption by 20%. Consumes 3 Spirit Ore. Enchant Power: Decreases MP consumption for magical skills.","icon.skill1397"});
        SkillSpecial.put(1414, new String[] {"315","title","For 5 minutes, a powerful spirit acts to increase nearby clan members' Max CP by 20%, CP recovery bonus by 20%, Max MP by 20%, Critical Rate by 20%, magic damage by 20%, P. Atk. by 10%, P. Def. by 20%, Atk. Spd. by 20%, M. Atk. by 20%, M. Def. by 20%, Casting Spd. by 20%, and Resistance to de-buffs by 10%. Consumes 40 Spirit Ore. Enchant Decrease Penalty: Eases the skill's restrictions.","icon.skill1414"});
        SkillSpecial.put(1416, new String[] {"115","title","Regenerates nearby clan members' CP by 950 and increases Max CP by 950 for 5 minutes. Consumes 20 Spirit Ore. Enchant Power: Increases Max CP.","icon.skill1416"});
        SkillSpecial.put(1470, new String[] {"1","title","Increases the M. Def. of all party members by 3000 for 30 seconds.","icon.skill1470"});
        SkillSpecial.put(1476, new String[] {"3","title","Awakens a party members destructive instincts and increases P. Atk., critical attack rate and critical damage by 50%.","icon.skill1476"});
        SkillSpecial.put(1477, new String[] {"3","title","Stirs up the bloodsucking urge and gives all party members the ability to recover as HP 80% of the standard short-range physical damage inflicted on the enemy.","icon.skill1477"});
        SkillSpecial.put(1478, new String[] {"2","title","Stirs up the defense instinct to increase M. Atk. by 1800 and M. Def. by 1350 for 15 seconds.","icon.skill1478"});
        SkillSpecial.put(1479, new String[] {"3","title","Stirs up the magic urge to increase M. Atk. by 95%, Casting Spd. by 15%, and the rate of Prominent Damage occurring with damage magic by 300% for 15 seconds.","icon.skill1479"});
        SkillSpecial.put(1499, new String[] {"1","title","Combines P. Atk. increase and P. Def. increase to have more advanced combat power increase effect. Increases P. Atk. by 15% and P. Def. by 15%.","icon.skill1499"});
        SkillSpecial.put(1500, new String[] {"1","title","Combines magic power increase and magic resistance increase to have more advanced magic ability increase effect. Increases M. Atk. by 75% and M. Def. by 30%.","icon.skill1500"});
        SkillSpecial.put(1501, new String[] {"1","title","Combines maximum HP increase and maximum MP increase to have more advanced mental and physical power. Increases Max HP by 35% and Max MP by 35%.","icon.skill1501"});
        SkillSpecial.put(1502, new String[] {"1","title","Combines critical rate increase and critical power increase to have more advanced critical increase effect. Increases critical rate by 30% and critical power by 35%.","icon.skill1502"});
        SkillSpecial.put(1503, new String[] {"1","title","Combines shield Def. rate increase and shield P. Def. increase to have more advanced shield ability increase effect. Increases shield defense rate by 30% and shield P. Def. by 50%.","icon.skill1503"});
        SkillSpecial.put(1504, new String[] {"1","title","Combines Spd. increase and Evasion increase to have more advanced movement increase effect. Increases moving speed by 33 and evasion by 4.","icon.skill1504"});
        SkillSpecial.put(4699, new String[] {"13","title","Queens buff magic Temporarily increases your party members critical damage amount rate and power. Effect 3.","icon.skill1331"});
        SkillSpecial.put(4700, new String[] {"13","title","Queens buff magic temporarily increases party members P. Atk. and Accuracy. Effect 3.","icon.skill1331"});
        SkillSpecial.put(4702, new String[] {"13","title","Buff magic used by the Unicorn Seraphim. Party members MP regeneration bonus temporarily increased. Effect 3.","icon.skill1332"});
        SkillSpecial.put(4703, new String[] {"13","title","Unicorn Seraphims buff magic temporarily reduces party members magic skill recovery time. Effect 3.","icon.skill1332"});
    }
    private boolean Check()
    {
        if (L2HelperIsCombat == false && (this.activeChar.isInCombat() || this.activeChar.isInDuel() || this.activeChar.isInOlympiadMode()))
        {
            this.activeChar.sendMessage("Not use Helper in combat!");
            return false;
        }

        if (L2HelperIsPeace == true && this.activeChar.isInsideZone(ZoneId.PEACE) == false)
        {
            this.activeChar.sendMessage("Helper must use in peace zone.");
            return false;
        }

        if (L2HelperIsCraft == false && this.activeChar.isInCraftMode())
        {
            this.activeChar.sendMessage("Not use Helper in crafting!");
            return false;
        }

        return true;
    }

    /*
     * Html
     */
    private void getDefault()
    {
        this.html = "<html><body scroll=\"no\"><title>Lineage II Helper</title>";
        this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"L2UI_CH3.refinewnd_back_Pattern\"><tr><td valign=\"top\" align=\"center\">";
            this.html += "<br>";
            this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                this.html += "<tr>";
                this.html += "<td align=center><button action=\"bypass -h voice .helper view info\" value=\"Information\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                this.html += "</tr>";
            this.html += "</table>";
            if(L2HelperBuffer == true) {
                this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr>";
                    if(L2HelperBufferBuffs == true || L2HelperBufferDS == true || L2HelperBufferSpecial == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer\" value=\"Buffer\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    if(L2HelperBufferRecharge == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view recharge\" value=\"Recharge\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    this.html += "</tr>";
                this.html += "</table>";
            }
            this.html += "<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>For Premium User";
            if(this.activeChar.getSponsor() == 0) {
                this.html += " : You not Premium user.";
            }
            if(L2HelperLevelUp == true || L2HelperLevelDown == true || L2HelperVitaltity == true || L2HelperRecommend == true) {
            this.html += "<br><table border=0 cellpadding=0 cellspacing=0>";
                if(L2HelperLevelUp == true || L2HelperLevelDown == true) {
                    this.html += "<tr>";
                    if(L2HelperLevelUp == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view level\" value=\"Level +1\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    if(L2HelperLevelDown == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view delevel\" value=\"Delevel -1\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    this.html += "</tr>";
                }
                if(L2HelperVitaltity == true || L2HelperRecommend == true) {
                    this.html += "<tr>";
                    if(L2HelperVitaltity == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view vitality\" value=\"Vitality\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    if(L2HelperRecommend == true) {
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view rec\" value=\"Recommend\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else {
                        this.html += "<td></td>";
                    }
                    this.html += "</tr>";
                }
            this.html += "</table>";
            }
            if(L2HelperEnchant == true) {
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr>";
                    this.html += "<td align=center><button action=\"bypass -h voice .helper view ench\" value=\"Enchant\" width=260 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    this.html += "</tr>";
                this.html += "</table>";
            }
            this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
        this.html += "</td></tr></table>";
        this.html += "</body></html>";
    }
    private void getInfo()
    {
        this.html = HtmCache.getInstance().getHtm("en", "data/html/helper/helper.htm");
        if (this.html == null)
        {
            this.html = "<html><title>Lineage II Helper</title><body><br><br><center><font color=LEVEL>404:</font> File Not Found<br><br>data/html/helper/helper.htm</center></body></html>";
        }
    }
    private void getEnchant()
    {
        if(L2HelperEnchant == true)
        {
            String type = "";

            if(L2HelperEnchantWeapon == true) {
                type += "Weapon";
            }

            if(L2HelperEnchantArmor == true) {
                if(L2HelperEnchantWeapon == true) {
                    type += ";";
                }
                type += "Helmet;Chest;Leggings;Gloves;Boots;Shield";
            }

            if(L2HelperEnchantJewels == true) {
                if(L2HelperEnchantWeapon == true || L2HelperEnchantArmor == true) {
                    type += ";";
                }
                type += "Necklace;UpperEarring;LowerEarring;UpperRing;LowerRing";
            }

            if(L2HelperEnchantOther == true) {
                if(L2HelperEnchantWeapon == true || L2HelperEnchantArmor == true || L2HelperEnchantJewels == true) {
                    type += ";";
                }
                type += "Cloak;Shirt;Belt";
            }

            this.html = "<html><body scroll=\"no\"><title>Lineage II Helper</title>";
            this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"L2UI_CH3.refinewnd_back_Pattern\"><tr><td valign=\"top\" align=\"center\">";
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr><td width=256 height=80 background=\"L2UI_CT1.OlympiadWnd_DF_GrandTexture\"></td></tr>";
                this.html += "</table>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr>";
                    this.html += "<td align=center><combobox width=140 var=enchbox list="+type+"></td>";
                    this.html += "</tr>";
                    this.html += "<tr>";
                    this.html += "<td><br><br><br></td>";
                    this.html += "</tr>";
                    this.html += "<tr>";
                    this.html += "<td align=center><button action=\"bypass -h voice .helper view ench $enchbox\" value=\"Add Enchant\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    this.html += "</tr>";
                    this.html += "</tr>";
                this.html += "</table>";
                this.html += "<br><br><br><br><br><br><br><br><br><br><br>";
                this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr>";
                    this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    this.html += "</tr>";
                this.html += "</table>";
            this.html += "</td></tr></table>";
            this.html += "</body></html>";
        }
    }
    private void getBuffer()
    {
        if(L2HelperBuffer == true)
        {
            this.html = "<html><body><title>Lineage II Helper</title>";
            this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"L2UI_CH3.refinewnd_back_Pattern\"><tr><td valign=\"top\" align=\"center\">";
                this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                if(L2HelperBufferBuffs == true) {
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer h1\" value=\"Buffs\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                }
                if(L2HelperBufferDS == true) {
                    this.html += "<br><table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer h2\" value=\"Songs\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer h3\" value=\"Dances\" width=130 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                }
                if(L2HelperBufferSpecial == true) {
                    this.html += "<br><table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer h4\" value=\"Special\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                }
                if(L2HelperBufferRecharge == true) {
                    this.html += "<br><table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper view buffer r1\" value=\"Remove All Buffs\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                }

                this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    this.html += "<tr>";
                    this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    this.html += "</tr>";
                this.html += "</table>";
            this.html += "</td></tr></table>";
            this.html += "</body></html>";
        }
    }
    private void getBufferBuffs(String page)
    {
        if(L2HelperBufferBuffs == true)
        {
            String cahce = "getBufferBuffs";
            Integer pg   = 0;

            switch(page) {
                case "h1":
                case "h1-0":
                    cahce += "-0";
                    break;
                case "h1-1":
                    cahce += "-1";
                    pg = 1;
                    break;
                case "h1-2":
                    cahce += "-2";
                    pg = 2;
                    break;
            }

            if(Html.containsKey(cahce))
            {
                this.html = Html.get(cahce);
            }
            else
            {
                this.html = "<html><body><title>Lineage II Helper</title>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358><tr><td valign=\"top\" align=\"center\">";
                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    Integer i = 0;
                    for (Iterator<Integer> it = SkillBuffs.keySet().iterator(); it.hasNext();) {
                        Integer key = it.next();
                        if(pg == 0 && i >= 0 && i < 24) {
                            String[] skill = SkillBuffs.get(key);
                            this.html += "<tr>";
                            this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                            this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h1 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                            this.html += "</tr>";
                        } else if(pg == 1 && i >= 24 && i < 48) {
                            String[] skill = SkillBuffs.get(key);
                            this.html += "<tr>";
                            this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                            this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h1 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                            this.html += "</tr>";
                        } else if(pg == 2 && i >= 48 && i < 75) {
                            String[] skill = SkillBuffs.get(key);
                            this.html += "<tr>";
                            this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                            this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h1 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                            this.html += "</tr>";
                        }
                        i++;
                    }
                    this.html += "</table><br>";

                    this.html += "<table border=0 cellpadding=0 cellspacing=0><tr>";
                    if(pg == 0) {
                        this.html += "<td></td>";
                        this.html += "<td align=\"right\" width=\"100\"><button action=\"bypass -h voice .helper view buffer h1-1\" value=\"Next\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else if(pg == 1) {
                        this.html += "<td align=\"right\" width=\"100\"><button action=\"bypass -h voice .helper view buffer h1-0\" value=\"Preview\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "<td align=\"right\" width=\"100\"><button action=\"bypass -h voice .helper view buffer h1-2\" value=\"Next\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                    } else if(pg == 2) {
                        this.html += "<td align=\"right\" width=\"100\"><button action=\"bypass -h voice .helper view buffer h1-1\" value=\"Preview\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "<td></td>";
                    }
                    this.html += "</tr></table>";

                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                this.html += "</td></tr></table>";
                this.html += "</body></html>";

                Html.put(cahce, this.html);
            }
        }
    }
    private void getBufferSongs()
    {
        if(L2HelperBufferDS == true)
        {
            if(Html.containsKey("getBufferSongs"))
            {
                this.html = Html.get("getBufferSongs");
            }
            else
            {
                this.html = "<html><body><title>Lineage II Helper</title>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358><tr><td valign=\"top\" align=\"center\">";
                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    for (Iterator<Integer> it = SkillSongs.keySet().iterator(); it.hasNext();) {
                        Integer key = it.next();
                        String[] skill = SkillSongs.get(key);
                        this.html += "<tr>";
                        this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                        this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h2 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    }
                    this.html += "</table>";

                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                this.html += "</td></tr></table>";
                this.html += "</body></html>";

                Html.put("getBufferSongs", this.html);
            }
        }
    }
    private void getBufferDances()
    {
        if(L2HelperBufferDS == true)
        {
            if(Html.containsKey("getBufferDances"))
            {
                this.html = Html.get("getBufferDances");
            }
            else
            {
                this.html = "<html><body><title>Lineage II Helper</title>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358><tr><td valign=\"top\" align=\"center\">";
                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    for (Iterator<Integer> it = SkillDances.keySet().iterator(); it.hasNext();) {
                        Integer key = it.next();
                        String[] skill = SkillDances.get(key);
                        this.html += "<tr>";
                        this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                        this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h3 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    }
                    this.html += "</table>";

                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                this.html += "</td></tr></table>";
                this.html += "</body></html>";

                Html.put("getBufferDances", this.html);
            }
        }
    }
    private void getBufferSpecial()
    {
        if(L2HelperBufferSpecial == true)
        {
            if(Html.containsKey("getBufferSpecial"))
            {
                this.html = Html.get("getBufferSpecial");
            }
            else
            {
                this.html = "<html><body><title>Lineage II Helper</title>";
                this.html += "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358><tr><td valign=\"top\" align=\"center\">";
                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                    for (Iterator<Integer> it = SkillSpecial.keySet().iterator(); it.hasNext();) {
                        Integer key = it.next();
                        String[] skill = SkillSpecial.get(key);
                        this.html += "<tr>";
                        this.html += "<td align=\"left\" width=\"40\"><img src="+skill[3]+" width=32 height=32></td>";
                        this.html += "<td align=\"left\" width=\"200\"><button action=\"bypass -h voice .helper view buffer h4 "+key+"\" value=\""+skill[1]+"\" width=160 height=40 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    }
                    this.html += "</table>";

                    this.html += "<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
                    this.html += "<table border=0 cellpadding=0 cellspacing=0>";
                        this.html += "<tr>";
                        this.html += "<td align=center><button action=\"bypass -h voice .helper\" value=\"Back\" width=240 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
                        this.html += "</tr>";
                    this.html += "</table>";
                this.html += "</td></tr></table>";
                this.html += "</body></html>";

                Html.put("getBufferSpecial", this.html);
            }
        }
    }

    /*
     * Helper
     */
    private void setLevel(Integer lvl)
    {
        try
        {
            if(L2HelperLevelUp == true || L2HelperLevelDown == true)
            {
                Integer item;
                Long count;

                if(lvl == 1) {
                    item = Integer.parseInt(Consumable.get("L2HelperLevelUpItem"));
                    count = Long.parseLong(Consumable.get("L2HelperLevelUpItemCount"));
                    lvl = (this.activeChar.getLevel() + 1);
                } else {
                    item = Integer.parseInt(Consumable.get("L2HelperLevelDownItem"));
                    count = Long.parseLong(Consumable.get("L2HelperLevelDownItemCount"));
                    lvl = (this.activeChar.getLevel() - 1);
                }

                if ((lvl >= 1) && (lvl <= ExperienceTable.getInstance().getMaxLevel()))
                {
                    if(this.activeChar.destroyItemByItemId("Item", item, count, this.activeChar, true))
                    {
                        long pXp = this.activeChar.getExp();
                        long tXp = ExperienceTable.getInstance().getExpForLevel(lvl);

                        if (pXp > tXp) {
                            this.activeChar.removeExpAndSp(pXp - tXp, 0);
                        } else if (pXp < tXp) {
                            this.activeChar.addExpAndSp(tXp - pXp, 0);
                        }
                        this.activeChar.broadcastPacket(new CharInfo(this.activeChar));
                        this.activeChar.sendPacket(new UserInfo(this.activeChar));
                        this.activeChar.broadcastPacket(new ExBrExtraUserInfo(this.activeChar));
                    }
                    else
                    {
                        this.activeChar.sendMessage("Not have require item or count.");
                    }
                }
                else
                {
                    this.activeChar.sendMessage("You have minimal or maximal level.");
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "L2Helper change Level error.", e);
        }
    }
    private void setVitality()
    {
        try
        {
            if(L2HelperVitaltity == true)
            {
                if (this.activeChar.getVitalityPoints() < PcStat.MAX_VITALITY_POINTS)
                {
                    Integer item = Integer.parseInt(Consumable.get("L2HelperVitalityItem"));
                    Long count = Long.parseLong(Consumable.get("L2HelperVitalityItemCount"));

                    if(this.activeChar.destroyItemByItemId("Item", item, count, this.activeChar, true))
                    {
                        this.activeChar.setVitalityPoints(PcStat.MAX_VITALITY_POINTS, true);
                        this.activeChar.broadcastPacket(new CharInfo(this.activeChar));
                        this.activeChar.sendPacket(new UserInfo(this.activeChar));
                        this.activeChar.broadcastPacket(new ExBrExtraUserInfo(this.activeChar));
                    }
                    else
                    {
                        this.activeChar.sendMessage("Not have require item or count.");
                    }
                }
                else
                {
                    this.activeChar.sendMessage("You have maximal vitality points.");
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "L2Helper change Vitality error.", e);
        }
    }
    private void setRec()
    {
        try
        {
            if(L2HelperRecommend == true)
            {
                int currentRec = this.activeChar.getRecomHave();
                int newRec     = currentRec + 1;

                if(currentRec < 255)
                {
                    Integer item = Integer.parseInt(Consumable.get("L2HelperRecommendItem"));
                    Long count = Long.parseLong(Consumable.get("L2HelperRecommendItemCount"));

                    if(this.activeChar.destroyItemByItemId("Item", item, count, this.activeChar, true))
                    {
                        this.activeChar.setRecomHave(newRec);
                        this.activeChar.broadcastUserInfo();
                        this.activeChar.sendPacket(new UserInfo(this.activeChar));
                        this.activeChar.sendPacket(new ExBrExtraUserInfo(this.activeChar));
                        this.activeChar.sendPacket(new ExVoteSystemInfo(this.activeChar));
                        this.activeChar.sendMessage("+1 Recommend. You have " + newRec + " recommend points.");
                    }
                    else
                    {
                        this.activeChar.sendMessage("Not have require item or count.");
                    }
                }
                else
                {
                    this.activeChar.sendMessage("You have maximal recommend points.");
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "L2Helper change Recommend error.", e);
        }
    }
    private void setEnchant()
    {
        try
        {
            if(L2HelperEnchant == true)
            {
                Integer id;
                String type = "";

                if(this.param != null)
                {
                    String[] prm = this.param.split(" ", 3);
                    for (int i = 0; i < prm.length; i++)
                    {
                        if(i == 2)
                        {
                            type = prm[i];
                        }
                    }
                }

                if(!"".equals(type))
                {
                    switch(type)
                    {
                        case "Weapon":
                            id = Inventory.PAPERDOLL_RHAND;
                            break;
                        case "Helmet":
                            id = Inventory.PAPERDOLL_HEAD;
                            break;
                        case "Chest":
                            id = Inventory.PAPERDOLL_CHEST;
                            break;
                        case "Leggings":
                            id = Inventory.PAPERDOLL_LEGS;
                            break;
                        case "Gloves":
                            id = Inventory.PAPERDOLL_GLOVES;
                            break;
                        case "Boots":
                            id = Inventory.PAPERDOLL_FEET;
                            break;
                        case "Shield":
                            id = Inventory.PAPERDOLL_LHAND;
                            break;
                        case "Necklace":
                            id = Inventory.PAPERDOLL_NECK;
                            break;
                        case "UpperEarring":
                            id = Inventory.PAPERDOLL_LEAR;
                            break;
                        case "LowerEarring":
                            id = Inventory.PAPERDOLL_REAR;
                            break;
                        case "UpperRing":
                            id = Inventory.PAPERDOLL_LFINGER;
                            break;
                        case "LowerRing":
                            id = Inventory.PAPERDOLL_RFINGER;
                            break;
                        case "Cloak":
                            id = Inventory.PAPERDOLL_CLOAK;
                            break;
                        case "Shirt":
                            id = Inventory.PAPERDOLL_UNDER;
                            break;
                        case "Belt":
                            id = Inventory.PAPERDOLL_BELT;
                            break;
                        default:
                            id = -1;
                            break;
                    }

                    if(L2HelperEnchantWeapon == false && "Weapon".equals(type)) {
                        id = -1;
                    }

                    if(L2HelperEnchantArmor == false && ("Helmet".equals(type) || "Chest".equals(type) || "Leggings".equals(type) || "Gloves".equals(type) || "Boots".equals(type) || "Shield".equals(type))) {
                        id = -1;
                    }

                    if(L2HelperEnchantJewels == false && ("Necklace".equals(type) || "UpperEarring".equals(type) || "LowerEarring".equals(type) || "UpperRing".equals(type) || "LowerRing".equals(type))) {
                        id = -1;
                    }

                    if(L2HelperEnchantOther == false && ("Belt".equals(type) || "Shirt".equals(type) || "Cloak".equals(type))) {
                        id = -1;
                    }

                    if(id == -1)
                    {
                        this.activeChar.sendMessage("Please select the type of enchant item.");
                    }
                    else
                    {
                        L2ItemInstance itemInstance = null;
                        L2ItemInstance parmorInstance = this.activeChar.getInventory().getPaperdollItem(id);

                        if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == id))
                        {
                            itemInstance = parmorInstance;
                        }

                        if(itemInstance != null)
                        {
                            int curEnchant = itemInstance.getEnchantLevel();
                            int Enchant = curEnchant + 1;

                            if(curEnchant < L2HelperEnchantMax)
                            {
                                if(this.activeChar.destroyItemByItemId("Item", L2HelperEnchantItem, L2HelperEnchantItemCount, this.activeChar, true))
                                {
                                    this.activeChar.getInventory().unEquipItemInSlot(id);
                                    itemInstance.setEnchantLevel(Enchant);
                                    this.activeChar.getInventory().equipItem(itemInstance);
                                    InventoryUpdate iu = new InventoryUpdate();
                                    iu.addModifiedItem(itemInstance);

                                    this.activeChar.sendPacket(iu);
                                    this.activeChar.broadcastPacket(new CharInfo(this.activeChar));
                                    this.activeChar.sendPacket(new UserInfo(this.activeChar));
                                    this.activeChar.broadcastPacket(new ExBrExtraUserInfo(this.activeChar));
                                    this.activeChar.sendMessage("Changed enchantment of " + this.activeChar.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + Enchant + ".");
                                    this.activeChar.sendMessage("Changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + Enchant + ".");
                                }
                                else
                                {
                                    this.activeChar.sendMessage("Not have require item or count.");
                                }
                            }
                            else
                            {
                                this.activeChar.sendMessage("You have max allowed enchant level.");
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "L2Helper change enchant error.", e);
        }
    }

    /*
     * Newbie
     */
    private void Nwb()
    {
        try
        {
            this.activeChar.sendMessage("L2Helper support for newbie characters.");
            this.activeChar.setNewbie(0);

            if(L2HelperNewbieSetNoble == true)
            {
                this.activeChar.setNoble(true);
            }

            if(L2HelperNewbieSupportAdena != 0)
            {
                this.activeChar.addItem("Adena", 57, L2HelperNewbieSupportAdena, this.activeChar, true);
            }

            if(L2HelperNewbieSupport == true)
            {
                this.activeChar.addItem("EtcItem", 21093, 5, this.activeChar, true); // Sweet Fruit Cocktail
                this.activeChar.addItem("EtcItem", 21094, 5, this.activeChar, true); // Fresh Fruit Cocktail
                this.activeChar.addItem("EtcItem", 21091, 1, this.activeChar, true); // Rune of Experience Points 50% 7-Day Packd
                this.activeChar.addItem("EtcItem", 21092, 1, this.activeChar, true); // Rune of SP 50% 7-Day Pack
                this.activeChar.addItem("EtcItem", 20391, 1, this.activeChar, true); // Vitality Maintaining Potion
                this.activeChar.addItem("EtcItem", 20392, 1, this.activeChar, true); // Vitality Replenishing Potion
            }

            if(!"".equals(L2HelperNewbieSetTitle))
            {
                this.activeChar.setTitle(L2HelperNewbieSetTitle);
                this.activeChar.broadcastTitleInfo();
            }

            if(!"".equals(L2HelperNewbieSetNameColor))
            {
				this.activeChar.getAppearance().setTitleColor(Integer.decode("0x" + L2HelperNewbieSetNameColor));
                this.activeChar.broadcastTitleInfo();
            }

            if(!"".equals(L2HelperNewbieSetTitleColor))
            {
                this.activeChar.getAppearance().setNameColor(Integer.decode("0x" + L2HelperNewbieSetTitleColor));
                this.activeChar.broadcastTitleInfo();
            }

            if(L2HelperNewbieGetProtection == true)
            {
                SkillTable.getInstance().getInfo(5182,1).getEffects(this.activeChar,this.activeChar);
            }

            List<L2MacroCmd> commands = new ArrayList<>();
            L2ShortCut sc;

            commands.add(new L2MacroCmd(0, 3, 0, 0, ".helper"));
            this.activeChar.getMacros().registerMacro(new L2Macro(1000, 3, "Helper", "", "", commands.toArray(new L2MacroCmd[commands.size()])));

            sc = new L2ShortCut(0, 3, 4, 1000, 0, 0);
            new ShortCuts(this.activeChar).registerShortCut(sc);
            this.activeChar.sendPacket(new ShortCutRegister(sc));

            if(this.activeChar.isGM()) // For GMs
            {
                commands.clear();
                commands.add(new L2MacroCmd(0, 3, 0, 0, "//admin"));
                this.activeChar.getMacros().registerMacro(new L2Macro(1001, 4, "Admin", "", "", commands.toArray(new L2MacroCmd[commands.size()])));
                sc = new L2ShortCut(1, 3, 4, 1001, 0, 0);
                new ShortCuts(this.activeChar).registerShortCut(sc);
                this.activeChar.sendPacket(new ShortCutRegister(sc));

                commands.clear();
                commands.add(new L2MacroCmd(0, 3, 0, 0, "//gmshop"));
                this.activeChar.getMacros().registerMacro(new L2Macro(1002, 5, "GMShop", "", "", commands.toArray(new L2MacroCmd[commands.size()])));
                sc = new L2ShortCut(2, 3, 4, 1002, 0, 0);
                new ShortCuts(this.activeChar).registerShortCut(sc);
                this.activeChar.sendPacket(new ShortCutRegister(sc));

                commands.clear();
                commands.add(new L2MacroCmd(0, 3, 0, 0, "//heal"));
                this.activeChar.getMacros().registerMacro(new L2Macro(1003, 6, "Heal", "", "", commands.toArray(new L2MacroCmd[commands.size()])));
                sc = new L2ShortCut(4, 3, 4, 1003, 0, 0);
                new ShortCuts(this.activeChar).registerShortCut(sc);
                this.activeChar.sendPacket(new ShortCutRegister(sc));

                commands.clear();
                commands.add(new L2MacroCmd(0, 3, 0, 0, "//kill 500"));
                this.activeChar.getMacros().registerMacro(new L2Macro(1004, 2, "Kill Area", "", "", commands.toArray(new L2MacroCmd[commands.size()])));
                sc = new L2ShortCut(5, 3, 4, 1004, 0, 0);
                new ShortCuts(this.activeChar).registerShortCut(sc);
                this.activeChar.sendPacket(new ShortCutRegister(sc));
            }

            this.activeChar.getMacros().sendUpdate();

            if ((L2HelperNewbieStartLevel != 0) && (40 >= 1) && (40 <= ExperienceTable.getInstance().getMaxLevel()))
            {
                long pXp = this.activeChar.getExp();
                long tXp = ExperienceTable.getInstance().getExpForLevel(40);

                if (pXp > tXp) {
                    this.activeChar.removeExpAndSp(pXp - tXp, 0);
                } else if (pXp < tXp) {
                    this.activeChar.addExpAndSp(tXp - pXp, 0);
                }

                if(L2HelperNewbieGetAllSkill == true && Config.AUTO_LEARN_SKILLS == false)
                {
                    this.activeChar.getAllSkills();
                    this.activeChar.sendSkillList();
                }

                this.activeChar.broadcastPacket(new CharInfo(this.activeChar));
                this.activeChar.sendPacket(new UserInfo(this.activeChar));
                this.activeChar.broadcastPacket(new ExBrExtraUserInfo(this.activeChar));
            }

        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "", e);
        }
    }

    /*
     * Buffer
     */
    private void Buff()
    {
        if(L2HelperBuffer == true)
        {
            if(this.param != null)
            {
                String[] prm = this.param.split(" ", 4);
                for (int i = 0; i < prm.length; i++)
                {
                    if(i == 2)
                    {
                        this.Buff(prm[i], null);
                    }
                    if(i == 3)
                    {
                        this.Buff(prm[2], prm[i]);
                    }
                }
            }
        }
    }
    private void Buff(String ID, String Skill)
    {
        if(ID != null)
        {
            switch(ID)
            {
                case "h1": // HTML Buffs
                case "h1-0": // HTML Buffs
                case "h1-1": // HTML Buffs
                case "h1-2": // HTML Buffs
                    if(L2HelperBufferBuffs == true)
                    {
                        this.BuffGet(1, Skill);
                        this.getBufferBuffs(ID);
                    }
                    break;

                case "h2": // HTML Songs
                    if(L2HelperBufferDS == true)
                    {
                        this.BuffGet(2, Skill);
                        this.getBufferSongs();
                    }
                    break;

                case "h3": // HTML Dances
                    if(L2HelperBufferDS == true)
                    {
                        this.BuffGet(3, Skill);
                        this.getBufferDances();
                    }
                    break;

                case "h4": // HTML Special
                    if(L2HelperBufferSpecial == true)
                    {
                        this.BuffGet(4, Skill);
                        this.getBufferSpecial();
                    }
                    break;

                case "r1":
                    if(L2HelperBufferRecharge == true)
                    {
                        this.activeChar.stopAllEffects();
                    }
                    break;
            }
        }

    }
    private void BuffGet(Integer Type, String Skill)
    {
        try
        {
            if(Skill != null)
            {
                Integer id = Integer.parseInt(Skill);
                String[] skill = null;
                Integer item = null;
                Long count = null;

                switch(Type)
                {
                    case 1: // Get Buff
                        skill = SkillBuffs.get(id);
                        item = Integer.parseInt(Consumable.get("L2HelperBufferItemsByIdBuffs"));
                        count = Long.parseLong(Consumable.get("L2HelperBufferItemPriceCountBuffs"));
                        break;

                    case 2: // Get Songs
                        skill = SkillSongs.get(id);
                        item = Integer.parseInt(Consumable.get("L2HelperBufferItemsByIdDS"));
                        count = Long.parseLong(Consumable.get("L2HelperBufferItemPriceCountDS"));
                        break;

                    case 3: // Get Dances
                        skill = SkillDances.get(id);
                        item = Integer.parseInt(Consumable.get("L2HelperBufferItemsByIdDS"));
                        count = Long.parseLong(Consumable.get("L2HelperBufferItemPriceCountDS"));
                        break;

                    case 4: // Get Special
                        skill = SkillSpecial.get(id);
                        item = Integer.parseInt(Consumable.get("L2HelperBufferItemsByIdSpecial"));
                        count = Long.parseLong(Consumable.get("L2HelperBufferItemPriceCountSpecial"));
                        break;
                }

                if(skill != null && item != null && count != null) {
                    if(this.activeChar.destroyItemByItemId("Item", item, count, this.activeChar, true))
                    {
                        SkillTable.getInstance().getInfo(id,Integer.parseInt(skill[0])).getEffects(this.activeChar, this.activeChar);
                    }
                    else
                    {
                        this.activeChar.sendMessage("Not have require item or count.");
                    }
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "", e);
        }

    }
    private void BuffRecharge()
    {
        try
        {
            if(L2HelperBufferRecharge == true)
            {
                Integer item = Integer.parseInt(Consumable.get("L2HelperBufferItemsById"));
                Long count = Long.parseLong(Consumable.get("L2HelperBufferItemPriceCount"));

                if(this.activeChar.destroyItemByItemId("Item", item, count, this.activeChar, true))
                {
                    this.activeChar.setCurrentHpMp(this.activeChar.getMaxHp(), this.activeChar.getMaxMp());
                    this.activeChar.setCurrentCp(this.activeChar.getMaxCp());
                    this.activeChar.broadcastPacket(new CharInfo(this.activeChar));
                    this.activeChar.sendPacket(new UserInfo(this.activeChar));
                    this.activeChar.broadcastPacket(new ExBrExtraUserInfo(this.activeChar));
                    this.activeChar.sendMessage("Set max HP MP CP");
                }
                else
                {
                    this.activeChar.sendMessage("Not have require item or count.");
                }
            }
        }
        catch (Exception e)
        {
            _log.log(Level.WARNING, "", e);
        }
    }

}
