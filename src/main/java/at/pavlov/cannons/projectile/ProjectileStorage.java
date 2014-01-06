package at.pavlov.cannons.projectile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.container.MaterialHolder;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileProperties;
import at.pavlov.cannons.utils.CannonsUtil;

public class ProjectileStorage
{
	Cannons plugin;


    private static List<Projectile> projectileList = new ArrayList<Projectile>();
	
	public ProjectileStorage(Cannons plugin)
	{
		this.plugin = plugin;
		projectileList = new ArrayList<Projectile>();
	}
	
	/**
	 * returns the projectile that can be loaded with this item. If data=-1 the data is ignored
	 * @param item
	 * @return
	 */
	public static Projectile getProjectile(Cannon cannon, ItemStack item)
	{
		if (item == null) return null;
		return getProjectile(cannon, item.getTypeId(), item.getData().getData());
	}
	
	/**
	 * returns the projectiles that can be loaded int the cannon with this id and data. If data=-1 the data is ignored
	 * @param id
	 * @param data
	 * @return
	 */
	public static Projectile getProjectile(Cannon cannon, int id, int data)
	{
		for (Projectile projectile : projectileList)
		{
            if (cannon.getCannonDesign().canLoad(projectile) && projectile.equalsFuzzy(id, data))
				return projectile;
		}
		return null;
	}
	
	/**
	 * loads all projectile designs from the disk or copys the defaults if there is no design
	 */
	public void loadProjectiles()
	{
		plugin.logInfo("Loading projectile configs");
		
		//clear old list
		this.projectileList.clear();
		
		//load defaults if there no projectile folder
		// check if design folder is empty or does not exist
		if (CannonsUtil.isFolderEmpty(getPath()))
		{
			// the folder is empty, copy defaults
			plugin.logInfo("No projectiles loaded - loading default projectiles");
			copyDefaultProjectiles();
		}
		
		//get list of all files in /projectiles/
		ArrayList<String> projectileFileList = getProjectilesFiles();
		
		// stop if there are no files found
		if (projectileFileList == null || projectileFileList.size() == 0)
			return;

		for (String file : projectileFileList)
		{
			//load .yml
			Projectile projectile = loadYml(file);

			plugin.logDebug("load projectile " + file + " item " + projectile.getLoadingItem().toString());
			// add to the list if valid
			if (projectile != null)
				projectileList.add(projectile);
		}	
	}
	
