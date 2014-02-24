package at.pavlov.cannons.Enum;

public enum MessageEnum
{
	//Error Messages
    ErrorFiringInProgress ("Error.FiringInProgress"),
	ErrorBarrelTooHot ("Error.BarrelTooHot"),
	ErrorNoProjectile ("Error.NoProjectile"),
	ErrorNoGunpowder ("Error.NoGunpowder"),
	ErrorNoFlintAndSteel ("Error.NoFlintAndSteel"),
	ErrorMaximumGunpowderLoaded ("Error.MaximumGunpowderLoaded"),
	ErrorProjectileAlreadyLoaded ("Error.ProjectileAlreadyLoaded"),
	ErrorCannonBuiltLimit ("Error.CannonBuiltLimit"),
	ErrorNotTheOwner ("Error.NotTheOwner"),
    ErrorMissingSign ("Error.MissingSign"),
	
	//Aiming
	SettingCombinedAngle ("Aiming.SettingCombinedAngle"),
	SettingVerticalAngleUp ("Aiming.SettingVerticalAngleUp"),
	SettingVerticalAngleDown ("Aiming.SettingVerticalAngleDown"),
	SettingHorizontalAngleRight ("Aiming.SettingHorizontalAngleRight"),
	SettingHorizontalAngleLeft ("Aiming.SettingHorizontalAngleLeft"),
	AimingModeEnabled ("Aiming.EnableAimingMode"),
	AimingModeDisabled ("Aiming.DisableAimingMode"),
    AimingModeTooFarAway ("Aiming.TooFarForAimingMode"),
	
	//load
	loadProjectile ("Load.Projectile"),
	loadGunpowder ("Load.Gunpowder"),
	
	//cannon
	CannonCreated ("Cannon.Created"),
	CannonDestroyed ("Cannon.Destroyed"),
    CannonsReseted ("Cannon.Reseted"),
	CannonFire ("Cannon.Fire"),

    //projectile
    ProjectileExplosion ("Projectile.Explosion"),
    ProjectileCanceled ("Projectile.Canceled"),

    //heatManagement
    HeatManagementBurn ("HeatManagement.Burn"),
    HeatManagementInfo ("HeatManagement.Info"),
    HeatManagementCritical ("HeatManagement.Critical"),
    HeatManagementOverheated ("HeatManagement.Overheated"),
	
	//Permission
	PermissionErrorRedstone ("Permission.ErrorRedstone"),
	PermissionErrorBuild ("Permission.ErrorBuild"),
	PermissionErrorFire ("Permission.ErrorFire"),
	PermissionErrorLoad ("Permission.ErrorLoad"),
	PermissionErrorAdjust ("Permission.ErrorAdjust"),
	PermissionErrorProjectile ("Permission.ErrorProjectile"),
	
	//Help
	HelpText ("Help.Text"),
	HelpBuild ("Help.Build"),
    HelpFire ("Help.Fire"),
	HelpAdjust ("Help.Adjust");

	
	private final String str;
	
	MessageEnum(String str)
	{
		this.str = str;
	}

	public String getString()
	{
		return str;
	}
}