	/**
	 * get all projectile file names form /projectiles
	 * @return
	 */
	private ArrayList<String> getProjectilesFiles()
	{
		ArrayList<String> projectileList = new ArrayList<String>();

		try
		{
			// check plugin/cannons/designs for .yml and .schematic files
			String ymlFile;
			File folder = new File(getPath());

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					ymlFile = listOfFiles[i].getName();
					if (ymlFile.endsWith(".yml") || ymlFile.endsWith(".yaml"))
					{
						// there is a shematic file and a .yml file
						projectileList.add(ymlFile);
					}
				}
			}
		}
		catch (Exception e)
		{
			plugin.logSevere("Error while checking yml and schematic " + e);
		}
		return projectileList;
	}
	
	/**
	 * loads the config for one cannon from the .yml file
	 * @param ymlFile
	 *            of the cannon config file
	 */
	private Projectile loadYml(String ymlFile)
	{
		//create a new projectile
		String id = CannonsUtil.removeExtension(ymlFile);
		Projectile projectile = new Projectile(id);
		// load .yml file

		File projectileFile = new File(getPath() + ymlFile);;
		FileConfiguration projectileConfig = YamlConfiguration.loadConfiguration(projectileFile);
		
		//load it from the disk
		
		//general
		projectile.setProjectileName(projectileConfig.getString("general.projectileName", "noProjectileName"));
		projectile.setItemName(projectileConfig.getString("general.itemName", "noItemName"));
		projectile.setLoadingItem(new MaterialHolder(projectileConfig.getString("general.loadingItem", "1:0")));	
		projectile.setAlternativeItemList(CannonsUtil.toMaterialHolderList(projectileConfig.getStringList("general.alternativeId")));
		
		//cannonball
        projectile.setProjectileEntity(getProjectileEntity(projectileConfig.getString("cannonball.entityType", "SNOWBALL")));
        projectile.setProjectileOnFire(projectileConfig.getBoolean("cannonball.isOnFire", false));
		projectile.setVelocity(projectileConfig.getDouble("cannonball.velocity", 1.0));
		projectile.setPenetration(projectileConfig.getDouble("cannonball.penetration", 0.0));
		projectile.setPenetrationDamage(projectileConfig.getBoolean("cannonball.doesPenetrationDamage", true));
		projectile.setTimefuse(projectileConfig.getDouble("cannonball.timefuse", 0.0));
        projectile.setAutomaticFiringDelay(projectileConfig.getDouble("cannonball.automaticFiringDelay", 1.0));
        projectile.setAutomaticFiringMagazineSize(projectileConfig.getInt("cannonball.automaticFiringMagazineSize", 1));
		projectile.setNumberOfBullets(projectileConfig.getInt("cannonball.numberOfBullets", 1));
		projectile.setSpreadMultiplier(projectileConfig.getDouble("cannonball.spreadMultiplier", 1.0));
		projectile.setPropertyList(toProperties(projectileConfig.getStringList("cannonball.properties")));
		
		//explosion
		projectile.setExplosionPower(projectileConfig.getInt("explosion.explosionPower", 2));
		projectile.setExplosionDamage(projectileConfig.getBoolean("explosion.doesExplosionDamage", true));
        projectile.setUnderwaterDamage(projectileConfig.getBoolean("explosion.doesUnderwaterExplosion", false));
        projectile.setDirectHitDamage(projectileConfig.getDouble("explosion.directHitDamage", 5.0));
        projectile.setPlayerDamageRange(projectileConfig.getDouble("explosion.playerDamageRange", 3.0));
        projectile.setPlayerDamage(projectileConfig.getDouble("explosion.playerDamage", 5.0));
		projectile.setPotionRange(projectileConfig.getDouble("explosion.potionRange", 1.0));
		projectile.setPotionDuration(projectileConfig.getDouble("explosion.potionDuration", 1.0));
		projectile.setPotionAmplifier(projectileConfig.getInt("explosion.potionAmplifier", 0));
		projectile.setPotionsEffectList(toPotionEffect(projectileConfig.getStringList("explosion.potionEffects")));

		//placeBlock
		projectile.setBlockPlaceRadius(projectileConfig.getDouble("placeBlock.radius", 3.0));
		projectile.setBlockPlaceAmount(projectileConfig.getInt("placeBlock.amount", 3));
		projectile.setBlockPlaceVelocity(projectileConfig.getDouble("placeBlock.velocity", 0.1));
        projectile.setTntFuseTime(projectileConfig.getDouble("placeBlock.tntFuseTime", 3));
		projectile.setBlockPlaceList(CannonsUtil.toMaterialHolderList(projectileConfig.getStringList("placeBlock.material")));

        //spawnProjectiles
        projectile.setSpawnProjectiles(projectileConfig.getStringList("spawnProjectiles"));

        //spawnFireworks
        projectile.setFireworksEnabled(projectileConfig.getBoolean("spawnFireworks.enabled", false));
        projectile.setFireworksFlicker(projectileConfig.getBoolean("spawnFireworks.flicker",false));
        projectile.setFireworksTrail(projectileConfig.getBoolean("spawnFireworks.trail",false));
        projectile.setFireworksType(getFireworksType(projectileConfig.getString("spawnFireworks.type", "BALL")));
        projectile.setFireworksColors(toColor(projectileConfig.getStringList("spawnFireworks.colors")));
        projectile.setFireworksFadeColors(toColor(projectileConfig.getStringList("spawnFireworks.fadeColors")));

        //messages
        projectile.setImpactMessage(projectileConfig.getBoolean("messages.hasImpactMessage", false));

		//loadPermissions
		projectile.setPermissionLoad(projectileConfig.getStringList("loadPermission"));
		
		return projectile;
	}
	
	
	/**
	 * copys the default designs from the .jar to the disk
	 */
	private void copyDefaultProjectiles()
    {
        copyFile("tnt");
        copyFile("cobblestone");

        copyFile("enderpearl");

        copyFile("firework1");
        copyFile("firework2");
        copyFile("firework3");
        copyFile("firework4");
	}

    /**
     * copies the yml file from the .jar to the projectile folder
     * @param filename - name of the .yml file
     */
    private void copyFile(String filename)
    {
        File YmlFile = new File(plugin.getDataFolder(), "projectiles/" + filename + ".yml");
        if (!YmlFile.exists())
        {
            YmlFile.getParentFile().mkdirs();
            CannonsUtil.copyFile(plugin.getResource("projectiles/" + filename + ".yml"), YmlFile);
        }
    }
	
	
	/**
	 * returns the path of the projectiles folder
	 * @return
	 */
	private String getPath()
	{
		// Directory path here
		return "plugins/Cannons/projectiles/";
	}
	

	/**
	 * returns a PotionEffectTypeList from a list of strings
	 * @param stringList
	 * @return
	 */
	private List<PotionEffectType> toPotionEffect(List<String> stringList)
	{
		List<PotionEffectType> effectList = new ArrayList<PotionEffectType>();
		
		for (String str : stringList)
		{
			PotionEffectType effect = PotionEffectType.getByName(str);
			if (effect != null)
				effectList.add(effect);
            else
                plugin.logSevere("No potion effect found with the name: " + str);
		}
		return effectList;
	}
	
	/**
	 * returns a PotionEffectTypeList from a list of strings
	 * @param stringList
	 * @return
	 */
	private List<ProjectileProperties> toProperties(List<String> stringList)
	{
		List<ProjectileProperties> projectileList = new ArrayList<ProjectileProperties>();

		for (String str : stringList)
		{
			ProjectileProperties projectile = ProjectileProperties.getByName(str);
			if (projectile != null)
				projectileList.add(projectile);
            else
                plugin.logSevere("No projectile property with the name: " + str + " found");
		}
		return projectileList;
	}



    /**
     * returns a list of colors in RGB integer format from a list of strings in hex format
     * @param stringList
     * @return
     */
    private List<Integer> toColor(List<String> stringList)
    {
        List<Integer> colorList = new ArrayList<Integer>();

        for (String str : stringList)
        {
            try
            {
                Integer color = Integer.parseInt(str,16);
                colorList.add(color);
            }
            catch (Exception ex)
            {
                plugin.logSevere(str + " is not a hexadecimal number");
            }
        }
        return colorList;
    }

    /**
     * returns the projectile with the matching ID
     * @param str
     * @return
     */
    public Projectile getByName(String str)
    {
        for (Projectile projectile : projectileList)
        {
            if (projectile.getProjectileID().equalsIgnoreCase(str))
                return projectile;
        }
        return null;
    }

    /**
     * converts a string into a firework effect
     * @param str - name of the effect
     * @return fittiong firework effect
     */
    public FireworkEffect.Type getFireworksType(String str)
    {
        try
        {
            return FireworkEffect.Type.valueOf(str);
        }
        catch(Exception ex)
        {
            plugin.logDebug(str + " is not a valid fireworks type. BALL was used instead.");
            return FireworkEffect.Type.BALL;
        }
    }

    /**
     * returns converts a string into a firework effect
     * @param str - name of the effect
     * @return fittiong firework effect
     */
    public EntityType getProjectileEntity(String str)
    {
        try
        {
            return EntityType.valueOf(str.toUpperCase());
        }
        catch(Exception ex)
        {
            plugin.logSevere(str + " is not a valid entity type. SNOWBALL was used instead.");
            return EntityType.SNOWBALL;
        }
    }

}
